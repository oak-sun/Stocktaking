package nam.gor.stocktaking.api.services;

import lombok.AllArgsConstructor;
import nam.gor.stocktaking.infrastucture.dao.intrfc.StockKeeperDao;
import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperOutDTO;
import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperSaveDTO;
import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperUpdateDTO;
import nam.gor.stocktaking.domain.IdGenerator;
import nam.gor.stocktaking.api.RequestValidator;
import nam.gor.stocktaking.domain.exceptions.EntityNotFoundException;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static nam.gor.stocktaking.infrastucture.util.StringPatterns.STOCK_KEEPER_NOT_FOUND;


    @Service
    @AllArgsConstructor
    public class StockKeeperService {
        private final StockKeeperDao dao;
        private final RequestValidator validator;
        private final IdGenerator idGen;

        public Flux<StockKeeperOutDTO> findAllStockKeepers() {
            return dao.findAll().map(StockKeeperOutDTO::toDto);
        }

        public Mono<StockKeeperOutDTO> saveStockKeeper(final StockKeeperSaveDTO dto) {
            return validator
                    .validate(dto)
                    .map(save -> save.byIdToEntity(idGen.newId()))
                    .flatMap(keeper -> dao.save(keeper).thenReturn(keeper))
                    .map(StockKeeperOutDTO::toDto);
        }

        public Mono<Void> updateStockKeeperById(final String stockKeeperId,
                                               final StockKeeperUpdateDTO dto) {
            return validator
                    .validate(dto)
                    .flatMap(update -> dao
                            .findById(stockKeeperId)
                            .map(update::fromEntityToEntity))
                    .switchIfEmpty(Mono.error(
                            new EntityNotFoundException(
                                    STOCK_KEEPER_NOT_FOUND,
                                    stockKeeperId)))
                    .flatMap(dao::save);
        }

        public Mono<Void> deleteStockKeeperById(final String stockKeeperId) {
            return dao
                    .deleteById(stockKeeperId)
                    .filter(BooleanUtils::isTrue)
                    .switchIfEmpty(Mono.error(
                            new EntityNotFoundException(
                                    STOCK_KEEPER_NOT_FOUND,
                                    stockKeeperId)))
                    .then();
        }
    }

