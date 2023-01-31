package nam.gor.stocktaking.api.dto.machines;

import nam.gor.stocktaking.domain.entities.Machinery;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.domain.factories.MachineryFactory;
import nam.gor.stocktaking.domain.factories.TaskMasterFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MachinerySaveDTOTest {

    @Nested
    @DisplayName("method: toEntity(String)")
    class ToEntityMethod {
        private final String MACHINERY_ID = UUID.randomUUID().toString();
        private final TaskMaster TASK_MASTER = TaskMasterFactory
                .newTaskMasterEntity();

        @Test
        @DisplayName(
                "when called, then it should " +
                "convert from MachinerySaveDTO " +
                "to MachineryOutDTO")
        void whenCalled_shouldConvertFromMachinerySaveDTOToMachinery() {
            final MachinerySaveDTO dto = MachineryFactory.newSaveMachineryDto();
            final Machinery expectedM = saveMachineryFromDto(dto);
            final Machinery actualM= dto.toEntity(MACHINERY_ID, TASK_MASTER);
            assertThat(actualM).isEqualTo(expectedM);
        }

        private Machinery saveMachineryFromDto(final MachinerySaveDTO dto) {
            return Machinery
                    .builder()
                    .id(MACHINERY_ID)
                    .sku(dto.getSku())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .price(dto.getPrice())
                    .quantity(dto.getQuantity())
                    .stockIdentifier(dto.getStockIdentifier())
                    .taskMaster(TASK_MASTER)
                    .build();
        }
    }
}
