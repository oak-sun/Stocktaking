package nam.gor.stocktaking.api.dto.stockkeeper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import nam.gor.stocktaking.domain.entities.StockKeeper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Optional;

@Value
public class StockKeeperUpdateDTO {

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
    public StockKeeperUpdateDTO(
            @JsonProperty("firstName") final String firstName,
            @JsonProperty("lastName") final String lastName,
            @JsonProperty("workContractNumber") final Long workContractNumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.workContractNumber = workContractNumber;
    }

    public StockKeeper fromEntityToEntity(final StockKeeper keeper) {
        final StockKeeper.StockKeeperBuilder builder = keeper.toBuilder();
        getFirstName()
                .map(builder::firstName);
        getLastName()
                .map(builder::lastName);
        getWorkContractNumber()
                .map(builder::workContractNumber);
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

}

