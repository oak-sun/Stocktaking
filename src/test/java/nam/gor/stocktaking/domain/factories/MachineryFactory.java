package nam.gor.stocktaking.domain.factories;

import com.github.javafaker.Faker;
import nam.gor.stocktaking.api.dto.machines.MachineryFindDTO;
import nam.gor.stocktaking.api.dto.machines.MachinerySaveDTO;
import nam.gor.stocktaking.api.dto.machines.MachineryUpdateDTO;
import nam.gor.stocktaking.infrastucture.documents.MachineryDocument;

import nam.gor.stocktaking.domain.entities.Machinery;

import java.math.BigDecimal;

public class MachineryFactory {

    private static final Faker FAKER = Faker.instance();

    public static Machinery newMachineryEntity() {
        return Machinery
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

    public static MachineryDocument newMachineryDocument() {
        return new MachineryDocument(
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

    public static MachinerySaveDTO newSaveMachineryDto() {
        return new MachinerySaveDTO(
                FAKER.lorem().characters(),
                FAKER.lorem().fixedString(8),
                FAKER.lorem().sentence(),
                BigDecimal.valueOf(FAKER.random().nextInt(12, 100)),
                FAKER.random().nextInt(1, 1000),
                FAKER.internet().uuid(),
                FAKER.internet().uuid()
        );
    }

    public static MachineryUpdateDTO newUpdateMachineryDto() {
        return new MachineryUpdateDTO(
                FAKER.lorem().characters(),
                FAKER.lorem().fixedString(8),
                FAKER.lorem().sentence(),
                BigDecimal.valueOf(FAKER.random().nextInt(12, 100)),
                FAKER.random().nextInt(1, 1000),
                FAKER.internet().uuid(),
                FAKER.internet().uuid()
        );
    }

    public static MachineryUpdateDTO newUpdateMachineryDtoWithoutTaskMasterId() {
        return new MachineryUpdateDTO(
                FAKER.lorem().characters(),
                FAKER.lorem().fixedString(8),
                FAKER.lorem().sentence(),
                BigDecimal.valueOf(FAKER.random().nextInt(12, 100)),
                FAKER.random().nextInt(1, 1000),
                FAKER.internet().uuid(),
                null
        );
    }

    public static MachineryFindDTO newFindMachinesDto() {
        return new MachineryFindDTO(
                FAKER.lorem().characters(),
                FAKER.random().nextInt(2, 10),
                FAKER.internet().uuid()
        );
    }
}
