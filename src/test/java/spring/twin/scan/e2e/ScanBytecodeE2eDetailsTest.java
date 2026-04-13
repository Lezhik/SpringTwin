package spring.twin.scan.e2e;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import spring.twin.scan.LinkDetails;
import spring.twin.scan.ScanBytecodeParams;
import spring.twin.scan.ScanBytecodeService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
        // TODO: implement
    }

    /**
     * Pipeline with include/exclude masks, verifies filtering in the output JSON.
     */
    @Test
    void e2e_scanBytecodeDetails_withMasks_filtersClasses() throws IOException {
        // TODO: implement
    }

    /**
     * Pipeline with merge-inner-classes=true, verifies merging of inner classes.
     */
    @Test
    void e2e_scanBytecodeDetails_withMergeInnerClasses_mergesInner() throws IOException {
        // TODO: implement
    }

    /**
     * Pipeline with merge-inner-classes=false, verifies separate entries for inner classes.
     */
    @Test
    void e2e_scanBytecodeDetails_withoutMergeInnerClasses_separateEntries() throws IOException {
        // TODO: implement
    }

    /**
     * Verifies that each LinkDetails in JSON has fields 'type' and 'details'.
     */
    @Test
    void e2e_scanBytecodeDetails_jsonFormat_linkDetailsStructure() throws IOException {
        // TODO: implement
    }

    /**
     * Verifies that keys at first and second levels are sorted alphabetically.
     */
    @Test
    void e2e_scanBytecodeDetails_jsonFormat_sortedKeys() throws IOException {
        // TODO: implement
    }

    /**
     * Verifies that all link types are present in the output JSON:
     * SUPERCLASS, INTERFACE, FIELD, METHOD, CLASS_ANNOTATION, FIELD_ANNOTATION,
     * METHOD_ANNOTATION, METHOD_ARG_ANNOTATION, STATIC_BLOCK
     */
    @Test
    void e2e_scanBytecodeDetails_allLinkTypesPresent() throws IOException {
        // TODO: implement
    }
}