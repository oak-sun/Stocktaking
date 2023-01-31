package nam.gor.stocktaking.api;

import nam.gor.stocktaking.domain.exceptions.RequestValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import reactor.core.publisher.Mono;

import javax.validation.Validator;

import static java.util.stream.Collectors.toUnmodifiableList;

@AllArgsConstructor
@Component
public class RequestValidator {
    private final Validator validator;

    public <T> Mono<T> validate(T req) {
        return Mono
                .create(sink -> {
            final var errors = validator
                                       .validate(req)
                                       .stream()
                                       .map(err -> new FieldError(
                                              req.getClass().getName(),
                                              err.getPropertyPath().toString(),
                                              err.getMessage()))
                                       .collect(toUnmodifiableList());
            if (!errors.isEmpty()) {
                sink.error(
                        new RequestValidationException(errors));
            }
            sink.success(req);
        });
    }
}
