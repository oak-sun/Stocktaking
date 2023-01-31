package nam.gor.stocktaking.infrastucture.dao.intrfc;

import nam.gor.stocktaking.domain.entities.TaskMaster;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskMasterDao {
    Flux<TaskMaster> findAll();
    Mono<TaskMaster> findById(final String taskmasterId);
    Mono<Void> save(final TaskMaster taskMaster);
    Mono<Boolean> deleteById(final String taskmasterId);
}
