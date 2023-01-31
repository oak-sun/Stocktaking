package nam.gor.stocktaking.api.services;

import nam.gor.stocktaking.api.dto.machines.MachineryFindDTO;
import nam.gor.stocktaking.api.dto.machines.MachineryOutDTO;
import nam.gor.stocktaking.api.dto.machines.MachinerySaveDTO;
import nam.gor.stocktaking.api.dto.machines.MachineryUpdateDTO;
import nam.gor.stocktaking.infrastucture.dao.impl.StockDaoAmazonS3Impl;
import nam.gor.stocktaking.infrastucture.dao.intrfc.MachineryDao;
import nam.gor.stocktaking.infrastucture.dao.intrfc.TaskMasterDao;
import nam.gor.stocktaking.domain.entities.Machinery;
import nam.gor.stocktaking.domain.entities.Query;
import nam.gor.stocktaking.domain.entities.Stock;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.domain.factories.MachineryFactory;
import nam.gor.stocktaking.domain.factories.StockFactory;
import nam.gor.stocktaking.domain.factories.TaskMasterFactory;
import nam.gor.stocktaking.domain.IdGenerator;
import nam.gor.stocktaking.api.RequestValidator;
import nam.gor.stocktaking.domain.exceptions.EntityNotFoundException;
import nam.gor.stocktaking.domain.exceptions.RequestValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.UUID;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MachineryServiceTest {

    @InjectMocks
    private MachineryService service;

    @Mock
    private MachineryDao mDao;

    @Mock
    private TaskMasterDao tmDao;

    @Mock
    private StockDaoAmazonS3Impl stDao;

    @Mock
    private IdGenerator idGen;

    @Mock
    private RequestValidator reqValid;

    @Nested
    @DisplayName("method: findAllMachines(MachineryFindDTO)")
    class FindAllMachinesMethod {
        private final MachineryFindDTO dto = MachineryFactory.newFindMachinesDto();
        private final Query query = dto.toEntity();

        private final Machinery m1 = MachineryFactory.newMachineryEntity();
        private final Machinery m2 = MachineryFactory.newMachineryEntity();
        private final Machinery m3 = MachineryFactory.newMachineryEntity();
        private final Stock st1 = StockFactory.newStockEntity();
        private final Stock st2 = StockFactory.newStockEntity();
        private final Stock st3 = StockFactory.newStockEntity();

        @BeforeEach
        void setUp() {
            when(mDao.findByQuery(query))
                    .thenReturn(Flux.just(m1, m2, m3));
            when(stDao.generatePreSignedUrlForVisualization(m1.getStockIdentifier()))
                    .thenReturn(Mono.just(st1));
            when(stDao.generatePreSignedUrlForVisualization(m2.getStockIdentifier()))
                    .thenReturn(Mono.just(st2));
            when(stDao.generatePreSignedUrlForVisualization(m3.getStockIdentifier()))
                    .thenReturn(Mono.just(st3));
        }

        @AfterEach
        void tearDown() {
            verify(mDao).findByQuery(query);
            verify(stDao).generatePreSignedUrlForVisualization(m1.getStockIdentifier());
            verify(stDao).generatePreSignedUrlForVisualization(m2.getStockIdentifier());
            verify(stDao).generatePreSignedUrlForVisualization(m3.getStockIdentifier());
            verifyNoMoreInteractions(mDao, stDao);
            verifyNoInteractions(idGen, reqValid, tmDao);
        }

        @Test
        @DisplayName(
                "when called, then it should" +
                " forward the call to the underlying repository " +
                "and convert the result to dto")
        void whenCalled_shouldForwardTheCallToTheUnderlyingDaoAndConvertTheResultDto() {
            StepVerifier
                    .create(service.findAllMachines(dto))
                    .expectSubscription()
                    .expectNext(MachineryOutDTO.toDto(m1, st1))
                    .expectNext(MachineryOutDTO.toDto(m2, st2))
                    .expectNext(MachineryOutDTO.toDto(m3, st3))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: findMachineryById(String)")
    class FindMachineryByIdMethod {
        private final Machinery machinery = MachineryFactory.newMachineryEntity();
        private final Stock stock = StockFactory.newStockEntity();

        @AfterEach
        void tearDown() {
            verify(mDao).findById(machinery.getId());
            verifyNoMoreInteractions(mDao);
            verifyNoInteractions(reqValid, idGen, tmDao);
        }

        @Test
        @DisplayName(
                "when machinery is not found, " +
                "then it should return an error")
        void whenMachineryIsNotFound_shouldReturnAnError() {
            when(mDao.findById(machinery.getId()))
                    .thenReturn(Mono.empty());
            StepVerifier
                    .create(service.findMachineryById(machinery.getId()))
                    .expectSubscription()
                    .verifyErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage("Machinery with ID %s was not found",
                                    machinery.getId())
                    );
            verifyNoInteractions(stDao);
        }

        @Test
        @DisplayName(
                "when machinery is found, " +
                        "then it should convert it to dto")
        void whenMachineryIsFound_shouldConvertItToDto() {
            when(mDao.findById(machinery.getId()))
                    .thenReturn(Mono.just(machinery));
            when(stDao.generatePreSignedUrlForVisualization(machinery
                    .getStockIdentifier())).thenReturn(Mono.just(stock));
            StepVerifier
                    .create(service.findMachineryById(machinery.getId()))
                    .expectSubscription()
                    .expectNext(MachineryOutDTO.toDto(machinery, stock))
                    .verifyComplete();
            verify(stDao)
                    .generatePreSignedUrlForVisualization(machinery.getStockIdentifier());
            verifyNoMoreInteractions(stDao);
        }
    }

    @Nested
    @DisplayName("method: saveMachinery(MachinerySaveDTO)")
    class SaveMachineryMethod {
        private final MachinerySaveDTO dto = MachineryFactory.newSaveMachineryDto();
        private final TaskMaster master = TaskMasterFactory.newTaskMasterEntity();
        private final Machinery machinery = dto.toEntity(UUID.randomUUID().toString(), master);
        private final Stock stock = StockFactory.newStockEntity();

        @AfterEach
        void tearDown() {
            verify(reqValid).validate(dto);
        }

        @Test
        @DisplayName(
                "when validation fails, " +
                "then it should return an error")
        void whenValidationFails_shouldReturnAnError() {
            when(reqValid.validate(dto))
                    .thenReturn(Mono.error(
                            new RequestValidationException(emptyList())));
            StepVerifier
                    .create(service.saveMachinery(dto))
                    .expectSubscription()
                    .expectErrorSatisfies(error ->
                            assertThat(error)
                            .isInstanceOf(RequestValidationException.class)
                            .hasMessage("[]"))
                    .verify();
            verifyNoMoreInteractions(reqValid);
            verifyNoInteractions(idGen, tmDao, mDao, stDao);
        }

        @Test
        @DisplayName(
                "when taskMaster is not found, " +
                "then it should return an error")
        void whenTaskMasterIsNotFound_shouldReturnAnError() {
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(tmDao.findById(dto.getTaskmasterId()))
                    .thenReturn(Mono.empty());
            StepVerifier
                    .create(service.saveMachinery(dto))
                    .expectSubscription()
                    .verifyErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage("TaskMaster with ID %s was not found",
                                    dto.getTaskmasterId())
                    );
            verify(tmDao).findById(dto.getTaskmasterId());
            verifyNoInteractions(mDao, idGen, stDao);
        }

        @Test
        @DisplayName(
                "when the machinery is saved successfully," +
                " then it should return the dto representation of it")
        void whenTheMachineryIsSavedSuccessfully_shouldReturnTheDtoRepresentationOfIt() {
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(tmDao.findById(dto.getTaskmasterId()))
                    .thenReturn(Mono.just(master));
            when(mDao.save(machinery)).thenReturn(Mono.empty());
            when(stDao.generatePreSignedUrlForVisualization(machinery.getStockIdentifier()))
                    .thenReturn(Mono.just(stock));
            when(idGen.newId()).thenReturn(machinery.getId());
            StepVerifier
                    .create(service.saveMachinery(dto))
                    .expectSubscription()
                    .expectNext(MachineryOutDTO.toDto(machinery, stock))
                    .verifyComplete();
            verify(mDao).save(machinery);
            verify(tmDao).findById(dto.getTaskmasterId());
            verify(idGen).newId();
            verify(stDao)
                    .generatePreSignedUrlForVisualization(machinery.getStockIdentifier());
            verifyNoMoreInteractions(reqValid, idGen, mDao, tmDao);
        }
    }

    @Nested
    @DisplayName("method: updateMachineryById(String, MachineryUpdateDTO)")
    class UpdateMachineryByIdMethod {
        private final Machinery existingEq = MachineryFactory.newMachineryEntity();
        private final TaskMaster existingTM = TaskMasterFactory.newTaskMasterEntity();

        @AfterEach
        void tearDown() {
            verifyNoInteractions(idGen, stDao);
        }

        @Test
        @DisplayName(
                "when validator fails, " +
                "then it should return an error")
        void whenValidatorFails_shouldReturnAnError() {
            final MachineryUpdateDTO dto = MachineryFactory.newUpdateMachineryDto();
            when(reqValid.validate(dto))
                    .thenReturn(Mono.error(
                            new RequestValidationException(emptyList())));
            StepVerifier
                    .create(service.updateMachineryById(existingEq.getId(), dto))
                    .expectSubscription()
                    .expectErrorSatisfies(error ->
                            assertThat(error)
                            .isInstanceOf(RequestValidationException.class).hasMessage("[]"))
                    .verify();
            verify(reqValid).validate(dto);
            verifyNoMoreInteractions(reqValid);
            verifyNoInteractions(mDao);
        }

        @Test
        @DisplayName(
                "when machinery is not found, " +
                "then it should return an error")
        void whenMachineryIsNotFound_shouldReturnAnError() {
            final MachineryUpdateDTO dto = MachineryFactory.newUpdateMachineryDto();
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(mDao.findById(existingEq.getId()))
                    .thenReturn(Mono.empty());
            StepVerifier
                    .create(service.updateMachineryById(existingEq.getId(), dto))
                    .expectSubscription()
                    .expectErrorSatisfies(error ->
                            assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage(
                                    "Machinery with ID %s was not found",
                                    existingEq.getId()))
                    .verify();
            verify(reqValid).validate(dto);
            verify(mDao).findById(existingEq.getId());
            verifyNoMoreInteractions(mDao, reqValid);
            verifyNoInteractions(tmDao);
        }

        @Test
        @DisplayName(
                "when taskMasterId is present, " +
                "and taskMaster is not found, " +
                "then it should return an error")
        void whenTaskMasterIdIsPresent_andTaskMasterIsNotFound_shouldReturnAnError() {
            final MachineryUpdateDTO dto = MachineryFactory.newUpdateMachineryDto();
            final String taskMasterId = dto.getTaskmasterId().orElseThrow();
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(mDao.findById(existingEq.getId()))
                    .thenReturn(Mono.just(existingEq));
            when(tmDao.findById(taskMasterId))
                    .thenReturn(Mono.empty());
            StepVerifier
                    .create(service.updateMachineryById(existingEq.getId(), dto))
                    .expectSubscription()
                    .verifyErrorSatisfies(error ->
                            assertThat(error)
                                    .isInstanceOf(EntityNotFoundException.class)
                                    .hasMessage(
                                            "TaskMaster with ID %s was not found",
                                            taskMasterId)
                    );
            verify(reqValid).validate(dto);
            verify(mDao).findById(existingEq.getId());
            verify(tmDao).findById(taskMasterId);
            verifyNoMoreInteractions(mDao, tmDao, reqValid);
        }

        @Test
        @DisplayName(
                "when the taskMasterId is present, " +
                "then it should fetch the taskMaster" +
                " and update the machinery")
        void whenTheTaskMasterIdIsPresent_shouldFetchTheTaskMasterAndUpdateTheMachinery() {
            final MachineryUpdateDTO dto = MachineryFactory.newUpdateMachineryDto();
            final Machinery newEq = dto.toEntity(existingEq).withTaskMaster(existingTM);
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(mDao.findById(existingEq.getId()))
                    .thenReturn(Mono.just(existingEq));
            when(tmDao.findById(dto.getTaskmasterId().orElseThrow()))
                    .thenReturn(Mono.just(existingTM));
            when(mDao.save(newEq)).thenReturn(Mono.empty());
            StepVerifier
                    .create(service.updateMachineryById(existingEq.getId(), dto))
                    .expectSubscription()
                    .verifyComplete();
            verify(reqValid).validate(dto);
            verify(mDao).findById(existingEq.getId());
            verify(mDao).save(newEq);
            verify(tmDao).findById(dto.getTaskmasterId().orElseThrow());
            verifyNoMoreInteractions(reqValid, mDao, tmDao);
        }

        @Test
        @DisplayName(
                "when the taskMasterId is not present, " +
                "then it should not fetch the taskMaster")
        void whenTheTaskMasterIdIsNotPresent_shouldNotFetchTheTaskMaster() {
            final MachineryUpdateDTO dto = MachineryFactory
                    .newUpdateMachineryDtoWithoutTaskMasterId();
            final Machinery newEq = dto.toEntity(existingEq);
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(mDao.findById(existingEq.getId()))
                    .thenReturn(Mono.just(existingEq));
            when(mDao.save(newEq))
                    .thenReturn(Mono.empty());
            StepVerifier
                    .create(service.updateMachineryById(existingEq.getId(), dto))
                    .expectSubscription()
                    .verifyComplete();
            verify(reqValid).validate(dto);
            verify(mDao).findById(existingEq.getId());
            verify(mDao).save(newEq);
            verifyNoMoreInteractions(mDao, reqValid);
            verifyNoInteractions(tmDao);
        }
    }

    @Nested
    @DisplayName("method: deleteMachineryById(String)")
    class DeleteMachineryByIdMethod {
        private final String machineryId = UUID.randomUUID().toString();

        @AfterEach
        void tearDown() {
            verify(mDao).deleteById(machineryId);
            verifyNoInteractions(idGen, reqValid, stDao, tmDao);
            verifyNoMoreInteractions(mDao);
        }

        @Test
        @DisplayName(
                "when the delete fails, " +
                "then it should return an error")
        void whenTheDeleteFails_shouldReturnAnError() {
            when(mDao.deleteById(machineryId))
                    .thenReturn(Mono.just(false));
            StepVerifier
                    .create(service.deleteMachineryById(machineryId))
                    .expectSubscription()
                    .verifyErrorSatisfies(error ->
                            assertThat(error)
                                    .isInstanceOf(EntityNotFoundException.class)
                                    .hasMessage(
                                            "Machinery with ID %s was not found",
                                            machineryId)
                    );
        }

        @Test
        @DisplayName(
                "when the delete succeed, " +
                "then it should return an empty Mono")
        void whenTheDeleteSucceed_shouldReturnAnEmptyMono() {
            when(mDao.deleteById(machineryId)).thenReturn(Mono.just(true));
            StepVerifier
                    .create(service.deleteMachineryById(machineryId))
                    .expectSubscription()
                    .verifyComplete();
        }
    }
}
