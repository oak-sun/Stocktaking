package nam.gor.stocktaking.infrastucture.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "taskmasters")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskMasterDocument {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private Long workContractNumber;
    private String objectName;
    private Long teamNumber;
}
