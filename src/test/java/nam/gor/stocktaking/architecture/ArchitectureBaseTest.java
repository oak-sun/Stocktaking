package nam.gor.stocktaking.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;

public abstract class ArchitectureBaseTest {

    protected static final String DOMAIN_LAYER_PACKAGES = "nam.gor.stocktaking.domain..";
    protected static final String API_LAYER_PACKAGES = "nam.gor.stocktaking.api..";
    protected static final String INFRASTRUCTURE_LAYER_PACKAGES = "nam.gor.stocktaking.infrastructure..";

    protected static JavaClasses classes;

    @BeforeAll
    public static void setUp() {
        classes = new ClassFileImporter()
                .withImportOption(
                        ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .withImportOption(
                        ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(
                        ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES)
                .importPackages("nam.gor.stocktaking");
    }
}
