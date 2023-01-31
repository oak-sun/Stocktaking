package nam.gor.stocktaking.infrastucture.annotations;

import io.swagger.v3.oas.annotations.Operation;
import nam.gor.stocktaking.api.services.StockKeeperService;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static nam.gor.stocktaking.infrastucture.util.StringPatterns.STOCK_KEEPER_URL;


@Target({ElementType.METHOD,
         ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations({
        @RouterOperation(
                path = STOCK_KEEPER_URL,
                method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(
                        operationId = "FetchAllStockKeepers",
                        summary = "Fetch all StockKeepers",
                        tags = "StockKeepers"),
                beanClass = StockKeeperService.class,
                beanMethod = "findAllStockKeepers"
        ),
        @RouterOperation(
                path = STOCK_KEEPER_URL,
                method = RequestMethod.POST,
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(
                        operationId = "StockStockKeeper",
                        summary = "Stock an StockKeeperOutDTO",
                        tags = "StockKeepers"),
                beanClass = StockKeeperService.class,
                beanMethod = "saveStockKeeper"
        ),
        @RouterOperation(
                path = STOCK_KEEPER_URL + "{stockKeeperId}",
                method = RequestMethod.PATCH,
                operation = @Operation(
                        operationId = "StockKeeperUpdateDTO",
                        summary = "Update an existing StockKeeperOutDTO",
                        tags = "StockKeepers"),
                consumes = MediaType.APPLICATION_JSON_VALUE,
                beanClass = StockKeeperService.class,
                beanMethod = "updateStockKeeperById"
        ),
        @RouterOperation(
                path = STOCK_KEEPER_URL + "{stockKeeperId}",
                method = RequestMethod.DELETE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(
                        operationId = "DeleteStockKeeper",
                        summary = "Delete an existing StockKeeperOutDTO",
                        tags = "StockKeepers"),
                beanClass = StockKeeperService.class,
                beanMethod = "deleteStockKeeperById"
        )
})
public @interface StockKeeperMark  {
}
