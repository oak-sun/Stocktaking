package nam.gor.stocktaking.infrastucture.serializers.impl;

import nam.gor.stocktaking.infrastucture.serializers.Serializer;
import nam.gor.stocktaking.infrastucture.documents.StockKeeperDocument;
import nam.gor.stocktaking.domain.entities.StockKeeper;
import org.springframework.stereotype.Component;


@Component
public class StockKeeperSerialImpl implements Serializer<StockKeeper,
        StockKeeperDocument> {

    @Override
    public StockKeeper fromDocument(final StockKeeperDocument document) {
        return StockKeeper
                .builder()
                .id(document.getId())
                .firstName(document.getFirstName())
                .lastName(document.getLastName())
                .workContractNumber(document.getWorkContractNumber())
                .build();
    }

    @Override
    public StockKeeperDocument toDocument(final StockKeeper keeper) {
        return new StockKeeperDocument(keeper.getId(),
                keeper.getFirstName(),
                keeper.getLastName(),
                keeper.getWorkContractNumber());
               
    }
}
