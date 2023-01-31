package nam.gor.stocktaking.infrastucture.dao.intrfc;

import nam.gor.stocktaking.domain.entities.StockKeeper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockKeeperDao {
    Flux<StockKeeper> findAll();
    Mono<StockKeeper> findById(final String stockKeeperId);
    Mono<Void> save(final StockKeeper stockKeeper);
    Mono<Boolean> deleteById(final String stockKeeperId);
}
