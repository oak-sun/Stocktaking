package nam.gor.stocktaking.infrastructure.handlers;

import com.github.javafaker.Faker;
import nam.gor.stocktaking.infrastucture.dao.impl.TaskMasterDaoImpl;
import nam.gor.stocktaking.infrastucture.dao.intrfc.TaskMasterDao;
import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterSaveDTO;
import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterUpdateDTO;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.domain.factories.TaskMasterFactory;
import nam.gor.stocktaking.infrastucture.serializers.impl.TaskMasterSerialImpl;
import nam.gor.stocktaking.domain.IdGenerator;
import nam.gor.stocktaking.utils.DBRollbackExtension;
import nam.gor.stocktaking.infrastructure.DBTestAutoConfig;
import nam.gor.stocktaking.utils.init.DBContainerInit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ContextConfiguration(initializers = {
        DBContainerInit.class
})
@SpringBootTest(classes = {
        TaskMasterDaoImpl.class,
        DBTestAutoConfig.class,
        TaskMasterSerialImpl.class
})
@AutoConfigureWebTestClient
@ComponentScan("nam.gor.stocktaking")
@ExtendWith(DBRollbackExtension.class)
class TaskMasterHandlerTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private TaskMasterDao tmDao;

    @SpyBean
    private IdGenerator idGenerator;

    private static final String URI = "/api/v1/taskmasters";


    private TaskMaster tm1;
    private TaskMaster tm2;
    private TaskMaster tm3;

    @BeforeEach
    void setUp() {
        this.tm1 = TaskMasterFactory
                .newTaskMasterEntity()
                .toBuilder()
                .firstName("TaskMaster first name 2")
                .lastName("TaskMaster last name 2")
                .workContractNumber(111_111_002L)
                .objectName("TaskMaster object name 2")
                .teamNumber(11_002L)
                .build();
        this.tm2 = TaskMasterFactory
                .newTaskMasterEntity()
                .toBuilder()
                .firstName("TaskMaster first name 1")
                .lastName("TaskMaster last name 1")
                .workContractNumber(111_111_001L)
                .objectName("TaskMaster object name 1")
                .teamNumber(11_001L)
                .build();
        this.tm3 = TaskMasterFactory.newTaskMasterEntity()
                .toBuilder()
                .firstName("TaskMaster first name 3")
                .lastName("TaskMaster last name 3")
                .workContractNumber(111_111_003L)
                .objectName("TaskMaster object name 3")
                .teamNumber(11_003L)
                .build();
        tmDao.save(tm1).block();
        tmDao.save(tm2).block();
        tmDao.save(tm3).block();
    }

    @Nested
    @DisplayName("method: findAllTaskMasters(ServerRequest)")
    class FindAllTaskMastersMethod {
    

        @Test
        @DisplayName(
                "when called, then it should " + 
                "return all the task masters ordered by name")
        void whenCalled_shouldReturnAllTheTaskMastersOrderedByName() {
            client
                    .get()
                    .uri(URI)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$[0].id").value(is(tm2.getId()))
                    .jsonPath("$[0].firstName")
                    .value(is(tm2.getFirstName()))
                    .jsonPath("$[0].lastName")
                    .value(is(tm2.getLastName()))
                    .jsonPath("$[0].workContractNumber")
                    .value(is(tm2.getWorkContractNumber()))
                    .jsonPath("$[0].objectName")
                    .value(is(tm2.getObjectName()))
                    .jsonPath("$[0].teamNumber")
                    .value(is(tm2.getTeamNumber()))
                    
                    .jsonPath("$[1].id").value(is(tm1.getId()))
                    .jsonPath("$[1].firstName")
                    .value(is(tm1.getFirstName()))
                    .jsonPath("$[1].lastName")
                    .value(is(tm1.getLastName()))
                    .jsonPath("$[1].workContractNumber")
                    .value(is(tm1.getWorkContractNumber()))
                    .jsonPath("$[1].objectName")
                    .value(is(tm1.getObjectName()))
                    .jsonPath("$[1].teamNumber")
                    .value(is(tm1.getTeamNumber()))
                    
                    .jsonPath("$[2].id").value(is(tm3.getId()))
                    .jsonPath("$[2].firstName")
                    .value(is(tm3.getFirstName()))
                    .jsonPath("$[2].lastName")
                    .value(is(tm3.getLastName()))
                    .jsonPath("$[2].workContractNumber")
                    .value(is(tm3.getWorkContractNumber()))
                    .jsonPath("$[2].objectName")
                    .value(is(tm3.getObjectName()))
                    .jsonPath("$[2].teamNumber")
                    .value(is(tm3.getTeamNumber()));
        }
    }

    @Nested
    @DisplayName("method: saveTaskMaster(ServerRequest)")
    class SaveTaskMasterMethod {
        private final String TASK_MASTER_ID = UUID.randomUUID().toString();
        private final Faker faker = Faker.instance();

        @Test
        @DisplayName(
                "when called with valid payload, " +
                "then it should return 201")
        void whenCalledWithValidPayload_shouldReturn201() {
            when(idGenerator.newId())
                    .thenReturn(TASK_MASTER_ID);
            final TaskMasterSaveDTO dto = new TaskMasterSaveDTO(
                    "TaskMaster first name",
                    "TaskMaster last name",
                    111_111_001L,
                    "TaskMaster object name",
                    11_001L

            );
            client
                    .post()
                    .uri(URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").value(is(TASK_MASTER_ID))
                    .jsonPath("$.firstName").value(is("TaskMaster first name"))
                    .jsonPath("$.lastName").value(is("TaskMaster last name"))
                    .jsonPath("$.workContractNumber").value(is(111_111_001L))
                    .jsonPath("$.objectName").value(is("TaskMaster object name"))
                    .jsonPath("$.teamNumber").value(is(11_001L));
        }

        @Test
        @DisplayName(
                "when called without first name, " +
                "then it should return 400 error")
        void whenCalledWithoutFirstName_shouldReturn400Error() {
            final TaskMasterSaveDTO dto = new TaskMasterSaveDTO(
                    null,
                    faker.lorem().characters(),
                    faker.random().nextLong(100_000_090L),
                    faker.lorem().characters(),
                    faker.random().nextLong(10_080L)
                    );
            client
                    .post()
                    .uri(URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("first name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field is mandatory"));
        }

        @Test
        @DisplayName(
                "when called with invalid first name, " +
                "then it should return 400 error")
        void whenCalledWithInvalidFirstName_shouldReturn400Error() {
            final TaskMasterSaveDTO dto = new TaskMasterSaveDTO(
                    faker.lorem().fixedString(155),
                    faker.lorem().fixedString(8),
                    faker.random().nextLong(100_000_090L),
                    faker.lorem().fixedString(8),
                    faker.random().nextLong(100_090L)
            );
            client
                    .post()
                    .uri(URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("first name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 150 characters"));
        }
        
        
        @Test
        @DisplayName(
                "when called without last name, " +
                        "then it should return 400 error")
        void whenCalledWithoutLastName_shouldReturn400Error() {
            final TaskMasterSaveDTO dto = new TaskMasterSaveDTO(
                    faker.lorem().characters(),
                    null,
                    faker.random().nextLong(100_000_090L),
                    faker.lorem().characters(),
                    faker.random().nextLong(10_080L)
            );
            client
                    .post()
                    .uri(URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("last name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field is mandatory"));
        }

        @Test
        @DisplayName("when called with invalid last name, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidLastName_shouldReturn400Error() {
            final TaskMasterSaveDTO dto = new TaskMasterSaveDTO(
                    faker.lorem().fixedString(8),
                    faker.lorem().fixedString(155),
                    faker.random().nextLong(100_000_090L),
                    faker.lorem().fixedString(8),
                    faker.random().nextLong(100_090L)
            );
            client
                    .post()
                    .uri(URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("last name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 150 characters"));
        }
        

        @Test
        @DisplayName("when called without work contract number, " +
                        "then it should return 400 error")
        void whenCalledWithoutWorkContractNumber_shouldReturn400Error() {
            final TaskMasterSaveDTO dto = new TaskMasterSaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    null,
                    faker.lorem().characters(),
                    faker.random().nextLong(10_666L)
            );
            client
                    .post()
                    .uri(URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("work contract number"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field is mandatory"));
        }

        @ParameterizedTest
        @ValueSource(longs = {-29L, -1L, 0L})
        @DisplayName("when called with invalid work contract number, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidWorkContractNumber_shouldReturn400Error(
                                                       long workContractNumber) {
            final TaskMasterSaveDTO dto = new TaskMasterSaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    workContractNumber,
                    faker.lorem().characters(),
                    faker.random().nextLong()
            );
            client
                    .post()
                    .uri(URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("work contract number"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName("when called without object name, " +
                "then it should return 400 error")
        void whenCalledWithoutObjectName_shouldReturn400Error() {
            final TaskMasterSaveDTO dto = new TaskMasterSaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().characters(),
                    faker.random().nextLong(100_000_090L),
                    null,
                    faker.random().nextLong(10_080L)
            );
            client
                    .post()
                    .uri(URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("object name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field is mandatory"));
        }

        @Test
        @DisplayName("when called with invalid object name, " +
                "then it should return 400 error")
        void whenCalledWithInvalidObjectName_shouldReturn400Error() {
            final TaskMasterSaveDTO dto = new TaskMasterSaveDTO(
                    faker.lorem().fixedString(8),
                    faker.lorem().fixedString(8),
                    faker.random().nextLong(100_000_090L),
                    faker.lorem().fixedString(155),
                    faker.random().nextLong(100_090L)
            );
            client
                    .post()
                    .uri(URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("object name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 150 characters"));
        }
        
        @Test
        @DisplayName("when called without team number " +
                "then it should return 400 error")
        void whenCalledWithoutTeamNumber_shouldReturn400Error() {
            final TaskMasterSaveDTO dto = new TaskMasterSaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    faker.random().nextLong(888_008_999L),
                    faker.lorem().characters(),
                    null
            );
            client
                    .post()
                    .uri(URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("team number"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field is mandatory"));
        }

        @ParameterizedTest
        @ValueSource(longs = {-29L, -1L, 0L})
        @DisplayName("when called with invalid team number, " +
                "then it should return 400 error")
        void whenCalledWithInvalidTeamNumber_shouldReturn400Error(
                                                   long teamNumber) {
            final TaskMasterSaveDTO dto = new TaskMasterSaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    faker.random().nextLong(888_008_999L),
                    faker.lorem().characters(),
                    teamNumber
            );
            client
                    .post()
                    .uri(URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("team number"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must contain a positive value"));
        }

    }

    @Nested
    @DisplayName("method: updateTaskMasterById(ServerRequest)")
    class UpdateTaskMasterByIdMethod {
        private final TaskMaster master = TaskMasterFactory
                .newTaskMasterEntity();

        @BeforeEach
        void setUp() {
            tmDao.save(master).block();
        }

        final Faker faker = Faker.instance();

        @Test
        @DisplayName(
                "when called with invalid first name, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidFirstName_shouldReturn400Error() {
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    faker.lorem().fixedString(151),
                    null,
                    null,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + tm1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is(" first name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 150 characters"));
        }

        @Test
        @DisplayName("when called with valid first name, " +
                        "then it should return 204 and update it")
        void whenCalledWithValidFirstName_shouldReturn204AndUpdateIt() {
            final String newFirstName = faker.lorem().sentence();
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    newFirstName,
                    null,
                    null,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + tm1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            final TaskMaster expectedEq = tm1
                    .toBuilder()
                    .firstName(newFirstName)
                    .build();
            StepVerifier
                    .create(tmDao.findById(tm1.getId()))
                    .expectSubscription()
                    .expectNext(expectedEq)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with invalid last name, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidLastName_shouldReturn400Error() {
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    null,
                    faker.lorem().fixedString(151),
                    null,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + tm1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("last name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 150 characters"));
        }

        @Test
        @DisplayName("when called with valid last name, " +
                "then it should return 204 and update it")
        void whenCalledWithValidLastName_shouldReturn204AndUpdateIt() {
            final String newLastName = faker.lorem().sentence();
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    null,
                    newLastName,
                    null,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + tm1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            final TaskMaster expectedEq = tm1
                    .toBuilder()
                    .lastName(newLastName)
                    .build();
            StepVerifier
                    .create(tmDao.findById(tm1.getId()))
                    .expectSubscription()
                    .expectNext(expectedEq)
                    .verifyComplete();
        }
        
        @ParameterizedTest
        @ValueSource(longs = {-23L, -1L, 0L})
        @DisplayName("when called with invalid work contract number, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidWorkContractNumber_shouldReturn400Error(
                                                       Long workContractNumber) {
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    null,
                    null,
                    workContractNumber,
                    null,
                    null
                    
            );
            client
                    .patch()
                    .uri(URI + "/" + tm1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("work contract number"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName("when called with valid work contract number, " +
                        "then it should return 204 and update it")
        void whenCalledWithValidWorkContractNumber_shouldReturn204AndUpdateIt() {
            final Long newWorkContractNumber = faker.random().nextLong(100_007L);
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    null,
                    null,
                    newWorkContractNumber,
                    null,
                    null
                    
            );
            client
                    .patch()
                    .uri(URI + "/" + tm1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            final TaskMaster expectedM = tm1
                    .toBuilder()
                    .workContractNumber(newWorkContractNumber)
                    .build();
            StepVerifier
                    .create(tmDao.findById(tm1.getId()))
                    .expectSubscription()
                    .expectNext(expectedM)
                    .verifyComplete();
        }


        @Test
        @DisplayName("when called with invalid object name, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidObjectName_shouldReturn400Error() {
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    null,
                    null,
                    null,
                    faker.lorem().fixedString(151),
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + tm1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("object name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 150 characters"));
        }

        @Test
        @DisplayName("when called with valid object name, " +
                "then it should return 204 and update it")
        void whenCalledWithValidObjectName_shouldReturn204AndUpdateIt() {
            final String newObjectName = faker.lorem().sentence();
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    null,
                    null,
                    null,
                    newObjectName,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + tm1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            final TaskMaster expectedEq = tm1
                    .toBuilder()
                    .objectName(newObjectName)
                    .build();
            StepVerifier
                    .create(tmDao.findById(tm1.getId()))
                    .expectSubscription()
                    .expectNext(expectedEq)
                    .verifyComplete();
        }


        @ParameterizedTest
        @ValueSource(longs = {-23L, -1L, 0L})
        @DisplayName("when called with invalid work contract number, " +
                "then it should return 400 error")
        void whenCalledWithInvalidTeamNumber_shouldReturn400Error(
                                                        Long teamNumber) {
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    teamNumber

            );
            client
                    .patch()
                    .uri(URI + "/" + tm1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("team number"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName("when called with valid work team number, " +
                "then it should return 204 and update it")
        void whenCalledWithValidTeamNumber_shouldReturn204AndUpdateIt() {
            final Long newTeamNumber = faker.random().nextLong(100_007L);
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    newTeamNumber

            );
            client
                    .patch()
                    .uri(URI + "/" + tm1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            final TaskMaster expectedM = tm1
                    .toBuilder()
                    .teamNumber(newTeamNumber)
                    .build();
            StepVerifier
                    .create(tmDao.findById(tm1.getId()))
                    .expectSubscription()
                    .expectNext(expectedM)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with unknown taskMasterId," +
                " then it should return 404 error")
        void whenCalledWithUnknownTaskMasterId_shouldReturn404() {
            final TaskMasterUpdateDTO dto = TaskMasterFactory.newUpdateTaskMaster();
            final String unknownId = UUID.randomUUID().toString();
            client
                    .patch()
                    .uri(URI + "/{taskMasterId}",
                            unknownId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNotFound()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is(format("TaskMaster with ID %s was not found",
                            unknownId)))
                    .jsonPath("$.details")
                    .isEmpty();
        }



        @Test
        @DisplayName(
                "when called with multiple invalid fields," +
                        " then it should return 400")
        void whenCalledWithMultipleInvalidFields_shouldReturn400() {
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    faker.lorem().fixedString(1501),
                    faker.lorem().fixedString(1054),
                    -7L,
                    faker.lorem().fixedString(1051),
                    -1L
            );
            client
                    .patch()
                    .uri(URI + "/" + tm1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("first name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 1000 characters"))
                    .jsonPath("$.details[1].field")
                    .value(is("object name"))
                    .jsonPath("$.details[1].message")
                    .value(is("the field must not exceed 1000 characters"))
                    .jsonPath("$.details[2].field")
                    .value(is("team number"))
                    .jsonPath("$.details[2].message")
                    .value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName("when called with multiple valid fields, " +
                        "then it should return 204 and update all of them")
        void whenCalledWithMultipleValidFields_shouldReturn204AndUpdateAllOfThem() {
            final String newLastName = faker.lorem().sentence();
            final long newWorkNumberContract = faker.random().nextLong(555_999L);
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    null,
                    null,
                    newWorkNumberContract,
                    null,
                   null
            );
            client
                    .patch()
                    .uri(URI + "/" + tm1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            final TaskMaster expectedM = tm1
                    .toBuilder()
                    .lastName(newLastName)
                    .workContractNumber(newWorkNumberContract).build();
            StepVerifier
                    .create(tmDao.findById(tm1.getId()))
                    .expectSubscription()
                    .expectNext(expectedM)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when some unexpected error is throw," +
                        " then it should return 500")
        void whenSomeUnexpectedErrorIsThrown_shouldReturn500() {
            doThrow(RuntimeException.class)
                    .when(tmDao).findById(tm1.getId());
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    90L
            );
            client
                    .patch()
                    .uri(URI + "/" + tm1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .is5xxServerError()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("There's been an unexpected error. " +
                            "Please, contact support."))
                    .jsonPath("$.details")
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("method: deleteTaskMasterById(ServerRequest)")
    class DeleteTaskMasterByIdMethod {
        private final TaskMaster master = TaskMasterFactory
                .newTaskMasterEntity();

        @BeforeEach
        void setUp() {
            tmDao.save(master).block();
        }

        @Test
        @DisplayName(
                "when called with unknown taskMasterId, " +
                "then it should return 404 error")
        void whenCalledWithUnknownTaskMasterId_shouldReturn404() {
            final String unknownId = UUID.randomUUID().toString();
            client
                    .delete()
                    .uri(URI + "/{taskMasterId}",
                            unknownId)
                    .exchange()
                    .expectStatus()
                    .isNotFound()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is(format(
                            "TaskMaster with ID %s was not found",
                            unknownId)))
                    .jsonPath("$.details")
                    .isEmpty();
            assertThat(tmDao.findById(master.getId())
                    .blockOptional())
                    .hasValue(master);
        }

        @Test
        @DisplayName(
                "when called with existing id," +
                " then it should return 204")
        void whenCalledWithExistingId_shouldReturn204() {
            client
                    .delete()
                    .uri(URI + "/{taskMasterId}",
                            master.getId())
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();
            assertThat(tmDao.findById(master.getId()).blockOptional())
                    .isEmpty();
        }
    }
}