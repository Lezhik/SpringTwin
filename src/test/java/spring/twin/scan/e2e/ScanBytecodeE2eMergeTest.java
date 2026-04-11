package spring.twin.scan.e2e;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import spring.twin.scan.ScanBytecodeParams;
import spring.twin.scan.ScanBytecodeService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-End tests for the --merge-inner-classes option.
 * <p>
 * Tests call ScanBytecodeService.execute() with various mergeInnerClasses values
 * and verify the output JSON file.
 */
@SpringBootTest
class ScanBytecodeE2eMergeTest {

    @TempDir
    Path tempDir;

    @Autowired
    ScanBytecodeService scanBytecodeService;

    Path classesDir;

    @BeforeEach
    void setUp() {
        classesDir = getTesteeClassesDir();
    }

    /**
     * Returns the path to compiled testee classes directory.
     *
     * @return path to build/classes/java/test/spring/twin/testee/
     */
    Path getTesteeClassesDir() {
        return Path.of("build/classes/java/test/spring/twin/testee/");
    }

    /**
     * Reads and parses the output JSON file.
     *
     * @param outputFile path to the JSON output file
     * @return map from class FQCN to list of dependency FQCNs
     * @throws IOException if the file cannot be read
     */
    Map<String, List<String>> readOutputJson(Path outputFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
    }

    /**
     * Tests that with mergeInnerClasses=true, inner classes are merged into outer classes.
     * Outer$Inner should be absent from keys, and Outer should contain dependencies of the inner class.
     */
    @Test
    void e2e_mergeInnerClassesTrue_innerClassesMergedIntoOuter() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of(),
                true
        );

        scanBytecodeService.execute(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        Map<String, List<String>> result = readOutputJson(outputFile);

        // Outer$Inner should be absent from keys
        assertFalse(result.containsKey("spring.twin.testee.Outer$Inner"),
                "Outer$Inner should be absent from keys when mergeInnerClasses=true");

        // Outer should be present
        assertTrue(result.containsKey("spring.twin.testee.Outer"),
                "Outer should be present in keys");
    }

    /**
     * Tests that with mergeInnerClasses=false, inner classes appear as separate entries in the graph.
     * Outer$Inner should be present as a separate key.
     */
    @Test
    void e2e_mergeInnerClassesFalse_innerClassesSeparate() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of(),
                false
        );

        scanBytecodeService.execute(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        Map<String, List<String>> result = readOutputJson(outputFile);

        // Outer$Inner should be present as a separate key
        assertTrue(result.containsKey("spring.twin.testee.Outer$Inner"),
                "Outer$Inner should be present as a separate key when mergeInnerClasses=false");
    }

    /**
     * Tests that the 4-parameter constructor of ScanBytecodeParams defaults mergeInnerClasses to true.
     */
    @Test
    void e2e_mergeInnerClassesDefault_isTrue() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        // Use 4-parameter constructor (without mergeInnerClasses flag)
        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of()
        );

        scanBytecodeService.execute(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        Map<String, List<String>> result = readOutputJson(outputFile);

        // By default, mergeInnerClasses should be true, so Outer$Inner should be merged
        assertFalse(result.containsKey("spring.twin.testee.Outer$Inner"),
                "Outer$Inner should be absent from keys when using default mergeInnerClasses value");

        // Outer should be present
        assertTrue(result.containsKey("spring.twin.testee.Outer"),
                "Outer should be present in keys");
    }

    /**
     * Tests that after merge, the outer class does not contain a self-reference.
     */
    @Test
    void e2e_mergeInnerClassesTrue_noSelfReference() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of(),
                true
        );

        scanBytecodeService.execute(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        Map<String, List<String>> result = readOutputJson(outputFile);

        // Check that no class has a self-reference
        for (Map.Entry<String, List<String>> entry : result.entrySet()) {
            String className = entry.getKey();
            List<String> dependencies = entry.getValue();
            assertFalse(dependencies.contains(className),
                    "Class " + className + " should not have a self-reference");
        }
    }

    /**
     * Tests that when another class depends on Outer$Inner, after merge the dependency
     * is remapped to Outer.
     */
    @Test
    void e2e_mergeInnerClassesTrue_dependencyOnInnerClass_remappedToOuter() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of(),
                true
        );

        scanBytecodeService.execute(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        Map<String, List<String>> result = readOutputJson(outputFile);

        // Check that no dependency references Outer$Inner directly
        for (Map.Entry<String, List<String>> entry : result.entrySet()) {
            List<String> dependencies = entry.getValue();
            assertFalse(dependencies.contains("spring.twin.testee.Outer$Inner"),
                    "Dependencies should not contain Outer$Inner after merge");
        }
    }

    /**
     * Tests that keys and values in the output JSON are sorted after merge.
     */
    @Test
    void e2e_mergeInnerClassesTrue_sortedOutput() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of(),
                true
        );

        scanBytecodeService.execute(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        Map<String, List<String>> result = readOutputJson(outputFile);

        // Check that keys are sorted alphabetically
        List<String> keys = List.copyOf(result.keySet());
        for (int i = 1; i < keys.size(); i++) {
            assertTrue(keys.get(i - 1).compareTo(keys.get(i)) <= 0,
                    "Keys should be sorted alphabetically");
        }

        // Check that each dependency list is sorted
        for (List<String> dependencies : result.values()) {
            for (int i = 1; i < dependencies.size(); i++) {
                assertTrue(dependencies.get(i - 1).compareTo(dependencies.get(i)) <= 0,
                        "Dependency values should be sorted alphabetically");
            }
        }
    }
}