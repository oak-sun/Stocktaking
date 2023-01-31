package nam.gor.stocktaking.domain.factories;

import com.github.javafaker.Faker;
import nam.gor.stocktaking.infrastucture.documents.StockKeeperDocument;
import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperSaveDTO;
import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperUpdateDTO;
import nam.gor.stocktaking.domain.entities.StockKeeper;

public class StockKeeperFactory {
    private static final Faker FAKER = Faker.instance();

    public static StockKeeper newStockKeeperEntity() {
        return StockKeeper
                .builder()
                .id(FAKER.internet().uuid())
                .firstName(FAKER.lorem().characters())
                .lastName(FAKER.lorem().characters())
                .workContractNumber(FAKER.random().nextLong(100_000_000L))
                .build();
    }

    public static StockKeeperDocument newStockKeeperDocument() {
        return new StockKeeperDocument(
                FAKER.internet().uuid(),
                FAKER.lorem().characters(),
                FAKER.lorem().characters(),
                FAKER.random().nextLong(100_000_000L)
        );
    }

    public static StockKeeperSaveDTO newSaveStockKeeper() {
        return new StockKeeperSaveDTO(
                FAKER.lorem().characters(),
                FAKER.lorem().characters(),
                FAKER.random().nextLong(100_000_000L)
        );
    }

    public static StockKeeperUpdateDTO newUpdateStockKeeper() {
        return new StockKeeperUpdateDTO(
                FAKER.lorem().characters(),
                FAKER.lorem().characters(),
                FAKER.random().nextLong(100_000_000L)
        );
    }
}
