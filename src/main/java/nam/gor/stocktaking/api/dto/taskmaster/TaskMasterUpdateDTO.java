package nam.gor.stocktaking.api.dto.taskmaster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import nam.gor.stocktaking.domain.entities.TaskMaster;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Optional;

@Value
public class TaskMasterUpdateDTO {
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
    public TaskMasterUpdateDTO(
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

    public TaskMaster fromEntityToEntity(final TaskMaster master) {
        final TaskMaster.TaskMasterBuilder builder = master.toBuilder();
        getFirstName().map(builder::firstName);
        getLastName().map(builder::lastName);
        getWorkContractNumber().map(builder::workContractNumber);
        getObjectName().map(builder::objectName);
        getTeamNumber().map(builder::teamNumber);
        return builder.build();
    }

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public Optional<String> getLastName() {
        return Optional.ofNullable(lastName);
    }

    public Optional<Long> getWorkContractNumber() {
        return Optional.ofNullable(workContractNumber);
    }

    public Optional<String> getObjectName() {
        return Optional.ofNullable(objectName);
    }

    public Optional<Long> getTeamNumber() {
        return Optional.ofNullable(teamNumber);

    }

}
