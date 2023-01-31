package nam.gor.stocktaking.infrastructure.handlers;

import com.github.javafaker.Faker;
import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperSaveDTO;
import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperUpdateDTO;
import nam.gor.stocktaking.domain.IdGenerator;
import nam.gor.stocktaking.domain.entities.StockKeeper;
import nam.gor.stocktaking.domain.factories.StockKeeperFactory;
import nam.gor.stocktaking.infrastructure.DBTestAutoConfig;
import nam.gor.stocktaking.infrastucture.dao.impl.StockKeeperDaoImpl;
import nam.gor.stocktaking.infrastucture.dao.intrfc.StockKeeperDao;
import nam.gor.stocktaking.infrastucture.serializers.impl.StockKeeperSerialImpl;
import nam.gor.stocktaking.utils.DBRollbackExtension;
import nam.gor.stocktaking.utils.init.DBContainerInit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive
        .AutoConfigureWebTestClient;
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
        StockKeeperDaoImpl.class,
        DBTestAutoConfig.class,
        StockKeeperSerialImpl.class
})
@AutoConfigureWebTestClient
@ComponentScan("nam.gor.stocktaking")
@ExtendWith(DBRollbackExtension.class)
class StockKeeperHandlerTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private StockKeeperDao stDao;

    @SpyBean
    private IdGenerator idGen;

    private static final String URI = "/api/v1/stockkeepers";


    private StockKeeper k1;
    private StockKeeper k2;
    private StockKeeper k3;

    @BeforeEach
    void setUp() {
        this.k1 = StockKeeperFactory
                .newStockKeeperEntity()
                .toBuilder()
                .firstName("StockKeeper first name 2")
                .lastName("StockKeeper last name 2")
                .workContractNumber(111_111_002L)
                .build();
        this.k2 = StockKeeperFactory
                .newStockKeeperEntity()
                .toBuilder()
                .firstName("StockKeeper first name 1")
                .lastName("StockKeeper last name 1")
                .workContractNumber(111_111_001L)
                .build();
        
        this.k3 = StockKeeperFactory.newStockKeeperEntity()
                .toBuilder()
                .firstName("StockKeeper first name 3")
                .lastName("StockKeeper last name 3")
                .workContractNumber(111_111_003L)
                .build();
        stDao.save(k1).block();
        stDao.save(k2).block();
        stDao.save(k3).block();
    }

    @Nested
    @DisplayName("method: findAllTaskMasters(ServerRequest)")
    class FindAllTaskMastersMethod {


        @Test
        @DisplayName(
                "when called, then it should " + 
                "return all the task masters ordered by name")
        void whenCalled_shouldReturnAllTheStockKeepersOrderedByName() {
            client
                    .get()
                    .uri(URI)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$[0].id").value(is(k2.getId()))
                    .jsonPath("$[0].firstName")
                    .value(is(k2.getFirstName()))
                    .jsonPath("$[0].lastName")
                    .value(is(k2.getLastName()))
                    .jsonPath("$[0].workContractNumber")
                    .value(is(k2.getWorkContractNumber()))
                    
                    .jsonPath("$[1].id").value(is(k1.getId()))
                    .jsonPath("$[1].firstName")
                    .value(is(k1.getFirstName()))
                    .jsonPath("$[1].lastName")
                    .value(is(k1.getLastName()))
                    .jsonPath("$[1].workContractNumber")
                    .value(is(k1.getWorkContractNumber()))
                    
                    .jsonPath("$[2].id").value(is(k3.getId()))
                    .jsonPath("$[2].firstName")
                    .value(is(k3.getFirstName()))
                    .jsonPath("$[2].lastName")
                    .value(is(k3.getLastName()))
                    .jsonPath("$[2].workContractNumber")
                    .value(is(k3.getWorkContractNumber()));
        }
    }

    @Nested
    @DisplayName("method: saveStockKeeper(ServerRequest)")
    class SaveStockKeeperMethod {
        private final String STOCK_KEEPER_ID = UUID.randomUUID().toString();
        private final Faker faker = Faker.instance();

        @Test
        @DisplayName(
                "when called with valid payload, " + 
                "then it should return 201")
        void whenCalledWithValidPayload_shouldReturn201() {
            when(idGen.newId())
                    .thenReturn(STOCK_KEEPER_ID);
            final StockKeeperSaveDTO dto = new StockKeeperSaveDTO(
                    "StockKeeper first name",
                    "StockKeeper last name",
                    111_111_001L
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
                    .jsonPath("$.id")
                    .value(is(STOCK_KEEPER_ID))
                    .jsonPath("$.firstName")
                    .value(is("StockKeeper first name"))
                    .jsonPath("$.lastName")
                    .value(is("StockKeeper last name"))
                    .jsonPath("$.workContractNumber")
                    .value(is(111_111_001L));
        }

        @Test
        @DisplayName(
                "when called without first name, " +
                        "then it should return 400 error")
        void whenCalledWithoutFirstName_shouldReturn400Error() {
            final StockKeeperSaveDTO dto = new StockKeeperSaveDTO(
                    null,
                    faker.lorem().characters(),
                    faker.random().nextLong(100_000_090L)
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
                    .value(is("first name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field is mandatory"));
        }

        @Test
        @DisplayName(
                "when called with invalid first name, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidFirstName_shouldReturn400Error() {
            final StockKeeperSaveDTO dto = new StockKeeperSaveDTO(
                    faker.lorem().fixedString(155),
                    faker.lorem().fixedString(8),
                    faker.random().nextLong(100_000_090L)
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
                    .value(is("first name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 150 characters"));
        }


        @Test
        @DisplayName(
                "when called without last name, " + 
                "then it should return 400 error")
        void whenCalledWithoutLastName_shouldReturn400Error() {
            final StockKeeperSaveDTO dto = new StockKeeperSaveDTO(
                    faker.lorem().characters(),
                    null,
                    faker.random().nextLong(100_000_090L)
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
                    .value(is("last name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field is mandatory"));
        }

        @Test
        @DisplayName(
                "when called with invalid last name, " +
                "then it should return 400 error")
        void whenCalledWithInvalidLastName_shouldReturn400Error() {
            final StockKeeperSaveDTO dto = new StockKeeperSaveDTO(
                    faker.lorem().fixedString(8),
                    faker.lorem().fixedString(155),
                    faker.random().nextLong(100_000_090L)
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
                    .value(is("last name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 150 characters"));
        }


        @Test
        @DisplayName("when called without work contract number, " +
                "then it should return 400 error")
        void whenCalledWithoutWorkContractNumber_shouldReturn400Error() {
            final StockKeeperSaveDTO dto = new StockKeeperSaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
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
            final StockKeeperSaveDTO dto = new StockKeeperSaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    workContractNumber
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
    }

    @Nested
    @DisplayName("method: updateStockKeeperById(ServerRequest)")
    class UpdateStockKeeperByIdMethod {
        private final StockKeeper master = StockKeeperFactory
                .newStockKeeperEntity();

        @BeforeEach
        void setUp() {
            stDao.save(master).block();
        }

        final Faker faker = Faker.instance();

        @Test
        @DisplayName(
                "when called with invalid first name, " + 
                "then it should return 400 error")
        void whenCalledWithInvalidFirstName_shouldReturn400Error() {
            final StockKeeperUpdateDTO dto = new StockKeeperUpdateDTO(
                    faker.lorem().fixedString(151),
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + k1.getId())
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
                    .value(is(" first name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 150 characters"));
        }

        @Test
        @DisplayName(
                "when called with valid first name, " +
                "then it should return 204 and update it")
        void whenCalledWithValidFirstName_shouldReturn204AndUpdateIt() {
            final String newFirstName = faker.lorem().sentence();
            final StockKeeperUpdateDTO dto = new StockKeeperUpdateDTO(
                    newFirstName,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + k1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            final StockKeeper expectedEq = k1
                    .toBuilder()
                    .firstName(newFirstName)
                    .build();
            StepVerifier
                    .create(stDao.findById(k1.getId()))
                    .expectSubscription()
                    .expectNext(expectedEq)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with invalid last name, " + 
                "then it should return 400 error")
        void whenCalledWithInvalidLastName_shouldReturn400Error() {
            final StockKeeperUpdateDTO dto = new StockKeeperUpdateDTO(
                    null,
                    faker.lorem().fixedString(151),
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + k1.getId())
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
                    .value(is("last name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 150 characters"));
        }

        @Test
        @DisplayName(
                "when called with valid last name, " +
                "then it should return 204 and update it")
        void whenCalledWithValidLastName_shouldReturn204AndUpdateIt() {
            final String newLastName = faker.lorem().sentence();
            final StockKeeperUpdateDTO dto = new StockKeeperUpdateDTO(
                    null,
                    newLastName,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + k1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            final StockKeeper expectedEq = k1
                    .toBuilder()
                    .lastName(newLastName)
                    .build();
            StepVerifier
                    .create(stDao.findById(k1.getId()))
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
            final StockKeeperUpdateDTO dto = new StockKeeperUpdateDTO(
                    null,
                    null,
                    workContractNumber
            );
            client
                    .patch()
                    .uri(URI + "/" + k1.getId())
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
            final StockKeeperUpdateDTO dto = new StockKeeperUpdateDTO(
                    null,
                    null,
                    newWorkContractNumber
            );
            client
                    .patch()
                    .uri(URI + "/" + k1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            final StockKeeper expectedM = k1
                    .toBuilder()
                    .workContractNumber(newWorkContractNumber)
                    .build();
            StepVerifier
                    .create(stDao.findById(k1.getId()))
                    .expectSubscription()
                    .expectNext(expectedM)
                    .verifyComplete();
        }
        
        @Test
        @DisplayName(
                "when called with unknown stockKeeperId," +
                " then it should return 404 error")
        void whenCalledWithUnknownStockKeeperId_shouldReturn404() {
            final StockKeeperUpdateDTO dto = StockKeeperFactory
                    .newUpdateStockKeeper();
            final String unknownId = UUID.randomUUID().toString();
            client
                    .patch()
                    .uri(URI + "/{stockKeeperId}",
                            unknownId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNotFound()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is(format(
                            "StockKeeper with ID %s was not found",
                            unknownId)))
                    .jsonPath("$.details")
                    .isEmpty();
        }

        @Test
        @DisplayName(
                "when called with multiple invalid fields," +
                " then it should return 400")
        void whenCalledWithMultipleInvalidFields_shouldReturn400() {
            final StockKeeperUpdateDTO dto = new StockKeeperUpdateDTO(
                    faker.lorem().fixedString(1501),
                    faker.lorem().fixedString(1054),
                    -7L
            );
            client
                    .patch()
                    .uri(URI + "/" + k1.getId())
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
                    .value(is("last name"))
                    .jsonPath("$.details[1].message")
                    .value(is("the field must not exceed 1000 characters"))
                    .jsonPath("$.details[2].field")
                    .value(is("work contract number"))
                    .jsonPath("$.details[2].message")
                    .value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName(
                "when called with multiple valid fields, " +
                "then it should return 204 and update all of them")
        void whenCalledWithMultipleValidFields_shouldReturn204AndUpdateAllOfThem() {
            final String newLastName = faker.lorem().sentence();
            final long newWorkNumberContract = faker.random().nextLong(555_999L);
            final StockKeeperUpdateDTO dto = new StockKeeperUpdateDTO(
                    null,
                    null,
                    newWorkNumberContract
            );
            client
                    .patch()
                    .uri(URI + "/" + k1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            final StockKeeper expectedM = k1
                    .toBuilder()
                    .lastName(newLastName)
                    .workContractNumber(newWorkNumberContract)
                    .build();
            StepVerifier
                    .create(stDao.findById(k1.getId()))
                    .expectSubscription()
                    .expectNext(expectedM)
                    .verifyComplete();
        }

        @Test
        @DisplayName("when some unexpected error is throw," +
                " then it should return 500")
        void whenSomeUnexpectedErrorIsThrown_shouldReturn500() {
            doThrow(RuntimeException.class)
                    .when(stDao)
                    .findById(k1.getId());
            final StockKeeperUpdateDTO dto = new StockKeeperUpdateDTO(
                    null,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + k1.getId())
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
    @DisplayName("method: deleteStockKeeperById(ServerRequest)")
    class DeleteStockKeeperByIdMethod {
        private final StockKeeper master = StockKeeperFactory
                .newStockKeeperEntity();

        @BeforeEach
        void setUp() {
            stDao.save(master).block();
        }

        @Test
        @DisplayName(
                "when called with unknown stockKeeperId, " +
                "then it should return 404 error")
        void whenCalledWithUnknownStockKeeperId_shouldReturn404() {
            final String unknownId = UUID.randomUUID().toString();
            client
                    .delete()
                    .uri(URI + "/{stockKeeperId}",
                            unknownId)
                    .exchange()
                    .expectStatus()
                    .isNotFound()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is(format(
                            "StockKeeper with ID %s was not found",
                            unknownId)))
                    .jsonPath("$.details")
                    .isEmpty();
            assertThat(stDao.findById(master.getId())
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
                    .uri(URI + "/{stockKeeperId}",
                            master.getId())
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            assertThat(stDao.findById(master.getId()).blockOptional())
                    .isEmpty();
        }
    }
}
