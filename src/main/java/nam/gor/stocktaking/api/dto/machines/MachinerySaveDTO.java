package nam.gor.stocktaking.api.dto.machines;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.domain.entities.Machinery;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Value
public class MachinerySaveDTO {

        @NotBlank(message = "the field is mandatory")
        @Size(max = 150,
                message = "the field must not exceed {max} characters")
        String name;

        @NotBlank(message = "the field is mandatory")
        @Size(max = 20,
                message = "the field must not exceed {max} characters")
        String sku;

        @NotBlank(message = "the field is mandatory")
        @Size(max = 1000,
                message = "the field must not exceed {max} characters")
        String description;

        @NotNull(message = "the field is mandatory")
        @Positive(message = "the field must contain a positive value")
        BigDecimal price;

        @PositiveOrZero(message = "the field must contain a positive value")
        @NotNull(message = "the field is mandatory")
        Integer quantity;

        @NotBlank(message = "the field is mandatory")
        @Size(max = 36,
                message = "the field must not exceed {max} characters")
        String stockIdentifier;

        @NotBlank(message = "the field is mandatory")
        @Size(max = 36,
                message = "the field must not exceed {max} characters")
        String taskmasterId;

        @JsonCreator
        public MachinerySaveDTO(
                @JsonProperty("name") final String name,
                @JsonProperty("sku") final String sku,
                @JsonProperty("description") final String description,
                @JsonProperty("price") final BigDecimal price,
                @JsonProperty("quantity") final Integer quantity,
                @JsonProperty("stockIdentifier") final String stockIdentifier,
                @JsonProperty("taskmasterId") final String taskmasterId) {
            this.name = name;
            this.sku = sku;
            this.description = description;
            this.price = price;
            this.quantity = quantity;
            this.stockIdentifier = stockIdentifier;
            this.taskmasterId = taskmasterId;
        }

        public Machinery toEntity(final String machineryId,
                                  final TaskMaster taskMaster) {
            return Machinery
                    .builder()
                    .id(machineryId)
                    .name(name)
                    .sku(sku)
                    .description(description)
                    .price(price)
                    .quantity(quantity)
                    .stockIdentifier(stockIdentifier)
                    .taskMaster(taskMaster)
                    .build();
        }
    }


