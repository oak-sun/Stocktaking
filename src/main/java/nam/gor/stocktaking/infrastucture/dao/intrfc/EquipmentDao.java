package nam.gor.stocktaking.infrastucture.dao.intrfc;

import nam.gor.stocktaking.domain.entities.Equipment;
import nam.gor.stocktaking.domain.entities.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EquipmentDao {
    Flux<Equipment> findByQuery(final Query query);
    Mono<Equipment> findById(final String equipmentId);
    Mono<Void> save(final Equipment equipment);
    Mono<Boolean> deleteById(final String equipmentId);
}
