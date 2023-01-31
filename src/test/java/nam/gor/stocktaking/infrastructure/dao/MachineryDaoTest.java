package nam.gor.stocktaking.infrastructure.dao;

import nam.gor.stocktaking.infrastucture.dao.intrfc.MachineryDao;
import nam.gor.stocktaking.infrastucture.documents.MachineryDocument;
import nam.gor.stocktaking.domain.entities.Machinery;
import nam.gor.stocktaking.domain.entities.Query;
import nam.gor.stocktaking.domain.factories.MachineryFactory;
import nam.gor.stocktaking.infrastucture.serializers.impl.MachinerySerialImpl;
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
                MachineryDao.class,
                MachinerySerialImpl.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@EnableAutoConfiguration
@ComponentScan("nam.gor.stocktaking.dao")
@ContextConfiguration(initializers = DBContainerInit.class)
@ExtendWith(DBRollbackExtension.class)
class MachineryDaoTest {

    @Autowired
    private MachineryDao dao;

    @Autowired
    private ReactiveMongoTemplate template;

    @AfterEach
    void tearDown() {
        cleanCollection();
    }

    @Nested
    @DisplayName("method: findByQuery()")
    class FindByQueryMethod {
        private Machinery m1;
        private Machinery m2;
        private Machinery m3;

        @BeforeEach
        void setUp() {
            this.m1 = MachineryFactory
                    .newMachineryEntity()
                    .toBuilder()
                    .quantity(5)
                    .name("456")
                    .build();
            this.m2 = MachineryFactory
                    .newMachineryEntity()
                    .toBuilder()
                    .quantity(10)
                    .name("123")
                    .build();
            this.m3 = MachineryFactory
                    .newMachineryEntity()
                    .toBuilder()
                    .quantity(15)
                    .name("658")
                    .build();
            Flux
                    .just(m1, m2, m3)
                    .flatMap(dao::save)
                    .blockLast();
        }

        @Test
        @DisplayName(
                "when query is empty, " +
                "then it should return all persisted" +
                " machines sorted by name")
        void whenQueryIsEmpty_shouldReturnAllPersistedMachinesSortedByName() {
            final Query query = Query.builder().build();
            final Flux<Machinery> machines = dao.findByQuery(query);
            StepVerifier
                    .create(machines)
                    .expectSubscription()
                    .expectNext(m2, m1, m3)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when query contains 'name', " +
                "then it should return all " +
                "machines matching it")
        void whenQueryContainsName_shouldReturnAllMachinesMatchingIt() {
            final Query query = Query
                    .builder()
                    .name(m1.getName())
                    .build();
            final Flux<Machinery> machines = dao.findByQuery(query);
            StepVerifier
                    .create(machines)
                    .expectSubscription()
                    .expectNext(m1)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when query contains 'minQuantity', " +
                "then it should return all the machines" +
                " which have a quantity greater" +
                " than or equals to it")
        void whenQueryContainsMinQuantity_shouldReturnAllTheMachinesWhichHaveAPriceGreaterThanOrEqualsToIt() {
            final Query query = Query
                    .builder()
                    .minQuantity(10)
                    .build();
            final Flux<Machinery> machines = dao.findByQuery(query);
            StepVerifier
                    .create(machines)
                    .expectSubscription()
                    .expectNext(m2)
                    .expectNext(m3)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when query contains 'taskMasterId', " +
                "then it should return all " +
                "the machines matching it")
        void whenQueryContainsTaskMasterId_shouldReturnAllTheMachinesMatchingIt() {
            final Query query = Query
                    .builder()
                    .taskmasterId(m1.getTaskMaster().getId())
                    .build();
            final Flux<Machinery> machines = dao.findByQuery(query);
            StepVerifier
                    .create(machines)
                    .expectSubscription()
                    .expectNext(m1)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when query contains multiple elements," +
                " then it should return only " +
                "the machinery matching it")
        void whenQueryContainsMultipleElements_shouldReturnOnlyTheMachineryMatchingIt() {
            final Query query = Query
                    .builder()
                    .minQuantity(10)
                    .name(m1.getName())
                    .minQuantity(m1.getQuantity())
                    .build();
            final Flux<Machinery> machines = dao.findByQuery(query);
            StepVerifier
                    .create(machines)
                    .expectSubscription()
                    .expectNext(m1)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: findById(String)")
    class FindByIdMethod {
        private final Machinery machinery = MachineryFactory
                .newMachineryEntity();

        @BeforeEach
        void setUp() {
            dao.save(machinery).block();
        }

        @Test
        @DisplayName(
                "when called with existing id, " +
                "then it should return the " +
                "matching machinery wrapped into a Mono")
        void whenCalledWithExistingId_shouldReturnTheMatchingMachineryWrappedIntoAMono() {
            final Mono<Machinery> foundEq = dao
                    .findById(machinery.getId());
            StepVerifier
                    .create(foundEq)
                    .expectSubscription()
                    .expectNext(machinery)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with unknown id, " +
                "then it should return an empty Mono")
        void whenCalledWithUnknownId_shouldReturnAnEmptyMono() {
            final Mono<Machinery> foundM = dao.findById(UUID.randomUUID().toString());
            StepVerifier
                    .create(foundM)
                    .expectSubscription()
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("class: save(MachineryOutDTO)")
    class SaveMethod {

        @Test
        @DisplayName(
                "when called and the machinery " +
                "was not previously saved, " +
                "then it should persist the machinery")
        void whenCalledAndTheMachinesWasNotPreviouslySaved_shouldPersistTheMachinery() {
            final Machinery machinery = MachineryFactory.newMachineryEntity();
            final Mono<Machinery> mono = dao
                    .save(machinery)
                    .then(dao.findById(machinery.getId()));
            StepVerifier
                    .create(mono)
                    .expectSubscription()
                    .expectNext(machinery)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called and the machinery " +
                        "was previously saved, " +
                        "then it should update the machinery")
        void whenTheFieldIdMatchesAnExistingMachinery_shouldThrowAnException() {
            final Machinery persistedTime = MachineryFactory.newMachineryEntity();
            final Machinery newM = persistedTime
                    .toBuilder()
                    .name("newName")
                    .build();
            final Mono<Machinery> mono = dao
                    .save(persistedTime)
                    .then(dao.save(newM))
                    .then(dao.findById(persistedTime.getId()));
            StepVerifier
                    .create(mono)
                    .expectSubscription()
                    .expectNext(newM)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: deleteById(String)")
    class DeleteByIdMethod {
        private final Machinery machinery = MachineryFactory
                .newMachineryEntity();

        @BeforeEach
        void setUp() {
            dao.save(machinery).block();
        }

        @Test
        @DisplayName(
                "when called with existing id, " +
                        "then it should return true")
        void whenCalledWithExistingId_shouldReturnTrue() {
            Mono<Boolean> mono = dao.deleteById(machinery.getId());
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
        template.dropCollection(MachineryDocument.class).block();
    }
}
