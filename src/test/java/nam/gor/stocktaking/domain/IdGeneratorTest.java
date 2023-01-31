package nam.gor.stocktaking.domain;

import nam.gor.stocktaking.domain.IdGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdGeneratorTest {

    private final IdGenerator idGen = new IdGenerator();

    @Test
    @DisplayName(
            "when called, then it" +
            " should generate an uuid")
    void whenCalled_shouldGenerateAnUUID() {
        assertThat(idGen.newId())
                .hasSize(36);
    }
}