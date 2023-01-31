package nam.gor.stocktaking.api.dto.stockkeepers;

import com.github.javafaker.Faker;
import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperUpdateDTO;
import nam.gor.stocktaking.domain.entities.StockKeeper;
import nam.gor.stocktaking.domain.factories.StockKeeperFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StockKeeperUpdateDTOTest {


    @Nested
    @DisplayName("method: fromEntityToEntity(StockKeeperOutDTO)")
    class FromEntityToEntityMethod {
        final StockKeeper KEEPER = StockKeeperFactory
                .newStockKeeperEntity();
        private final Faker FAKER = Faker.instance();

        @Test
        @DisplayName(
                "when no field is present, " +
                "then it should not update anything")
        void whenNoFieldIsPresent_shouldNotUpdateAnything() {
            final StockKeeperUpdateDTO dto = new StockKeeperUpdateDTO(
                    null,
                    null,
                    null);
            assertThat(dto.fromEntityToEntity(KEEPER))
                    .isEqualTo(KEEPER);
        }

        @Test
        @DisplayName(
                "when all fields are filled, " +
                "then it should return new taskmaster" +
                " with the given fields")
        void whenAllFieldsAreFilled_shouldReturnNewStockKeeperWithTheGivenFields() {
            final String firstName = FAKER.lorem().characters();
            final String lastName = FAKER.lorem().characters();
            final Long workContractNumber = FAKER.random().nextLong(100_010_111L);
            final var dto = new StockKeeperUpdateDTO(
                    firstName,
                    lastName,
                    workContractNumber
            );
            final var expectedEq = KEEPER.toBuilder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .workContractNumber(workContractNumber)
                    .build();
            final var actualMaster = dto.fromEntityToEntity(KEEPER);
            assertThat(actualMaster).isEqualTo(expectedEq);
        }

        @Test
        @DisplayName(
                "when only the first name field is present," +
                " then it should only update it")
        void whenOnlyTheFirstNameFieldIsPresent_shouldOnlyUpdateIt() {
            final String firstName = UUID.randomUUID().toString();
            final StockKeeperUpdateDTO dto = new StockKeeperUpdateDTO(
                    firstName,
                    null,
                    null
            );
            final StockKeeper expected = KEEPER
                    .toBuilder()
                    .firstName(firstName).build();
            final StockKeeper actual = dto.fromEntityToEntity(KEEPER);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName
                ("when only the last name field is present," +
                " then it should only update it")
        void whenOnlyTheLastNameFieldIsPresent_shouldOnlyUpdateIt() {
            final String lastName = UUID.randomUUID().toString();
            final StockKeeperUpdateDTO dto = new StockKeeperUpdateDTO(
                    null,
                    lastName,
                    null
            );
            final StockKeeper expected = KEEPER
                    .toBuilder()
                    .lastName(lastName).build();
            final StockKeeper actual = dto.fromEntityToEntity(KEEPER);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName(
                "when only the work contract number" +
                " field is present," +
                " then it should only update it")
        void whenOnlyTheWorkContractNumberFieldIsPresent_shouldOnlyUpdateIt() {
            final Long workContractNumber = FAKER.random().nextLong(100_000_000L);
            final StockKeeperUpdateDTO dto = new StockKeeperUpdateDTO(
                    null,
                    null,
                    workContractNumber
            );
            final StockKeeper expected = KEEPER
                    .toBuilder()
                    .workContractNumber(workContractNumber).build();
            final StockKeeper actual = dto.fromEntityToEntity(KEEPER);
            assertThat(actual).isEqualTo(expected);
        }

    }
}
