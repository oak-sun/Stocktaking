package nam.gor.stocktaking.api.dto.machines;

import lombok.Value;
import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterOutDTO;

import nam.gor.stocktaking.domain.entities.Machinery;
import nam.gor.stocktaking.domain.entities.Stock;
import java.math.BigDecimal;
@Value
public class MachineryOutDTO {

        String id;
        String sku;
        String name;
        String description;
        BigDecimal price;
        int quantity;
        String imageUrl;
        TaskMasterOutDTO dto;

        public static MachineryOutDTO toDto(final Machinery machinery,
                                            Stock stock) {
            return new MachineryOutDTO(
                    machinery.getId(),
                    machinery.getSku(),
                    machinery.getName(),
                    machinery.getDescription(),
                    machinery.getPrice(),
                    machinery.getQuantity(),
                    stock.getPreSignedUrl(),
                    TaskMasterOutDTO.toDto(machinery.getTaskMaster())
            );
        }
    }

