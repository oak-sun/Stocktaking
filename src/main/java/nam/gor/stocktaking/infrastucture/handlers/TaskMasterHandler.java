package nam.gor.stocktaking.infrastucture.handlers;

import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterOutDTO;
import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterSaveDTO;
import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterUpdateDTO;
import nam.gor.stocktaking.api.services.TaskMasterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class TaskMasterHandler {
    private final TaskMasterService service;

    public Mono<ServerResponse> findAllTaskMasters(ServerRequest req) {
        return ServerResponse
                .ok()
                .body(service.findAllTaskMasters(),
                        TaskMasterOutDTO.class);
    }

    public Mono<ServerResponse> saveTaskMaster(ServerRequest req) {
        final Function<TaskMasterOutDTO, Mono<ServerResponse>>
                response = master ->
                        ServerResponse
                        .created(URI.create(req.path() +
                                "/" + master.getId()))
                        .bodyValue(master);
        return req
                .bodyToMono(TaskMasterSaveDTO.class)
                .flatMap(service::saveTaskMaster)
                .flatMap(response);
    }

    public Mono<ServerResponse> updateTaskMasterById(ServerRequest req) {
        return req
                .bodyToMono(TaskMasterUpdateDTO.class)
                .flatMap(master -> service.updateTaskMasterById(
                                   req.pathVariable("taskmasterId"),
                                   master))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> deleteTaskMasterById(ServerRequest req) {
        return service
                .deleteTaskMasterById(req.pathVariable("taskmasterId"))
                .then(ServerResponse.noContent().build());
    }
}

