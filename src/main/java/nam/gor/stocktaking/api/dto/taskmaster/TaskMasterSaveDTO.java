package nam.gor.stocktaking.api.dto.taskmaster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import nam.gor.stocktaking.domain.entities.TaskMaster;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Value
public class TaskMasterSaveDTO {

    @NotBlank(message = "the field is mandatory")
    @Size(max = 150,
            message = "the field must not exceed {max} characters")
    String firstName;
    @NotBlank(message = "the field is mandatory")
    @Size(max = 150,
            message = "the field must not exceed {max} characters")
    String lastName;
    @NotNull(message = "the field is mandatory")
    @Positive(message = "the field must contain a positive value")
    Long workContractNumber;
    @NotBlank(message = "the field is mandatory")
    @Size(max = 150,
            message = "the field must not exceed {max} characters")
    String objectName;

    @NotNull(message = "the field is mandatory")
    @Positive(message = "the field must contain a positive value")
    Long teamNumber;


    @JsonCreator
    public TaskMasterSaveDTO(
            @JsonProperty("firstName") final String firstName,
            @JsonProperty("lastName") final String lastName,
            @JsonProperty("workContractNumber") final Long workContractNumber,
            @JsonProperty("objectName") final String objectName,
             @JsonProperty("teamNumber") final Long teamNumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.workContractNumber = workContractNumber;
        this.objectName = objectName;
        this.teamNumber = teamNumber;

    }

    public TaskMaster byIdToEntity(final String id) {
        return TaskMaster
                .builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .workContractNumber(workContractNumber)
                .objectName(objectName)
                .teamNumber(teamNumber)
                .build();
    }
}
