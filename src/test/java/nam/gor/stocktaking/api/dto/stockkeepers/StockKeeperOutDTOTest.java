package nam.gor.stocktaking.api.dto.stockkeepers;

import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperOutDTO;
import nam.gor.stocktaking.domain.factories.StockKeeperFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class StockKeeperOutDTOTest {

    @Nested
    @DisplayName("method: toDto(StockKeeperOutDTO)")
    class ToDtoMethod {

        @Test
        @DisplayName(
                "when called, then it should" +
                        " convert from entity to dto")
        void whenCalled_shouldConvertFromEntityToDto() {
            final var entity = StockKeeperFactory
                    .newStockKeeperEntity();
            final StockKeeperOutDTO expected = new StockKeeperOutDTO(
                    entity.getId(),
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getWorkContractNumber()
            );
            final StockKeeperOutDTO actual = StockKeeperOutDTO.toDto(entity);
            assertThat(actual).isEqualTo(expected);
        }
    }
}
