package nam.gor.stocktaking.infrastucture.documents;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "machines")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MachineryDocument {

    @Id
    private String id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String imageIdentifier;
    private TaskMasterDocument taskmaster;
}
