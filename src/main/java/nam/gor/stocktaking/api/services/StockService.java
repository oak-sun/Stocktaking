package nam.gor.stocktaking.api.services;

import nam.gor.stocktaking.api.dto.StockOutDTO;
import nam.gor.stocktaking.infrastucture.dao.intrfc.StockDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class StockService {
    private final StockDao dao;

    public Mono<StockOutDTO> generatePreSignedUrlForUpload() {
        return dao
                .generatePreSignedUrlForUpload()
                .map(StockOutDTO::toDto);
    }
}
