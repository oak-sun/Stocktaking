package nam.gor.stocktaking.domain.entities;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
public class Equipment {

    @NonNull
    String id;

    @NonNull
    String name;

    @NonNull
    String sku;

    @NonNull
    String description;

    @NonNull
    BigDecimal price;

    @NonNull
    Integer quantity;

    @NonNull
    String stockIdentifier;

    @With
    @NonNull
    TaskMaster taskMaster;
}
