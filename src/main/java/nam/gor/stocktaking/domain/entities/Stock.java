package nam.gor.stocktaking.domain.entities;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@Builder(toBuilder = true)
public class Stock {

    String identifier;

    @With
    String preSignedUrl;

    @With
    @NonNull
    StockKeeper stockKeeper;
}
