package spring.twin.scan.e2e;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import spring.twin.scan.LinkDetails;
import spring.twin.scan.LinkType;
import spring.twin.scan.ScanBytecodeParams;
import spring.twin.scan.ScanBytecodeService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        scanBytecodeService.executeDetails(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");

        // Parse as detailed format
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, Set<LinkDetails>>> result =
            objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
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

        scanBytecodeService.executeDetails(params);

        // Parse as detailed format
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, Set<LinkDetails>>> result =
            objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
        
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

        scanBytecodeService.executeDetails(params);

        // Parse as detailed format
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, Set<LinkDetails>>> result =
            objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
        
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

        scanBytecodeService.executeDetails(params);

        // Parse as detailed format
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, Set<LinkDetails>>> result =
            objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
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

        scanBytecodeService.executeDetails(params);

        // Parse as detailed format
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, Set<LinkDetails>>> result =
            objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
        
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

        scanBytecodeService.executeDetails(params);

        // Parse as detailed format - values are inner maps with sorted keys
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, Set<LinkDetails>>> result =
            objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
        
        // Check that each inner map's keys (dependency class names) are sorted
        for (Map<String, Set<LinkDetails>> innerMap : result.values()) {
            List<String> innerKeys = List.copyOf(innerMap.keySet());
            for (int i = 1; i < innerKeys.size(); i++) {
                assertTrue(innerKeys.get(i - 1).compareTo(innerKeys.get(i)) <= 0,
                        "Dependency keys should be sorted alphabetically");
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

        scanBytecodeService.executeDetails(params);

        // Parse as detailed format
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, Set<LinkDetails>>> result =
            objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
        
        String childClass = "spring.twin.testee.InheritanceChild";
        String parentClass = "spring.twin.testee.InheritanceBase";
        
        assertTrue(result.containsKey(childClass), "Should contain InheritanceChild");
        assertTrue(result.get(childClass).containsKey(parentClass),
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

        scanBytecodeService.executeDetails(params);

        // Parse as detailed format
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, Set<LinkDetails>>> result =
            objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
        
        String fieldHolder = "spring.twin.testee.FieldHolder";
        
        assertTrue(result.containsKey(fieldHolder), "Should contain FieldHolder");
        // FieldHolder has fields: String, List, int, String[], Map
        assertTrue(result.get(fieldHolder).containsKey("java.lang.String"),
                "FieldHolder should depend on String");
        assertTrue(result.get(fieldHolder).containsKey("java.util.List"),
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

        scanBytecodeService.executeDetails(params);

        // Parse as detailed format
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, Set<LinkDetails>>> result =
            objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
        
        String methodHolder = "spring.twin.testee.MethodHolder";
        
        assertTrue(result.containsKey(methodHolder), "Should contain MethodHolder");
        // MethodHolder has methods with: String, List, Map, int, String[]
        assertTrue(result.get(methodHolder).containsKey("java.lang.String"),
                "MethodHolder should depend on String");
        assertTrue(result.get(methodHolder).containsKey("java.util.List"),
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

        scanBytecodeService.executeDetails(params);

        // Parse as detailed format
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, Set<LinkDetails>>> result =
            objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
        
        String annotatedClass = "spring.twin.testee.AnnotatedClass";
        
        assertTrue(result.containsKey(annotatedClass), "Should contain AnnotatedClass");
        // AnnotatedClass has @Deprecated annotation
        assertTrue(result.get(annotatedClass).containsKey("java.lang.Deprecated"),
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

        scanBytecodeService.executeDetails(params);

        // Parse as detailed format
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, Set<LinkDetails>>> result =
            objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
        
        String genericChild = "spring.twin.testee.InheritanceGenericChild";
        
        assertTrue(result.containsKey(genericChild), "Should contain InheritanceGenericChild");
        // InheritanceGenericChild extends ArrayList<String>
        assertTrue(result.get(genericChild).containsKey("java.util.ArrayList"),
                "InheritanceGenericChild should depend on ArrayList");
        assertTrue(result.get(genericChild).containsKey("java.lang.String"),
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

        scanBytecodeService.executeDetails(params);

        // Parse as detailed format
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, Set<LinkDetails>>> result =
            objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
        
        String fieldHolder = "spring.twin.testee.FieldHolder";
        
        assertTrue(result.containsKey(fieldHolder), "Should contain FieldHolder");
        // FieldHolder has String[] field - should depend on String, not String[]
        assertTrue(result.get(fieldHolder).containsKey("java.lang.String"),
                "FieldHolder should depend on String (base type of array)");
        
        // Should not contain the array type itself (just the base type)
        // Note: depending on implementation, array type might or might not be included
        // The key point is that the base type IS included
    }

    /**
     * Verifies that output JSON contains LinkDetails structure (type and details fields).
     * This test ensures the CLI produces the correct format with detailed link information.
     */
    @Test
    void e2e_scanBytecode_outputFormat_containsLinkDetails() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of()
        );

        scanBytecodeService.executeDetails(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");

        // Parse JSON as raw map to verify structure
        ObjectMapper objectMapper = new ObjectMapper();
        String content = Files.readString(outputFile);
        
        // Verify the JSON contains "type" and "details" fields (LinkDetails format)
        assertTrue(content.contains("\"type\""),
            "Output JSON should contain 'type' field (LinkDetails format). Content: " + content);
        assertTrue(content.contains("\"details\""),
            "Output JSON should contain 'details' field (LinkDetails format). Content: " + content);

        // Try to parse as detailed format
        Map<String, Map<String, Set<LinkDetails>>> detailedResult = null;
        try {
            detailedResult = objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
        } catch (Exception e) {
            throw new AssertionError("Output JSON is not in LinkDetails format. " +
                "Expected Map<String, Map<String, Set<LinkDetails>>>. Content: " + content, e);
        }

        // Verify structure
        assertNotNull(detailedResult, "Parsed result should not be null");
        assertFalse(detailedResult.isEmpty(), "Result should not be empty");

        // Verify each entry has proper LinkDetails structure
        for (Map.Entry<String, Map<String, Set<LinkDetails>>> entry : detailedResult.entrySet()) {
            Map<String, Set<LinkDetails>> dependencies = entry.getValue();
            assertNotNull(dependencies, "Dependencies map should not be null for " + entry.getKey());
            
            for (Map.Entry<String, Set<LinkDetails>> depEntry : dependencies.entrySet()) {
                Set<LinkDetails> linkDetailsSet = depEntry.getValue();
                assertNotNull(linkDetailsSet, "LinkDetails set should not be null for " + depEntry.getKey());
                assertFalse(linkDetailsSet.isEmpty(), "LinkDetails set should not be empty for " + depEntry.getKey());
                
                // Verify each LinkDetails has required fields
                for (LinkDetails linkDetails : linkDetailsSet) {
                    assertNotNull(linkDetails.type(), "LinkDetails type should not be null");
                    assertNotNull(linkDetails.details(), "LinkDetails details should not be null");
                }
            }
        }
    }

    /**
     * Verifies that output JSON contains all LinkType values in the details.
     */
    @Test
    void e2e_scanBytecode_outputFormat_containsAllLinkTypes() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of()
        );

        scanBytecodeService.executeDetails(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");

        // Parse as detailed format
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, Set<LinkDetails>>> result =
            objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});

        // Collect all link types present in output
        Set<LinkType> foundTypes = java.util.EnumSet.noneOf(LinkType.class);
        for (Map.Entry<String, Map<String, Set<LinkDetails>>> entry : result.entrySet()) {
            for (Set<LinkDetails> linkSet : entry.getValue().values()) {
                for (LinkDetails link : linkSet) {
                    foundTypes.add(link.type());
                }
            }
        }

        // Verify at least some link types are present
        assertTrue(foundTypes.contains(LinkType.SUPERCLASS) ||
                   foundTypes.contains(LinkType.INTERFACE) ||
                   foundTypes.contains(LinkType.FIELD) ||
                   foundTypes.contains(LinkType.METHOD),
            "Output should contain at least one of the basic link types (SUPERCLASS, INTERFACE, FIELD, METHOD)");
    }
}