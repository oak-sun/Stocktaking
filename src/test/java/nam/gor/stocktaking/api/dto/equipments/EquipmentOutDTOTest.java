package nam.gor.stocktaking.api.dto.equipments;

import nam.gor.stocktaking.api.dto.equipment.EquipmentOutDTO;
import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterOutDTO;
import nam.gor.stocktaking.domain.entities.Equipment;
import nam.gor.stocktaking.domain.entities.Stock;
import nam.gor.stocktaking.domain.factories.EquipmentFactory;

import nam.gor.stocktaking.domain.factories.StockFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EquipmentOutDTOTest {

    @Nested
    @DisplayName("method: toDto(EquipmentOutDTO, Stock)")
    class ToDtoMethod {

        @Test
        @DisplayName(
                "when called, it should" +
                " convert from entity to dto")
        void whenCalled_shouldConvertFromEntityToDto() {
            final var eqEntity = EquipmentFactory
                    .newEquipmentEntity();
            final var stEntity = StockFactory
                    .newStockEntity();
            final var expectedDto = saveDtoFromEntity(
                    eqEntity, stEntity);
            final var actualDto = EquipmentOutDTO
                    .toDto(eqEntity, stEntity);
            assertThat(actualDto).isEqualTo(expectedDto);
        }

        private EquipmentOutDTO saveDtoFromEntity(final Equipment equipment,
                                                   final Stock stock) {
            return new EquipmentOutDTO(
                    equipment.getId(),
                    equipment.getSku(),
                    equipment.getName(),
                    equipment.getDescription(),
                    equipment.getPrice(),
                    equipment.getQuantity(),
                    stock.getPreSignedUrl(),
                    TaskMasterOutDTO
                            .toDto(equipment.getTaskMaster())

            );
        }
    }
}