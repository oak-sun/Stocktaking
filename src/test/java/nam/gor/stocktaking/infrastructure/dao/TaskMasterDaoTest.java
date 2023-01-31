package nam.gor.stocktaking.infrastructure.dao;


import nam.gor.stocktaking.infrastucture.dao.intrfc.TaskMasterDao;
import nam.gor.stocktaking.infrastucture.documents.TaskMasterDocument;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.domain.factories.TaskMasterFactory;
import nam.gor.stocktaking.infrastucture.serializers.impl.TaskMasterSerialImpl;
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
                TaskMasterDao.class,
                TaskMasterSerialImpl.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@EnableAutoConfiguration
@ComponentScan("nam.gor.stocktaking.dao")
@ContextConfiguration(initializers = DBContainerInit.class)
@ExtendWith(DBRollbackExtension.class)
class TaskMasterDaoTest {

    @Autowired
    private TaskMasterDao dao;

    @Autowired
    private ReactiveMongoTemplate template;

    @AfterEach
    void tearDown() {
        cleanCollection();
    }

    @Nested
    @DisplayName("method: findAll()")
    class FindAllMethod {
        private TaskMaster tm1;
        private TaskMaster tm2;
        private TaskMaster tm3;

        @BeforeEach
        void setUp() {
            tm1 = TaskMasterFactory
                    .newTaskMasterEntity()
                    .toBuilder()
                    .firstName("TaskMaster 2")
                    .lastName("LastName 2")
                    .workContractNumber(222L)
                    .objectName("object name 2")
                    .teamNumber(2L)
                    .build();
            tm2 = TaskMasterFactory.newTaskMasterEntity()
                    .toBuilder()
                    .firstName("TaskMaster 1")
                    .lastName("LastName 1")
                    .workContractNumber(345L)
                    .objectName("object name 1")
                    .teamNumber(1L)
                    .build();
            tm3 = TaskMasterFactory.newTaskMasterEntity()
                    .toBuilder()
                    .firstName("TaskMaster 3")
                    .lastName("LastName 3")
                    .workContractNumber(678L)
                    .objectName("object name 3")
                    .teamNumber(3L)
                    .build();
            dao.save(tm1).block();
            dao.save(tm2).block();
            dao.save(tm3).block();
        }

        @Test
        @DisplayName(
                "when called, then it should return" +
                " all taskMasters ordered by name")
        void whenCalled_shouldReturnAllTaskMastersOrderedByName() {
            StepVerifier
                    .create(dao.findAll())
                    .expectSubscription()
                    .expectNext(tm2, tm1, tm3)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: findById(String)")
    class FindByIdMethod {
        private final TaskMaster master = TaskMasterFactory
                .newTaskMasterEntity();

        @BeforeEach
        void setUp() {
            dao.save(master).block();
        }

        @Test
        @DisplayName(
                "when called with existing id, " +
                "then it should return the matching taskMaster")
        void whenCalledWithExistingId_shouldReturnTheMatchingTaskMaster() {
            StepVerifier
                    .create(dao.findById(master.getId()))
                    .expectSubscription()
                    .expectNext(master)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with unknown id, " +
                "then it should return an empty Mono")
        void whenCalledWithUnknownId_shouldReturnAnEmptyMono() {
            StepVerifier
                    .create(dao.findById("taskMasterId"))
                    .expectSubscription()
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: save(TaskMasterOutDTO)")
    class SaveMethod {

        @Test
        @DisplayName(
                "when taskMaster was not previously saved, " +
                "then it should persist it")
        void whenTaskMasterWasNotPreviouslySaved_shouldPersistIt() {
            final TaskMaster master = TaskMasterFactory
                    .newTaskMasterEntity();
            dao.save(master).block();
            StepVerifier
                    .create(dao.findById(master.getId()))
                    .expectSubscription()
                    .expectNext(master)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when taskMaster was previously saved," +
                " then it should update it")
        void whenTaskMasterWasPreviouslySaved_shouldUpdateIt() {
            final TaskMaster master = TaskMasterFactory
                    .newTaskMasterEntity();
            dao.save(master).block();
            final TaskMaster newMaster = master
                    .toBuilder()
                    .firstName("New TaskMaster First Name")
                    .lastName("New TaskMaster Last Name")
                    .workContractNumber(111L)
                    .objectName("New object name")
                    .teamNumber(11L)
                    .build();
            dao.save(newMaster).block();
            StepVerifier
                    .create(dao.findById(master.getId()))
                    .expectSubscription()
                    .expectNext(newMaster)
                    .verifyComplete();
        }
    }
    
    @Nested
    @DisplayName("method: deleteById(String)")
    class DeleteById {
        private final TaskMaster master = TaskMasterFactory
                .newTaskMasterEntity();

        @BeforeEach
        void setUp() {
            dao.save(master).block();
        }

        @Test
        @DisplayName(
                "when called with existing id, " +
                "then it should return true" +
                " and delete the taskMaster")
        void whenCalledWithExistingId_shouldReturnTrueAndDeleteTheTaskMaster() {
            StepVerifier
                    .create(dao.deleteById(master.getId()))
                    .expectSubscription()
                    .expectNext(true)
                    .verifyComplete();
            StepVerifier
                    .create(dao.findById(master.getId()))
                    .expectSubscription()
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with unknown id, " +
                "then it should return false" +
                " and no taskMaster should be deleted")
        void whenCalledWithUnknownId_shouldReturnFalseAndNoTaskMasterShouldBeDeleted() {
            StepVerifier
                    .create(dao.deleteById("taskMasterId"))
                    .expectSubscription()
                    .expectNext(false)
                    .verifyComplete();
            StepVerifier
                    .create(dao.findById(master.getId()))
                    .expectSubscription()
                    .expectNext(master)
                    .verifyComplete();
        }
    }

    private void cleanCollection() {
        template.dropCollection(TaskMasterDocument.class).block();
    }
}