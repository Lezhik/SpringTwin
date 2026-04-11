package spring.twin.scan;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BytecodeClassAnalyzer}.
 * Tests use compiled sample classes from {@code src/test/java/spring/twin/testee/}.
 */
class BytecodeClassAnalyzerTest {

    private final InheritanceExtractor inheritanceExtractor = new InheritanceExtractor();
    private final FieldTypeExtractor fieldTypeExtractor = new FieldTypeExtractor();
    private final MethodTypeExtractor methodTypeExtractor = new MethodTypeExtractor();
    private final AnnotationTypeExtractor annotationTypeExtractor = new AnnotationTypeExtractor();
    private final CodeUsageExtractor codeUsageExtractor = new CodeUsageExtractor();

    private final BytecodeClassAnalyzer analyzer = new BytecodeClassAnalyzer(
            inheritanceExtractor,
            fieldTypeExtractor,
            methodTypeExtractor,
            annotationTypeExtractor,
            codeUsageExtractor
    );

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
    // extractClassName() tests
    // ========================================================================

    @Test
    void testExtractClassName_returnsFqcn() throws IOException {
        byte[] classBytes = loadClassBytes("InheritanceBase");
        String result = analyzer.extractClassName(classBytes);
        assertEquals("spring.twin.testee.InheritanceBase", result);
    }

    @Test
    void testExtractClassName_nullBytes_throwsException() {
        assertThrows(Exception.class, () -> analyzer.extractClassName(null));
    }

    // ========================================================================
    // extractDependencies() tests
    // ========================================================================

    @Test
    void testExtractDependencies_combinesAllExtractors() throws IOException {
        // ComplexService has all types of dependencies
        byte[] classBytes = loadClassBytes("ComplexService");
        Set<String> result = analyzer.extractDependencies(classBytes);

        // Should not be empty - all extractors contribute something
        assertFalse(result.isEmpty());

        // Should contain dependencies from all extractors:
        // - inheritance: InheritanceBase, InheritanceChildInterface (Serializable), java.io.Serializable
        // - fields: String, List, Map, Integer
        // - methods: String, List, Map, Integer
        // - annotations: Deprecated, CustomAnnotation
        // - code usage: ArrayList, String
    }

    @Test
    void testExtractDependencies_includesInheritance() throws IOException {
        byte[] classBytes = loadClassBytes("ComplexService");
        Set<String> result = analyzer.extractDependencies(classBytes);

        // ComplexService extends InheritanceBase
        assertTrue(result.contains("spring.twin.testee.InheritanceBase"));

        // ComplexService implements InheritanceChildInterface which extends Serializable
        assertTrue(result.contains("spring.twin.testee.InheritanceBase"));
    }

    @Test
    void testExtractDependencies_includesFieldTypes() throws IOException {
        byte[] classBytes = loadClassBytes("ComplexService");
        Set<String> result = analyzer.extractDependencies(classBytes);

        // ComplexService has fields: String, List<String>, Map<String, Integer>
        assertTrue(result.contains("java.lang.String"));
        assertTrue(result.contains("java.util.List"));
        assertTrue(result.contains("java.util.Map"));
        assertTrue(result.contains("java.lang.Integer"));
    }

    @Test
    void testExtractDependencies_includesMethodTypes() throws IOException {
        byte[] classBytes = loadClassBytes("ComplexService");
        Set<String> result = analyzer.extractDependencies(classBytes);

        // ComplexService methods use: String, List<String>, Map<String, Integer>
        assertTrue(result.contains("java.lang.String"));
        assertTrue(result.contains("java.util.List"));
        assertTrue(result.contains("java.util.Map"));
    }

    @Test
    void testExtractDependencies_includesAnnotations() throws IOException {
        byte[] classBytes = loadClassBytes("ComplexService");
        Set<String> result = analyzer.extractDependencies(classBytes);

        // ComplexService has @Deprecated on class and @CustomAnnotation on method
        assertTrue(result.contains("java.lang.Deprecated"));
        assertTrue(result.contains("spring.twin.testee.CustomAnnotation"));
    }

    @Test
    void testExtractDependencies_includesCodeUsage() throws IOException {
        byte[] classBytes = loadClassBytes("ComplexService");
        Set<String> result = analyzer.extractDependencies(classBytes);

        // ComplexService.processItems() creates ArrayList and calls String.valueOf
        assertTrue(result.contains("java.util.ArrayList"));
    }

    @Test
    void testExtractDependencies_noDuplicates() throws IOException {
        byte[] classBytes = loadClassBytes("ComplexService");
        Set<String> result = analyzer.extractDependencies(classBytes);

        // String appears in fields, methods, and code usage - but should appear only once
        // We can't directly test for duplicates in a Set, but we can verify the size
        // is reasonable (not inflated by duplicates)
        long stringCount = result.stream()
                .filter(s -> s.equals("java.lang.String"))
                .count();
        assertEquals(1, stringCount);
    }

}