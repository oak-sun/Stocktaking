package nam.gor.stocktaking.infrastucture.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stock_keepers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockKeeperDocument {

    @Id private String id;
    private String firstName;
    private String lastName;
    private Long workContractNumber;
}
