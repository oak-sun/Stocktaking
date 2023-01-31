package nam.gor.stocktaking.infrastucture.handlers;

import nam.gor.stocktaking.api.dto.equipment.EquipmentFindDTO;
import nam.gor.stocktaking.api.dto.equipment.EquipmentOutDTO;
import nam.gor.stocktaking.api.dto.equipment.EquipmentSaveDTO;
import nam.gor.stocktaking.api.dto.equipment.EquipmentUpdateDTO;
import nam.gor.stocktaking.api.services.EquipmentService;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class EquipmentHandler {
    private final EquipmentService service;

    public @NonNull Mono<ServerResponse> findAllEquipments(ServerRequest req) {
        return service
                .findAllEquipments(EquipmentFindDTO
                        .fromQueryParams(req.queryParams()))
                .collectList()
                .flatMap(equipments ->
                        ServerResponse.ok().bodyValue(equipments));
    }

    public @NonNull Mono<ServerResponse> findEquipmentById(ServerRequest req) {
        return service
                .findEquipmentById(req.pathVariable("equipmentId"))
                .flatMap(equipment ->
                        ServerResponse.ok().bodyValue(equipment));
    }

    public @NonNull Mono<ServerResponse> saveEquipment(ServerRequest req) {
        final Function<EquipmentOutDTO, Mono<ServerResponse>> response =
                equipment -> ServerResponse
                          .created(URI.create(req.path() +
                                   "/" + equipment.getId()))
                          .bodyValue(equipment);
        return req
                .bodyToMono(EquipmentSaveDTO.class)
                .flatMap(service::saveEquipment)
                .flatMap(response);
    }

    public @NonNull Mono<ServerResponse> updateEquipmentById(ServerRequest req) {
        return req
                .bodyToMono(EquipmentUpdateDTO.class)
                .flatMap(equipment -> service
                                   .updateEquipmentById(req.pathVariable("equipmentId"),
                                               equipment))
                .then(ServerResponse.noContent().build());
    }

    public @NonNull Mono<ServerResponse> deleteEquipmentById(ServerRequest req) {
        return service
                .deleteEquipmentById(req.pathVariable("equipmentId"))
                .then(ServerResponse.noContent().build());
    }
}
