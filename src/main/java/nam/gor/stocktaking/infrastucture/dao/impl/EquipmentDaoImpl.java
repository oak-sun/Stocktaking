package nam.gor.stocktaking.infrastucture.dao.impl;

import nam.gor.stocktaking.infrastucture.documents.EquipmentDocument;
import nam.gor.stocktaking.domain.entities.Equipment;
import nam.gor.stocktaking.domain.entities.Query;
import nam.gor.stocktaking.infrastucture.dao.intrfc.EquipmentDao;
import nam.gor.stocktaking.infrastucture.serializers.Serializer;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Comparator;

@Repository
@AllArgsConstructor
public class EquipmentDaoImpl implements EquipmentDao {

    private final ReactiveMongoTemplate template;
    private final Serializer<Equipment, EquipmentDocument> serializer;

    @Override
    public Flux<Equipment> findByQuery(final Query query) {
        final var mongoQuery = new org.springframework
                                  .data
                                  .mongodb
                                  .core
                                  .query
                                  .Query();
        query
                .getName()
                .ifPresent(name -> mongoQuery
                        .addCriteria(new Criteria(
                                "name").is(name)));
        query
                .getTaskmasterId()
                .ifPresent(categoryId -> mongoQuery
                        .addCriteria(new Criteria(
                                "taskMaster.id").is(categoryId)));
        query
                .getMinQuantity()
                .ifPresent(quantity -> mongoQuery
                        .addCriteria(new Criteria(
                                "quantity").gte(quantity)));

        return template
                .query(EquipmentDocument.class)
                .matching(mongoQuery)
                .all()
                .sort(Comparator.comparing(EquipmentDocument::getName))
                .map(serializer::fromDocument);
    }

    @Override
    public Mono<Equipment> findById(final String equipmentId) {
        return template
                .findById(equipmentId, EquipmentDocument.class)
                .map(serializer::fromDocument);
    }

    @Override
    public Mono<Void> save(final Equipment equipment) {
        return template
                .save(serializer.toDocument(equipment))
                .then();
    }

    @Override
    public Mono<Boolean> deleteById(final String equipmentId) {
        final var mongoQuery = new org.springframework
                                    .data
                                    .mongodb
                                    .core
                                    .query
                                   .Query(
                                           new Criteria("id").is(equipmentId));
        return template
                .remove(mongoQuery, EquipmentDocument.class)
                .map(result -> result.getDeletedCount() > 0);
    }
}
