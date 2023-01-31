package nam.gor.stocktaking.api.dto.equipment;

import lombok.Value;
import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterOutDTO;
import nam.gor.stocktaking.domain.entities.Equipment;
import nam.gor.stocktaking.domain.entities.Stock;

import java.math.BigDecimal;

@Value
public class EquipmentOutDTO {
    String id;
    String sku;
    String name;
    String description;
    BigDecimal price;
    int quantity;
    String stockUrl;
    TaskMasterOutDTO dto;

    public static EquipmentOutDTO toDto(final Equipment equipment,
                                       Stock stock) {
        return new EquipmentOutDTO(
                equipment.getId(),
                equipment.getSku(),
                equipment.getName(),
                equipment.getDescription(),
                equipment.getPrice(),
                equipment.getQuantity(),
                stock.getPreSignedUrl(),
                TaskMasterOutDTO.toDto(equipment.getTaskMaster())
        );
    }
}
