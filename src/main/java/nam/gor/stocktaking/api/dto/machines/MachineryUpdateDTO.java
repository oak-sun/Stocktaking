package nam.gor.stocktaking.api.dto.machines;

import lombok.Value;
import nam.gor.stocktaking.domain.entities.Machinery;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Optional;

@Value
public class MachineryUpdateDTO {
    @Size(max = 150, message = "the field must not exceed {max} characters")
    String name;

    @Size(max = 20, message = "the field must not exceed {max} characters")
    String sku;

    @Size(max = 1000, message = "the field must not exceed {max} characters")
    String description;

    @Positive(message = "the field must contain a positive value")
    BigDecimal price;

    @PositiveOrZero(message = "the field must contain a positive value")
    Integer quantity;

    @Size(max = 36, message = "the field must not exceed {max} characters")
    String stockIdentifier;

    @Size(max = 36, message = "the field must not exceed {max} characters")
    String taskMasterId;

    public Machinery toEntity(final Machinery machinery) {
        final Machinery
                .MachineryBuilder builder = machinery.toBuilder();
        getName()
                .map(builder::name);
        getSku()
                .map(builder::sku);
        getDescription()
                .map(builder::description);
        getPrice()
                .map(builder::price);
        getQuantity()
                .map(builder::quantity);
        getStockIdentifier()
                .map(builder::stockIdentifier);
        return builder.build();
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getSku() {
        return Optional.ofNullable(sku);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public Optional<BigDecimal> getPrice() {
        return Optional.ofNullable(price);
    }

    public Optional<Integer> getQuantity() {
        return Optional.ofNullable(quantity);
    }

    public Optional<String> getStockIdentifier() {
        return Optional.ofNullable(stockIdentifier);
    }

    public Optional<String> getTaskmasterId() {
        return Optional.ofNullable(taskMasterId);
    }
}
