package nam.gor.stocktaking.infrastructure.handlers;

import nam.gor.stocktaking.utils.init.LocalStackContainerInit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.hasSize;

@ContextConfiguration(initializers = LocalStackContainerInit.class)
@EnableAutoConfiguration
@SpringBootTest
@AutoConfigureWebTestClient
class StockHandlerTest {

    @Autowired
    private WebTestClient client;

    private static final String URI = "/api/v1/stock";

    @Nested
    @DisplayName("method: generatePreSignedUrlForUpload(ServerRequest)")
    class GeneratePreSignedUrlForUploadMethod {

        @Test
        @DisplayName(
                "when called, then it should " +
                "generate a preSignedUrl and return 201")
        void whenCalled_shouldGenerateAPreSignedUrlAndReturn201() {
            client
                    .post()
                    .uri(URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue("")
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.identifier", hasSize(36)).hasJsonPath()
                    .jsonPath("$.preSignedUrl").isNotEmpty();

        }
    }
}