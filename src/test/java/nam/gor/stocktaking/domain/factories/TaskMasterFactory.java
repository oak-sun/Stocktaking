package nam.gor.stocktaking.domain.factories;

import com.github.javafaker.Faker;
import nam.gor.stocktaking.infrastucture.documents.TaskMasterDocument;
import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterSaveDTO;
import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterUpdateDTO;
import nam.gor.stocktaking.domain.entities.TaskMaster;

public class TaskMasterFactory {
    private static final Faker FAKER = Faker.instance();

    public static TaskMaster newTaskMasterEntity() {
        return TaskMaster
                .builder()
                .id(FAKER.internet().uuid())
                .firstName(FAKER.lorem().characters())
                .lastName(FAKER.lorem().characters())
                .workContractNumber(FAKER.random().nextLong(100_000_000L))
                .objectName(FAKER.commerce().department())
                .teamNumber(FAKER.random().nextLong(100_000L))
                .build();
    }

    public static TaskMasterDocument newTaskMasterDocument() {
        return new TaskMasterDocument(
                FAKER.internet().uuid(),
                FAKER.lorem().characters(),
                FAKER.lorem().characters(),
                FAKER.random().nextLong(100_000_000L),
                FAKER.commerce().department(),
                FAKER.random().nextLong(100_000L)

        );
    }

    public static TaskMasterSaveDTO newSaveTaskMaster() {
        return new TaskMasterSaveDTO(
                FAKER.lorem().characters(),
                FAKER.lorem().characters(),
                FAKER.random().nextLong(100_000_000L),
                FAKER.commerce().department(),
                FAKER.random().nextLong(100_000L)
        );
    }

    public static TaskMasterUpdateDTO newUpdateTaskMaster() {
        return new TaskMasterUpdateDTO(
                FAKER.lorem().characters(),
                FAKER.lorem().characters(),
                FAKER.random().nextLong(100_000_000L),
                FAKER.commerce().department(),
                FAKER.random().nextLong(100_000L)
        );
    }
}
