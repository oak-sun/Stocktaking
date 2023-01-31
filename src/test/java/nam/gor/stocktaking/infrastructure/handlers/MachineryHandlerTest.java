package nam.gor.stocktaking.infrastructure.handlers;

import com.github.javafaker.Faker;
import nam.gor.stocktaking.infrastucture.dao.impl.MachineryDaoImpl;
import nam.gor.stocktaking.infrastucture.dao.impl.StockDaoAmazonS3Impl;
import nam.gor.stocktaking.infrastucture.dao.impl.TaskMasterDaoImpl;
import nam.gor.stocktaking.infrastucture.dao.intrfc.StockDao;
import nam.gor.stocktaking.api.dto.machines.MachinerySaveDTO;
import nam.gor.stocktaking.api.dto.machines.MachineryUpdateDTO;
import nam.gor.stocktaking.domain.entities.Machinery;
import nam.gor.stocktaking.domain.entities.Query;
import nam.gor.stocktaking.domain.entities.Stock;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.domain.factories.MachineryFactory;
import nam.gor.stocktaking.domain.factories.TaskMasterFactory;
import nam.gor.stocktaking.infrastucture.serializers.impl.MachinerySerialImpl;
import nam.gor.stocktaking.infrastructure.DBTestAutoConfig;
import nam.gor.stocktaking.utils.DBRollbackExtension;
import nam.gor.stocktaking.utils.init.DBContainerInit;
import nam.gor.stocktaking.utils.init.LocalStackContainerInit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web
        .reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.util.UUID;
import static java.lang.String.format;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;

@ContextConfiguration(initializers = {
        DBContainerInit.class,
        LocalStackContainerInit.class
})
@SpringBootTest(classes = {
        MachineryDaoImpl.class,
        TaskMasterDaoImpl.class,
        DBTestAutoConfig.class,
        StockDaoAmazonS3Impl.class,
        MachinerySerialImpl.class
})
@AutoConfigureWebTestClient
@ComponentScan("nam.gor.stocktaking")
@ExtendWith(DBRollbackExtension.class)
class MachineryHandlerTest {
    @Autowired
    private WebTestClient client;

    @SpyBean
    private MachineryDaoImpl mDao;

    @Autowired
    private TaskMasterDaoImpl tmDao;

    @Autowired
    private StockDao stDao;

    private final Machinery m1 = MachineryFactory
            .newMachineryEntity()
            .toBuilder()
            .name("name1")
            .quantity(5)
            .build();
    private final Machinery m2 = MachineryFactory
            .newMachineryEntity()
            .toBuilder()
            .name("name2")
            .quantity(10)
            .build();
    private final Machinery m3 = MachineryFactory
            .newMachineryEntity()
            .toBuilder()
            .name("name3")
            .quantity(15)
            .build();

    private Stock st1;
    private Stock st2;
    private Stock st3;

    private static final String URI = "/api/v1/machines";

    @BeforeEach
    void setUp() {
        mDao.save(m1).block();
        mDao.save(m2).block();
        mDao.save(m3).block();
        st1 = stDao
                .generatePreSignedUrlForVisualization(
                        m1.getStockIdentifier())
                .blockOptional()
                .orElseThrow();
        st2 = stDao
                .generatePreSignedUrlForVisualization(
                        m2.getStockIdentifier())
                .blockOptional()
                .orElseThrow();
        st3 = stDao
                .generatePreSignedUrlForVisualization(
                        m3.getStockIdentifier())
                .blockOptional()
                .orElseThrow();
    }

    @Nested
    @DisplayName("method: findAllMachines(MachineryFindDTO)")
    class FindAllMachinesMethod {

