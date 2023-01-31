package nam.gor.stocktaking.api.services;

import nam.gor.stocktaking.infrastucture.dao.intrfc.TaskMasterDao;
import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterOutDTO;
import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterSaveDTO;
import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterUpdateDTO;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.domain.factories.TaskMasterFactory;
import nam.gor.stocktaking.domain.IdGenerator;
import nam.gor.stocktaking.api.RequestValidator;
import nam.gor.stocktaking.domain.exceptions.EntityNotFoundException;
import nam.gor.stocktaking.domain.exceptions.RequestValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskMasterServiceTest {

    @InjectMocks
    private TaskMasterService service;

    @Mock
    private TaskMasterDao dao;

    @Mock
    private IdGenerator idGen;

    @Mock
    private RequestValidator reqValid;

    @Nested
    @DisplayName("method: findAllTaskMasters()")
    class FindAllTaskMastersMethod {
        private final TaskMaster m1 = TaskMasterFactory.newTaskMasterEntity();
        private final TaskMaster m2 = TaskMasterFactory.newTaskMasterEntity();
        private final TaskMaster m3 = TaskMasterFactory.newTaskMasterEntity();

        @AfterEach
        void tearDown() {
            verify(dao).findAll();
            verifyNoMoreInteractions(dao);
            verifyNoInteractions(reqValid, idGen);
        }

        @Test
        @DisplayName(
                "when called, then it should" +
                " forward the call to the dao")
        void whenCalled_shouldForwardTheCallToTheDao() {
            when(dao.findAll())
                    .thenReturn(Flux.just(m1, m2, m3));
            StepVerifier
                    .create(service.findAllTaskMasters())
                    .expectSubscription()
                    .expectNext(TaskMasterOutDTO.toDto(m1))
                    .expectNext(TaskMasterOutDTO.toDto(m2))
                    .expectNext(TaskMasterOutDTO.toDto(m3))
                    .verifyComplete();
        }
    }
    
    @Nested
    @DisplayName("method: saveTaskMaster(TaskMasterSaveDTO)")
    class SaveTaskMasterMethod {
        private final String TASK_MASTER_ID = UUID.randomUUID().toString();
        private final TaskMasterSaveDTO dto = TaskMasterFactory
                .newSaveTaskMaster();

        private final TaskMaster master = dto.byIdToEntity(TASK_MASTER_ID);

        @AfterEach
        void tearDown() {
            verify(reqValid).validate(dto);
            verifyNoMoreInteractions(reqValid);
        }

        @Test
        @DisplayName("when validation fails, then it should" +
                " not forward more calls")
        void whenValidationFails_shouldNotForwardMoreCalls() {
            final var error = new RequestValidationException(Collections.emptyList());
            when(reqValid.validate(dto))
                    .thenReturn(Mono.error(error));
            StepVerifier
                    .create(service.saveTaskMaster(dto))
                    .expectSubscription()
                    .verifyError(RequestValidationException.class);
            verifyNoInteractions(idGen, dao);
        }

        @Test
        @DisplayName(
                "when validation succeed, " +
                "then it should forward the calls")
        void whenValidationSucceed_shouldForwardTheCalls() {
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(idGen.newId())
                    .thenReturn(TASK_MASTER_ID);
            when(dao.save(master))
                    .thenReturn(Mono.empty());
            StepVerifier
                    .create(service.saveTaskMaster(dto))
                    .expectSubscription()
                    .expectNext(TaskMasterOutDTO.toDto(master))
                    .verifyComplete();
            verify(idGen).newId();
            verify(dao).save(master);
            verifyNoMoreInteractions(idGen, dao);
        }
    }

    @Nested
    @DisplayName("method: updateTaskMasterById(String, TaskMasterUpdateDTO)")
    class UpdateTaskMasterByIdMethod {
        private final TaskMaster master = TaskMasterFactory
                .newTaskMasterEntity();
        private final TaskMasterUpdateDTO dto = TaskMasterFactory
                .newUpdateTaskMaster();

        private final TaskMaster newMaster = dto.fromEntityToEntity(master);

        @AfterEach
        void tearDown() {
            verify(reqValid).validate(dto);
            verifyNoMoreInteractions(reqValid);
            verifyNoInteractions(idGen);
        }

        @Test
        @DisplayName(
                "when validation fails, " +
                "then it should not forward" +
                " any more calls")
        void whenValidationFails_shouldNotForwardAnyMoreCalls() {
            final var error = new RequestValidationException(
                    Collections.emptyList());
            when(reqValid.validate(dto))
                    .thenReturn(Mono.error(error));
            StepVerifier
                    .create(service
                            .updateTaskMasterById(master.getId(), dto))
                    .expectSubscription()
                    .verifyError(RequestValidationException.class);
            verifyNoInteractions(dao);
        }

        @Test
        @DisplayName(
                "when taskMaster is not found," +
                " then it should return an error")
        void whenTaskMasterIsNotFound_shouldReturnAnError() {
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(dao.findById(master.getId()))
                    .thenReturn(Mono.empty());
            StepVerifier
                    .create(service
                            .updateTaskMasterById(master.getId(), dto))
                    .expectSubscription()
                    .verifyErrorSatisfies(error ->
                            assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage(
                                    "TaskMaster with ID %s was not found",
                                    master.getId())
                    );
            verify(dao).findById(master.getId());
            verifyNoMoreInteractions(dao);
        }

        @Test
        @DisplayName(
                "when taskMaster is found," +
                " then it should update it" +
                " and no errors should happen")
        void whenTaskMasterIsFound_shouldUpdateItAndNoErrorsShouldHappen() {
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(dao.findById(master.getId()))
                    .thenReturn(Mono.just(master));
            when(dao.save(newMaster))
                    .thenReturn(Mono.empty());
            StepVerifier
                    .create(service.updateTaskMasterById(master.getId(), dto))
                    .expectSubscription()
                    .verifyComplete();
            verify(dao).findById(master.getId());
            verify(dao).save(newMaster);
            verifyNoMoreInteractions(dao);
        }
    }

    @Nested
    @DisplayName("method: deleteTaskMasterById(String)")
    class DeleteTaskMasterByIdMethod {
        private final String TASK_MASTER_ID = UUID.randomUUID().toString();

        @AfterEach
        void tearDown() {
            verify(dao).deleteById(TASK_MASTER_ID);
            verifyNoMoreInteractions(dao);
            verifyNoInteractions(idGen, reqValid);
        }

        @Test
        @DisplayName("when taskMaster is deleted successfully," +
                " then it should not return any error")
        void whenTaskMasterIsDeletedSuccessfully_shouldNotReturnAnyError() {
            when(dao.deleteById(TASK_MASTER_ID))
                    .thenReturn(Mono.just(true));
            StepVerifier
                    .create(service.deleteTaskMasterById(TASK_MASTER_ID))
                    .expectSubscription()
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when taskMaster is not " +
                "deleted successfully, " +
                "then it should not return an error")
        void whenTaskMasterIsNotDeletedSuccessfully_shouldNotReturnAnError() {
            when(dao.deleteById(TASK_MASTER_ID))
                    .thenReturn(Mono.just(false));
            StepVerifier
                    .create(service.deleteTaskMasterById(TASK_MASTER_ID))
                    .expectSubscription()
                    .verifyErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage(
                                    "TaskMaster with ID %s was not found",
                                    TASK_MASTER_ID)
                    );
        }
    }
}