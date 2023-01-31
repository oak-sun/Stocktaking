package nam.gor.stocktaking.infrastructure.serializers;

import nam.gor.stocktaking.infrastucture.documents.EquipmentDocument;
import nam.gor.stocktaking.domain.entities.Equipment;
import nam.gor.stocktaking.domain.factories.EquipmentFactory;
import nam.gor.stocktaking.infrastucture.serializers.impl.EquipmentSerialImpl;
import nam.gor.stocktaking.infrastucture.serializers.impl.TaskMasterSerialImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EquipmentSerializerTest {
    private static final TaskMasterSerialImpl tmSerial = new TaskMasterSerialImpl();
    private static final EquipmentSerialImpl eqSerial = new EquipmentSerialImpl(tmSerial);

    @Nested
    @DisplayName("method: fromDocument(EquipmentDocument)")
    class FromDocumentMethod {

        @Test
        @DisplayName(
                "when called, then it should " +
                "convert from EquipmentDocument to EquipmentOutDTO")
        void whenCalled_shouldConvertFromEquipmentDocumentToEquipment() {
            final EquipmentDocument document = EquipmentFactory.newEquipmentDocument();
            final Equipment expectedEq = saveEntityFromDocument(document);
            final Equipment actualEq = eqSerial.fromDocument(document);
            assertThat(actualEq).isEqualTo(expectedEq);
        }

        private Equipment saveEntityFromDocument(final EquipmentDocument document) {
            return Equipment
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
                "convert from EquipmentOutDTO to EquipmentDocument")
        void whenCalled_shouldConvertFromEquipmentToEquipmentDocument() {
            final Equipment entity = EquipmentFactory.newEquipmentEntity();
            final EquipmentDocument expectedDoc = saveDocumentFromEntity(entity);
            final EquipmentDocument actualDoc = eqSerial.toDocument(entity);
            assertThat(actualDoc).isEqualTo(expectedDoc);
        }

        private EquipmentDocument saveDocumentFromEntity(final Equipment entity) {
            return new EquipmentDocument(
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