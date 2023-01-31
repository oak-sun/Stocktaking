package nam.gor.stocktaking.infrastucture.serializers.impl;

import nam.gor.stocktaking.infrastucture.documents.TaskMasterDocument;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.infrastucture.serializers.Serializer;
import org.springframework.stereotype.Component;

@Component
public class TaskMasterSerialImpl implements Serializer<TaskMaster,
        TaskMasterDocument> {

    @Override
    public TaskMaster fromDocument(final TaskMasterDocument document) {
        return TaskMaster
                .builder()
                .id(document.getId())
                .firstName(document.getFirstName())
                .lastName(document.getLastName())
                .workContractNumber(document.getWorkContractNumber())
                .objectName(document.getObjectName())
                .teamNumber(document.getTeamNumber())
                .build();
    }

    @Override
    public TaskMasterDocument toDocument(final TaskMaster master) {
        return new TaskMasterDocument(master.getId(),
                                      master.getFirstName(),
                                      master.getLastName(),
                                      master.getWorkContractNumber(),
                                      master.getObjectName(),
                                      master.getTeamNumber());
    }
}
