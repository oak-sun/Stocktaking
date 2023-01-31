package nam.gor.stocktaking.api.dto;

import nam.gor.stocktaking.domain.entities.Stock;
import nam.gor.stocktaking.domain.entities.StockKeeper;
import nam.gor.stocktaking.domain.factories.StockKeeperFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StockOutDTOTest {

    @Nested
    @DisplayName("method: toDto(Stock)")
    class ToDtoMethod {

        @Test
        @DisplayName(
                "when called, then it should " +
                "convert from entity to dto")
        void whenCalled_shouldConvertFromEntityToDto() {
            final String identifier = UUID.randomUUID().toString();
            final String preSignedUrl = UUID.randomUUID().toString();
            final StockKeeper keeper = StockKeeperFactory.newStockKeeperEntity();
            final var entity = Stock
                    .builder()
                    .identifier(identifier)
                    .preSignedUrl(preSignedUrl)
                    .stockKeeper(keeper)
                    .build();
            final StockOutDTO expected = new StockOutDTO(
                    identifier,
                    preSignedUrl,
                    keeper);
            final StockOutDTO actual = StockOutDTO.toDto(entity);
            assertThat(actual).isEqualTo(expected);
        }
    }
}