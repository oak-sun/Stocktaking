package nam.gor.stocktaking.domain.entities;

import nam.gor.stocktaking.domain.factories.TaskMasterFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskMasterTest {

    @Nested
    @DisplayName("method: TaskMasterBuilder")
    class TaskMasterBuilderClass {

        @Nested
        @DisplayName("method: build()")
        class BuildMethod {
            private final TaskMaster master = TaskMasterFactory.newTaskMasterEntity();

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

            @Test
            @DisplayName(
                    "when called and 'objectName' is null, " +
                            "then it should throw a NullPointerException")
            void whenCalledAndObjectNameIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(master
                        .toBuilder()
                        .objectName(null)::build)
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("objectName is marked non-null but is null");
            }

            @Test
            @DisplayName(
                    "when called and 'teamNumber' is null, " +
                            "then it should throw a NullPointerException")
            void whenCalledAndTeamNumberIsNull_shouldThrowANullPointerException() {
                assertThatThrownBy(master
                        .toBuilder()
                        .teamNumber(null)::build)
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("teamNumber is marked non-null but is null");
            }
        }
    }
}
