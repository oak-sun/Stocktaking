package nam.gor.stocktaking.api.dto.taskmasters;

import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterOutDTO;
import nam.gor.stocktaking.domain.factories.TaskMasterFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskMasterOutDTOTest {

    @Nested
    @DisplayName("method: toDto(TaskMasterOutDTO)")
    class ToDtoMethod {

        @Test
        @DisplayName(
                "when called, then it should" +
                " convert from entity to dto")
        void whenCalled_shouldConvertFromEntityToDto() {
            final var entity = TaskMasterFactory
                    .newTaskMasterEntity();
            final TaskMasterOutDTO expected = new TaskMasterOutDTO(
                    entity.getId(),
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getWorkContractNumber(),
                    entity.getObjectName(),
                    entity.getTeamNumber()
                    );
            final TaskMasterOutDTO actual = TaskMasterOutDTO.toDto(entity);
            assertThat(actual).isEqualTo(expected);
        }
    }
}