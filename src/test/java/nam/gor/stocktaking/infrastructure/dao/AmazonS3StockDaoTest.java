package nam.gor.stocktaking.infrastructure.dao;

import nam.gor.stocktaking.infrastucture.dao.impl.StockDaoAmazonS3Impl;
import nam.gor.stocktaking.infrastucture.util.AmazonS3Config;
import nam.gor.stocktaking.domain.IdGenerator;
import nam.gor.stocktaking.utils.init.LocalStackContainerInit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = {
                AmazonS3Config.class,
                PropertyPlaceholderAutoConfiguration.class,
                IdGenerator.class,
                StockDaoAmazonS3Impl.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles("dev")
@ContextConfiguration(initializers = LocalStackContainerInit.class)
class AmazonS3StockDaoTest {

    @Autowired
    private StockDaoAmazonS3Impl dao;

    @SpyBean
    private IdGenerator idGen;

    @Nested
    @DisplayName("method: generatePreSignedUrlForUpload()")
    class GeneratePreSignedUrlForUploadMethod {

        @Test
        @DisplayName(
                "when called, then it should" +
                " generate identifier and url")
        void whenCalled_shouldGenerateIdentifierAndUrl() {
            final String identifier = UUID.randomUUID().toString();
            when(idGen.newId()).thenReturn(identifier);
            StepVerifier
                    .create(dao.generatePreSignedUrlForUpload())
                    .expectSubscription()
                    .assertNext(stock -> {
                        assertThat(stock.getIdentifier())
                                .isEqualTo(identifier);
                        assertThat(stock.getPreSignedUrl())
                                .contains(identifier);
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: generatePreSignedUrlForVisualization(String)")
    class GeneratePreSignedUrlForVisualizationMethod {

        @Test
        @DisplayName(
                "when called, then it should" +
                " generate a url for the given identifier")
        void whenCalled_shouldGenerateAUrlForTheGivenIdentifier() {
            final String identifier = UUID.randomUUID().toString();
            StepVerifier
                    .create(dao.generatePreSignedUrlForVisualization(identifier))
                    .expectSubscription()
                    .assertNext(stock -> {
                        assertThat(stock.getIdentifier())
                                .isEqualTo(identifier);
                        assertThat(stock.getPreSignedUrl())
                                .contains(identifier);
                    })
                    .verifyComplete();
            verifyNoInteractions(idGen);
        }
    }
}