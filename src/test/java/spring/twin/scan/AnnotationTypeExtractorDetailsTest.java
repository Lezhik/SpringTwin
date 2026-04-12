package spring.twin.scan;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AnnotationTypeExtractor#extractDetails(byte[])}.
 * Tests use compiled sample classes from {@code src/test/java/spring/twin/testee/}.
 */
class AnnotationTypeExtractorDetailsTest {

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
    // extractDetails() tests
    // ========================================================================

    @Test
    void testExtractDetails_classAnnotation_returnsClassAnnotationLink() throws IOException {
        // AnnotatedClass has @Deprecated at class level -> map contains FQCN java.lang.Deprecated
        // with value {LinkDetails.of(LinkType.CLASS_ANNOTATION)}
        byte[] classBytes = loadClassBytes("AnnotatedClass");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("java.lang.Deprecated"));
        Set<LinkDetails> links = result.get("java.lang.Deprecated");
        assertNotNull(links);
        assertEquals(1, links.size());
        assertTrue(links.contains(LinkDetails.of(LinkType.CLASS_ANNOTATION)));
    }

    @Test
    void testExtractDetails_fieldAnnotation_returnsFieldAnnotationLink() throws IOException {
        // FieldAnnotatedClass has field annotatedField with @CustomAnnotation -> map contains
        // FQCN spring.twin.testee.CustomAnnotation with value {LinkDetails.of(LinkType.FIELD_ANNOTATION, fieldName)}
        byte[] classBytes = loadClassBytes("FieldAnnotatedClass");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("spring.twin.testee.CustomAnnotation"));
        Set<LinkDetails> links = result.get("spring.twin.testee.CustomAnnotation");
        assertNotNull(links);
        assertEquals(1, links.size());
        assertTrue(links.contains(LinkDetails.of(LinkType.FIELD_ANNOTATION, "annotatedField")));
    }

    @Test
    void testExtractDetails_methodAnnotation_returnsMethodAnnotationLink() throws IOException {
        // MethodAnnotatedClass has method annotatedMethod with @CustomAnnotation -> map contains
        // FQCN spring.twin.testee.CustomAnnotation with value {LinkDetails.of(LinkType.METHOD_ANNOTATION, methodSignature)}
        byte[] classBytes = loadClassBytes("MethodAnnotatedClass");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("spring.twin.testee.CustomAnnotation"));
        Set<LinkDetails> links = result.get("spring.twin.testee.CustomAnnotation");
        assertNotNull(links);
        assertEquals(1, links.size());
        // Signature format: Lspring/twin/testee/MethodAnnotatedClass;annotatedMethod()I
        assertTrue(links.stream().anyMatch(link ->
            link.type() == LinkType.METHOD_ANNOTATION &&
            link.details().contains("annotatedMethod()")
        ));
    }

    @Test
    void testExtractDetails_parameterAnnotation_returnsMethodArgAnnotationLink() throws IOException {
        // ParameterAnnotatedClass has method methodWithAnnotatedParameter with @CustomAnnotation on parameter -> map contains
        // FQCN spring.twin.testee.CustomAnnotation with value {LinkDetails.of(LinkType.METHOD_ARG_ANNOTATION, methodSignature)}
        byte[] classBytes = loadClassBytes("ParameterAnnotatedClass");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("spring.twin.testee.CustomAnnotation"));
        Set<LinkDetails> links = result.get("spring.twin.testee.CustomAnnotation");
        assertNotNull(links);
        assertEquals(1, links.size());
        // Signature format includes method name and parameter type
        assertTrue(links.stream().anyMatch(link ->
            link.type() == LinkType.METHOD_ARG_ANNOTATION &&
            link.details().contains("methodWithAnnotatedParameter") &&
            link.details().contains("Ljava/lang/String;")
        ));
    }

    @Test
    void testExtractDetails_customAnnotation_returnsCorrectLink() throws IOException {
        // CustomAnnotatedClass has @CustomAnnotation at class level -> map contains correct FQCN
        // with LinkType.CLASS_ANNOTATION
        byte[] classBytes = loadClassBytes("CustomAnnotatedClass");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("spring.twin.testee.CustomAnnotation"));
        Set<LinkDetails> links = result.get("spring.twin.testee.CustomAnnotation");
        assertNotNull(links);
        assertEquals(1, links.size());
        assertTrue(links.contains(LinkDetails.of(LinkType.CLASS_ANNOTATION)));
    }

    @Test
    void testExtractDetails_multipleAnnotationsOnSameTarget_returnsAllLinks() throws IOException {
        // AnnotatedClass has @Deprecated on class and @CustomAnnotation on field, method, and parameter
        // -> map contains all annotation types with correct link details
        byte[] classBytes = loadClassBytes("AnnotatedClass");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        // Check class annotation (@Deprecated)
        assertTrue(result.containsKey("java.lang.Deprecated"));
        Set<LinkDetails> deprecatedLinks = result.get("java.lang.Deprecated");
        assertNotNull(deprecatedLinks);
        assertTrue(deprecatedLinks.contains(LinkDetails.of(LinkType.CLASS_ANNOTATION)));

        // Check field annotation (@CustomAnnotation on annotatedField)
        assertTrue(result.containsKey("spring.twin.testee.CustomAnnotation"));
        Set<LinkDetails> customLinks = result.get("spring.twin.testee.CustomAnnotation");
        assertNotNull(customLinks);

        // Should have links for field, method, and parameter annotations
        assertTrue(customLinks.stream().anyMatch(link ->
            link.type() == LinkType.FIELD_ANNOTATION && link.details().equals("annotatedField")
        ));
        assertTrue(customLinks.stream().anyMatch(link ->
            link.type() == LinkType.METHOD_ANNOTATION && link.details().contains("annotatedMethod()")
        ));
        assertTrue(customLinks.stream().anyMatch(link ->
            link.type() == LinkType.METHOD_ARG_ANNOTATION && link.details().contains("methodWithAnnotatedParameter")
        ));
    }

    @Test
    void testExtractDetails_nullBytes_throwsException() {
        // null -> should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> extractor.extractDetails(null));
    }

}