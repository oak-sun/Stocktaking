package nam.gor.stocktaking.api.dto.equipments;

import nam.gor.stocktaking.api.dto.equipment.EquipmentFindDTO;
import nam.gor.stocktaking.domain.entities.Query;
import nam.gor.stocktaking.domain.factories.EquipmentFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

class EquipmentFindDTOTest {

    @Nested
    @DisplayName("method: toEntity()")
    class ToEntityMethod {

        @Test
        @DisplayName(
                "when called, then it should " +
                "return the correct entity object")
        void whenCalled_shouldReturnTheCorrectEntityObject() {
            final EquipmentFindDTO dto = EquipmentFactory
                    .newFindEquipmentsDto();
            final Query expectedQuery = Query
                    .builder()
                    .name(dto.getName())
                    .minQuantity(dto.getMinQuantity())
                    .taskmasterId(dto.getTaskMasterId())
                    .build();
            final Query actualQuery = dto.toEntity();
            assertThat(actualQuery).isEqualTo(expectedQuery);
        }
    }

    @Nested
    @DisplayName("method: fromQueryParams(MultiValueMap<String, String>)")
    class FromQueryParamsMethod {

        @Test
        @DisplayName(
                "when called with empty map, " +
                "then it should create a dto" +
                " with null fields")
        void whenCalledWithEmptyMap_shouldCreateADtoWithNullFields() {
            final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            final EquipmentFindDTO dto = EquipmentFindDTO.fromQueryParams(map);
            assertThat(dto.getName()).isNull();
            assertThat(dto.getMinQuantity()).isNull();
            assertThat(dto.getTaskMasterId()).isNull();
        }

        @Test
        @DisplayName(
                "when map contains a name, " +
                "then it should create a dto with it")
        void whenMapContainsAName_shouldCreateADtoWithIt() {
            final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("name", "Drilling Rigs");
            final EquipmentFindDTO dto = EquipmentFindDTO.fromQueryParams(map);
            assertThat(dto.getName()).isEqualTo("Drilling Rigs");
            assertThat(dto.getMinQuantity()).isNull();
            assertThat(dto.getTaskMasterId()).isNull();
        }

        @Test
        @DisplayName(
                "when map contains minQuantity, " +
                "then it should create a dto with it")
        void whenMapContainsMinQuantity_shouldCreateADtoWithIt() {
            final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("minQuantity", "12");
            final EquipmentFindDTO dto = EquipmentFindDTO.fromQueryParams(map);
            assertThat(dto.getName()).isNull();
            assertThat(dto.getMinQuantity()).isEqualTo(12);
            assertThat(dto.getTaskMasterId()).isNull();
        }

        @Test
        @DisplayName(
                "when map contains taskMasterId, " +
                "then it should create a dto with it")
        void whenMapContainsTaskMasterId_shouldCreateADtoWithIt() {
            final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("taskMasterId", "some-id");
            final EquipmentFindDTO dto = EquipmentFindDTO.fromQueryParams(map);
            assertThat(dto.getName()).isNull();
            assertThat(dto.getMinQuantity()).isNull();
            assertThat(dto.getTaskMasterId()).isEqualTo("some-id");
        }

        @Test
        @DisplayName(
                "when map contains all fields, " +
                "then it should create a dto with them all")
        void whenMapContainsAllFields_shouldCreateADtoWithThemAll() {
            final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("name", "Grader");
            map.add("minQuantity", "12");
            map.add("taskMasterId", "some-id");
            final EquipmentFindDTO dto = EquipmentFindDTO.fromQueryParams(map);
            assertThat(dto.getName()).isEqualTo("Grader");
            assertThat(dto.getMinQuantity()).isEqualTo(12);
            assertThat(dto.getTaskMasterId()).isEqualTo("some-id");
        }
    }
}