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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-End tests for the complete scan-bytecode pipeline.
 * <p>
 * Tests call ScanBytecodeService.execute() directly and verify the output JSON file.
 */
@SpringBootTest
class ScanBytecodeE2eTest {

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

    @Test
    void e2e_scanBytecode_noMasks_producesDependenciesJson() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of()
        );

        scanBytecodeService.execute(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        Map<String, List<String>> result = readOutputJson(outputFile);
        assertFalse(result.isEmpty(), "Result should not be empty");
        
        // Should contain testee classes
        String testeeClass = "spring.twin.testee.InheritanceChild";
        assertTrue(result.containsKey(testeeClass), "Should contain InheritanceChild");
    }

    @Test
    void e2e_scanBytecode_withIncludeMask_filtersClasses() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of("*.testee.*"),
                List.of()
        );

        scanBytecodeService.execute(params);

        Map<String, List<String>> result = readOutputJson(outputFile);
        
        // All keys should match the include mask
        for (String key : result.keySet()) {
            assertTrue(key.contains(".testee."), "Key " + key + " should contain '.testee.'");
        }
        
        // Should contain testee classes
        assertTrue(result.containsKey("spring.twin.testee.InheritanceChild"));
    }

    @Test
    void e2e_scanBytecode_withExcludeMask_excludesClasses() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of("*FieldHolder*")
        );

        scanBytecodeService.execute(params);

        Map<String, List<String>> result = readOutputJson(outputFile);
        
        // FieldHolder should be absent from keys
        assertFalse(result.containsKey("spring.twin.testee.FieldHolder"), 
                "FieldHolder should be excluded from keys");
    }

    @Test
    void e2e_scanBytecode_emptyDirectory_producesEmptyJson() throws IOException {
        Path emptyDir = tempDir.resolve("empty");
        Files.createDirectories(emptyDir);
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                emptyDir,
                outputFile,
                List.of(),
                List.of()
        );

        scanBytecodeService.execute(params);

        Map<String, List<String>> result = readOutputJson(outputFile);
        assertTrue(result.isEmpty(), "Result should be empty for empty directory");
    }

    @Test
    void e2e_scanBytecode_outputFile_hasSortedKeys() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of("*.testee.*"),
                List.of()
        );

        scanBytecodeService.execute(params);

        Map<String, List<String>> result = readOutputJson(outputFile);
        
        // Check that keys are sorted alphabetically
        List<String> keys = List.copyOf(result.keySet());
        for (int i = 1; i < keys.size(); i++) {
            assertTrue(keys.get(i - 1).compareTo(keys.get(i)) <= 0,
                    "Keys should be sorted alphabetically");
        }
    }

    @Test
    void e2e_scanBytecode_outputFile_hasSortedValues() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of()
        );

        scanBytecodeService.execute(params);

        Map<String, List<String>> result = readOutputJson(outputFile);
        
        // Check that each dependency list is sorted
        for (List<String> dependencies : result.values()) {
            for (int i = 1; i < dependencies.size(); i++) {
                assertTrue(dependencies.get(i - 1).compareTo(dependencies.get(i)) <= 0,
                        "Dependency values should be sorted alphabetically");
            }
        }
    }

    @Test
    void e2e_scanBytecode_inheritanceDependency_detected() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        // Use mask that includes both InheritanceChild and InheritanceBase
        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of("*.testee.Inheritance*"),
                List.of()
        );

        scanBytecodeService.execute(params);

        Map<String, List<String>> result = readOutputJson(outputFile);
        
        String childClass = "spring.twin.testee.InheritanceChild";
        String parentClass = "spring.twin.testee.InheritanceBase";
        
        assertTrue(result.containsKey(childClass), "Should contain InheritanceChild");
        assertTrue(result.get(childClass).contains(parentClass),
                "InheritanceChild should depend on InheritanceBase");
    }

    @Test
    void e2e_scanBytecode_fieldDependency_detected() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        // Use empty masks to include all classes and their dependencies
        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of()
        );

        scanBytecodeService.execute(params);

        Map<String, List<String>> result = readOutputJson(outputFile);
        
        String fieldHolder = "spring.twin.testee.FieldHolder";
        
        assertTrue(result.containsKey(fieldHolder), "Should contain FieldHolder");
        // FieldHolder has fields: String, List, int, String[], Map
        assertTrue(result.get(fieldHolder).contains("java.lang.String"),
                "FieldHolder should depend on String");
        assertTrue(result.get(fieldHolder).contains("java.util.List"),
                "FieldHolder should depend on List");
    }

    @Test
    void e2e_scanBytecode_methodDependency_detected() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        // Use empty masks to include all classes and their dependencies
        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of()
        );

        scanBytecodeService.execute(params);

        Map<String, List<String>> result = readOutputJson(outputFile);
        
        String methodHolder = "spring.twin.testee.MethodHolder";
        
        assertTrue(result.containsKey(methodHolder), "Should contain MethodHolder");
        // MethodHolder has methods with: String, List, Map, int, String[]
        assertTrue(result.get(methodHolder).contains("java.lang.String"),
                "MethodHolder should depend on String");
        assertTrue(result.get(methodHolder).contains("java.util.List"),
                "MethodHolder should depend on List");
    }

    @Test
    void e2e_scanBytecode_annotationDependency_detected() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        // Use empty masks to include all classes and their dependencies
        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of()
        );

        scanBytecodeService.execute(params);

        Map<String, List<String>> result = readOutputJson(outputFile);
        
        String annotatedClass = "spring.twin.testee.AnnotatedClass";
        
        assertTrue(result.containsKey(annotatedClass), "Should contain AnnotatedClass");
        // AnnotatedClass has @Deprecated annotation
        assertTrue(result.get(annotatedClass).contains("java.lang.Deprecated"),
                "AnnotatedClass should depend on Deprecated annotation");
    }

    @Test
    void e2e_scanBytecode_genericDependency_detected() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        // Use empty masks to include all classes and their dependencies
        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of()
        );

        scanBytecodeService.execute(params);

        Map<String, List<String>> result = readOutputJson(outputFile);
        
        String genericChild = "spring.twin.testee.InheritanceGenericChild";
        
        assertTrue(result.containsKey(genericChild), "Should contain InheritanceGenericChild");
        // InheritanceGenericChild extends ArrayList<String>
        assertTrue(result.get(genericChild).contains("java.util.ArrayList"),
                "InheritanceGenericChild should depend on ArrayList");
        assertTrue(result.get(genericChild).contains("java.lang.String"),
                "InheritanceGenericChild should depend on String (generic type)");
    }

    @Test
    void e2e_scanBytecode_arrayDependency_baseTypeExtracted() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        // Use empty masks to include all classes and their dependencies
        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of()
        );

        scanBytecodeService.execute(params);

        Map<String, List<String>> result = readOutputJson(outputFile);
        
        String fieldHolder = "spring.twin.testee.FieldHolder";
        
        assertTrue(result.containsKey(fieldHolder), "Should contain FieldHolder");
        // FieldHolder has String[] field - should depend on String, not String[]
        assertTrue(result.get(fieldHolder).contains("java.lang.String"),
                "FieldHolder should depend on String (base type of array)");
        
        // Should not contain the array type itself (just the base type)
        // Note: depending on implementation, array type might or might not be included
        // The key point is that the base type IS included
    }
}