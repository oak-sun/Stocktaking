package nam.gor.stocktaking.api.dto.equipments;

import com.github.javafaker.Faker;
import nam.gor.stocktaking.api.dto.equipment.EquipmentUpdateDTO;
import nam.gor.stocktaking.domain.entities.Equipment;
import nam.gor.stocktaking.domain.factories.EquipmentFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class EquipmentUpdateDTOTest {

    @Nested
    @DisplayName("method: toEntity(EquipmentOutDTO)")
    class ToEntityMethod {
        private final Equipment EQUIPMENT = EquipmentFactory.newEquipmentEntity();
        private final Faker FAKER = Faker.instance();


        @Test
        @DisplayName(
                "when all fields are filled, " +
                "then it should return new equipment" +
                " with the given fields")
        void whenAllFieldsAreFilled_shouldReturnNewEquipmentWithTheGivenFields() {
            final String name = FAKER.lorem().characters();
            final String sku = FAKER.lorem().fixedString(8);
            final String description = FAKER.lorem().sentence();
            final BigDecimal price = BigDecimal.valueOf(FAKER.random().nextInt(1, 10));
            final int quantity = FAKER.random().nextInt(1, 100);
            final String stockIdentifier = FAKER.internet().uuid();
            final String taskMasterId = FAKER.internet().uuid();
            final var dto = new EquipmentUpdateDTO(
                    name,
                    sku,
                    description,
                    price,
                    quantity,
                    stockIdentifier,
                    taskMasterId
            );

            final var expectedEq = EQUIPMENT.toBuilder()
                    .name(name)
                    .sku(sku)
                    .description(description)
                    .price(price)
                    .quantity(quantity)
                    .stockIdentifier(stockIdentifier)
                    .build();
            final var actualEq = dto.toEntity(EQUIPMENT);
            assertThat(actualEq).isEqualTo(expectedEq);
        }

        @Test
        @DisplayName(
                "when only the name is filled, " +
                "then it should only update it")
        void whenOnlyTheNameIsFilled_shouldOnlyUpdateIt() {
            final String name = FAKER.lorem().characters();
            final var dto = new EquipmentUpdateDTO(
                    name,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            final var expectedEq = EQUIPMENT.toBuilder().name(name).build();
            final var actualEq = dto.toEntity(EQUIPMENT);
            assertThat(actualEq).isEqualTo(expectedEq);
        }

        @Test
        @DisplayName(
                "when only the sku is filled, " +
                "then it should only update it")
        void whenOnlyTheSkuIsFilled_shouldOnlyUpdateIt() {
            final String sku = FAKER.lorem().fixedString(8);
            final var dto = new EquipmentUpdateDTO(
                    null,
                    sku,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            final var expectedEq = EQUIPMENT.toBuilder().sku(sku).build();
            final var actualEq = dto.toEntity(EQUIPMENT);
            assertThat(actualEq).isEqualTo(expectedEq);
        }

        @Test
        @DisplayName(
                "when only the description is filled, " +
                "then it should only update it")
        void whenOnlyTheDescriptionIsFilled_shouldOnlyUpdateIt() {
            final String description = FAKER.lorem().sentence();
            final var dto = new EquipmentUpdateDTO(
                    null,
                    null,
                    description,
                    null,
                    null,
                    null,
                    null
            );
            final var expectedEq = EQUIPMENT
                    .toBuilder()
                    .description(description)
                    .build();
            final var actualEq = dto.toEntity(EQUIPMENT);
            assertThat(actualEq).isEqualTo(expectedEq);
        }

        @Test
        @DisplayName(
                "when only the price is filled, " +
                "then it should only update it")
        void whenOnlyThePriceIsFilled_shouldOnlyUpdateIt() {
            final BigDecimal price = BigDecimal.valueOf(FAKER.random().nextInt(1, 10));
            final var dto = new EquipmentUpdateDTO(
                    null,
                    null,
                    null,
                    price,
                    null,
                    null,
                    null
            );
            final var expectedEq = EQUIPMENT.toBuilder().price(price).build();
            final var actualEq = dto.toEntity(EQUIPMENT);
            assertThat(actualEq).isEqualTo(expectedEq);
        }

        @Test
        @DisplayName(
                "when only the quantity is filled, " +
                "then it should only update it")
        void whenOnlyTheQuantityIsFilled_shouldOnlyUpdateIt() {
            final int quantity = FAKER.random().nextInt(1, 10);
            final var dto = new EquipmentUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    quantity,
                    null,
                    null
            );
            final var expectedEq = EQUIPMENT.toBuilder().quantity(quantity).build();
            final var actualEq = dto.toEntity(EQUIPMENT);
            assertThat(actualEq).isEqualTo(expectedEq);
        }

        @Test
        @DisplayName(
                "when only the stockIdentifier is filled," +
                " then it should only update it")
        void whenOnlyTheStockIdentifierIsFilled_shouldOnlyUpdateIt() {
            final String stockIdentifier = FAKER.internet().uuid();
            final var dto = new EquipmentUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    null,
                    stockIdentifier,
                    null
            );
            final var expectedEq = EQUIPMENT
                    .toBuilder()
                    .stockIdentifier(stockIdentifier)
                    .build();
            final var actualEq = dto.toEntity(EQUIPMENT);
            assertThat(actualEq).isEqualTo(expectedEq);
        }

        @Test
        @DisplayName(
                "when only the taskMasterId is filled, " +
                "then it should not update nothing")
        void whenOnlyTheTaskMasterIdIsFilled_shouldNotUpdateNothing() {
            final var dto = new EquipmentUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    FAKER.internet().uuid()
            );
            final var actualEq = dto.toEntity(EQUIPMENT);
            assertThat(actualEq).isEqualTo(EQUIPMENT);
        }
    }
}