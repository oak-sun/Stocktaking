package nam.gor.stocktaking.domain.factories;


import com.github.javafaker.Faker;
import nam.gor.stocktaking.domain.entities.Stock;

public class StockFactory {

    private static final Faker FAKER = Faker.instance();

    public static Stock newStockEntity() {
        return Stock
                .builder()
                .identifier(FAKER.internet().uuid())
                .preSignedUrl(FAKER.internet().url())
                .build();
    }
}
