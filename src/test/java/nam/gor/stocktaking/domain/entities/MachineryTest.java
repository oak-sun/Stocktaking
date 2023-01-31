package nam.gor.stocktaking.domain.entities;

import nam.gor.stocktaking.domain.factories.MachineryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MachineryTest {

    @Nested
    @DisplayName("class: MachineryBuilder")
    class MachineryBuilderClass {
        private final Machinery machinery = MachineryFactory.newMachineryEntity();

        @Nested
        @DisplayName("method: build()")
        class BuildMethod {

            @Test
            @DisplayName(
                    "when called and 'id' is null, " +
                    "then it should throw a NullPointerException")
            void whenCalledAndIdIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(machinery
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
                assertThatThrownBy(machinery
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
                assertThatThrownBy(machinery
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
                assertThatThrownBy(machinery
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
                assertThatThrownBy(machinery
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
                        machinery
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
                assertThatThrownBy(machinery
                        .toBuilder()
                        .taskMaster(null)::build)
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("taskMaster is marked non-null but is null");
            }
        }
    }
}
