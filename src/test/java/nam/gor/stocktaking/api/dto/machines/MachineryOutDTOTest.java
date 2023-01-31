package nam.gor.stocktaking.api.dto.machines;

import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterOutDTO;
import nam.gor.stocktaking.domain.entities.Machinery;
import nam.gor.stocktaking.domain.entities.Stock;
import nam.gor.stocktaking.domain.factories.MachineryFactory;
import nam.gor.stocktaking.domain.factories.StockFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MachineryOutDTOTest {

    @Nested
    @DisplayName("method: toDto(MachineryOutDTO, Stock)")
    class ToDtoMethod {

        @Test
        @DisplayName(
                "when called, it should" +
                " convert from entity to dto")
        void whenCalled_shouldConvertFromEntityToDto() {
            final var mEntity = MachineryFactory
                    .newMachineryEntity();
            final var stEntity = StockFactory
                    .newStockEntity();
            final var expectedDto = saveDtoFromEntity(
                    mEntity, stEntity);
            final var actualDto = MachineryOutDTO
                    .toDto(mEntity, stEntity);
            assertThat(actualDto).isEqualTo(expectedDto);
        }

        private MachineryOutDTO saveDtoFromEntity(final Machinery machinery,
                                                  final Stock stock) {
            return new MachineryOutDTO(
                    machinery.getId(),
                    machinery.getSku(),
                    machinery.getName(),
                    machinery.getDescription(),
                    machinery.getPrice(),
                    machinery.getQuantity(),
                    stock.getPreSignedUrl(),
                    TaskMasterOutDTO
                            .toDto(machinery.getTaskMaster())

            );
        }
    }
}