        @Test
        @DisplayName(
                "when called without query params, " +
                        "then it should return all " +
                        "the persisted machines")
        void whenCalledWithoutQueryParams_shouldReturnAllThePersistedMachines() {
            client.get()
                    .uri(URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$[0].id").value(is(m1.getId()))
                    .jsonPath("$[0].sku").value(is(m1.getSku()))
                    .jsonPath("$[0].name").value(is(m1.getName()))
                    .jsonPath("$[0].description").value(is(m1.getDescription()))
                    .jsonPath("$[0].price").value(is(m1.getPrice().intValue()))
                    .jsonPath("$[0].quantity").value(is(m1.getQuantity()))
                    .jsonPath("$[0].imageUrl").value(is(st1.getPreSignedUrl()))
                    .jsonPath("$[0].taskMaster.id")
                    .value(is(m1.getTaskMaster().getId()))
                    .jsonPath("$[0].taskMaster.firstName")
                    .value(is(m1.getTaskMaster().getFirstName()))
                    .jsonPath("$.[0].taskMaster.lastName")
                    .value(is(m1.getTaskMaster().getLastName()))
                    .jsonPath("$.[0].taskMaster.workContractNumber")
                    .value(is(m1.getTaskMaster().getWorkContractNumber()))
                    .jsonPath("$.[0].taskMaster.objectName")
                    .value(is(m1.getTaskMaster().getObjectName()))
                    .jsonPath("$.[0].taskMaster.teamNumber")
                    .value(is(m1.getTaskMaster().getTeamNumber()))

                    .jsonPath("$[1].id").value(is(m2.getId()))
                    .jsonPath("$[1].sku").value(is(m2.getSku()))
                    .jsonPath("$[1].name").value(is(m2.getName()))
                    .jsonPath("$[1].description").value(is(m2.getDescription()))
                    .jsonPath("$[1].price").value(is(m2.getPrice().intValue()))
                    .jsonPath("$[1].quantity").value(is(m2.getQuantity()))
                    .jsonPath("$[1].imageUrl").value(is(st2.getPreSignedUrl()))
                    .jsonPath("$[1].taskMaster.id")
                    .value(is(m2.getTaskMaster().getId()))
                    .jsonPath("$[1].taskMaster.firstName")
                    .value(is(m2.getTaskMaster().getFirstName()))
                    .jsonPath("$.[1].taskMaster.lastName")
                    .value(is(m2.getTaskMaster().getLastName()))
                    .jsonPath("$.[1].taskMaster.workContractNumber")
                    .value(is(m2.getTaskMaster().getWorkContractNumber()))
                    .jsonPath("$.[1].taskMaster.objectName")
                    .value(is(m2.getTaskMaster().getObjectName()))
                    .jsonPath("$.[1].taskMaster.teamNumber")
                    .value(is(m2.getTaskMaster().getTeamNumber()))

                    .jsonPath("$[2].id").value(is(m3.getId()))
                    .jsonPath("$[2].sku").value(is(m3.getSku()))
                    .jsonPath("$[2].name").value(is(m3.getName()))
                    .jsonPath("$[2].description").value(is(m3.getDescription()))
                    .jsonPath("$[2].price").value(is(m3.getPrice().intValue()))
                    .jsonPath("$[2].quantity").value(is(m3.getQuantity()))
                    .jsonPath("$[2].imageUrl").value(is(st3.getPreSignedUrl()))
                    .jsonPath("$[2].taskMaster.id")
                    .value(is(m3.getTaskMaster().getId()))
                    .jsonPath("$[2].taskMaster.firstName")
                    .value(is(m3.getTaskMaster().getFirstName()))
                    .jsonPath("$.[2].taskMaster.lastName")
                    .value(is(m3.getTaskMaster().getLastName()))
                    .jsonPath("$.[2].taskMaster.workContractNumber")
                    .value(is(m3.getTaskMaster().getWorkContractNumber()))
                    .jsonPath("$.[2].taskMaster.objectName")
                    .value(is(m3.getTaskMaster().getObjectName()))
                    .jsonPath("$.[2].taskMaster.teamNumber")
                    .value(is(m3.getTaskMaster().getTeamNumber()));
        }

        @Test
        @DisplayName(
                "when called with name query param, " +
                        "then it should return only " +
                        "machines matching it")
        void whenCalledWithNameQueryParam_shouldReturnOnlyMachinesMatchingIt() {
            final String uri = UriComponentsBuilder
                    .fromUriString(URI)
                    .queryParam("name", m1.getName())
                    .toUriString();
            client.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$[0].id").value(is(m1.getId()))
                    .jsonPath("$[0].sku").value(is(m1.getSku()))
                    .jsonPath("$[0].name").value(is(m1.getName()))
                    .jsonPath("$[0].description").value(is(m1.getDescription()))
                    .jsonPath("$[0].price").value(is(m1.getPrice().intValue()))
                    .jsonPath("$[0].quantity").value(is(m1.getQuantity()))
                    .jsonPath("$[0].imageUrl").value(is(st1.getPreSignedUrl()))
                    .jsonPath("$[0].taskMaster.id")
                    .value(is(m1.getTaskMaster().getId()))
                    .jsonPath("$[0].taskMaster.firstName")
                    .value(is(m1.getTaskMaster().getFirstName()))
                    .jsonPath("$.[0].taskMaster.lastName")
                    .value(is(m1.getTaskMaster().getLastName()))
                    .jsonPath("$.[0].taskMaster.workContractNumber")
                    .value(is(m1.getTaskMaster().getWorkContractNumber()))
                    .jsonPath("$.[0].taskMaster.objectName")
                    .value(is(m1.getTaskMaster().getObjectName()))
                    .jsonPath("$.[0].taskMaster.teamNumber")
                    .value(is(m1.getTaskMaster().getTeamNumber()));
        }

        @Test
        @DisplayName(
                "when called with minQuantity query param, " +
                        "then it should return only " +
                        "machines matching it")
        void whenCalledWithMinQuantityQueryParam_shouldReturnOnlyMachinesMatchingIt() {
            final String uri = UriComponentsBuilder
                    .fromUriString(URI)
                    .queryParam("minQuantity", m2.getQuantity())
                    .toUriString();
            client.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$[0].id").value(is(m2.getId()))
                    .jsonPath("$[0].sku").value(is(m2.getSku()))
                    .jsonPath("$[0].name").value(is(m2.getName()))
                    .jsonPath("$[0].description").value(is(m2.getDescription()))
                    .jsonPath("$[0].price").value(is(m2.getPrice().intValue()))
                    .jsonPath("$[0].quantity").value(is(m2.getQuantity()))
                    .jsonPath("$[0].imageUrl").value(is(st2.getPreSignedUrl()))
                    .jsonPath("$[0].taskMaster.id")
                    .value(is(m2.getTaskMaster().getId()))
                    .jsonPath("$[0].taskMaster.firstName")
                    .value(is(m2.getTaskMaster().getFirstName()))
                    .jsonPath("$.[0].taskMaster.lastName")
                    .value(is(m2.getTaskMaster().getLastName()))
                    .jsonPath("$.[0].taskMaster.workContractNumber")
                    .value(is(m2.getTaskMaster().getWorkContractNumber()))
                    .jsonPath("$.[0].taskMaster.objectName")
                    .value(is(m2.getTaskMaster().getObjectName()))
                    .jsonPath("$.[0].taskMaster.teamNumber")
                    .value(is(m2.getTaskMaster().getTeamNumber()))

                    .jsonPath("$[1].id").value(is(m3.getId()))
                    .jsonPath("$[1].name").value(is(m3.getName()))
                    .jsonPath("$[1].description").value(is(m3.getDescription()))
                    .jsonPath("$[1].price").value(is(m3.getPrice().intValue()))
                    .jsonPath("$[1].quantity").value(is(m3.getQuantity()))
                    .jsonPath("$[1].imageUrl").value(is(st3.getPreSignedUrl()))
                    .jsonPath("$[1].taskMaster.id")
                    .value(is(m3.getTaskMaster().getId()))
                    .jsonPath("$[1].taskMaster.firstName")
                    .value(is(m3.getTaskMaster().getFirstName()))
                    .jsonPath("$.[1].taskMaster.lastName")
                    .value(is(m3.getTaskMaster().getLastName()))
                    .jsonPath("$.[1].taskMaster.workContractNumber")
                    .value(is(m3.getTaskMaster().getWorkContractNumber()))
                    .jsonPath("$.[1].taskMaster.objectName")
                    .value(is(m3.getTaskMaster().getObjectName()))
                    .jsonPath("$.[1].taskMaster.teamNumber")
                    .value(is(m3.getTaskMaster().getTeamNumber()));
        }

        @Test
        @DisplayName(
                "when called with taskMasterId query param, " +
                        "then it should return only " +
                        "machines matching it")
        void whenCalledWithTaskMasterIdQueryParam_shouldReturnOnlyMachinesMatchingIt() {
            final String uri = UriComponentsBuilder
                    .fromUriString(URI)
                    .queryParam("taskMasterId", m2.getTaskMaster().getId())
                    .toUriString();
            client
                    .get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$[0].id").value(is(m2.getId()))
                    .jsonPath("$[0].sku").value(is(m2.getSku()))
                    .jsonPath("$[0].name").value(is(m2.getName()))
                    .jsonPath("$[0].description").value(is(m2.getDescription()))
                    .jsonPath("$[0].price").value(is(m2.getPrice().intValue()))
                    .jsonPath("$[0].quantity").value(is(m2.getQuantity()))
                    .jsonPath("$[0].imageUrl").value(is(st2.getPreSignedUrl()))

                    .jsonPath("$[0].taskMaster.id").value(is(m2.getTaskMaster().getId()))
                    .jsonPath("$[0].taskMaster.firstName")
                    .value(is(m2.getTaskMaster().getFirstName()))
                    .jsonPath("$.[0].taskMaster.lastName")
                    .value(is(m2.getTaskMaster().getLastName()))
                    .jsonPath("$.[0].taskMaster.workContractNumber")
                    .value(is(m2.getTaskMaster().getWorkContractNumber()))
                    .jsonPath("$.[0].taskMaster.objectName")
                    .value(is(m2.getTaskMaster().getObjectName()))
                    .jsonPath("$.[0].taskMaster.teamNumber")
                    .value(is(m2.getTaskMaster().getTeamNumber()));
        }
    }

