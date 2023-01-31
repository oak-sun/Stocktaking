package nam.gor.stocktaking.api.services;

import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterOutDTO;
import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterSaveDTO;
import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterUpdateDTO;
import nam.gor.stocktaking.api.RequestValidator;
import nam.gor.stocktaking.infrastucture.dao.intrfc.TaskMasterDao;
import nam.gor.stocktaking.domain.IdGenerator;
import nam.gor.stocktaking.domain.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static nam.gor.stocktaking.infrastucture.util.StringPatterns.TASKMASTER_NOT_FOUND;

@Service
@AllArgsConstructor
public class TaskMasterService {
    private final TaskMasterDao dao;
    private final RequestValidator validator;
    private final IdGenerator idGen;



    public Flux<TaskMasterOutDTO> findAllTaskMasters() {
        return dao
                .findAll()
                .map(TaskMasterOutDTO::toDto);
    }

    public Mono<TaskMasterOutDTO> saveTaskMaster(final TaskMasterSaveDTO dto) {
        return validator
                .validate(dto)
                .map(save -> save.byIdToEntity(idGen.newId()))
                .flatMap(master -> dao.save(master).thenReturn(master))
                .map(TaskMasterOutDTO::toDto);
    }

    public Mono<Void> updateTaskMasterById(final String taskmasterId,
                                 final TaskMasterUpdateDTO dto) {
        return validator
                .validate(dto)
                .flatMap(update -> dao
                                    .findById(taskmasterId)
                                    .map(update::fromEntityToEntity))
                .switchIfEmpty(Mono.error(
                             new EntityNotFoundException(
                                     TASKMASTER_NOT_FOUND,
                                     taskmasterId)))
                .flatMap(dao::save);
    }

    public Mono<Void> deleteTaskMasterById(final String taskmasterId) {
        return dao
                .deleteById(taskmasterId)
                .filter(BooleanUtils::isTrue)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException(
                                TASKMASTER_NOT_FOUND,
                               taskmasterId)))
                .then();
    }
}
