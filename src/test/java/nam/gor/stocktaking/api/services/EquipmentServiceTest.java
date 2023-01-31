package nam.gor.stocktaking.api.services;

import nam.gor.stocktaking.api.dto.equipment.EquipmentFindDTO;
import nam.gor.stocktaking.api.dto.equipment.EquipmentOutDTO;
import nam.gor.stocktaking.api.dto.equipment.EquipmentSaveDTO;
import nam.gor.stocktaking.api.dto.equipment.EquipmentUpdateDTO;
import nam.gor.stocktaking.infrastucture.dao.impl.StockDaoAmazonS3Impl;
import nam.gor.stocktaking.infrastucture.dao.intrfc.EquipmentDao;
import nam.gor.stocktaking.infrastucture.dao.intrfc.TaskMasterDao;
import nam.gor.stocktaking.domain.entities.Equipment;
import nam.gor.stocktaking.domain.entities.Query;
import nam.gor.stocktaking.domain.entities.Stock;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.domain.factories.EquipmentFactory;
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
class EquipmentServiceTest {

    @InjectMocks
    private EquipmentService service;

    @Mock
    private EquipmentDao eqDao;

    @Mock
    private TaskMasterDao tmDao;

    @Mock
    private StockDaoAmazonS3Impl stDao;

    @Mock
    private IdGenerator idGen;

    @Mock
    private RequestValidator reqValid;

    @Nested
    @DisplayName("method: findAllEquipments(EquipmentFindDTO)")
    class FindAllEquipmentsMethod {
        private final EquipmentFindDTO dto = EquipmentFactory.newFindEquipmentsDto();
        private final Query query = dto.toEntity();

        private final Equipment eq1 = EquipmentFactory.newEquipmentEntity();
        private final Equipment eq2 = EquipmentFactory.newEquipmentEntity();
        private final Equipment eq3 = EquipmentFactory.newEquipmentEntity();
        private final Stock st1 = StockFactory.newStockEntity();
        private final Stock st2 = StockFactory.newStockEntity();
        private final Stock st3 = StockFactory.newStockEntity();

        @BeforeEach
        void setUp() {
            when(eqDao.findByQuery(query))
                    .thenReturn(Flux.just(eq1, eq2, eq3));
            when(stDao.generatePreSignedUrlForVisualization(eq1.getStockIdentifier()))
                    .thenReturn(Mono.just(st1));
            when(stDao.generatePreSignedUrlForVisualization(eq2.getStockIdentifier()))
                    .thenReturn(Mono.just(st2));
            when(stDao.generatePreSignedUrlForVisualization(eq3.getStockIdentifier()))
                    .thenReturn(Mono.just(st3));
        }

        @AfterEach
        void tearDown() {
            verify(eqDao).findByQuery(query);
            verify(stDao).generatePreSignedUrlForVisualization(eq1.getStockIdentifier());
            verify(stDao).generatePreSignedUrlForVisualization(eq2.getStockIdentifier());
            verify(stDao).generatePreSignedUrlForVisualization(eq3.getStockIdentifier());
            verifyNoMoreInteractions(eqDao, stDao);
            verifyNoInteractions(idGen, reqValid, tmDao);
        }

        @Test
        @DisplayName(
                "when called, then it should" +
                " forward the call to the underlying repository " +
                "and convert the result to dto")
        void whenCalled_shouldForwardTheCallToTheUnderlyingDaoAndConvertTheResultDto() {
            StepVerifier
                    .create(service.findAllEquipments(dto))
                    .expectSubscription()
                    .expectNext(EquipmentOutDTO.toDto(eq1, st1))
                    .expectNext(EquipmentOutDTO.toDto(eq2, st2))
                    .expectNext(EquipmentOutDTO.toDto(eq3, st3))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: findEquipmentById(String)")
    class FindEquipmentByIdMethod {
        private final Equipment equipment = EquipmentFactory.newEquipmentEntity();
        private final Stock stock = StockFactory.newStockEntity();

        @AfterEach
        void tearDown() {
            verify(eqDao).findById(equipment.getId());
            verifyNoMoreInteractions(eqDao);
            verifyNoInteractions(reqValid, idGen, tmDao);
        }

        @Test
        @DisplayName(
                "when equipment is not found, " +
                "then it should return an error")
        void whenEquipmentIsNotFound_shouldReturnAnError() {
            when(eqDao.findById(equipment.getId()))
                    .thenReturn(Mono.empty());
            StepVerifier
                    .create(service.findEquipmentById(equipment.getId()))
                    .expectSubscription()
                    .verifyErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage("Equipment with ID %s was not found", 
                                    equipment.getId())
                    );
            verifyNoInteractions(stDao);
        }

        @Test
        @DisplayName(
                "when equipment is found, " +
                "then it should convert it to dto")
        void whenEquipmentIsFound_shouldConvertItToDto() {
            when(eqDao.findById(equipment.getId()))
                    .thenReturn(Mono.just(equipment));
            when(stDao.generatePreSignedUrlForVisualization(equipment
                    .getStockIdentifier())).thenReturn(Mono.just(stock));
            StepVerifier
                    .create(service.findEquipmentById(equipment.getId()))
                    .expectSubscription()
                    .expectNext(EquipmentOutDTO.toDto(equipment, stock))
                    .verifyComplete();

            verify(stDao).generatePreSignedUrlForVisualization(equipment.getStockIdentifier());
            verifyNoMoreInteractions(stDao);
        }
    }

