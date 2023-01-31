package nam.gor.stocktaking.api.dto.stockkeeper;

import lombok.Value;
import nam.gor.stocktaking.domain.entities.StockKeeper;

@Value
public class StockKeeperOutDTO {
        String id;
        String firstName;
        String lastName;
        Long workContractNumber;

        public static StockKeeperOutDTO toDto(final StockKeeper keeper) {
            return new StockKeeperOutDTO(
                    keeper.getId(),
                    keeper.getFirstName(),
                    keeper.getLastName(),
                    keeper.getWorkContractNumber());
        }
    }

