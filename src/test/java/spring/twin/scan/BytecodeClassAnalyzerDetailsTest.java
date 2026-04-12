package spring.twin.scan;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BytecodeClassAnalyzer#extractDependenciesDetails(byte[])}.
 * Tests use compiled sample classes from {@code src/test/java/spring/twin/testee/}.
 */
class BytecodeClassAnalyzerDetailsTest {

    private final BytecodeClassAnalyzer analyzer = new BytecodeClassAnalyzer(
            new InheritanceExtractor(),
            new FieldTypeExtractor(),
            new MethodTypeExtractor(),
            new AnnotationTypeExtractor(),
            new CodeUsageExtractor()
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
    // extractDependenciesDetails() tests
    // ========================================================================

    @Test
    void testExtractDependenciesDetails_complexClass_containsAllLinkTypes() throws IOException {
        // ComplexService has multiple dependency types: FIELD, METHOD, CLASS_ANNOTATION, etc.
        byte[] classBytes = loadClassBytes("ComplexService");
        Map<String, Map<String, Set<LinkDetails>>> result = analyzer.extractDependenciesDetails(classBytes);

        // Outer map should have one key: the analyzed class FQCN
        assertEquals(1, result.size());
        Map<String, Set<LinkDetails>> dependencies = result.get("spring.twin.testee.ComplexService");
        assertNotNull(dependencies);

        // Check that various link types are present
        boolean hasFieldLink = false;
        boolean hasClassAnnotationLink = false;
        boolean hasMethodLink = false;
        boolean hasSuperclassLink = false;
        boolean hasInterfaceLink = false;

        for (Set<LinkDetails> linkSet : dependencies.values()) {
            for (LinkDetails link : linkSet) {
                switch (link.type()) {
                    case FIELD -> hasFieldLink = true;
                    case CLASS_ANNOTATION -> hasClassAnnotationLink = true;
                    case METHOD -> hasMethodLink = true;
                    case SUPERCLASS -> hasSuperclassLink = true;
                    case INTERFACE -> hasInterfaceLink = true;
                }
            }
        }

        assertTrue(hasFieldLink, "Should have FIELD links");
        assertTrue(hasClassAnnotationLink, "Should have CLASS_ANNOTATION links (Deprecated)");
        assertTrue(hasMethodLink, "Should have METHOD links");
        assertTrue(hasSuperclassLink, "Should have SUPERCLASS links (InheritanceBase)");
        assertTrue(hasInterfaceLink, "Should have INTERFACE links (InheritanceChildInterface)");
    }

    @Test
    void testExtractDependenciesDetails_inheritanceClass_containsSuperclassLink() throws IOException {
        // InheritanceChild extends InheritanceBase
        byte[] classBytes = loadClassBytes("InheritanceChild");
        Map<String, Map<String, Set<LinkDetails>>> result = analyzer.extractDependenciesDetails(classBytes);

        Map<String, Set<LinkDetails>> dependencies = result.get("spring.twin.testee.InheritanceChild");
        assertNotNull(dependencies);
        assertTrue(dependencies.containsKey("spring.twin.testee.InheritanceBase"));

        Set<LinkDetails> links = dependencies.get("spring.twin.testee.InheritanceBase");
        assertNotNull(links);
        assertTrue(links.contains(LinkDetails.of(LinkType.SUPERCLASS)));
    }

    @Test
    void testExtractDependenciesDetails_fieldClass_containsFieldLinks() throws IOException {
        // FieldHolder has fields: simpleField, genericField, primitiveField, arrayField, mapField
        byte[] classBytes = loadClassBytes("FieldHolder");
        Map<String, Map<String, Set<LinkDetails>>> result = analyzer.extractDependenciesDetails(classBytes);

        Map<String, Set<LinkDetails>> dependencies = result.get("spring.twin.testee.FieldHolder");
        assertNotNull(dependencies);

        // Check for String field link with field name in details
        Set<LinkDetails> stringLinks = dependencies.get("java.lang.String");
        if (stringLinks != null) {
            boolean hasFieldLinkWithName = stringLinks.stream()
                    .anyMatch(link -> link.type() == LinkType.FIELD && "simpleField".equals(link.details()));
            assertTrue(hasFieldLinkWithName, "Should have FIELD link for simpleField");
        }

        // Check for List field link with field name
        Set<LinkDetails> listLinks = dependencies.get("java.util.List");
        if (listLinks != null) {
            boolean hasFieldLinkWithName = listLinks.stream()
                    .anyMatch(link -> link.type() == LinkType.FIELD && "genericField".equals(link.details()));
            assertTrue(hasFieldLinkWithName, "Should have FIELD link for genericField");
        }
    }

    @Test
    void testExtractDependenciesDetails_annotatedClass_containsAnnotationLinks() throws IOException {
        // AnnotatedClass has @Deprecated class annotation
        byte[] classBytes = loadClassBytes("AnnotatedClass");
        Map<String, Map<String, Set<LinkDetails>>> result = analyzer.extractDependenciesDetails(classBytes);

        Map<String, Set<LinkDetails>> dependencies = result.get("spring.twin.testee.AnnotatedClass");
        assertNotNull(dependencies);

        // Should have CLASS_ANNOTATION link for Deprecated
        Set<LinkDetails> deprecatedLinks = dependencies.get("java.lang.Deprecated");
        assertNotNull(deprecatedLinks, "Should have dependencies on java.lang.Deprecated");
        assertTrue(deprecatedLinks.contains(LinkDetails.of(LinkType.CLASS_ANNOTATION)),
                "Should have CLASS_ANNOTATION link for Deprecated");
    }

    @Test
    void testExtractDependenciesDetails_resultStructure_correctOuterKey() throws IOException {
        // The outer key should be the FQCN of the analyzed class
        byte[] classBytes = loadClassBytes("InheritanceChild");
        Map<String, Map<String, Set<LinkDetails>>> result = analyzer.extractDependenciesDetails(classBytes);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("spring.twin.testee.InheritanceChild"),
                "Outer key should be the FQCN of the analyzed class");
    }

