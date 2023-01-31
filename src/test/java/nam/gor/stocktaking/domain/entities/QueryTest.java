package nam.gor.stocktaking.domain.entities;

import nam.gor.stocktaking.domain.entities.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class QueryTest {

    private static final Query EMPTY_QUERY = Query.builder().build();

    @Nested
    @DisplayName("method: getName()")
    class GetNameMethod {

        @Test
        @DisplayName(
                "when called and name is null," +
                " then it should return Optional empty")
        void whenCalledAndNameIsNull_shouldReturnOptionalEmpty() {
            assertThat(EMPTY_QUERY.getName()).isEmpty();
        }

        @Test
        @DisplayName(
                "when called and name is not null, " +
                "then it should return it wrapped in an Optional")
        void whenCalledAndNameIsNotNull_shouldReturnItWrappedInAnOptional() {
            final String name = UUID.randomUUID().toString();
            final Query query = Query.builder().name(name).build();
            assertThat(query.getName()).hasValue(name);
        }
    }

    @Nested
    @DisplayName("method: getMinQuantity()")
    class GetMinQuantityMethod {

        @Test
        @DisplayName(
                "when called and minQuantity is null, " +
                "then it should return Optional empty")
        void whenCalledAndMinQuantityIsNull_shouldReturnOptionalEmpty() {
            assertThat(EMPTY_QUERY.getMinQuantity()).isEmpty();
        }

        @Test
        @DisplayName(
                "when called and name is not null, " +
                "then it should return it wrapped in an Optional")
        void whenCalledAndNameIsNotNull_shouldReturnItWrappedInAnOptional() {
            final Query query = Query.builder().minQuantity(23).build();
            assertThat(query.getMinQuantity()).hasValue(23);
        }
    }

    @Nested
    @DisplayName("method: getTaskMasterId()")
    class GetTaskMasterIdMethod {

        @Test
        @DisplayName(
                "when called and taskMasterId is null, " +
                "then it should return Optional empty")
        void whenCalledAndTaskMasterIdIsNull_shouldReturnOptionalEmpty() {
            assertThat(EMPTY_QUERY.getTaskmasterId()).isEmpty();
        }

        @Test
        @DisplayName(
                "when called and taskMasterId is not null, " +
                "then it should return it wrapped in an Optional")
        void whenCalledAndTaskMasterIdIsNotNull_shouldReturnItWrappedInAnOptional() {
            final String taskMasterId = UUID.randomUUID().toString();
            final Query query = Query.builder().taskmasterId(taskMasterId).build();
            assertThat(query.getTaskmasterId()).hasValue(taskMasterId);
        }
    }
}