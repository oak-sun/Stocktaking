package nam.gor.stocktaking.api.dto;

import lombok.Value;
import nam.gor.stocktaking.domain.entities.Stock;
import nam.gor.stocktaking.domain.entities.StockKeeper;

@Value
public class StockOutDTO {
    String identifier;
    String preSignedUrl;
    StockKeeper stockKeeper;

    public static StockOutDTO toDto(final Stock stock) {
        return new StockOutDTO(
                stock.getIdentifier(),
                stock.getPreSignedUrl(),
                stock.getStockKeeper()
        );
    }
}
