package nam.gor.stocktaking.domain.entities;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;


@Value
@Builder(toBuilder = true)
public class TaskMaster {

    @NonNull
    String id;

    @NonNull
    String firstName;

    @NonNull
    String lastName;

    @NonNull
    Long workContractNumber;

    @NonNull
    String objectName;

    @NonNull
    Long teamNumber;
}
