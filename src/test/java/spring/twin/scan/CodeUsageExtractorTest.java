package spring.twin.scan;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CodeUsageExtractor}.
 * Tests use compiled sample classes from {@code src/test/java/spring/twin/testee/}.
 */
class CodeUsageExtractorTest {

    private final CodeUsageExtractor extractor = new CodeUsageExtractor();

    /**
     * Loads bytecode of a testee class from the classpath.
     *
     * @param className the simple name of the class in spring.twin.testee package
     * @return the bytecode of the class
     * @throws IOException if the class file cannot be loaded
     */
    private byte[] loadClassBytes(String className) throws IOException {
        String resourcePath = "/spring/twin/testee/" + className + ".class";
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Cannot find resource: " + resourcePath);
            }
            return is.readAllBytes();
        }
    }

    // ========================================================================
    // extract() tests
    // ========================================================================

    @Test
    void testExtract_newObject_returnsObjectType() throws IOException {
        // new ArrayList<>() -> contains java.util.ArrayList
        byte[] classBytes = loadClassBytes("CodeUsageExample");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.util.ArrayList"));
    }

    @Test
    void testExtract_methodCall_returnsOwnerType() throws IOException {
        // String.valueOf(42) -> contains java.lang.String
        byte[] classBytes = loadClassBytes("CodeUsageExample");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.lang.String"));
    }

    @Test
    void testExtract_castType_returnsTargetType() throws IOException {
        // (String) obj -> contains java.lang.String
        byte[] classBytes = loadClassBytes("CodeUsageExample");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.lang.String"));
    }

    @Test
    void testExtract_instanceofCheck_returnsCheckedType() throws IOException {
        // obj instanceof String -> contains java.lang.String
        byte[] classBytes = loadClassBytes("CodeUsageExample");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.lang.String"));
    }

    @Test
    void testExtract_fieldAccess_returnsOwnerType() throws IOException {
        // System.out -> contains java.lang.System
        byte[] classBytes = loadClassBytes("CodeUsageExample");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.lang.System"));
    }

    @Test
    void testExtract_staticInit_returnsUsedTypes() throws IOException {
        // Static block with System.setProperty -> contains java.lang.System
        byte[] classBytes = loadClassBytes("CodeUsageExample");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.lang.System"));
    }

    @Test
    void testExtract_noCode_returnsEmptySet() throws IOException {
        // Interface without implementation -> empty set
        byte[] classBytes = loadClassBytes("InheritanceChildInterface");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtract_nullBytes_throwsException() {
        // null -> should throw exception
        assertThrows(Exception.class, () -> extractor.extract(null));
    }
}