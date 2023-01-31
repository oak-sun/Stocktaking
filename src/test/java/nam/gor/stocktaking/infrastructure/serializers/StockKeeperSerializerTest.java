package nam.gor.stocktaking.infrastructure.serializers;

import nam.gor.stocktaking.infrastucture.documents.StockKeeperDocument;
import nam.gor.stocktaking.domain.entities.StockKeeper;
import nam.gor.stocktaking.domain.factories.StockKeeperFactory;
import nam.gor.stocktaking.infrastucture.serializers.impl.StockKeeperSerialImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StockKeeperSerializerTest {

    private final StockKeeperSerialImpl serializer = new StockKeeperSerialImpl();

    @Nested
    @DisplayName("method: fromDocument(StockKeeperDocument)")
    class FromDocumentMethod {

        @Test
        @DisplayName(
                "when called, then it should " +
                "convert from StockKeeperDocument to domain")
        void whenCalled_shouldConvertFromStockKeeperDocumentToDomain() {
            final StockKeeperDocument document = StockKeeperFactory
                    .newStockKeeperDocument();
            final StockKeeper expected = StockKeeper
                    .builder()
                    .id(document.getId())
                    .firstName(document.getFirstName())
                    .lastName(document.getLastName())
                    .workContractNumber(document.getWorkContractNumber())
                    .build();
            final StockKeeper actual = serializer.fromDocument(document);
            assertThat(expected).isEqualTo(actual);
        }
    }

    @Nested
    @DisplayName("method: toDocument(StockKeeperOutDTO)")
    class ToDocumentMethod {

        @Test
        @DisplayName(
                "when called, then it should " +
                "convert from StockKeeperOutDTO to document")
        void whenCalled_shouldConvertFromStockKeeperToDocument() {
            final StockKeeper master = StockKeeperFactory.newStockKeeperEntity();
            final StockKeeperDocument expected = new StockKeeperDocument(
                    master.getId(),
                    master.getFirstName(),
                    master.getLastName(),
                    master.getWorkContractNumber());
            final StockKeeperDocument actual = serializer.toDocument(master);
            assertThat(actual).isEqualTo(expected);
        }
    }
}
