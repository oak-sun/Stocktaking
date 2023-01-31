package nam.gor.stocktaking.domain.factories;

import com.github.javafaker.Faker;
import nam.gor.stocktaking.infrastucture.documents.EquipmentDocument;
import nam.gor.stocktaking.api.dto.equipment.EquipmentFindDTO;
import nam.gor.stocktaking.api.dto.equipment.EquipmentSaveDTO;
import nam.gor.stocktaking.api.dto.equipment.EquipmentUpdateDTO;
import nam.gor.stocktaking.domain.entities.Equipment;

import java.math.BigDecimal;

public class EquipmentFactory {

    private static final Faker FAKER = Faker.instance();

    public static Equipment newEquipmentEntity() {
                return Equipment
                .builder()
                .id(FAKER.internet().uuid())
                .sku(FAKER.lorem().fixedString(8))
                .name(FAKER.commerce().productName())
                .description(FAKER.lorem().sentence())
                .price(BigDecimal.valueOf(FAKER.random().nextInt(12, 100)))
                .quantity(FAKER.random().nextInt(1, 1000))
                .stockIdentifier(FAKER.internet().uuid())
                .taskMaster(TaskMasterFactory.newTaskMasterEntity())
                .build();
    }

    public static EquipmentDocument newEquipmentDocument() {
        return new EquipmentDocument(
                FAKER.internet().uuid(),
                FAKER.lorem().fixedString(8),
                FAKER.lorem().characters(),
                FAKER.lorem().sentence(),
                BigDecimal.valueOf(FAKER.random().nextInt(12, 100)),
                FAKER.random().nextInt(1, 1000),
                FAKER.internet().uuid(),
                TaskMasterFactory.newTaskMasterDocument()
        );
    }

    public static EquipmentSaveDTO newSaveEquipmentDto() {
        return new EquipmentSaveDTO(
                FAKER.lorem().characters(),
                FAKER.lorem().fixedString(8),
                FAKER.lorem().sentence(),
                BigDecimal.valueOf(FAKER.random().nextInt(12, 100)),
                FAKER.random().nextInt(1, 1000),
                FAKER.internet().uuid(),
                FAKER.internet().uuid()
        );
    }

    public static EquipmentUpdateDTO newUpdateEquipmentDto() {
        return new EquipmentUpdateDTO(
                FAKER.lorem().characters(),
                FAKER.lorem().fixedString(8),
                FAKER.lorem().sentence(),
                BigDecimal.valueOf(FAKER.random().nextInt(12, 100)),
                FAKER.random().nextInt(1, 1000),
                FAKER.internet().uuid(),
                FAKER.internet().uuid()
        );
    }

    public static EquipmentUpdateDTO newUpdateEquipmentDtoWithoutTaskMasterId() {
        return new EquipmentUpdateDTO(
                FAKER.lorem().characters(),
                FAKER.lorem().fixedString(8),
                FAKER.lorem().sentence(),
                BigDecimal.valueOf(FAKER.random().nextInt(12, 100)),
                FAKER.random().nextInt(1, 1000),
                FAKER.internet().uuid(),
                null
        );
    }

    public static EquipmentFindDTO newFindEquipmentsDto() {
        return new EquipmentFindDTO(
                FAKER.lorem().characters(),
                FAKER.random().nextInt(2, 10),
                FAKER.internet().uuid()
        );
    }
}
