package nam.gor.stocktaking.infrastructure.dao;

import nam.gor.stocktaking.infrastucture.dao.intrfc.StockKeeperDao;
import nam.gor.stocktaking.infrastucture.documents.StockKeeperDocument;
import nam.gor.stocktaking.domain.entities.StockKeeper;
import nam.gor.stocktaking.domain.factories.StockKeeperFactory;
import nam.gor.stocktaking.infrastucture.serializers.impl.StockKeeperSerialImpl;
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
import reactor.test.StepVerifier;

@SpringBootTest(
        classes = {
                DBTestAutoConfig.class,
                StockKeeperDao.class,
                StockKeeperSerialImpl.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@EnableAutoConfiguration
@ComponentScan("nam.gor.stocktaking.dao")
@ContextConfiguration(initializers = DBContainerInit.class)
@ExtendWith(DBRollbackExtension.class)
class StockKeeperDaoTest {

    @Autowired
    private StockKeeperDao dao;

    @Autowired
    private ReactiveMongoTemplate template;

    @AfterEach
    void tearDown() {
        cleanCollection();
    }

    @Nested
    @DisplayName("method: findAll()")
    class FindAllMethod {
        private StockKeeper sk1;
        private StockKeeper sk2;
        private StockKeeper sk3;

        @BeforeEach
        void setUp() {
            sk1 = StockKeeperFactory
                    .newStockKeeperEntity()
                    .toBuilder()
                    .firstName("StockKeeper 2")
                    .lastName("LastName 2")
                    .workContractNumber(222L)
                    .build();
            sk2 = StockKeeperFactory
                    .newStockKeeperEntity()
                    .toBuilder()
                    .firstName("StockKeeper 1")
                    .lastName("LastName 1")
                    .workContractNumber(345L)
                    .build();
            sk3 = StockKeeperFactory
                    .newStockKeeperEntity()
                    .toBuilder()
                    .firstName("StockKeeper 3")
                    .lastName("LastName 3")
                    .workContractNumber(678L)
                    .build();
            dao.save(sk1).block();
            dao.save(sk2).block();
            dao.save(sk3).block();
        }

        @Test
        @DisplayName(
                "when called, then it should return" +
                " all StockKeepers ordered by name")
        void whenCalled_shouldReturnAllStockKeepersOrderedByName() {
            StepVerifier
                    .create(dao.findAll())
                    .expectSubscription()
                    .expectNext(sk2, sk1, sk3)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: findById(String)")
    class FindByIdMethod {
        private final StockKeeper keeper = StockKeeperFactory.newStockKeeperEntity();

        @BeforeEach
        void setUp() {
            dao.save(keeper).block();
        }

        @Test
        @DisplayName(
                "when called with existing id, " +
                "then it should return the matching StockKeeper")
        void whenCalledWithExistingId_shouldReturnTheMatchingStockKeeper() {
            StepVerifier
                    .create(dao.findById(keeper.getId()))
                    .expectSubscription()
                    .expectNext(keeper)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with unknown id, " +
                "then it should return an empty Mono")
        void whenCalledWithUnknownId_shouldReturnAnEmptyMono() {
            StepVerifier
                    .create(dao.findById("stockKeeperId"))
                    .expectSubscription()
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: save(StockKeeperOutDTO)")
    class SaveMethod {

        @Test
        @DisplayName(
                "when stockKeeper was not previously saved, " +
                "then it should persist it")
        void whenStockKeeperWasNotPreviouslySaved_shouldPersistIt() {
            final StockKeeper keeper = StockKeeperFactory.newStockKeeperEntity();
            dao.save(keeper).block();
            StepVerifier
                    .create(dao.findById(keeper.getId()))
                    .expectSubscription()
                    .expectNext(keeper)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when StockKeeper was previously saved," +
                " then it should update it")
        void whenStockKeeperWasPreviouslySaved_shouldUpdateIt() {
            final StockKeeper keeper = StockKeeperFactory
                    .newStockKeeperEntity();
            dao.save(keeper).block();
            final StockKeeper newKeeper = keeper
                    .toBuilder()
                    .firstName("New StockKeeper First Name")
                    .lastName("New StockKeeper Last Name")
                    .workContractNumber(111L)
                    .build();
            dao.save(newKeeper).block();
            StepVerifier
                    .create(dao.findById(keeper.getId()))
                    .expectSubscription()
                    .expectNext(newKeeper)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: deleteById(String)")
    class DeleteById {
        private final StockKeeper keeper = StockKeeperFactory
                .newStockKeeperEntity();

        @BeforeEach
        void setUp() {
            dao.save(keeper).block();
        }

        @Test
        @DisplayName(
                "when called with existing id, " +
                "then it should return true" +
                " and delete the stockKeeper")
        void whenCalledWithExistingId_shouldReturnTrueAndDeleteTheStockKeeper() {
            StepVerifier
                    .create(dao.deleteById(keeper.getId()))
                    .expectSubscription()
                    .expectNext(true)
                    .verifyComplete();
            StepVerifier
                    .create(dao.findById(keeper.getId()))
                    .expectSubscription()
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with unknown id, " +
                "then it should return false" +
                " and no StockKeeper should be deleted")
        void whenCalledWithUnknownId_shouldReturnFalseAndNoStockKeeperShouldBeDeleted() {
            StepVerifier
                    .create(dao.deleteById("stockKeeperId"))
                    .expectSubscription()
                    .expectNext(false)
                    .verifyComplete();
            StepVerifier
                    .create(dao.findById(keeper.getId()))
                    .expectSubscription()
                    .expectNext(keeper)
                    .verifyComplete();
        }
    }

    private void cleanCollection() {
        template.dropCollection(StockKeeperDocument.class).block();
    }
}
