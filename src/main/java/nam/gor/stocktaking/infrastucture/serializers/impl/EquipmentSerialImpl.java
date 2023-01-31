package nam.gor.stocktaking.infrastucture.serializers.impl;

import nam.gor.stocktaking.infrastucture.documents.TaskMasterDocument;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.infrastucture.serializers.Serializer;
import nam.gor.stocktaking.infrastucture.documents.EquipmentDocument;
import nam.gor.stocktaking.domain.entities.Equipment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class EquipmentSerialImpl implements Serializer<Equipment, EquipmentDocument> {
    private final Serializer<TaskMaster, TaskMasterDocument> serializer;

    @Override
    public Equipment fromDocument(final EquipmentDocument document) {
        return Equipment
                .builder()
                .id(document.getId())
                .sku(document.getSku())
                .name(document.getName())
                .description(document.getDescription())
                .price(document.getPrice())
                .quantity(document.getQuantity())
                .stockIdentifier(document.getImageIdentifier())
                .taskMaster(serializer.fromDocument(document.getTaskmaster()))
                .build();
    }

    @Override
    public EquipmentDocument toDocument(final Equipment equipment) {
        return new EquipmentDocument(
                equipment.getId(),
                equipment.getSku(),
                equipment.getName(),
                equipment.getDescription(),
                equipment.getPrice(),
                equipment.getQuantity(),
                equipment.getStockIdentifier(),
                serializer.toDocument(equipment.getTaskMaster())
        );
    }
}
