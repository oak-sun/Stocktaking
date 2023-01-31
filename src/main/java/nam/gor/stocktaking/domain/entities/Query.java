package nam.gor.stocktaking.domain.entities;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder(toBuilder = true)
public class Query {
    String name;
    Integer minQuantity;
    String taskmasterId;

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<Integer> getMinQuantity() {
        return Optional.ofNullable(minQuantity);
    }

    public Optional<String> getTaskmasterId() {
        return Optional.ofNullable(taskmasterId);
    }
}
