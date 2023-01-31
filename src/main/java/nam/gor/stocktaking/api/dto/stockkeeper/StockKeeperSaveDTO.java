package nam.gor.stocktaking.api.dto.stockkeeper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import nam.gor.stocktaking.domain.entities.StockKeeper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Value
public class StockKeeperSaveDTO {


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




    @JsonCreator
    public StockKeeperSaveDTO(
            @JsonProperty("firstName") final String firstName,
            @JsonProperty("lastName") final String lastName,
            @JsonProperty("workContractNumber") final Long workContractNumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.workContractNumber = workContractNumber;
    }

    public StockKeeper byIdToEntity(final String id) {
        return StockKeeper
                .builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .workContractNumber(workContractNumber)
                .build();
    }
}
