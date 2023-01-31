package nam.gor.stocktaking.api.dto.machines;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import nam.gor.stocktaking.domain.entities.Query;
import org.springframework.util.MultiValueMap;
import java.util.Optional;

@Value
public class MachineryFindDTO {
    String name;
    Integer minQuantity;
    String taskmasterId;

    @JsonCreator
    public MachineryFindDTO(
            @JsonProperty("name") String name,
            @JsonProperty("minQuantity") Integer minQuantity,
            @JsonProperty("taskmasterId") String taskmasterId) {
        this.name = name;
        this.minQuantity = minQuantity;
        this.taskmasterId= taskmasterId;
    }

    public Query toEntity() {
        return Query
                .builder()
                .name(name)
                .minQuantity(minQuantity)
                .taskmasterId(taskmasterId)
                .build();
    }

    public static MachineryFindDTO fromQueryParams(MultiValueMap<String, String> params) {
        return new MachineryFindDTO(
                params.getFirst("name"),
                Optional
                        .ofNullable(params.getFirst("minQuantity"))
                        .map(Integer::parseInt).orElse(null),
                params.getFirst("taskmasterId")
        );
    }
}

