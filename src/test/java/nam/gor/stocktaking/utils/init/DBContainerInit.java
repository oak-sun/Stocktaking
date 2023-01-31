package nam.gor.stocktaking.utils.init;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MongoDBContainer;

import java.util.Map;

@ContextConfiguration
public class DBContainerInit implements
                      ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final String MONGO_IMAGE = "mongo";

    private static final MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(MONGO_IMAGE);

    static {
        MONGO_CONTAINER.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        final PropertySource<Map<String, Object>> properties = new MapPropertySource(
                "DBContainerInit",
                Map.of(
                        "spring.data.mongodb.uri",
                        MONGO_CONTAINER.getReplicaSetUrl("test")
                )
        );
        context
                .getEnvironment()
                .getPropertySources()
                .addFirst(properties);
    }
}

