package nam.gor.stocktaking.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nam.gor.stocktaking.domain.exceptions.RequestValidationException;
import nam.gor.stocktaking.domain.exceptions.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static nam.gor.stocktaking.infrastucture.util.StringPatterns.ERROR_BODY_MESSAGE;
import static nam.gor.stocktaking.infrastucture.util.StringPatterns.ERROR_UNKNOWN_ENTITY;
import static nam.gor.stocktaking.infrastucture.util.StringPatterns.ERROR_VALIDATION;
import static nam.gor.stocktaking.infrastucture.util.StringPatterns.INVALID_PAYLOAD;
import static nam.gor.stocktaking.infrastucture.util.StringPatterns.UNEXPECTED_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionBodyDTO {

    String message;
    Iterable<ErrorDetail> details;

    @JsonIgnore
    HttpStatus status;

    public static ExceptionBodyDTO fromException(Throwable exception) {
        if (exception instanceof RequestValidationException) {
            log.warn(ERROR_VALIDATION, exception);
            return fromException((RequestValidationException) exception);
        }
        if (exception instanceof EntityNotFoundException) {
            log.warn(ERROR_UNKNOWN_ENTITY, exception);
            return fromMessage(exception.getMessage(), NOT_FOUND);
        }
        log.error(UNEXPECTED_ERROR + exception.getMessage(), exception);
        return fromMessage(ERROR_BODY_MESSAGE, INTERNAL_SERVER_ERROR);
    }

    private static ExceptionBodyDTO fromException(RequestValidationException exc) {
        final List<ErrorDetail> details = exc
                                               .getErrors()
                                               .stream()
                                               .sorted(comparing(FieldError::getField))
                                               .map(error ->
                                                      new ErrorDetail(
                                                              error.getField(),
                                                              error.getDefaultMessage()))
                                               .collect(Collectors.toList());
        return new ExceptionBodyDTO(INVALID_PAYLOAD, details, BAD_REQUEST);
    }

    private static ExceptionBodyDTO fromMessage(String message,
                                                HttpStatus status) {
        return new ExceptionBodyDTO(message,
                                     Collections.emptyList(),
                                     status);
    }

    @Value
    public static class ErrorDetail {
        String field;
        String message;
    }
}