package nam.gor.stocktaking.api.dto.equipments;

import nam.gor.stocktaking.api.dto.equipment.EquipmentSaveDTO;
import nam.gor.stocktaking.domain.entities.Equipment;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.domain.factories.EquipmentFactory;
import nam.gor.stocktaking.domain.factories.TaskMasterFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EquipmentSaveDTOTest {

    @Nested
    @DisplayName("method: toEntity(String)")
    class ToEntityMethod {
        private final String EQUIPMENT_ID = UUID.randomUUID().toString();
        private final TaskMaster TASK_MASTER = TaskMasterFactory
                .newTaskMasterEntity();

        @Test
        @DisplayName(
                "when called, then it should " +
                "convert from EquipmentSaveDTO " +
                "to EquipmentOutDTO")
        void whenCalled_shouldConvertFromEquipmentSaveDTOToEquipment() {
            final EquipmentSaveDTO dto = EquipmentFactory.newSaveEquipmentDto();
            final Equipment expectedEq = saveEquipmentFromDto(dto);
            final Equipment actualEq= dto.toEntity(EQUIPMENT_ID, TASK_MASTER);
            assertThat(actualEq).isEqualTo(expectedEq);
        }

        private Equipment saveEquipmentFromDto(final EquipmentSaveDTO dto) {
            return Equipment
                    .builder()
                    .id(EQUIPMENT_ID)
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