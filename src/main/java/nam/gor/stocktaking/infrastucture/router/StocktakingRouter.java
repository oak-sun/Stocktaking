package nam.gor.stocktaking.infrastucture.router;

import lombok.AllArgsConstructor;
import nam.gor.stocktaking.infrastucture.annotations.*;
import nam.gor.stocktaking.infrastucture.handlers.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import static nam.gor.stocktaking.infrastucture.util.StringPatterns.EQUIPMENT_URL;
import static nam.gor.stocktaking.infrastucture.util.StringPatterns.MACHINERY_URL;
import static nam.gor.stocktaking.infrastucture.util.StringPatterns.STOCK_KEEPER_URL;
import static nam.gor.stocktaking.infrastucture.util.StringPatterns.STOCK_URL;
import static nam.gor.stocktaking.infrastucture.util.StringPatterns.TASKMASTER_URL;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@AllArgsConstructor
@Configuration
public class StocktakingRouter {


    @Bean
    @StockMark
    public RouterFunction<ServerResponse> stockRouter(StockHandler handler) {
        return RouterFunctions
                .route(POST(STOCK_URL),
                        handler::generatePreSignedUrlForUpload);
    }

    @Bean
    @TaskMasterMark
    public RouterFunction<ServerResponse> taskMasterRouter(TaskMasterHandler handler) {
        return RouterFunctions
                .route(GET(TASKMASTER_URL),
                        handler::findAllTaskMasters)
                .andRoute(POST(TASKMASTER_URL)
                                .and(accept(MediaType.APPLICATION_JSON)),
                         handler::saveTaskMaster)
                .andRoute(PATCH(TASKMASTER_URL + "/{taskmasterId}")
                                   .and(accept(MediaType.APPLICATION_JSON)),
                          handler::updateTaskMasterById)
                .andRoute(DELETE(TASKMASTER_URL + "/{taskmasterId}")
                                .and(accept(MediaType.APPLICATION_JSON)),
                        handler::deleteTaskMasterById);
    }

    @Bean
    @StockKeeperMark
    public RouterFunction<ServerResponse> stockKeeperRouter(StockKeeperHandler handler) {
        return RouterFunctions
                .route(GET(STOCK_KEEPER_URL),
                        handler::findAllStockKeepers)
                .andRoute(POST(STOCK_KEEPER_URL)
                                .and(accept(MediaType.APPLICATION_JSON)),
                        handler::saveStockKeeper)
                .andRoute(PATCH(STOCK_KEEPER_URL + "/{stockKeeperId}")
                                .and(accept(MediaType.APPLICATION_JSON)),
                        handler::updateStockKeeperById)
                .andRoute(DELETE(STOCK_KEEPER_URL + "/{stockKeeperId}")
                                .and(accept(MediaType.APPLICATION_JSON)),
                        handler::deleteStockKeeperById);
    }

    @Bean
    @EquipmentMark
    public RouterFunction<ServerResponse> equipmentRouter(EquipmentHandler handler) {
        return RouterFunctions
                .route(GET(EQUIPMENT_URL),
                        handler::findAllEquipments)
                .andRoute(GET(EQUIPMENT_URL + "/{equipmentId}"),
                        handler::findEquipmentById)
                .andRoute(POST(EQUIPMENT_URL)
                                .and(accept(MediaType.APPLICATION_JSON)),
                        handler::saveEquipment)
                .andRoute(PATCH(EQUIPMENT_URL + "/{equipmentId}")
                                .and(accept(MediaType.APPLICATION_JSON)),
                        handler::updateEquipmentById)
                .andRoute(DELETE(EQUIPMENT_URL + "/{equipmentId}"),
                        handler::deleteEquipmentById);
    }

    @Bean
    @MachineryMark
    public RouterFunction<ServerResponse> machineryRouter(MachineryHandler handler) {
        return RouterFunctions
                .route(GET(MACHINERY_URL),
                        handler::findAllMachines)
                .andRoute(GET(MACHINERY_URL + "/{machineryId}"),
                        handler::findMachineryById)
                .andRoute(POST(EQUIPMENT_URL)
                                .and(accept(MediaType.APPLICATION_JSON)),
                        handler::saveMachinery)
                .andRoute(PATCH(MACHINERY_URL + "/{machineryId}")
                                .and(accept(MediaType.APPLICATION_JSON)),
                        handler::updateMachineryById)
                .andRoute(DELETE(MACHINERY_URL + "/{machineryId}"),
                        handler::deleteMachineryById);
    }
}
