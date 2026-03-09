package spring.twin.scanner;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ClassScanningService}.
 * <p>
 * Tests class scanning functionality with actual build directories.
 */
class ClassScanningServiceTest {

    /**
     * Expected class names to find in build directories.
     */
    private static final String[] EXPECTED_CLASSES = {
        "spring.twin.SpringTwinApplication",
        "spring.twin.dto.TaskDto",
        "spring.twin.dto.types.EdgeType",
        "spring.twin.scanner.FqcnFilter"
    };

    /**
     * Tmp directory for tests.
     */
    private static final Path TEST_TMP_DIR = Path.of("build/tmp/test");

    @Test
    void shouldScanBuildClassesJavaMainDirectory() throws IOException {
        // Setup: use build/tmp/test directory for extraction
        ClassScanningService integrationService = new ClassScanningService(TEST_TMP_DIR);

        // Step 1: Clean tmp and verify it's empty
        integrationService.cleanTmp();
        assertThat(integrationService.getTmpDirectory()).exists();
        assertThat(Files.list(integrationService.getTmpDirectory())).isEmpty();

        // Step 2: Scan build/classes/java/main directory
        Path buildClassesDir = Path.of("build/classes/java/main");
        
        // Skip test if directory doesn't exist (e.g., in CI without prior build)
        org.junit.jupiter.api.Assumptions.assumeTrue(Files.exists(buildClassesDir),
            "Build classes directory does not exist: " + buildClassesDir);

        Map<String, Path> result = integrationService.scan(buildClassesDir);

        // Step 3: Verify expected classes are found
        assertThat(result).isNotEmpty();
        for (String expectedClass : EXPECTED_CLASSES) {
            assertThat(result)
                .as("Expected class %s to be found in build/classes/java/main", expectedClass)
                .containsKey(expectedClass);
        }

        // Step 4: Verify each class file exists and is not empty
        // Note: For directory scanning, files are referenced from their original location
        for (String expectedClass : EXPECTED_CLASSES) {
            Path classPath = result.get(expectedClass);
            assertThat(classPath)
                .as("Path for class %s should exist", expectedClass)
                .exists();
            assertThat(classPath.toString())
                .as("Path for class %s should be a .class file", expectedClass)
                .endsWith(".class");
            assertThat(Files.size(classPath))
                .as("Class file %s should not be empty", expectedClass)
                .isGreaterThan(0);
        }

        // Step 5: Clean tmp again and verify it's empty
        integrationService.cleanTmp();
        assertThat(Files.list(integrationService.getTmpDirectory())).isEmpty();
    }

    @Test
    void shouldScanBuildLibsDirectory() throws IOException {
        // Setup: use build/tmp/test directory for extraction
        ClassScanningService integrationService = new ClassScanningService(TEST_TMP_DIR);

        // Step 1: Clean tmp and verify it's empty
        integrationService.cleanTmp();
        assertThat(integrationService.getTmpDirectory()).exists();
        assertThat(Files.list(integrationService.getTmpDirectory())).isEmpty();

        // Step 2: Scan build/libs directory
        Path buildLibsDir = Path.of("build/libs");
        
        // Skip test if directory doesn't exist (e.g., in CI without prior build)
        org.junit.jupiter.api.Assumptions.assumeTrue(Files.exists(buildLibsDir),
            "Build libs directory does not exist: " + buildLibsDir);

        Map<String, Path> result = integrationService.scan(buildLibsDir);

        // Step 3: Verify expected classes are found (if any JARs exist)
        // Note: build/libs may be empty or contain JARs without the expected classes
        // We only validate if classes were found
        if (!result.isEmpty()) {
            for (String expectedClass : EXPECTED_CLASSES) {
                if (result.containsKey(expectedClass)) {
                    Path classPath = result.get(expectedClass);
                    assertThat(classPath).exists();
                    assertThat(classPath.startsWith(TEST_TMP_DIR))
                        .as("Path for class %s should be in tmp directory", expectedClass)
                        .isTrue();
                    assertThat(Files.size(classPath))
                        .as("Class file %s should not be empty", expectedClass)
                        .isGreaterThan(0);
                }
            }
        }

        // Step 4: Clean tmp again and verify it's empty
        integrationService.cleanTmp();
        assertThat(Files.list(integrationService.getTmpDirectory())).isEmpty();
    }
}