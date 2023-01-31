package nam.gor.stocktaking.infrastucture.dao.intrfc;

import nam.gor.stocktaking.domain.entities.Machinery;
import nam.gor.stocktaking.domain.entities.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MachineryDao {

        Flux<Machinery> findByQuery(final Query query);
        Mono<Machinery> findById(final String machineryId);
        Mono<Void> save(final Machinery machinery);
        Mono<Boolean> deleteById(final String machineryId);
}
