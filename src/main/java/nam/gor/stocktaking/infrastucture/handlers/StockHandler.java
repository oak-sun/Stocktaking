package nam.gor.stocktaking.infrastucture.handlers;

import nam.gor.stocktaking.api.services.StockService;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@AllArgsConstructor
@Component
public class StockHandler {
    private final StockService service;

    public @NonNull Mono<ServerResponse> generatePreSignedUrlForUpload(ServerRequest req) {
        return service
                .generatePreSignedUrlForUpload()
                .flatMap(stock-> ServerResponse
                        .created(URI.create(
                                    stock.getPreSignedUrl()))
                        .bodyValue(stock)
                );
    }
}
