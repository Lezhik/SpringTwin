package spring.twin.scan;

import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ScanBytecodeParams.
 */
class ScanBytecodeParamsTest {

    private static final Path CLASSES_DIR = Paths.get("target/classes");
    private static final Path OUTPUT_FILE = Paths.get("output.json");

    @Test
    void testOf_createsParamsWithParsedMasks() {
        ScanBytecodeParams params = ScanBytecodeParams.of(
                CLASSES_DIR,
                OUTPUT_FILE,
                "com.example.*;*.service.*",
                "*.internal.*"
        );

        assertEquals(CLASSES_DIR, params.classesDir());
        assertEquals(OUTPUT_FILE, params.outputFile());
        assertEquals(List.of("com.example.*", "*.service.*"), params.includeMasks());
        assertEquals(List.of("*.internal.*"), params.excludeMasks());
    }

    @Test
    void testOf_emptyIncludeRaw_returnsEmptyIncludeList() {
        ScanBytecodeParams params = ScanBytecodeParams.of(
                CLASSES_DIR,
                OUTPUT_FILE,
                "",
                "*.test.*"
        );

        assertEquals(List.of(), params.includeMasks());
        assertEquals(List.of("*.test.*"), params.excludeMasks());
    }

    @Test
    void testOf_emptyExcludeRaw_returnsEmptyExcludeList() {
        ScanBytecodeParams params = ScanBytecodeParams.of(
                CLASSES_DIR,
                OUTPUT_FILE,
                "com.example.*",
                ""
        );

        assertEquals(List.of("com.example.*"), params.includeMasks());
        assertEquals(List.of(), params.excludeMasks());
    }

    @Test
    void testOf_nullIncludeRaw_returnsEmptyIncludeList() {
        ScanBytecodeParams params = ScanBytecodeParams.of(
                CLASSES_DIR,
                OUTPUT_FILE,
                null,
                "*.test.*"
        );

        assertEquals(List.of(), params.includeMasks());
        assertEquals(List.of("*.test.*"), params.excludeMasks());
    }

    @Test
    void testOf_nullExcludeRaw_returnsEmptyExcludeList() {
        ScanBytecodeParams params = ScanBytecodeParams.of(
                CLASSES_DIR,
                OUTPUT_FILE,
                "com.example.*",
                null
        );

        assertEquals(List.of("com.example.*"), params.includeMasks());
        assertEquals(List.of(), params.excludeMasks());
    }

    @Test
    void testOf_multipleMasksSeparatedBySemicolon() {
        ScanBytecodeParams params = ScanBytecodeParams.of(
                CLASSES_DIR,
                OUTPUT_FILE,
                "com.example.*;com.demo.*;*.service.*",
                "*.internal.*;*.legacy.*"
        );

        assertEquals(List.of("com.example.*", "com.demo.*", "*.service.*"), params.includeMasks());
        assertEquals(List.of("*.internal.*", "*.legacy.*"), params.excludeMasks());
    }

    @Test
    void testOf_trimsWhitespaceAroundMasks() {
        ScanBytecodeParams params = ScanBytecodeParams.of(
                CLASSES_DIR,
                OUTPUT_FILE,
                "  com.example.*  ;  *.service.* ",
                "  *.internal.*  ;   *.legacy.*   "
        );

        assertEquals(List.of("com.example.*", "*.service.*"), params.includeMasks());
        assertEquals(List.of("*.internal.*", "*.legacy.*"), params.excludeMasks());
    }

    @Test
    void testOf_filtersEmptyMasks() {
        ScanBytecodeParams params = ScanBytecodeParams.of(
                CLASSES_DIR,
                OUTPUT_FILE,
                "com.example.*;;*.service.*;;",
                "*.internal.*;;"
        );

        assertEquals(List.of("com.example.*", "*.service.*"), params.includeMasks());
        assertEquals(List.of("*.internal.*"), params.excludeMasks());
    }

    @Test
    void testOf_withMergeInnerClassesTrue_storesTrue() {
        ScanBytecodeParams params = ScanBytecodeParams.of(
                CLASSES_DIR,
                OUTPUT_FILE,
                "com.example.*",
                "*.internal.*",
                true
        );

        assertTrue(params.mergeInnerClasses());
    }

    @Test
    void testOf_withMergeInnerClassesFalse_storesFalse() {
        ScanBytecodeParams params = ScanBytecodeParams.of(
                CLASSES_DIR,
                OUTPUT_FILE,
                "com.example.*",
                "*.internal.*",
                false
        );

        assertFalse(params.mergeInnerClasses());
    }

    @Test
    void testConstructor_withMergeInnerClasses_storesValue() {
        List<String> includeMasks = List.of("com.example.*");
        List<String> excludeMasks = List.of("*.internal.*");

        ScanBytecodeParams paramsTrue = new ScanBytecodeParams(
                CLASSES_DIR, OUTPUT_FILE, includeMasks, excludeMasks, true
        );
        ScanBytecodeParams paramsFalse = new ScanBytecodeParams(
                CLASSES_DIR, OUTPUT_FILE, includeMasks, excludeMasks, false
        );

        assertTrue(paramsTrue.mergeInnerClasses());
        assertFalse(paramsFalse.mergeInnerClasses());
    }

    @Test
    void testBackwardCompatibleConstructor_defaultMergeIsTrue() {
        List<String> includeMasks = List.of("com.example.*");
        List<String> excludeMasks = List.of("*.internal.*");

        ScanBytecodeParams params = new ScanBytecodeParams(
                CLASSES_DIR, OUTPUT_FILE, includeMasks, excludeMasks
        );

        assertTrue(params.mergeInnerClasses());
    }

    @Test
    void testBackwardCompatibleOf_defaultMergeIsTrue() {
        ScanBytecodeParams params = ScanBytecodeParams.of(
                CLASSES_DIR,
                OUTPUT_FILE,
                "com.example.*",
                "*.internal.*"
        );

        assertTrue(params.mergeInnerClasses());
    }
}