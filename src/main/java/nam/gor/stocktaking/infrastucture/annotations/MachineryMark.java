package nam.gor.stocktaking.infrastucture.annotations;

import io.swagger.v3.oas.annotations.Operation;
import nam.gor.stocktaking.api.services.MachineryService;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static nam.gor.stocktaking.infrastucture.util.StringPatterns.MACHINERY_URL;

@Target({ElementType.METHOD,
         ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations({
        @RouterOperation(
                path = MACHINERY_URL,
                method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(
                        operationId = "FetchAllMachines",
                        summary = "Fetch all stocked Machines",
                        tags = "Machines"),
                beanClass = MachineryService.class,
                beanMethod = "findAllMachines"
        ),
        @RouterOperation(
                path = MACHINERY_URL + "{machineryId}",
                method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(
                        operationId = "FetchMachineryById",
                        summary = "Fetch an MachineryOutDTO by Id",
                        tags = "Machines"),
                beanClass = MachineryService.class,
                beanMethod = "findMachineryById"
        ),
        @RouterOperation(
                path = MACHINERY_URL,
                method = RequestMethod.POST,
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(
                        operationId = "StockMachinery",
                        summary = "Stock an MachineryOutDTO",
                        tags = "Machines"),
                beanClass = MachineryService.class,
                beanMethod = "saveMachinery"
        ),
        @RouterOperation(
                path = MACHINERY_URL + "{machineryId}",
                method = RequestMethod.PATCH,
                operation = @Operation(
                        operationId = "MachineryUpdateDTO",
                        summary = "Update an existing  MachineryOutDTO",
                        tags = "Machines"),
                consumes = MediaType.APPLICATION_JSON_VALUE,
                beanClass =  MachineryService.class,
                beanMethod = "updateMachineryById"
        ),
        @RouterOperation(
                path = MACHINERY_URL + "{machineryId}",
                method = RequestMethod.DELETE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(
                        operationId = "DeleteMachinery",
                        summary = "Delete an existing  MachineryOutDTO",
                        tags = "Machines"),
                beanClass =  MachineryService.class,
                beanMethod = "deleteMachineryById"
        )
})
public @interface  MachineryMark {
}