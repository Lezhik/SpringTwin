package spring.twin.scan;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link InheritanceExtractor#extractDetails(byte[])}.
 * Tests use compiled sample classes from {@code src/test/java/spring/twin/testee/}.
 */
class InheritanceExtractorDetailsTest {

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
    // extractDetails() tests
    // ========================================================================

    @Test
    void testExtractDetails_simpleClass_noInheritance_returnsEmpty() throws IOException {
        // Class without explicit extends (only Object) -> should return empty map
        byte[] classBytes = loadClassBytes("InheritanceBase");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractDetails_classExtendsAnother_returnsSuperclassLink() throws IOException {
        // InheritanceChild extends InheritanceBase -> map contains key spring.twin.testee.InheritanceBase
        // with value {LinkDetails.of(LinkType.SUPERCLASS)}
        byte[] classBytes = loadClassBytes("InheritanceChild");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("spring.twin.testee.InheritanceBase"));
        Set<LinkDetails> links = result.get("spring.twin.testee.InheritanceBase");
        assertNotNull(links);
        assertEquals(1, links.size());
        assertTrue(links.contains(LinkDetails.of(LinkType.SUPERCLASS)));
    }

    @Test
    void testExtractDetails_classImplementsInterface_returnsInterfaceLink() throws IOException {
        // InheritanceImpl implements Serializable -> map contains key java.io.Serializable
        // with value {LinkDetails.of(LinkType.INTERFACE)}
        byte[] classBytes = loadClassBytes("InheritanceImpl");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("java.io.Serializable"));
        Set<LinkDetails> links = result.get("java.io.Serializable");
        assertNotNull(links);
        assertEquals(1, links.size());
        assertTrue(links.contains(LinkDetails.of(LinkType.INTERFACE)));
    }

    @Test
    void testExtractDetails_classExtendsGeneric_returnsSuperclassAndGenericTypes() throws IOException {
        // InheritanceGenericChild extends ArrayList<String> -> map contains:
        // - java.util.ArrayList with LinkDetails.of(LinkType.SUPERCLASS)
        // - java.lang.String with LinkDetails.of(LinkType.SUPERCLASS) (generic inherits SUPERCLASS type)
        byte[] classBytes = loadClassBytes("InheritanceGenericChild");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        // Check superclass ArrayList
        assertTrue(result.containsKey("java.util.ArrayList"));
        Set<LinkDetails> arrayListLinks = result.get("java.util.ArrayList");
        assertNotNull(arrayListLinks);
        assertTrue(arrayListLinks.contains(LinkDetails.of(LinkType.SUPERCLASS)));

        // Check generic type String
        assertTrue(result.containsKey("java.lang.String"));
        Set<LinkDetails> stringLinks = result.get("java.lang.String");
        assertNotNull(stringLinks);
        assertTrue(stringLinks.contains(LinkDetails.of(LinkType.SUPERCLASS)));
    }

    @Test
    void testExtractDetails_interfaceExtendsInterface_returnsInterfaceLink() throws IOException {
        // InheritanceChildInterface extends Serializable -> contains parent interface
        // with LinkType.INTERFACE
        byte[] classBytes = loadClassBytes("InheritanceChildInterface");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("java.io.Serializable"));
        Set<LinkDetails> links = result.get("java.io.Serializable");
        assertNotNull(links);
        assertEquals(1, links.size());
        assertTrue(links.contains(LinkDetails.of(LinkType.INTERFACE)));
    }

    @Test
    void testExtractDetails_nullBytes_throwsException() {
        // null -> should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> extractor.extractDetails(null));
    }
}