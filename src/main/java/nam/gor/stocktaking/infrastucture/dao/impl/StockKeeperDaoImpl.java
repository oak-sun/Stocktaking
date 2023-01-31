package nam.gor.stocktaking.infrastucture.dao.impl;

import lombok.AllArgsConstructor;
import nam.gor.stocktaking.infrastucture.dao.intrfc.StockKeeperDao;
import nam.gor.stocktaking.infrastucture.documents.StockKeeperDocument;
import nam.gor.stocktaking.domain.entities.StockKeeper;
import nam.gor.stocktaking.infrastucture.serializers.Serializer;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@AllArgsConstructor
@Repository
public class StockKeeperDaoImpl implements StockKeeperDao {

    private final ReactiveMongoTemplate template;
    private final Serializer<StockKeeper, StockKeeperDocument> serializer;

    @Override
    public Flux<StockKeeper> findAll() {
        return template
                .findAll(StockKeeperDocument.class)
                .sort(Comparator.comparing(StockKeeperDocument::getWorkContractNumber))
                .map(serializer::fromDocument);
    }

    @Override
    public Mono<StockKeeper> findById(final String stockKeeperId) {
        return template
                .findById(stockKeeperId, StockKeeperDocument.class)
                .map(serializer::fromDocument);
    }

    @Override
    public Mono<Void> save(final StockKeeper keeper) {
        return template
                .save(serializer.toDocument(keeper))
                .then();
    }

    @Override
    public Mono<Boolean> deleteById(final String stockKeeperId) {
        final Query query = new Query(new Criteria("id")
                .is(stockKeeperId));
        return template
                .remove(query, StockKeeperDocument.class)
                .map(result -> result.getDeletedCount() > 0);
    }
}