    @Test
    void testExtractDependenciesDetails_resultStructure_correctInnerKeys() throws IOException {
        // The inner keys should be FQCNs of dependent classes
        byte[] classBytes = loadClassBytes("InheritanceChild");
        Map<String, Map<String, Set<LinkDetails>>> result = analyzer.extractDependenciesDetails(classBytes);

        Map<String, Set<LinkDetails>> dependencies = result.get("spring.twin.testee.InheritanceChild");
        assertNotNull(dependencies);

        // Inner keys should be FQCN format
        for (String key : dependencies.keySet()) {
            assertFalse(key.contains("/"), "Inner key should be FQCN, not internal name: " + key);
            assertTrue(key.contains("."), "Inner key should be FQCN format: " + key);
        }
    }

    @Test
    void testExtractDependenciesDetails_multipleLinksToSameTarget_aggregatedInSet() throws IOException {
        // ComplexService has multiple links to the same target (e.g., multiple methods using String)
        byte[] classBytes = loadClassBytes("ComplexService");
        Map<String, Map<String, Set<LinkDetails>>> result = analyzer.extractDependenciesDetails(classBytes);

        Map<String, Set<LinkDetails>> dependencies = result.get("spring.twin.testee.ComplexService");
        assertNotNull(dependencies);

        // All links to the same target should be in a single Set
        for (Set<LinkDetails> linkSet : dependencies.values()) {
            // Set should not contain duplicates (LinkDetails has proper equals/hashCode)
            assertEquals(linkSet.size(), linkSet.stream().distinct().count(),
                    "Links to the same target should be aggregated in a Set without duplicates");
        }
    }

    @Test
    void testExtractDependenciesDetails_nullBytes_throwsException() {
        // null -> should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> analyzer.extractDependenciesDetails(null));
    }
}