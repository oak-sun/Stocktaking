package nam.gor.stocktaking.infrastructure.serializers;

import nam.gor.stocktaking.infrastucture.documents.TaskMasterDocument;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.domain.factories.TaskMasterFactory;
import nam.gor.stocktaking.infrastucture.serializers.impl.TaskMasterSerialImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskMasterSerializerTest {

    private final TaskMasterSerialImpl serializer = new TaskMasterSerialImpl();

    @Nested
    @DisplayName("method: fromDocument(TaskMasterDocument)")
    class FromDocumentMethod {

        @Test
        @DisplayName(
                "when called, then it should " +
                "convert from TaskMasterDocument to domain")
        void whenCalled_shouldConvertFromTaskMasterDocumentToDomain() {
            final TaskMasterDocument document = TaskMasterFactory.newTaskMasterDocument();
            final TaskMaster expected = TaskMaster
                    .builder()
                    .id(document.getId())
                    .firstName(document.getFirstName())
                    .lastName(document.getLastName())
                    .workContractNumber(document.getWorkContractNumber())
                    .objectName(document.getObjectName())
                    .teamNumber(document.getTeamNumber())
                    .build();
            final TaskMaster actual = serializer.fromDocument(document);
            assertThat(expected).isEqualTo(actual);
        }
    }

    @Nested
    @DisplayName("method: toDocument(TaskMasterOutDTO)")
    class ToDocumentMethod {

        @Test
        @DisplayName(
                "when called, then it should " +
                "convert from TaskMasterOutDTO to document")
        void whenCalled_shouldConvertFromTaskMasterToDocument() {
            final TaskMaster master = TaskMasterFactory.newTaskMasterEntity();
            final TaskMasterDocument expected = new TaskMasterDocument(
                    master.getId(),
                    master.getFirstName(),
                    master.getLastName(),
                    master.getWorkContractNumber(),
                    master.getObjectName(),
                    master.getTeamNumber());
            final TaskMasterDocument actual = serializer.toDocument(master);
            assertThat(actual).isEqualTo(expected);
        }
    }
}