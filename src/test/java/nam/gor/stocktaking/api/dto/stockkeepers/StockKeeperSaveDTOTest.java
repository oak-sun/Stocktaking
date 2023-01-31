package nam.gor.stocktaking.api.dto.stockkeepers;

import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperSaveDTO;
import nam.gor.stocktaking.domain.entities.StockKeeper;
import nam.gor.stocktaking.domain.factories.StockKeeperFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class StockKeeperSaveDTOTest {

    @Nested
    @DisplayName("method: byIdToEntity(String)")
    class ByIdToEntityMethod {

        @Test
        @DisplayName(
                "when called, then it should " +
                "create the correct entity object")
        void whenCalled_shouldSaveTheCorrectEntityObject() {
            final String stockKeeperId = UUID.randomUUID().toString();
            final StockKeeperSaveDTO dto = StockKeeperFactory
                    .newSaveStockKeeper();
            final StockKeeper expected = StockKeeper
                    .builder()
                    .id(stockKeeperId)
                    .firstName(dto.getFirstName())
                    .lastName(dto.getLastName())
                    .workContractNumber(dto.getWorkContractNumber())
                    .build();
            final StockKeeper actual = dto.byIdToEntity(stockKeeperId);
            assertThat(actual).isEqualTo(expected);
        }
    }
}
