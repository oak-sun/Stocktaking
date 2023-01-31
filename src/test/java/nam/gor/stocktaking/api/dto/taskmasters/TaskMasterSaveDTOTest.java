package nam.gor.stocktaking.api.dto.taskmasters;

import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterSaveDTO;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.domain.factories.TaskMasterFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class TaskMasterSaveDTOTest {

    @Nested
    @DisplayName("method: byIdToEntity(String)")
    class ByIdToEntityMethod {

        @Test
        @DisplayName(
                "when called, then it should " +
                "create the correct entity object")
        void whenCalled_shouldSaveTheCorrectEntityObject() {
            final String taskMasterId = UUID.randomUUID().toString();
            final TaskMasterSaveDTO dto = TaskMasterFactory
                    .newSaveTaskMaster();
            final TaskMaster expected = TaskMaster
                    .builder()
                    .id(taskMasterId)
                    .firstName(dto.getFirstName())
                    .lastName(dto.getLastName())
                    .workContractNumber(dto.getWorkContractNumber())
                    .objectName(dto.getObjectName())
                    .teamNumber(dto.getTeamNumber())
                    .build();
            final TaskMaster actual = dto.byIdToEntity(taskMasterId);
            assertThat(actual).isEqualTo(expected);
        }
    }
}