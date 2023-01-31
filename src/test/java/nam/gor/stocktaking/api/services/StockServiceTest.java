package nam.gor.stocktaking.api.services;


import nam.gor.stocktaking.infrastucture.dao.impl.StockDaoAmazonS3Impl;
import nam.gor.stocktaking.api.dto.StockOutDTO;
import nam.gor.stocktaking.domain.entities.Stock;
import nam.gor.stocktaking.domain.factories.StockFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {
    @InjectMocks
    private StockService service;

    @Mock
    private StockDaoAmazonS3Impl dao;

    @Nested
    @DisplayName("method: generatePreSignedUrlForUpload()")
    class GeneratePreSignedUrlForUploadMethod {

        @Test
        @DisplayName(
                "when called, then it should " +
                "forward the call to the underlying client")
        void whenCalled_shouldForwardTheCallToTheUnderlyingClient() {
            final Stock stock = StockFactory.newStockEntity();
            when(dao.generatePreSignedUrlForUpload())
                    .thenReturn(Mono.just(stock));
            StepVerifier
                    .create(service.generatePreSignedUrlForUpload())
                    .expectSubscription()
                    .expectNext(StockOutDTO.toDto(stock))
                    .verifyComplete();
            verify(dao).generatePreSignedUrlForUpload();
            verifyNoMoreInteractions(dao);
        }
    }
}