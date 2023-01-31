package nam.gor.stocktaking.infrastucture.dao.impl;

import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.infrastucture.dao.intrfc.TaskMasterDao;
import nam.gor.stocktaking.infrastucture.documents.TaskMasterDocument;
import nam.gor.stocktaking.infrastucture.serializers.Serializer;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@AllArgsConstructor
@Repository
public class TaskMasterDaoImpl implements TaskMasterDao {
    private final ReactiveMongoTemplate template;
    private final Serializer<TaskMaster, TaskMasterDocument> serializer;

    @Override
    public Flux<TaskMaster> findAll() {
        return template
                .findAll(TaskMasterDocument.class)
                .sort(Comparator.comparing(TaskMasterDocument::getObjectName))
                .map(serializer::fromDocument);
    }

    @Override
    public Mono<TaskMaster> findById(final String taskmasterId) {
        return template
                .findById(taskmasterId, TaskMasterDocument.class)
                .map(serializer::fromDocument);
    }

    @Override
    public Mono<Void> save(final TaskMaster master) {
        return template
                .save(serializer.toDocument(master))
                .then();
    }

    @Override
    public Mono<Boolean> deleteById(final String taskmasterId) {
        final Query query = new Query(new Criteria("id")
                .is(taskmasterId));
        return template
                .remove(query, TaskMasterDocument.class)
                .map(result -> result.getDeletedCount() > 0);
    }
}
