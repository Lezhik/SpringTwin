package spring.twin.scan.e2e;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * End-to-End tests for the complete scan-bytecode pipeline.
 * <p>
 * Tests launch the application through CLI parameters and verify the output JSON file.
 */
@SpringBootTest
class ScanBytecodeE2eTest {

    @TempDir
    Path tempDir;

    Path classesDir;

    @Test
    void e2e_scanBytecode_noMasks_producesDependenciesJson() {
        // TODO: implement - run with --classes and --output without masks
        // → file dependencies.json created and contains correct entries
    }

    @Test
    void e2e_scanBytecode_withIncludeMask_filtersClasses() {
        // TODO: implement - run with --include com.example.*
        // → only classes matching the mask
    }

    @Test
    void e2e_scanBytecode_withExcludeMask_excludesClasses() {
        // TODO: implement - run with --exclude *Test
        // → excluded classes are absent
    }

    @Test
    void e2e_scanBytecode_emptyDirectory_producesEmptyJson() {
        // TODO: implement - run with empty directory
        // → {}
    }

    @Test
    void e2e_scanBytecode_outputFile_hasSortedKeys() {
        // TODO: implement - keys in output JSON are sorted
    }

    @Test
    void e2e_scanBytecode_outputFile_hasSortedValues() {
        // TODO: implement - dependency arrays are sorted
    }

    @Test
    void e2e_scanBytecode_inheritanceDependency_detected() {
        // TODO: implement - inheritance dependency is detected
    }

    @Test
    void e2e_scanBytecode_fieldDependency_detected() {
        // TODO: implement - field dependency is detected
    }

    @Test
    void e2e_scanBytecode_methodDependency_detected() {
        // TODO: implement - method dependency is detected
    }

    @Test
    void e2e_scanBytecode_annotationDependency_detected() {
        // TODO: implement - annotation dependency is detected
    }

    @Test
    void e2e_scanBytecode_genericDependency_detected() {
        // TODO: implement - generic types are extracted
    }

    @Test
    void e2e_scanBytecode_arrayDependency_baseTypeExtracted() {
        // TODO: implement - arrays reference base type
    }
}