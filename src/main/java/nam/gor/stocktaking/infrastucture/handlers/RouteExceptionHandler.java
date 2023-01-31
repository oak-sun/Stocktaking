package nam.gor.stocktaking.infrastucture.handlers;

import nam.gor.stocktaking.api.dto.ExceptionBodyDTO;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class RouteExceptionHandler extends AbstractErrorWebExceptionHandler {

    public RouteExceptionHandler(ErrorAttributes attributes,
                                 ApplicationContext context,
                                 ServerCodecConfigurer configurer) {
        super(
                attributes,
                new WebProperties.Resources(),
                context);
        super.setMessageWriters(configurer.getWriters());
        super.setMessageReaders(configurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes attributes) {
        return RouterFunctions
                .route(RequestPredicates.all(), this::renderErrorResponse
        );
    }

    private @NonNull Mono<ServerResponse> renderErrorResponse(ServerRequest req) {
        final var exception = ExceptionBodyDTO
                                             .fromException(getError(req));
        return ServerResponse
                .status(exception.getStatus())
                .bodyValue(exception);
    }
}