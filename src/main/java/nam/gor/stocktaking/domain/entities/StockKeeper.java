package nam.gor.stocktaking.domain.entities;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.stereotype.Component;

@Value
@Builder(toBuilder = true)
public class StockKeeper {

    @NonNull
    String id;

    @NonNull
    String firstName;

    @NonNull
    String lastName;

    @NonNull
    Long workContractNumber;
}
