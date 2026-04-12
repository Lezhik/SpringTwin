package spring.twin.scan;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CodeUsageExtractor#extractDetails(byte[])}.
 * Tests use compiled sample classes from {@code src/test/java/spring/twin/testee/}.
 */
class CodeUsageExtractorDetailsTest {

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
    // extractDetails() tests
    // ========================================================================

    @Test
    void testExtractDetails_newObjectInMethod_returnsMethodLink() throws IOException {
        // new ArrayList<>() in createObject() method -> map contains java.util.ArrayList
        // with value {LinkDetails.of(LinkType.METHOD, methodSignature)}
        byte[] classBytes = loadClassBytes("CodeUsageExample");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("java.util.ArrayList"));
        Set<LinkDetails> links = result.get("java.util.ArrayList");
        assertNotNull(links);
        assertEquals(1, links.size());
        // Signature format: Lspring/twin/testee/CodeUsageExample;createObject()V
        assertTrue(links.stream().anyMatch(link ->
            link.type() == LinkType.METHOD &&
            link.details().contains("createObject()")
        ));
    }

    @Test
    void testExtractDetails_methodInvocation_returnsMethodLink() throws IOException {
        // String.valueOf(42) in callMethod() method -> map contains java.lang.String
        // with value {LinkDetails.of(LinkType.METHOD, methodSignature)}
        byte[] classBytes = loadClassBytes("CodeUsageExample");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("java.lang.String"));
        Set<LinkDetails> links = result.get("java.lang.String");
        assertNotNull(links);
        // Signature format: Lspring/twin/testee/CodeUsageExample;callMethod()V
        assertTrue(links.stream().anyMatch(link ->
            link.type() == LinkType.METHOD &&
            link.details().contains("callMethod()")
        ));
    }

    @Test
    void testExtractDetails_staticBlockUsage_returnsStaticBlockLink() throws IOException {
        // System.setProperty("test", "value") in static block -> map contains java.lang.System
        // with value {LinkDetails.of(LinkType.STATIC_BLOCK)}
        byte[] classBytes = loadClassBytes("CodeUsageExample");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("java.lang.System"));
        Set<LinkDetails> links = result.get("java.lang.System");
        assertNotNull(links);
        // Should have at least one STATIC_BLOCK link from the static initializer
        assertTrue(links.stream().anyMatch(link ->
            link.type() == LinkType.STATIC_BLOCK &&
            link.details().isEmpty()
        ));
    }

    @Test
    void testExtractDetails_typeCast_returnsMethodLink() throws IOException {
        // (String) obj in castType() method -> map contains java.lang.String
        // with value {LinkDetails.of(LinkType.METHOD, methodSignature)}
        byte[] classBytes = loadClassBytes("CodeUsageExample");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("java.lang.String"));
        Set<LinkDetails> links = result.get("java.lang.String");
        assertNotNull(links);
        // Signature format: Lspring/twin/testee/CodeUsageExample;castType(Ljava/lang/Object;)V
        assertTrue(links.stream().anyMatch(link ->
            link.type() == LinkType.METHOD &&
            link.details().contains("castType") &&
            link.details().contains("Ljava/lang/Object;")
        ));
    }

    @Test
    void testExtractDetails_fieldAccess_returnsMethodLink() throws IOException {
        // System.out in accessField() method -> map contains java.lang.System
        // with value {LinkDetails.of(LinkType.METHOD, methodSignature)}
        byte[] classBytes = loadClassBytes("CodeUsageExample");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("java.lang.System"));
        Set<LinkDetails> links = result.get("java.lang.System");
        assertNotNull(links);
        // Signature format: Lspring/twin/testee/CodeUsageExample;accessField()V
        assertTrue(links.stream().anyMatch(link ->
            link.type() == LinkType.METHOD &&
            link.details().contains("accessField()")
        ));
    }

    @Test
    void testExtractDetails_multipleUsagesInSameMethod_sameSignature() throws IOException {
        // Multiple usages of String in callMethod() -> one record with method signature
        byte[] classBytes = loadClassBytes("CodeUsageExample");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("java.lang.String"));
        Set<LinkDetails> links = result.get("java.lang.String");
        assertNotNull(links);
        // Multiple usages in the same method should result in one LinkDetails entry
        // because Set eliminates duplicates based on equals/hashCode
        long callMethodLinksCount = links.stream()
            .filter(link -> link.type() == LinkType.METHOD && link.details().contains("callMethod()"))
            .count();
        assertEquals(1, callMethodLinksCount);
    }

    @Test
    void testExtractDetails_nullBytes_throwsException() {
        // null -> should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> extractor.extractDetails(null));
    }

}