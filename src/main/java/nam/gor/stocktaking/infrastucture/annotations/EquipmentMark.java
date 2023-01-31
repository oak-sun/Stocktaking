package nam.gor.stocktaking.infrastucture.annotations;


import nam.gor.stocktaking.api.services.EquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static nam.gor.stocktaking.infrastucture.util.StringPatterns.EQUIPMENT_URL;

@Target({ElementType.METHOD,
         ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RouterOperations({
        @RouterOperation(
                path = EQUIPMENT_URL,
                method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(
                        operationId = "FetchAllEquipments",
                        summary = "Fetch all stocked Equipments",
                        tags = "Equipments"),
                beanClass = EquipmentService.class,
                beanMethod = "findAllEquipments"
        ),
        @RouterOperation(
                path = EQUIPMENT_URL + "{equipmentId}",
                method = RequestMethod.GET,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(
                        operationId = "FetchEquipmentById",
                        summary = "Fetch an EquipmentOutDTO by Id",
                        tags = "Equipments"),
                beanClass = EquipmentService.class,
                beanMethod = "findEquipmentById"
        ),
        @RouterOperation(
                path = EQUIPMENT_URL,
                method = RequestMethod.POST,
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(
                        operationId = "StockEquipment",
                        summary = "Stock an EquipmentOutDTO",
                        tags = "Equipments"),
                beanClass = EquipmentService.class,
                beanMethod = "saveEquipment"
        ),
        @RouterOperation(
                path = EQUIPMENT_URL + "{equipmentId}",
                method = RequestMethod.PATCH,
                operation = @Operation(
                        operationId = "EquipmentUpdateDTO",
                        summary = "Update an existing EquipmentOutDTO",
                        tags = "Equipments"),
                consumes = MediaType.APPLICATION_JSON_VALUE,
                beanClass = EquipmentService.class,
                beanMethod = "updateEquipmentById"
        ),
        @RouterOperation(
                path = EQUIPMENT_URL + "{equipmentId}",
                method = RequestMethod.DELETE,
                produces = MediaType.APPLICATION_JSON_VALUE,
                operation = @Operation(
                        operationId = "DeleteEquipment",
                        summary = "Delete an existing EquipmentOutDTO",
                        tags = "Equipments"),
                beanClass = EquipmentService.class,
                beanMethod = "deleteEquipmentById"
        )
})
public @interface EquipmentMark {
}
