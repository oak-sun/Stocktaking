package nam.gor.stocktaking.api.dto.taskmaster;

import lombok.Value;
import nam.gor.stocktaking.domain.entities.TaskMaster;

@Value
public class TaskMasterOutDTO {
    String id;

    String firstName;

    String lastName;

    Long workContractNumber;

    String objectName;

    Long teamNumber;

    public static TaskMasterOutDTO toDto(final TaskMaster master) {
        return new TaskMasterOutDTO(
                master.getId(),
        master.getFirstName(),
        master.getLastName(),
        master.getWorkContractNumber(),
        master.getObjectName(),
        master.getTeamNumber()
        );
    }
}
