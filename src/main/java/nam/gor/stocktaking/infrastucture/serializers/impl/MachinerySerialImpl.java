package nam.gor.stocktaking.infrastucture.serializers.impl;

import lombok.AllArgsConstructor;
import nam.gor.stocktaking.infrastucture.documents.MachineryDocument;
import nam.gor.stocktaking.infrastucture.documents.TaskMasterDocument;
import nam.gor.stocktaking.domain.entities.Machinery;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.infrastucture.serializers.Serializer;
import org.springframework.stereotype.Component;

    @AllArgsConstructor
    @Component
    public class MachinerySerialImpl implements Serializer<Machinery, MachineryDocument> {
        private final Serializer<TaskMaster, TaskMasterDocument> serializer;

        @Override
        public Machinery fromDocument(final MachineryDocument document) {
            return Machinery
                    .builder()
                    .id(document.getId())
                    .sku(document.getSku())
                    .name(document.getName())
                    .description(document.getDescription())
                    .price(document.getPrice())
                    .quantity(document.getQuantity())
                    .stockIdentifier(document.getImageIdentifier())
                    .taskMaster(serializer.fromDocument(document.getTaskmaster()))
                    .build();
        }

        @Override
        public MachineryDocument toDocument(final Machinery machinery) {
            return new MachineryDocument(
                    machinery.getId(),
                    machinery.getSku(),
                    machinery.getName(),
                    machinery.getDescription(),
                    machinery.getPrice(),
                    machinery.getQuantity(),
                    machinery.getStockIdentifier(),
                    serializer.toDocument(machinery.getTaskMaster())
            );
        }
    }

