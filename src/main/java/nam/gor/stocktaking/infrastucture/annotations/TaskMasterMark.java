package nam.gor.stocktaking.infrastucture.annotations;

import nam.gor.stocktaking.api.services.TaskMasterService;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static nam.gor.stocktaking.infrastucture.util.StringPatterns.TASKMASTER_URL;

@Target({ElementType.METHOD,
         ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations({
        @RouterOperation(
                path = TASKMASTER_URL,
                method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(
                        operationId = "FetchAllTaskmasters",
                        summary = "Fetch all Taskmasters",
                        tags = "TaskMasters"),
                beanClass = TaskMasterService.class,
                beanMethod = "findAllTaskMasters"
        ),
        @RouterOperation(
                path = TASKMASTER_URL,
                method = RequestMethod.POST,
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(
                        operationId = "StockTaskMaster",
                        summary = "Stock an TaskMasterOutDTO",
                        tags = "Taskmasters"),
                beanClass = TaskMasterService.class,
                beanMethod = "saveTaskMaster"
        ),
        @RouterOperation(
                path = TASKMASTER_URL + "{taskmasterId}",
                method = RequestMethod.PATCH,
                operation = @Operation(
                        operationId = "TaskMasterUpdateDTO",
                        summary = "Update an existing TaskmasterOutDTO",
                        tags = "Taskmasters"),
                consumes = MediaType.APPLICATION_JSON_VALUE,
                beanClass = TaskMasterService.class,
                beanMethod = "updateTaskMasterById"
        ),
        @RouterOperation(
                path = TASKMASTER_URL + "{taskmasterId}",
                method = RequestMethod.DELETE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(
                        operationId = "DeleteTaskMaster",
                        summary = "Delete an existing TaskMasterOutDTO",
                        tags = "Taskmasters"),
                beanClass = TaskMasterService.class,
                beanMethod = "deleteTaskMasterById"
        )
})
public @interface TaskMasterMark {
}
