package nam.gor.stocktaking.infrastucture.handlers;

import lombok.AllArgsConstructor;
import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperOutDTO;
import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperSaveDTO;
import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperUpdateDTO;
import nam.gor.stocktaking.api.services.StockKeeperService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class StockKeeperHandler {
    private final StockKeeperService service;

    public Mono<ServerResponse> findAllStockKeepers(ServerRequest req) {
        return ServerResponse
                .ok()
                .body(service.findAllStockKeepers(),
                        StockKeeperOutDTO.class);
    }

    public Mono<ServerResponse> saveStockKeeper(ServerRequest req) {
        final Function<StockKeeperOutDTO, Mono<ServerResponse>>
                response = keeper ->
                ServerResponse
                        .created(URI.create(req.path() +
                                "/" + keeper.getId()))
                        .bodyValue(keeper);
        return req
                .bodyToMono(StockKeeperSaveDTO.class)
                .flatMap(service::saveStockKeeper)
                .flatMap(response);
    }

    public Mono<ServerResponse> updateStockKeeperById(ServerRequest req) {
        return req
                .bodyToMono(StockKeeperUpdateDTO.class)
                .flatMap(keeper -> service.updateStockKeeperById(
                        req.pathVariable("stockKeeperId"),
                        keeper))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> deleteStockKeeperById(ServerRequest req) {
        return service
                .deleteStockKeeperById(req.pathVariable("stockKeeperId"))
                .then(ServerResponse.noContent().build());
    }
}
