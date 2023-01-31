package nam.gor.stocktaking.api.dto.machines;

import com.github.javafaker.Faker;
import nam.gor.stocktaking.domain.entities.Machinery;
import nam.gor.stocktaking.domain.factories.MachineryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MachineryUpdateDTOTest {

    @Nested
    @DisplayName("method: toEntity(MachineryOutDTO)")
    class ToEntityMethod {
        private final Machinery MACHINERY = MachineryFactory.newMachineryEntity();
        private final Faker FAKER = Faker.instance();


        @Test
        @DisplayName(
                "when all fields are filled, " + 
                "then it should return new machinery" + 
                " with the given fields")
        void whenAllFieldsAreFilled_shouldReturnNewMachineryWithTheGivenFields() {
            final String name = FAKER.lorem().characters();
            final String sku = FAKER.lorem().fixedString(8);
            final String description = FAKER.lorem().sentence();
            final BigDecimal price = BigDecimal.valueOf(FAKER.random().nextInt(1, 10));
            final int quantity = FAKER.random().nextInt(1, 100);
            final String stockIdentifier = FAKER.internet().uuid();
            final String taskMasterId = FAKER.internet().uuid();
            final var dto = new MachineryUpdateDTO(
                    name,
                    sku,
                    description,
                    price,
                    quantity,
                    stockIdentifier,
                    taskMasterId
            );
            final var expectedM = MACHINERY.toBuilder()
                    .name(name)
                    .sku(sku)
                    .description(description)
                    .price(price)
                    .quantity(quantity)
                    .stockIdentifier(stockIdentifier)
                    .build();
            final var actualM = dto.toEntity(MACHINERY);
            assertThat(actualM).isEqualTo(expectedM);
        }

        @Test
        @DisplayName(
                "when only the name is filled, " + 
                "then it should only update it")
        void whenOnlyTheNameIsFilled_shouldOnlyUpdateIt() {
            final String name = FAKER.lorem().characters();
            final var dto = new MachineryUpdateDTO(
                    name,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            final var expectedM = MACHINERY.toBuilder().name(name).build();
            final var actualM = dto.toEntity(MACHINERY);
            assertThat(actualM).isEqualTo(expectedM);
        }

        @Test
        @DisplayName(
                "when only the sku is filled, " +
                "then it should only update it")
        void whenOnlyTheSkuIsFilled_shouldOnlyUpdateIt() {
            final String sku = FAKER.lorem().fixedString(8);
            final var dto = new MachineryUpdateDTO(
                    null,
                    sku,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            final var expectedM = MACHINERY.toBuilder().sku(sku).build();
            final var actualM = dto.toEntity(MACHINERY);
            assertThat(actualM).isEqualTo(expectedM);
        }

        @Test
        @DisplayName(
                "when only the description is filled, " +
                "then it should only update it")
        void whenOnlyTheDescriptionIsFilled_shouldOnlyUpdateIt() {
            final String description = FAKER.lorem().sentence();
            final var dto = new MachineryUpdateDTO(
                    null,
                    null,
                    description,
                    null,
                    null,
                    null,
                    null
            );
            final var expectedM = MACHINERY
                    .toBuilder()
                    .description(description)
                    .build();
            final var actualM = dto.toEntity(MACHINERY);
            assertThat(actualM).isEqualTo(expectedM);
        }

        @Test
        @DisplayName(
                "when only the price is filled, " +
                "then it should only update it")
        void whenOnlyThePriceIsFilled_shouldOnlyUpdateIt() {
            final BigDecimal price = BigDecimal
                    .valueOf(FAKER.random().nextInt(1, 10));
            final var dto = new MachineryUpdateDTO(
                    null,
                    null,
                    null,
                    price,
                    null,
                    null,
                    null
            );
            final var expectedM = MACHINERY
                    .toBuilder()
                    .price(price).build();
            final var actualM = dto.toEntity(MACHINERY);
            assertThat(actualM).isEqualTo(expectedM);
        }

        @Test
        @DisplayName(
                "when only the quantity is filled, " +
                "then it should only update it")
        void whenOnlyTheQuantityIsFilled_shouldOnlyUpdateIt() {
            final int quantity = FAKER.random().nextInt(1, 10);
            final var dto = new MachineryUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    quantity,
                    null,
                    null
            );
            final var expectedM = MACHINERY
                    .toBuilder()
                    .quantity(quantity)
                    .build();
            final var actualM = dto.toEntity(MACHINERY);
            assertThat(actualM).isEqualTo(expectedM);
        }

        @Test
        @DisplayName(
                "when only the stockIdentifier is filled," +
                " then it should only update it")
        void whenOnlyTheStockIdentifierIsFilled_shouldOnlyUpdateIt() {
            final String stockIdentifier = FAKER.internet().uuid();
            final var dto = new MachineryUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    null,
                    stockIdentifier,
                    null
            );
            final var expectedM = MACHINERY
                    .toBuilder()
                    .stockIdentifier(stockIdentifier)
                    .build();
            final var actualM = dto.toEntity(MACHINERY);
            assertThat(actualM).isEqualTo(expectedM);
        }

        @Test
        @DisplayName(
                "when only the taskMasterId is filled, " +
                "then it should not update nothing")
        void whenOnlyTheTaskMasterIdIsFilled_shouldNotUpdateNothing() {
            final var dto = new MachineryUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    FAKER.internet().uuid()
            );
            final var actualM = dto.toEntity(MACHINERY);
            assertThat(actualM).isEqualTo(MACHINERY);
        }
    }
}
