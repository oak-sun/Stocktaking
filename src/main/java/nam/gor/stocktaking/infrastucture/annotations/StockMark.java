package nam.gor.stocktaking.infrastucture.annotations;

import nam.gor.stocktaking.api.services.StockService;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static nam.gor.stocktaking.infrastucture.util.StringPatterns.STOCK_URL;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations({
        @RouterOperation(
                path = STOCK_URL,
                method = RequestMethod.POST,
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(
                        operationId = "GeneratePreSignedUrl",
                        summary = "Generate a Pre Signed Url for a" +
                                " StockOutDTO Upload",
                        tags = "Stock"),
                beanClass = StockService.class,
                beanMethod = "generatePreSignedUrlForUpload"
        )
})
public @interface StockMark {
}
