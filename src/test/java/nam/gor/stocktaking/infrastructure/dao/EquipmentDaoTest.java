package nam.gor.stocktaking.infrastructure.dao;


import nam.gor.stocktaking.infrastucture.dao.intrfc.EquipmentDao;
import nam.gor.stocktaking.infrastucture.documents.EquipmentDocument;
import nam.gor.stocktaking.domain.entities.Equipment;
import nam.gor.stocktaking.domain.entities.Query;
import nam.gor.stocktaking.domain.factories.EquipmentFactory;
import nam.gor.stocktaking.infrastucture.serializers.impl.EquipmentSerialImpl;
import nam.gor.stocktaking.infrastructure.DBTestAutoConfig;
import nam.gor.stocktaking.utils.DBRollbackExtension;
import nam.gor.stocktaking.utils.init.DBContainerInit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;


@SpringBootTest(
        classes = {
                DBTestAutoConfig.class,
                EquipmentDao.class,
                EquipmentSerialImpl.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@EnableAutoConfiguration
@ComponentScan("nam.gor.stocktaking.dao")
@ContextConfiguration(initializers = DBContainerInit.class)
@ExtendWith(DBRollbackExtension.class)
class EquipmentDaoTest {

    @Autowired
    private EquipmentDao dao;

    @Autowired
    private ReactiveMongoTemplate template;

    @AfterEach
    void tearDown() {
        cleanCollection();
    }

    @Nested
    @DisplayName("method: findByQuery()")
    class FindByQueryMethod {
        private Equipment eq1;
        private Equipment eq2;
        private Equipment eq3;

        @BeforeEach
        void setUp() {
            this.eq1 = EquipmentFactory
                    .newEquipmentEntity()
                    .toBuilder()
                    .quantity(5)
                    .name("456")
                    .build();

            this.eq2 = EquipmentFactory
                    .newEquipmentEntity()
                    .toBuilder()
                    .quantity(10)
                    .name("123")
                    .build();

            this.eq3 = EquipmentFactory
                    .newEquipmentEntity()
                    .toBuilder()
                    .quantity(15)
                    .name("658")
                    .build();

            Flux.just(eq1, eq2, eq3)
                    .flatMap(dao::save)
                    .blockLast();
        }

        @Test
        @DisplayName(
                "when query is empty, " +
                "then it should return all persisted" +
                " equipments sorted by name")
        void whenQueryIsEmpty_shouldReturnAllPersistedEquipmentsSortedByName() {
            final Query query = Query.builder().build();
            final Flux<Equipment> equipments = dao.findByQuery(query);
            StepVerifier
                    .create(equipments)
                    .expectSubscription()
                    .expectNext(eq2, eq1, eq3)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when query contains 'name', " +
                "then it should return all " +
                "equipments matching it")
        void whenQueryContainsName_shouldReturnAllEquipmentsMatchingIt() {
            final Query query = Query
                    .builder()
                    .name(eq1.getName())
                    .build();
            final Flux<Equipment> equipments = dao.findByQuery(query);
            StepVerifier
                    .create(equipments)
                    .expectSubscription()
                    .expectNext(eq1)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when query contains 'minQuantity', " +
                "then it should return all the equipments" +
                " which have a quantity greater" +
                " than or equals to it")
        void whenQueryContainsMinQuantity_shouldReturnAllTheEquipmentsWhichHaveAPriceGreaterThanOrEqualsToIt() {
            final Query query = Query
                    .builder()
                    .minQuantity(10)
                    .build();
            final Flux<Equipment> equipments = dao.findByQuery(query);
            StepVerifier
                    .create(equipments)
                    .expectSubscription()
                    .expectNext(eq2)
                    .expectNext(eq3)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when query contains 'taskMasterId', " +
                "then it should return all " +
                "the equipments matching it")
        void whenQueryContainsTaskMasterId_shouldReturnAllTheEquipmentsMatchingIt() {
            final Query query = Query
                    .builder()
                    .taskmasterId(eq1.getTaskMaster().getId())
                    .build();
            final Flux<Equipment> equipments = dao.findByQuery(query);
            StepVerifier
                    .create(equipments)
                    .expectSubscription()
                    .expectNext(eq1)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when query contains multiple elements," +
                " then it should return only " +
                "the equipment matching it")
        void whenQueryContainsMultipleElements_shouldReturnOnlyTheEquipmentMatchingIt() {
            final Query query = Query
                    .builder()
                    .minQuantity(10)
                    .name(eq1.getName())
                    .minQuantity(eq1.getQuantity())
                    .build();
            final Flux<Equipment> equipments = dao.findByQuery(query);
            StepVerifier
                    .create(equipments)
                    .expectSubscription()
                    .expectNext(eq1)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: findById(String)")
    class FindByIdMethod {
        private final Equipment equipment = EquipmentFactory
                .newEquipmentEntity();

        @BeforeEach
        void setUp() {
            dao.save(equipment).block();
        }

        @Test
        @DisplayName(
                "when called with existing id, " +
                "then it should return the " +
                "matching equipment wrapped into a Mono")
        void whenCalledWithExistingId_shouldReturnTheMatchingEquipmentWrappedIntoAMono() {
            final Mono<Equipment> foundEq = dao
                    .findById(equipment.getId());
            StepVerifier
                    .create(foundEq)
                    .expectSubscription()
                    .expectNext(equipment)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with unknown id, " +
                "then it should return an empty Mono")
        void whenCalledWithUnknownId_shouldReturnAnEmptyMono() {
            final Mono<Equipment> foundEq = dao.findById(UUID.randomUUID().toString());
            StepVerifier
                    .create(foundEq)
                    .expectSubscription()
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("class: save(EquipmentOutDTO)")
    class SaveMethod {

        @Test
        @DisplayName(
                "when called and the equipment " +
                "was not previously saved, " +
                "then it should persist the equipment")
        void whenCalledAndTheEquipmentsWasNotPreviouslySaved_shouldPersistTheEquipment() {
            final Equipment equipment = EquipmentFactory.newEquipmentEntity();
            final Mono<Equipment> mono = dao
                    .save(equipment)
                    .then(dao.findById(equipment.getId()));
            StepVerifier
                    .create(mono)
                    .expectSubscription()
                    .expectNext(equipment)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called and the equipment " +
                "was previously saved, " +
                "then it should update the equipment")
        void whenTheFieldIdMatchesAnExistingEquipment_shouldThrowAnException() {
            final Equipment persistedTime = EquipmentFactory.newEquipmentEntity();
            final Equipment newEq = persistedTime
                    .toBuilder()
                    .name("newName")
                    .build();
            final Mono<Equipment> mono = dao
                    .save(persistedTime)
                    .then(dao.save(newEq))
                    .then(dao.findById(persistedTime.getId()));
            StepVerifier
                    .create(mono)
                    .expectSubscription()
                    .expectNext(newEq)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: deleteById(String)")
    class DeleteByIdMethod {
        private final Equipment equipment = EquipmentFactory
                .newEquipmentEntity();

        @BeforeEach
        void setUp() {
            dao.save(equipment).block();
        }

        @Test
        @DisplayName(
                "when called with existing id, " +
                "then it should return true")
        void whenCalledWithExistingId_shouldReturnTrue() {
            Mono<Boolean> mono = dao.deleteById(equipment.getId());
            StepVerifier
                    .create(mono)
                    .expectSubscription()
                    .expectNext(true)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with unknown id, " +
                "then it should return false")
        void whenCalledWithUnknown_shouldReturnFalse() {
            Mono<Boolean> mono = dao.deleteById(UUID.randomUUID().toString());
            StepVerifier
                    .create(mono)
                    .expectSubscription()
                    .expectNext(false)
                    .verifyComplete();
        }
    }

    private void cleanCollection() {
        template.dropCollection(EquipmentDocument.class).block();
    }
}