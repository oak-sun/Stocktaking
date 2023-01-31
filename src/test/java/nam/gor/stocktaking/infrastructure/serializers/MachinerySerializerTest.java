package nam.gor.stocktaking.infrastructure.serializers;

import nam.gor.stocktaking.infrastucture.documents.MachineryDocument;
import nam.gor.stocktaking.domain.entities.Machinery;
import nam.gor.stocktaking.domain.factories.MachineryFactory;
import nam.gor.stocktaking.infrastucture.serializers.impl.MachinerySerialImpl;
import nam.gor.stocktaking.infrastucture.serializers.impl.TaskMasterSerialImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MachinerySerializerTest {
    private static final TaskMasterSerialImpl tmSerial = new TaskMasterSerialImpl();
    private static final MachinerySerialImpl mchSerial = new MachinerySerialImpl(tmSerial);

    @Nested
    @DisplayName("method: fromDocument(MachineryDocument)")
    class FromDocumentMethod {

        @Test
        @DisplayName(
                "when called, then it should " +
                "convert from MachineryDocument to MachineryOutDTO")
        void whenCalled_shouldConvertFromMachineryDocumentToMachinery() {
            final MachineryDocument document = MachineryFactory.newMachineryDocument();
            final Machinery expectedEq = saveEntityFromDocument(document);
            final Machinery actualEq = mchSerial.fromDocument(document);
            assertThat(actualEq).isEqualTo(expectedEq);
        }

        private Machinery saveEntityFromDocument(final MachineryDocument document) {
            return Machinery
                    .builder()
                    .id(document.getId())
                    .sku(document.getSku())
                    .name(document.getName())
                    .description(document.getDescription())
                    .price(document.getPrice())
                    .quantity(document.getQuantity())
                    .stockIdentifier(document.getImageIdentifier())
                    .taskMaster(tmSerial.fromDocument(document.getTaskmaster()))
                    .build();
        }
    }

    @Nested
    @DisplayName("method: toDocument()")
    class ToDocumentMethod {

        @Test
        @DisplayName(
                "when called, then it should " +
                "convert from MachineryOutDTO to MachineryDocument")
        void whenCalled_shouldConvertFromMachineryToMachineryDocument() {
            final Machinery entity = MachineryFactory.newMachineryEntity();
            final MachineryDocument expectedDoc = saveDocumentFromEntity(entity);
            final MachineryDocument actualDoc = mchSerial.toDocument(entity);
            assertThat(actualDoc).isEqualTo(expectedDoc);
        }

        private MachineryDocument saveDocumentFromEntity(final Machinery entity) {
            return new MachineryDocument(
                    entity.getId(),
                    entity.getSku(),
                    entity.getName(),
                    entity.getDescription(),
                    entity.getPrice(),
                    entity.getQuantity(),
                    entity.getStockIdentifier(),
                    tmSerial.toDocument(entity.getTaskMaster())
            );
        }
    }
}
