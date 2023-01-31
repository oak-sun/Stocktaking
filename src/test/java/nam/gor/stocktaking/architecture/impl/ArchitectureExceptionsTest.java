package nam.gor.stocktaking.architecture.impl;

import nam.gor.stocktaking.architecture.ArchitectureBaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ArchitectureExceptionsTest extends ArchitectureBaseTest {

    @Nested
    @DisplayName("architecture: Exceptions Test")
    class ExceptionsTest {

        @Test
        @DisplayName(
                "Exceptions should reside " +
                "in ..domain.exceptions package")
        void givenExceptions_shouldBeInAppropriatePackage() {
            classes()
                    .that()
                    .haveSimpleNameEndingWith("Exception")
                    .should()
                    .resideInAnyPackage("..domain.exceptions")
                    .check(classes);
        }
    }
}
