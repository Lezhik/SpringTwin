package spring.twin.scan;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AnnotationTypeExtractor}.
 * Tests use compiled sample classes from {@code src/test/java/spring/twin/testee/}.
 */
class AnnotationTypeExtractorTest {

    private final AnnotationTypeExtractor extractor = new AnnotationTypeExtractor();

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
    void testExtract_classAnnotation_returnsAnnotationType() throws IOException {
        byte[] classBytes = loadClassBytes("AnnotatedClass");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.lang.Deprecated"));
    }

    @Test
    void testExtract_fieldAnnotation_returnsAnnotationType() throws IOException {
        byte[] classBytes = loadClassBytes("AnnotatedClass");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.lang.SuppressWarnings"));
    }

    @Test
    void testExtract_methodAnnotation_returnsAnnotationType() throws IOException {
        byte[] classBytes = loadClassBytes("AnnotatedClass");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("java.lang.Override"));
    }

    @Test
    void testExtract_customAnnotation_returnsFqcn() throws IOException {
        byte[] classBytes = loadClassBytes("CustomAnnotatedClass");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.contains("spring.twin.testee.CustomAnnotation"));
    }

    @Test
    void testExtract_noAnnotations_returnsEmptySet() throws IOException {
        byte[] classBytes = loadClassBytes("InheritanceBase");
        Set<String> result = extractor.extract(classBytes);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtract_nullBytes_throwsException() {
        assertThrows(Exception.class, () -> extractor.extract(null));
    }

}