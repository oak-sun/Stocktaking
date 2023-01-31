package nam.gor.stocktaking.domain.entities;

import nam.gor.stocktaking.domain.factories.EquipmentFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EquipmentTest {

    @Nested
    @DisplayName("class: EquipmentBuilder")
    class EquipmentBuilderClass {
        private final Equipment equipment = EquipmentFactory.newEquipmentEntity();

        @Nested
        @DisplayName("method: build()")
        class BuildMethod {

            @Test
            @DisplayName(
                    "when called and 'id' is null, " +
                    "then it should throw a NullPointerException")
            void whenCalledAndIdIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(equipment
                        .toBuilder()
                        .id(null)::build)
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("id is marked non-null but is null");
            }

            @Test
            @DisplayName(
                    "when called and 'sku' is null, " +
                    "then it should throw a NullPointerException")
            void whenCalledAndSkuIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(equipment
                        .toBuilder()
                        .sku(null)::build)
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("sku is marked non-null but is null");
            }

            @Test
            @DisplayName(
                    "when called and 'name' is null, " +
                    "then it should throw a NullPointerException")
            void whenCalledAndNameIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(equipment
                        .toBuilder()
                        .name(null)::build)
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("name is marked non-null but is null");
            }

            @Test
            @DisplayName(
                    "when called and 'description' is null, " +
                    "then it should throw a NullPointerException")
            void whenCalledAndDescriptionIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(equipment
                        .toBuilder()
                        .description(null)::build)
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("description is marked non-null but is null");
            }

            @Test
            @DisplayName(
                    "when called and 'quantity' is null, " +
                    "then it should throw a NullPointerException")
            void whenCalledAndQuantityIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(equipment
                        .toBuilder()
                        .quantity(null)::build)
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("quantity is marked non-null but is null");
            }

            @Test
            @DisplayName(
                    "when called and 'stockIdentifier' is null, " +
                    "then it should throw a NullPointerException")
            void whenCalledAndStockIdentifierIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(() -> 
                        equipment
                                .toBuilder()
                                .stockIdentifier(null)
                                .build())
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("stockIdentifier is marked non-null but is null");
            }

            @Test
            @DisplayName(
                    "when called and 'taskMaster' is null, " +
                    "then it should throw a NullPointerException")
            void whenCalledAndTaskMasterIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(equipment
                        .toBuilder()
                        .taskMaster(null)::build)
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("taskMaster is marked non-null but is null");
            }
        }
    }
}