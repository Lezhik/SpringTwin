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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-End tests for the complete scan-bytecode pipeline with detailed output.
 * <p>
 * Tests call ScanBytecodeService.executeDetails() directly and verify the output JSON file.
 */
@SpringBootTest
class ScanBytecodeE2eDetailsTest {

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
     * Reads and parses the output JSON file with detailed structure.
     *
     * @param outputFile path to the JSON output file
     * @return map from class FQCN to map of dependency FQCNs to set of LinkDetails
     * @throws IOException if the file cannot be read
     */
    Map<String, Map<String, Set<LinkDetails>>> readOutputJson(Path outputFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
    }

    /**
     * Full pipeline: passes parameters to ScanBytecodeService.executeDetails(),
     * verifies structure and content of the output JSON file.
     */
    @Test
    void e2e_scanBytecodeDetails_producesCorrectJson() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of()
        );

        scanBytecodeService.executeDetails(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        Map<String, Map<String, Set<LinkDetails>>> result = readOutputJson(outputFile);
        assertFalse(result.isEmpty(), "Result should not be empty");

        // Should contain testee classes
        String testeeClass = "spring.twin.testee.InheritanceChild";
        assertTrue(result.containsKey(testeeClass), "Should contain InheritanceChild");

        // Verify structure: each value is a map from FQCN to set of LinkDetails
        for (Map.Entry<String, Map<String, Set<LinkDetails>>> entry : result.entrySet()) {
            Map<String, Set<LinkDetails>> dependencies = entry.getValue();
            for (Map.Entry<String, Set<LinkDetails>> depEntry : dependencies.entrySet()) {
                Set<LinkDetails> linkDetailsSet = depEntry.getValue();
                assertNotNull(linkDetailsSet, "LinkDetails set should not be null");
                assertFalse(linkDetailsSet.isEmpty(), "LinkDetails set should not be empty");
            }
        }
    }

    /**
     * Pipeline with include/exclude masks, verifies filtering in the output JSON.
     * Uses include="service.*" and empty exclude to filter only service package classes.
     */
    @Test
    void e2e_scanBytecodeDetails_withMasks_filtersClasses() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of("service.*"),
                List.of()
        );

        scanBytecodeService.executeDetails(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        Map<String, Map<String, Set<LinkDetails>>> result = readOutputJson(outputFile);

        // Only classes from service package should be present in keys
        for (String key : result.keySet()) {
            assertTrue(key.contains(".service."), "Key " + key + " should contain '.service.'");
        }
    }

    /**
     * Pipeline with merge-inner-classes=true, verifies merging of inner classes.
     */
    @Test
    void e2e_scanBytecodeDetails_withMergeInnerClasses_mergesInner() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of(),
                true
        );

        scanBytecodeService.executeDetails(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        Map<String, Map<String, Set<LinkDetails>>> result = readOutputJson(outputFile);

        // Outer$Inner should be absent from keys when merged
        assertFalse(result.containsKey("spring.twin.testee.Outer$Inner"),
                "Outer$Inner should be absent from keys when mergeInnerClasses=true");

        // Outer should be present
        assertTrue(result.containsKey("spring.twin.testee.Outer"),
                "Outer should be present in keys");
    }

    /**
     * Pipeline with merge-inner-classes=false, verifies separate entries for inner classes.
     */
    @Test
    void e2e_scanBytecodeDetails_withoutMergeInnerClasses_separateEntries() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of(),
                false
        );

        scanBytecodeService.executeDetails(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        Map<String, Map<String, Set<LinkDetails>>> result = readOutputJson(outputFile);

        // Outer$Inner should be present as a separate key
        assertTrue(result.containsKey("spring.twin.testee.Outer$Inner"),
                "Outer$Inner should be present as a separate key when mergeInnerClasses=false");
    }

    /**
     * Verifies that each LinkDetails in JSON has fields 'type' and 'details'.
     */
    @Test
    void e2e_scanBytecodeDetails_jsonFormat_linkDetailsStructure() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of()
        );

        scanBytecodeService.executeDetails(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        Map<String, Map<String, Set<LinkDetails>>> result = readOutputJson(outputFile);

        // Parse as raw JSON to verify field structure
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, List<Map<String, String>>>> rawResult =
                objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});

        // Verify each LinkDetails has type and details fields
        for (Map.Entry<String, Map<String, List<Map<String, String>>>> entry : rawResult.entrySet()) {
            Map<String, List<Map<String, String>>> dependencies = entry.getValue();
            for (Map.Entry<String, List<Map<String, String>>> depEntry : dependencies.entrySet()) {
                List<Map<String, String>> linkDetailsList = depEntry.getValue();
                for (Map<String, String> linkDetails : linkDetailsList) {
                    assertTrue(linkDetails.containsKey("type"),
                            "Each LinkDetails should have 'type' field");
                    assertTrue(linkDetails.containsKey("details"),
                            "Each LinkDetails should have 'details' field");
                    assertNotNull(linkDetails.get("type"), "'type' field should not be null");
                    assertNotNull(linkDetails.get("details"), "'details' field should not be null");
                }
            }
        }
    }

    /**
     * Verifies that keys at first and second levels are sorted alphabetically.
     */
    @Test
    void e2e_scanBytecodeDetails_jsonFormat_sortedKeys() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of()
        );

        scanBytecodeService.executeDetails(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        Map<String, Map<String, Set<LinkDetails>>> result = readOutputJson(outputFile);

        // Check that first-level keys are sorted alphabetically
        List<String> firstLevelKeys = List.copyOf(result.keySet());
        for (int i = 1; i < firstLevelKeys.size(); i++) {
            assertTrue(firstLevelKeys.get(i - 1).compareTo(firstLevelKeys.get(i)) <= 0,
                    "First-level keys should be sorted alphabetically");
        }

        // Check that second-level keys are sorted alphabetically within each entry
        for (Map.Entry<String, Map<String, Set<LinkDetails>>> entry : result.entrySet()) {
            List<String> secondLevelKeys = List.copyOf(entry.getValue().keySet());
            for (int i = 1; i < secondLevelKeys.size(); i++) {
                assertTrue(secondLevelKeys.get(i - 1).compareTo(secondLevelKeys.get(i)) <= 0,
                        "Second-level keys should be sorted alphabetically");
            }
        }
    }

    /**
     * Verifies that all link types are present in the output JSON:
     * SUPERCLASS, INTERFACE, FIELD, METHOD, CLASS_ANNOTATION, FIELD_ANNOTATION,
     * METHOD_ANNOTATION, METHOD_ARG_ANNOTATION, STATIC_BLOCK
     */
    @Test
    void e2e_scanBytecodeDetails_allLinkTypesPresent() throws IOException {
        Path outputFile = tempDir.resolve("dependencies.json");

        // Use empty masks to include all classes
        ScanBytecodeParams params = new ScanBytecodeParams(
                classesDir,
                outputFile,
                List.of(),
                List.of()
        );

        scanBytecodeService.executeDetails(params);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        Map<String, Map<String, Set<LinkDetails>>> result = readOutputJson(outputFile);

        // Collect all link types present in the output
        Set<LinkType> foundTypes = java.util.EnumSet.noneOf(LinkType.class);
        for (Map.Entry<String, Map<String, Set<LinkDetails>>> entry : result.entrySet()) {
            Map<String, Set<LinkDetails>> dependencies = entry.getValue();
            for (Set<LinkDetails> linkDetailsSet : dependencies.values()) {
                for (LinkDetails linkDetails : linkDetailsSet) {
                    foundTypes.add(linkDetails.type());
                }
            }
        }

        // Verify all expected link types are present
        assertTrue(foundTypes.contains(LinkType.SUPERCLASS),
                "Should contain SUPERCLASS link type");
        assertTrue(foundTypes.contains(LinkType.INTERFACE),
                "Should contain INTERFACE link type");
        assertTrue(foundTypes.contains(LinkType.FIELD),
                "Should contain FIELD link type");
        assertTrue(foundTypes.contains(LinkType.METHOD),
                "Should contain METHOD link type");
        assertTrue(foundTypes.contains(LinkType.CLASS_ANNOTATION),
                "Should contain CLASS_ANNOTATION link type");
        assertTrue(foundTypes.contains(LinkType.FIELD_ANNOTATION),
                "Should contain FIELD_ANNOTATION link type");
        assertTrue(foundTypes.contains(LinkType.METHOD_ANNOTATION),
                "Should contain METHOD_ANNOTATION link type");
        assertTrue(foundTypes.contains(LinkType.METHOD_ARG_ANNOTATION),
                "Should contain METHOD_ARG_ANNOTATION link type");
        assertTrue(foundTypes.contains(LinkType.STATIC_BLOCK),
                "Should contain STATIC_BLOCK link type");
    }
}