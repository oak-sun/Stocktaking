package nam.gor.stocktaking.utils;

import nam.gor.stocktaking.infrastucture.documents.EquipmentDocument;
import nam.gor.stocktaking.infrastucture.documents.MachineryDocument;
import nam.gor.stocktaking.infrastucture.documents.StockKeeperDocument;
import nam.gor.stocktaking.infrastucture.documents.TaskMasterDocument;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DBRollbackExtension implements BeforeAllCallback, AfterEachCallback {

    @Override
    public void beforeAll(final ExtensionContext context) {
        cleanTables(SpringExtension
                .getApplicationContext(context));
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        cleanTables(SpringExtension
                .getApplicationContext(context));
    }

    private void cleanTables(ApplicationContext context) {
        final var template = context
                .getBean(ReactiveMongoTemplate.class);
        template
                .dropCollection(TaskMasterDocument.class)
                .block();
        template
                .dropCollection(EquipmentDocument.class)
                .block();

        template
                .dropCollection(StockKeeperDocument.class)
                .block();
        template
                .dropCollection(MachineryDocument.class)
                .block();
    }
}