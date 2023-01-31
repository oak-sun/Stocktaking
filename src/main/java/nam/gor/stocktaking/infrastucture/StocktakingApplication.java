package nam.gor.stocktaking.infrastucture;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("nam.gor.stocktaking")
@OpenAPIDefinition(info = @Info(
                         title = "Stocktaking",
                         version = "1.0.0"))
public class StocktakingApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                StocktakingApplication.class, args);
    }
}
