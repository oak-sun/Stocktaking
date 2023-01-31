package nam.gor.stocktaking.api.dto.equipment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import nam.gor.stocktaking.domain.entities.Query;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

@Value
public class EquipmentFindDTO {
    String name;
    Integer minQuantity;
    String taskMasterId;

    @JsonCreator
    public EquipmentFindDTO(@JsonProperty("name") String name,
                            @JsonProperty("minQuantity") Integer minQuantity,
                            @JsonProperty("taskMasterId") String taskMasterId) {
        this.name = name;
        this.minQuantity = minQuantity;
        this.taskMasterId = taskMasterId;
    }

    public Query toEntity() {
        return Query
                .builder()
                .name(name)
                .minQuantity(minQuantity)
                .taskmasterId(taskMasterId)
                .build();
    }

    public static EquipmentFindDTO fromQueryParams(MultiValueMap<String, String> params) {
        return new EquipmentFindDTO(
                params.getFirst("name"),
                Optional
                        .ofNullable(params.getFirst("minQuantity"))
                        .map(Integer::parseInt).orElse(null),
                params.getFirst("taskMasterId")
        );
    }
}
