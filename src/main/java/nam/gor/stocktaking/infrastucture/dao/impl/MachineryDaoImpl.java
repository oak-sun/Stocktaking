package nam.gor.stocktaking.infrastucture.dao.impl;

import lombok.AllArgsConstructor;
import nam.gor.stocktaking.infrastucture.dao.intrfc.MachineryDao;
import nam.gor.stocktaking.infrastucture.documents.MachineryDocument;
import nam.gor.stocktaking.domain.entities.Machinery;
import nam.gor.stocktaking.domain.entities.Query;
import nam.gor.stocktaking.infrastucture.serializers.Serializer;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Repository
@AllArgsConstructor
public class MachineryDaoImpl implements MachineryDao {
        private final ReactiveMongoTemplate template;
        private final Serializer<Machinery, MachineryDocument> serializer;

        @Override
        public Flux<Machinery> findByQuery(final Query query) {
            final var mongoQuery = new org.springframework
                    .data
                    .mongodb
                    .core
                    .query
                    .Query();
            query
                    .getName()
                    .ifPresent(n -> mongoQuery
                            .addCriteria(new Criteria(
                                    "name").is(n)));
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
                    .query(MachineryDocument.class)
                    .matching(mongoQuery)
                    .all()
                    .sort(Comparator.comparing(MachineryDocument::getName))
                    .map(serializer::fromDocument);
        }

        @Override
        public Mono<Machinery> findById(final String machineryId) {
            return template
                    .findById(machineryId, MachineryDocument.class)
                    .map(serializer::fromDocument);
        }

        @Override
        public Mono<Void> save(final Machinery machinery) {
            return template
                    .save(serializer.toDocument(machinery))
                    .then();
        }

        @Override
        public Mono<Boolean> deleteById(final String machineryId) {
            final var mongoQuery = new org.springframework
                    .data
                    .mongodb
                    .core
                    .query
                    .Query(new Criteria(
                    "id").is(machineryId));
            return template
                    .remove(mongoQuery, MachineryDocument.class)
                    .map(result -> result.getDeletedCount() > 0);
        }
    }


