package nam.gor.stocktaking.infrastucture.handlers;

import lombok.AllArgsConstructor;
import nam.gor.stocktaking.api.dto.machines.MachinerySaveDTO;
import nam.gor.stocktaking.api.dto.machines.MachineryFindDTO;
import nam.gor.stocktaking.api.dto.machines.MachineryUpdateDTO;
import nam.gor.stocktaking.api.dto.machines.MachineryOutDTO;
import nam.gor.stocktaking.api.services.MachineryService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

    @Component
    @AllArgsConstructor
    public class MachineryHandler {
        private final MachineryService service;

        public @NonNull Mono<ServerResponse> findAllMachines(ServerRequest req) {
            return service
                    .findAllMachines(MachineryFindDTO
                            .fromQueryParams(req.queryParams()))
                    .collectList()
                    .flatMap(machines -> ServerResponse.ok().bodyValue(machines));
        }

        public @NonNull Mono<ServerResponse> findMachineryById(ServerRequest req) {
            return service
                    .findMachineryById(req.pathVariable("machineryId"))
                    .flatMap(machinery ->
                            ServerResponse.ok().bodyValue(machinery));
        }

        public @NonNull Mono<ServerResponse> saveMachinery(ServerRequest req) {
            final Function<MachineryOutDTO, Mono<ServerResponse>> response =
                    machinery -> ServerResponse
                            .created(URI.create(req.path() +
                                    "/" + machinery.getId()))
                            .bodyValue(machinery);
            return req
                    .bodyToMono(MachinerySaveDTO.class)
                    .flatMap(service::saveMachinery)
                    .flatMap(response);
        }

        public @NonNull Mono<ServerResponse> updateMachineryById(ServerRequest req) {
            return req
                    .bodyToMono(MachineryUpdateDTO.class)
                    .flatMap(machinery -> service.updateMachineryById(
                            req.pathVariable("machineryId"),
                                                   machinery))
                    .then(ServerResponse.noContent().build());
        }

        public @NonNull Mono<ServerResponse> deleteMachineryById(ServerRequest req) {
            return service
                    .deleteMachineryById(req.pathVariable("machineryId"))
                    .then(ServerResponse.noContent().build());
        }
    }

