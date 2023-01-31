package nam.gor.stocktaking.infrastucture.dao.intrfc;

import nam.gor.stocktaking.domain.entities.Stock;
import reactor.core.publisher.Mono;

public interface StockDao {
    Mono<Stock> generatePreSignedUrlForUpload();
    Mono<Stock> generatePreSignedUrlForVisualization(String identifier);
}
