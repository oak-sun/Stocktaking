package nam.gor.stocktaking.domain.entities;

import nam.gor.stocktaking.domain.factories.StockKeeperFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockKeeperTest {

    @Nested
    @DisplayName("method: StockKeeperBuilder")
    class StockKeeperBuilderClass {

        @Nested
        @DisplayName("method: build()")
        class BuildMethod {
            private final StockKeeper master = StockKeeperFactory.newStockKeeperEntity();

            @Test
            @DisplayName(
                    "when called and 'id' is null, " +
                    "then it should throw a NullPointerException")
            void whenCalledAndIdIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(master
                        .toBuilder()
                        .id(null)::build)
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("id is marked non-null but is null");
            }

            @Test
            @DisplayName(
                    "when called and 'firstName' is null, " +
                            "then it should throw a NullPointerException")
            void whenCalledAndFirstNameIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(master
                        .toBuilder()
                        .firstName(null)::build)
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("firstName is marked non-null but is null");
            }

            @Test
            @DisplayName(
                    "when called and 'lastName' is null, " +
                            "then it should throw a NullPointerException")
            void whenCalledAndLastNameIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(master
                        .toBuilder()
                        .lastName(null)::build)
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("lastName is marked non-null but is null");
            }

            @Test
            @DisplayName(
                    "when called and 'workContractNumber' is null, " +
                            "then it should throw a NullPointerException")
            void whenCalledAndWorkContractNumberIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(master.toBuilder()
                        .workContractNumber(null)::build)
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("workContractNumber is marked non-null but is null");
            }
        }
    }
}