    @Nested
    @DisplayName("method: saveEquipment(EquipmentSaveDTO)")
    class SaveEquipmentMethod {
        private final EquipmentSaveDTO dto = EquipmentFactory.newSaveEquipmentDto();
        private final TaskMaster master = TaskMasterFactory.newTaskMasterEntity();
        private final Equipment equipment = dto.toEntity(UUID.randomUUID().toString(), master);
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
                    .create(service.saveEquipment(dto))
                    .expectSubscription()
                    .expectErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(RequestValidationException.class)
                            .hasMessage("[]"))
                    .verify();
            verifyNoMoreInteractions(reqValid);
            verifyNoInteractions(idGen, tmDao, eqDao, stDao);
        }

        @Test
        @DisplayName(
                "when taskMaster is not found, " +
                "then it should return an error")
        void whenTaskMasterIsNotFound_shouldReturnAnError() {
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(tmDao.findById(dto.getTaskMasterId()))
                    .thenReturn(Mono.empty());
            StepVerifier
                    .create(service.saveEquipment(dto))
                    .expectSubscription()
                    .verifyErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage("TaskMaster with ID %s was not found",
                                    dto.getTaskMasterId())
                    );
            verify(tmDao).findById(dto.getTaskMasterId());
            verifyNoInteractions(eqDao, idGen, stDao);
        }

        @Test
        @DisplayName(
                "when the equipment is saved successfully," +
                " then it should return the dto representation of it")
        void whenTheEquipmentIsSavedSuccessfully_shouldReturnTheDtoRepresentationOfIt() {
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(tmDao.findById(dto.getTaskMasterId()))
                    .thenReturn(Mono.just(master));
            when(eqDao.save(equipment)).thenReturn(Mono.empty());
            when(stDao.generatePreSignedUrlForVisualization(equipment.getStockIdentifier()))
                    .thenReturn(Mono.just(stock));
            when(idGen.newId()).thenReturn(equipment.getId());
            StepVerifier
                    .create(service.saveEquipment(dto))
                    .expectSubscription()
                    .expectNext(EquipmentOutDTO.toDto(equipment, stock))
                    .verifyComplete();
            verify(eqDao).save(equipment);
            verify(tmDao).findById(dto.getTaskMasterId());
            verify(idGen).newId();
            verify(stDao)
                    .generatePreSignedUrlForVisualization(equipment.getStockIdentifier());
            verifyNoMoreInteractions(reqValid, idGen, eqDao, tmDao);
        }
    }

    @Nested
    @DisplayName("method: updateEquipmentById(String, EquipmentUpdateDTO)")
    class UpdateEquipmentByIdMethod {
        private final Equipment existingEq = EquipmentFactory.newEquipmentEntity();
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
            final EquipmentUpdateDTO dto = EquipmentFactory.newUpdateEquipmentDto();
            when(reqValid.validate(dto))
                    .thenReturn(Mono.error(
                            new RequestValidationException(emptyList())));
            StepVerifier
                    .create(service.updateEquipmentById(existingEq.getId(), dto))
                    .expectSubscription()
                    .expectErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(RequestValidationException.class).hasMessage("[]"))
                    .verify();
            verify(reqValid).validate(dto);
            verifyNoMoreInteractions(reqValid);
            verifyNoInteractions(eqDao);
        }

        @Test
        @DisplayName(
                "when equipment is not found, " +
                "then it should return an error")
        void whenEquipmentIsNotFound_shouldReturnAnError() {
            final EquipmentUpdateDTO dto = EquipmentFactory.newUpdateEquipmentDto();
            when(reqValid.validate(dto)).thenReturn(Mono.just(dto));
            when(eqDao.findById(existingEq.getId())).thenReturn(Mono.empty());
            StepVerifier
                    .create(service.updateEquipmentById(existingEq.getId(), dto))
                    .expectSubscription()
                    .expectErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage(
                                    "Equipment with ID %s was not found",
                                    existingEq.getId()))
                    .verify();
            verify(reqValid).validate(dto);
            verify(eqDao).findById(existingEq.getId());
            verifyNoMoreInteractions(eqDao, reqValid);
            verifyNoInteractions(tmDao);
        }

        @Test
        @DisplayName(
                "when taskMasterId is present, " +
                "and taskMaster is not found, " +
                "then it should return an error")
        void whenTaskMasterIdIsPresent_andTaskMasterIsNotFound_shouldReturnAnError() {
            final EquipmentUpdateDTO dto = EquipmentFactory.newUpdateEquipmentDto();
            final String taskMasterId = dto.getTaskMasterId().orElseThrow();
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(eqDao.findById(existingEq.getId()))
                    .thenReturn(Mono.just(existingEq));
            when(tmDao.findById(taskMasterId))
                    .thenReturn(Mono.empty());
            StepVerifier
                    .create(service.updateEquipmentById(existingEq.getId(), dto))
                    .expectSubscription()
                    .verifyErrorSatisfies(error ->
                            assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage("TaskMaster with ID %s was not found",
                                    taskMasterId)
                    );
            verify(reqValid).validate(dto);
            verify(eqDao).findById(existingEq.getId());
            verify(tmDao).findById(taskMasterId);
            verifyNoMoreInteractions(eqDao, tmDao, reqValid);
        }

        @Test
        @DisplayName(
                "when the taskMasterId is present, " +
                "then it should fetch the taskMaster" +
                " and update the equipment")
        void whenTheTaskMasterIdIsPresent_shouldFetchTheTaskMasterAndUpdateTheEquipment() {
            final EquipmentUpdateDTO dto = EquipmentFactory.newUpdateEquipmentDto();
            final Equipment newEq = dto.toEntity(existingEq).withTaskMaster(existingTM);
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(eqDao.findById(existingEq.getId()))
                    .thenReturn(Mono.just(existingEq));
            when(tmDao.findById(dto.getTaskMasterId().orElseThrow()))
                    .thenReturn(Mono.just(existingTM));
            when(eqDao.save(newEq)).thenReturn(Mono.empty());
            StepVerifier
                    .create(service.updateEquipmentById(existingEq.getId(), dto))
                    .expectSubscription()
                    .verifyComplete();
            verify(reqValid).validate(dto);
            verify(eqDao).findById(existingEq.getId());
            verify(eqDao).save(newEq);
            verify(tmDao).findById(dto.getTaskMasterId().orElseThrow());
            verifyNoMoreInteractions(reqValid, eqDao, tmDao);
        }

        @Test
        @DisplayName(
                "when the taskMasterId is not present, " +
                "then it should not fetch the taskMaster")
        void whenTheTaskMasterIdIsNotPresent_shouldNotFetchTheTaskMaster() {
            final EquipmentUpdateDTO dto = EquipmentFactory
                    .newUpdateEquipmentDtoWithoutTaskMasterId();
            final Equipment newEq = dto.toEntity(existingEq);
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(eqDao.findById(existingEq.getId()))
                    .thenReturn(Mono.just(existingEq));
            when(eqDao.save(newEq))
                    .thenReturn(Mono.empty());
            StepVerifier
                    .create(service.updateEquipmentById(existingEq.getId(), dto))
                    .expectSubscription()
                    .verifyComplete();
            verify(reqValid).validate(dto);
            verify(eqDao).findById(existingEq.getId());
            verify(eqDao).save(newEq);
            verifyNoMoreInteractions(eqDao, reqValid);
            verifyNoInteractions(tmDao);
        }
    }

    @Nested
    @DisplayName("method: deleteEquipmentById(String)")
    class DeleteEquipmentByIdMethod {
        private final String equipmentId = UUID.randomUUID().toString();

        @AfterEach
        void tearDown() {
            verify(eqDao).deleteById(equipmentId);
            verifyNoInteractions(idGen, reqValid, stDao, tmDao);
            verifyNoMoreInteractions(eqDao);
        }

        @Test
        @DisplayName(
                "when the delete fails, " +
                "then it should return an error")
        void whenTheDeleteFails_shouldReturnAnError() {
            when(eqDao.deleteById(equipmentId))
                    .thenReturn(Mono.just(false));
            StepVerifier
                    .create(service.deleteEquipmentById(equipmentId))
                    .expectSubscription()
                    .verifyErrorSatisfies(error ->
                            assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage("Equipment with ID %s was not found",
                                    equipmentId)
                    );
        }

        @Test
        @DisplayName(
                "when the delete succeed, " +
                "then it should return an empty Mono")
        void whenTheDeleteSucceed_shouldReturnAnEmptyMono() {
            when(eqDao.deleteById(equipmentId)).thenReturn(Mono.just(true));
            StepVerifier
                    .create(service.deleteEquipmentById(equipmentId))
                    .expectSubscription()
                    .verifyComplete();
        }
    }
}