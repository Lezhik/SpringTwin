package spring.twin.scan;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MethodTypeExtractor}.
 * Tests use compiled sample classes from {@code src/test/java/spring/twin/testee/}.
 */
class MethodTypeExtractorTest {

    private final MethodTypeExtractor extractor = new MethodTypeExtractor();

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
    void testExtract_simpleReturnType_returnsType() throws IOException {
        byte[] classBytes = loadClassBytes("MethodHolder");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.lang.String"));
    }

    @Test
    void testExtract_simpleParameter_returnsParamType() throws IOException {
        byte[] classBytes = loadClassBytes("MethodHolder");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.lang.String"));
    }

    @Test
    void testExtract_genericReturnType_returnsTypeAndParams() throws IOException {
        byte[] classBytes = loadClassBytes("MethodHolder");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.util.List"));
        assertTrue(result.contains("java.lang.String"));
    }

    @Test
    void testExtract_genericParameter_returnsTypeAndParams() throws IOException {
        byte[] classBytes = loadClassBytes("MethodHolder");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.util.Map"));
        assertTrue(result.contains("java.lang.String"));
        assertTrue(result.contains("java.lang.Integer"));
    }

    @Test
    void testExtract_primitiveReturnType_notIncluded() throws IOException {
        byte[] classBytes = loadClassBytes("MethodHolder");
        Set<String> result = extractor.extract(classBytes);
        assertFalse(result.contains("int"));
    }

    @Test
    void testExtract_arrayParameter_returnsBaseType() throws IOException {
        byte[] classBytes = loadClassBytes("MethodHolder");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.lang.String"));
    }

    @Test
    void testExtract_constructorParameter_returnsParamType() throws IOException {
        byte[] classBytes = loadClassBytes("MethodHolder");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.lang.String"));
    }

    @Test
    void testExtract_noMethods_returnsEmptySet() throws IOException {
        byte[] classBytes = loadClassBytes("InheritanceBase");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtract_nullBytes_throwsException() {
        assertThrows(Exception.class, () -> extractor.extract(null));
    }

}