    @Nested
    @DisplayName("method: findMachineryById(String)")
    class FindMachineryByIdMethod {

        @Test
        @DisplayName(
                "when called with existing id, " +
                        "then it should return the matching machinery")
        void whenCalledWithExistingId_shouldReturnTheMatchingMachinery() {
            client
                    .get()
                    .uri(URI + "/" + m1.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").value(is(m1.getId()))
                    .jsonPath("$.sku").value(is(m1.getSku()))
                    .jsonPath("$.name").value(is(m1.getName()))
                    .jsonPath("$.description")
                    .value(is(m1.getDescription()))
                    .jsonPath("$.price")
                    .value(is(m1.getPrice().intValue()))
                    .jsonPath("$.quantity").value(is(m1.getQuantity()))
                    .jsonPath("$.imageUrl")
                    .value(is(st1.getPreSignedUrl()))
                    .jsonPath("$.taskMaster.id")
                    .value(is(m1.getTaskMaster().getId()))
                    .jsonPath("$.taskMaster.firstName")
                    .value(is(m1.getTaskMaster().getFirstName()))
                    .jsonPath("$.taskMaster.lastName")
                    .value(is(m1.getTaskMaster().getLastName()))
                    .jsonPath("$.taskMaster.workContractNumber")
                    .value(is(m1.getTaskMaster().getWorkContractNumber()))
                    .jsonPath("$.taskMaster.objectName")
                    .value(is(m1.getTaskMaster().getObjectName()))
                    .jsonPath("$.taskMaster.teamNumber")
                    .value(is(m1.getTaskMaster().getTeamNumber()));
        }

        @Test
        @DisplayName(
                "when called with unknown id, " +
                "then it should return 404 error")
        void whenCalledWithUnknownId_shouldReturn404Error() {
            client
                    .get()
                    .uri(URI + "/machineryId")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("Machinery with ID machineryId was not found"))
                    .jsonPath("$.details")
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("method: saveMachinery(MachinerySaveDTO)")
    class SaveMachineryMethod {
        private final Faker faker = Faker.instance();

        @Test
        @DisplayName("when called without name, " +
                "then it should return 400 error")
        void whenCalledWithoutName_shouldReturn400Error() {
            final MachinerySaveDTO dto = new MachinerySaveDTO(
                    null,
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal
                            .valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid(),
                    faker.internet().uuid()
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
                    .jsonPath("$.details[0].field").value(is("name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field is mandatory"));
        }

        @Test
        @DisplayName(
                "when called with invalid name, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidName_shouldReturn400Error() {
            final MachinerySaveDTO dto = new MachinerySaveDTO(
                    faker.lorem().fixedString(155),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid(),
                    faker.internet().uuid()
            );

            client.post()
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
                    .jsonPath("$.details[0].field").value(is("name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 150 characters"));
        }

        @Test
        @DisplayName("when called without sku, " +
                        "then it should return 400 error")
        void whenCalledWithoutSku_shouldReturn400Error() {
            final MachinerySaveDTO dto = new MachinerySaveDTO(
                    faker.lorem().characters(),
                    null,
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid(),
                    faker.internet().uuid()
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
                    .jsonPath("$.details[0].field").value(is("sku"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field is mandatory"));
        }

        @Test
        @DisplayName("when called with invalid sku, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidSku_shouldReturn400Error() {
            final MachinerySaveDTO dto = new MachinerySaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(21),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid(),
                    faker.internet().uuid()
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
                    .jsonPath("$.details[0].field").value(is("sku"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 20 characters"));
        }

        @Test
        @DisplayName(
                "when called without description, " +
                        "then it should return 400 error")
        void whenCalledWithoutDescription_shouldReturn400Error() {
            final MachinerySaveDTO dto = new MachinerySaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    null,
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid(),
                    faker.internet().uuid()
            );

            client.post()
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
                    .jsonPath("$.details[0].field").value(is("description"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field is mandatory"));
        }

        @Test
        @DisplayName(
                "when called with invalid description, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidDescription_shouldReturn400Error() {
            final MachinerySaveDTO dto = new MachinerySaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    faker.lorem().fixedString(1005),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid(),
                    faker.internet().uuid()
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
                    .value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("description"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 1000 characters"));
        }

        @Test
        @DisplayName("when called without price, " +
                        "then it should return 400 error")
        void whenCalledWithoutPrice_shouldReturn400Error() {
            final MachinerySaveDTO dto = new MachinerySaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    null,
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid(),
                    faker.internet().uuid()
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
                    .jsonPath("$.details[0].field").value(is("price"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field is mandatory"));
        }

        @ParameterizedTest
        @ValueSource(doubles = {-29, -1, 0.0})
        @DisplayName("when called with invalid price, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidPrice_shouldReturn400Error(double price) {
            final MachinerySaveDTO dto = new MachinerySaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(price),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid(),
                    faker.internet().uuid()
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
                    .value(is("price"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName(
                "when called without quantity, " +
                        "then it should return 400 error")
        void whenCalledWithoutQuantity_shouldReturn400Error() {
            final MachinerySaveDTO dto = new MachinerySaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    null,
                    faker.internet().uuid(),
                    faker.internet().uuid()
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
                    .value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("quantity"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field is mandatory"));
        }

        @Test
        @DisplayName(
                "when called with invalid quantity, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidQuantity_shouldReturn400Error() {
            final MachinerySaveDTO dto = new MachinerySaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    -1,
                    faker.internet().uuid(),
                    faker.internet().uuid()
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
                    .jsonPath("$.details[0].field").value(is("quantity"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName(
                "when called without stockIdentifier, " +
                        "then it should return 400 error")
        void whenCalledWithoutStockIdentifier_shouldReturn400Error() {
            final MachinerySaveDTO dto = new MachinerySaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    null,
                    faker.internet().uuid()
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
                    .value(is("stockIdentifier"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field is mandatory"));
        }

        @Test
        @DisplayName(
                "when called with invalid stockIdentifier, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidStockIdentifier_shouldReturn400Error() {
            final MachinerySaveDTO dto = new MachinerySaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid().repeat(2),
                    faker.internet().uuid()
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
                    .value(is("stockIdentifier"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 36 characters"));
        }

        @Test
        @DisplayName(
                "when called without taskMasterId, " +
                        "then it should return 400 error")
        void whenCalledWithoutTaskMasterId_shouldReturn400Error() {
            final MachinerySaveDTO dto = new MachinerySaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid(),
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
                    .jsonPath("$.details[0].field").value(is("taskMasterId"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field is mandatory"));
        }

        @Test
        @DisplayName(
                "when called with invalid taskMasterId, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidTaskMasterId_shouldReturn400Error() {
            final MachinerySaveDTO dto = new MachinerySaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid(),
                    faker.internet().uuid().repeat(2)
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
                    .value(is("The given payload is invalid." +
                            " Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("taskMasterId"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 36 characters"));
        }

        @Test
        @DisplayName(
                "when called with unknown taskMasterId, " +
                        "then it should return 400 error")
        void whenCalledWithUnknownTaskMasterId_shouldReturn400Error() {
            final MachinerySaveDTO dto = new MachinerySaveDTO(
                    faker.lorem().characters(),
                    faker.lorem().fixedString(8),
                    faker.lorem().sentence(),
                    BigDecimal.valueOf(faker.random().nextInt(1, 100)),
                    faker.random().nextInt(4, 20),
                    faker.internet().uuid(),
                    faker.internet().uuid()
            );
            client
                    .post()
                    .uri(URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is(format("TaskMaster with ID %s was not found",
                            dto.getTaskmasterId())))
                    .jsonPath("$.details").isEmpty();
        }

        @Test
        @DisplayName(
                "when called with valid payload, " +
                        "then it should return 201" +
                        " and persist the machinery")
        void whenCalledWithValidPayload_shouldReturn201AndPersistTheMachinery() {
            final MachinerySaveDTO dto = MachineryFactory.newSaveMachineryDto();
            final TaskMaster master = TaskMasterFactory
                    .newTaskMasterEntity()
                    .toBuilder()
                    .id(dto.getTaskmasterId())
                    .build();
            tmDao.save(master).block();
            final Stock stock = stDao
                    .generatePreSignedUrlForVisualization(
                            dto.getStockIdentifier())
                    .blockOptional()
                    .orElseThrow();
            client.post()
                    .uri(URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.name").value(is(dto.getName()))
                    .jsonPath("$.sku").value(is(dto.getSku()))
                    .jsonPath("$.description").value(is(dto.getDescription()))
                    .jsonPath("$.price").value(is(dto.getPrice().intValue()))
                    .jsonPath("$.quantity").value(is(dto.getQuantity()))
                    .jsonPath("$.imageUrl").value(is(stock.getPreSignedUrl()))
                    .jsonPath("$.taskMaster.id").value(is(master.getId()))
                    .jsonPath("$.taskMaster.firstName")
                    .value(is(master.getFirstName()))
                    .jsonPath("$.taskMaster.lastName")
                    .value(is(master.getLastName()))
                    .jsonPath("$.taskMaster.workContractNumber")
                    .value(is(master.getWorkContractNumber()))
                    .jsonPath("$.taskMaster.objectName")
                    .value(is(master.getObjectName()))
                    .jsonPath("$.taskMaster.teamNumber")
                    .value(is(master.getTeamNumber()));
            StepVerifier
                    .create(mDao.findByQuery(Query.builder().build()))
                    .expectSubscription()
                    .expectNextCount(4)
                    .verifyComplete();

        }
    }

    @Nested
    @DisplayName("method: updateMachineryById(String, MachineryUpdateDTO)")
    class UpdateMachineryByIdMethod {
        final Faker faker = Faker.instance();

        @Test
        @DisplayName(
                "when called with invalid name, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidName_shouldReturn400Error() {
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    faker.lorem().fixedString(151),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("name"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 150 characters"));
        }

        @Test
        @DisplayName(
                "when called with valid name, " +
                        "then it should return 204 and update it")
        void whenCalledWithValidName_shouldReturn204AndUpdateIt() {
            final String newName = faker.lorem().sentence();
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    newName,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            final Machinery expectedEq = m1.toBuilder().name(newName).build();
            StepVerifier
                    .create(mDao.findById(m1.getId()))
                    .expectSubscription()
                    .expectNext(expectedEq)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with invalid sku, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidSku_shouldReturn400Error() {
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    null,
                    faker.lorem().fixedString(21),
                    null,
                    null,
                    null,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("sku"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 20 characters"));
        }

        @Test
        @DisplayName(
                "when called with valid sku, " +
                        "then it should return 204 and update it")
        void whenCalledWithValidSku_shouldReturn204AndUpdateIt() {
            final String newSku = faker.lorem().fixedString(8);
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    null,
                    newSku,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody()
                    .isEmpty();
            final Machinery expectedEq = m1.toBuilder().sku(newSku).build();
            StepVerifier
                    .create(mDao.findById(m1.getId()))
                    .expectSubscription()
                    .expectNext(expectedEq)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with invalid description, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidDescription_shouldReturn400Error() {
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    null,
                    null,
                    faker.lorem().fixedString(1001),
                    null,
                    null,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("description"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 1000 characters"));
        }

        @Test
        @DisplayName(
                "when called with valid description, " +
                        "then it should return 204 and update it")
        void whenCalledWithValidDescription_shouldReturn204AndUpdateIt() {
            final String newDescription = faker.lorem().sentence();
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    null,
                    null,
                    newDescription,
                    null,
                    null,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();
            final Machinery expectedEq = m1
                    .toBuilder()
                    .description(newDescription)
                    .build();
            StepVerifier
                    .create(mDao.findById(m1.getId()))
                    .expectSubscription()
                    .expectNext(expectedEq)
                    .verifyComplete();
        }

        @ParameterizedTest
        @ValueSource(doubles = {-23.8, -1.0, 0.0})
        @DisplayName(
                "when called with invalid price, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidPrice_shouldReturn400Error(double price) {
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    null,
                    null,
                    null,
                    BigDecimal.valueOf(price),
                    null,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("price"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName(
                "when called with valid price, " +
                        "then it should return 204 and update it")
        void whenCalledWithValidPrice_shouldReturn204AndUpdateIt() {
            final BigDecimal newPrice = BigDecimal.valueOf(faker.random().nextInt(1, 100));
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    null,
                    null,
                    null,
                    newPrice,
                    null,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();
            final Machinery expectedEq = m1.toBuilder().price(newPrice).build();
            StepVerifier
                    .create(mDao.findById(m1.getId()))
                    .expectSubscription()
                    .expectNext(expectedEq)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with invalid quantity, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidQuantity_shouldReturn400Error() {
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    -1,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("quantity"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName(
                "when called with valid quantity, " +
                        "then it should return 204 and update it")
        void whenCalledWithValidQuantity_shouldReturn204AndUpdateIt() {
            final int newQuantity = faker.random().nextInt(1, 100);
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    newQuantity,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();
            final Machinery expectedEq = m1
                    .toBuilder()
                    .quantity(newQuantity)
                    .build();
            StepVerifier
                    .create(mDao.findById(m1.getId()))
                    .expectSubscription()
                    .expectNext(expectedEq)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with invalid stockIdentifier, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidStockIdentifier_shouldReturn400Error() {
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    null,
                    faker.internet().uuid().repeat(2),
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field")
                    .value(is("stockIdentifier"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 36 characters"));
        }

        @Test
        @DisplayName(
                "when called with valid stockIdentifier, " +
                        "then it should return 204 and update it")
        void whenCalledWithValidStockIdentifier_shouldReturn204AndUpdateIt() {
            final String newStockIdentifier = faker.internet().uuid();
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    null,
                    newStockIdentifier,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            final Machinery expectedEq = m1
                    .toBuilder()
                    .stockIdentifier(newStockIdentifier)
                    .build();
            StepVerifier
                    .create(mDao.findById(m1.getId()))
                    .expectSubscription()
                    .expectNext(expectedEq)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with invalid taskMasterId, " +
                        "then it should return 400 error")
        void whenCalledWithInvalidTaskMasterId_shouldReturn400Error() {
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    faker.internet().uuid().repeat(2)
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("taskMasterId"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 36 characters"));
        }

        @Test
        @DisplayName(
                "when called with unknown taskMasterId, " +
                        "then it should return 404 error")
        void whenCalledWithUnknownTaskMasterId_shouldReturn404Error() {
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    faker.internet().uuid()
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is(format("TaskMaster with ID %s was not found",
                            dto.getTaskmasterId().orElse(null))))
                    .jsonPath("$.details").isEmpty();
        }

        @Test
        @DisplayName(
                "when called with valid taskMasterId, " +
                        "then it should return 204 and" +
                        " update the taskMaster")
        void whenCalledWithValidTaskMasterId_shouldReturn204AndUpdateTheTaskMaster() {
            final TaskMaster master = TaskMasterFactory.newTaskMasterEntity();
            tmDao.save(master).block();
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    master.getId()
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            final Machinery expectedEq = m1.toBuilder().taskMaster(master).build();
            StepVerifier
                    .create(mDao.findById(m1.getId()))
                    .expectSubscription()
                    .expectNext(expectedEq)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when called with multiple invalid fields," +
                        " then it should return 400")
        void whenCalledWithMultipleInvalidFields_shouldReturn400() {
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    faker.lorem().fixedString(151),
                    null,
                    faker.lorem().fixedString(1051),
                    null,
                    -1,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("The given payload is invalid. " +
                            "Check the 'details' field."))
                    .jsonPath("$.details[0].field").value(is("description"))
                    .jsonPath("$.details[0].message")
                    .value(is("the field must not exceed 1000 characters"))
                    .jsonPath("$.details[1].field").value(is("name"))
                    .jsonPath("$.details[1].message")
                    .value(is("the field must not exceed 150 characters"))
                    .jsonPath("$.details[2].field").value(is("quantity"))
                    .jsonPath("$.details[2].message")
                    .value(is("the field must contain a positive value"));
        }

        @Test
        @DisplayName(
                "when called with multiple valid fields, " +
                        "then it should return 204 and update all of them")
        void whenCalledWithMultipleValidFields_shouldReturn204AndUpdateAllOfThem() {
            final String newName = faker.lorem().sentence();
            final int newQuantity = faker.random().nextInt(1, 100);
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    newName,
                    null,
                    null,
                    null,
                    newQuantity,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            final Machinery expectedEq = m1
                    .toBuilder()
                    .name(newName)
                    .quantity(newQuantity).build();
            StepVerifier
                    .create(mDao.findById(m1.getId()))
                    .expectSubscription()
                    .expectNext(expectedEq)
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when some unexpected error is throw," +
                        " then it should return 500")
        void whenSomeUnexpectedErrorIsThrown_shouldReturn500() {
            doThrow(RuntimeException.class)
                    .when(mDao)
                    .findById(m1.getId());
            final MachineryUpdateDTO dto = new MachineryUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    90,
                    null,
                    null
            );
            client
                    .patch()
                    .uri(URI + "/" + m1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().is5xxServerError()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is("There's been an unexpected error. " +
                            "Please, contact support."))
                    .jsonPath("$.details").isEmpty();
        }
    }

    @Nested
    @DisplayName("method: deleteMachineryById(String)")
    class DeleteMachineryByIdMethod {

        @Test
        @DisplayName(
                "when called with unknown id, " +
                        "then it should return 404 error")
        void whenCalledWithUnknownId_shouldReturn404Error() {
            final String unknownId = UUID.randomUUID().toString();
            client
                    .delete()
                    .uri(URI + "/" + unknownId)
                    .exchange()
                    .expectStatus()
                    .isNotFound()
                    .expectBody()
                    .jsonPath("$.message")
                    .value(is(format("Machinery with ID %s was not found",
                            unknownId)))
                    .jsonPath("$.details")
                    .isEmpty();
        }

        @Test
        @DisplayName(
                "when called with existing id, " +
                        "then it should return 204 " +
                        "and delete the machinery in the db")
        void whenCalledWithExistingId_shouldReturn204AndDeleteTheMachineryInTheDB() {
            client
                    .delete()
                    .uri(URI + "/" + m1.getId())
                    .exchange()
                    .expectStatus()
                    .isNoContent()
                    .expectBody()
                    .isEmpty();
            StepVerifier
                    .create(mDao.findById(m1.getId()))
                    .expectSubscription()
                    .verifyComplete();
        }
    }
}
