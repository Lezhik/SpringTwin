package spring.twin.scan;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link InheritanceExtractor}.
 * Tests use compiled sample classes from {@code src/test/java/spring/twin/testee/}.
 */
class InheritanceExtractorTest {

    private final InheritanceExtractor extractor = new InheritanceExtractor();

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
    void testExtract_simpleClass_noInheritance_returnsEmpty() throws IOException {
        // Class without explicit extends (only Object) -> should not include java.lang.Object
        byte[] classBytes = loadClassBytes("InheritanceBase");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.isEmpty() || !result.contains("java.lang.Object"));
    }

    @Test
    void testExtract_classExtendsAnother_returnsSuperClass() throws IOException {
        // InheritanceChild extends InheritanceBase -> should contain spring.twin.testee.InheritanceBase
        byte[] classBytes = loadClassBytes("InheritanceChild");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("spring.twin.testee.InheritanceBase"));
    }

    @Test
    void testExtract_classImplementsInterface_returnsInterface() throws IOException {
        // InheritanceImpl implements Serializable -> should contain java.io.Serializable
        byte[] classBytes = loadClassBytes("InheritanceImpl");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.io.Serializable"));
    }

    @Test
    void testExtract_classExtendsGeneric_returnsGenericTypes() throws IOException {
        // InheritanceGenericChild extends ArrayList<String> -> should contain java.util.ArrayList and java.lang.String
        byte[] classBytes = loadClassBytes("InheritanceGenericChild");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.util.ArrayList"));
        assertTrue(result.contains("java.lang.String"));
    }

    @Test
    void testExtract_interfaceExtendsInterface_returnsParentInterface() throws IOException {
        // InheritanceChildInterface extends Serializable -> should contain java.io.Serializable
        byte[] classBytes = loadClassBytes("InheritanceChildInterface");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.io.Serializable"));
    }

    @Test
    void testExtract_nullBytes_throwsException() {
        // null -> should throw exception
        assertThrows(Exception.class, () -> extractor.extract(null));
    }
}