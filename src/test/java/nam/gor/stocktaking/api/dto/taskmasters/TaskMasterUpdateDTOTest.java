package nam.gor.stocktaking.api.dto.taskmasters;

import com.github.javafaker.Faker;
import nam.gor.stocktaking.api.dto.taskmaster.TaskMasterUpdateDTO;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.domain.factories.TaskMasterFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TaskMasterUpdateDTOTest {


    @Nested
    @DisplayName("method: fromEntityToEntity(TaskMasterOutDTO)")
    class FromEntityToEntityMethod {
        final TaskMaster MASTER = TaskMasterFactory
                .newTaskMasterEntity();
        private final Faker FAKER = Faker.instance();

        @Test
        @DisplayName(
                "when no field is present, " +
                "then it should not update anything")
        void whenNoFieldIsPresent_shouldNotUpdateAnything() {
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    null);
            assertThat(dto.fromEntityToEntity(MASTER))
                    .isEqualTo(MASTER);
        }

        @Test
        @DisplayName(
                "when all fields are filled, " +
                "then it should return new taskmaster" +
                " with the given fields")
        void whenAllFieldsAreFilled_shouldReturnNewTaskMasterWithTheGivenFields() {
            final String firstName = FAKER.lorem().characters();
            final String lastName = FAKER.lorem().characters();
            final Long workContractNumber = FAKER.random().nextLong(100_010_111L);
            final String objectName = FAKER.lorem().characters();
            final Long teamNumber = FAKER.random().nextLong(111_121L);
            final var dto = new TaskMasterUpdateDTO(
                    firstName,
                    lastName,
                    workContractNumber,
                    objectName,
                    teamNumber
            );
            final var expectedEq = MASTER.toBuilder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .workContractNumber(workContractNumber)
                    .objectName(objectName)
                    .teamNumber(teamNumber)
                    .build();
            final var actualMaster = dto.fromEntityToEntity(MASTER);
            assertThat(actualMaster).isEqualTo(expectedEq);
        }

        @Test
        @DisplayName(
                "when only the first name field is present," +
                " then it should only update it")
        void whenOnlyTheFirstNameFieldIsPresent_shouldOnlyUpdateIt() {
            final String firstName = UUID.randomUUID().toString();
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    firstName,
                    null,
                    null,
                    null,
                    null
            );
            final TaskMaster expected = MASTER
                    .toBuilder()
                    .firstName(firstName).build();
            final TaskMaster actual = dto.fromEntityToEntity(MASTER);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName(
                "when only the last name field is present," +
                " then it should only update it")
        void whenOnlyTheLastNameFieldIsPresent_shouldOnlyUpdateIt() {
            final String lastName = UUID.randomUUID().toString();
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    null,
                    lastName,
                    null,
                    null,
                    null
            );
            final TaskMaster expected = MASTER
                    .toBuilder()
                    .lastName(lastName).build();
            final TaskMaster actual = dto.fromEntityToEntity(MASTER);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName(
                "when only the work contract number" +
                        " field is present," +
                        " then it should only update it")
        void whenOnlyTheWorkContractNumberFieldIsPresent_shouldOnlyUpdateIt() {
            final Long workContractNumber = FAKER.random().nextLong(100_000_000L);
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    null,
                    null,
                    workContractNumber,
                    null,
                    null
            );
            final TaskMaster expected = MASTER
                    .toBuilder()
                    .workContractNumber(workContractNumber).build();
            final TaskMaster actual = dto.fromEntityToEntity(MASTER);
            assertThat(actual).isEqualTo(expected);
        }


        @Test
        @DisplayName(
                "when only the object name field is present," +
                        " then it should only update it")
        void whenOnlyTheObjectNameFieldIsPresent_shouldOnlyUpdateIt() {
            final String objectName = UUID.randomUUID().toString();
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    null,
                    null,
                    null,
                    objectName,
                    null
            );
            final TaskMaster expected = MASTER
                    .toBuilder()
                    .objectName(objectName).build();
            final TaskMaster actual = dto.fromEntityToEntity(MASTER);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName(
                "when only the team number" +
                        " field is present," +
                        " then it should only update it")
        void whenOnlyTheWorkTeamNumberFieldIsPresent_shouldOnlyUpdateIt() {
            final Long teamNumber = FAKER.random().nextLong(100_000L);
            final TaskMasterUpdateDTO dto = new TaskMasterUpdateDTO(
                    null,
                    null,
                    null,
                    null,
                    teamNumber
            );
            final TaskMaster expected = MASTER
                    .toBuilder()
                    .teamNumber(teamNumber).build();
            final TaskMaster actual = dto.fromEntityToEntity(MASTER);
            assertThat(actual).isEqualTo(expected);
        }
    }
}