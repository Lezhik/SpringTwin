package spring.twin.scan;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FieldTypeExtractor#extractDetails(byte[])}.
 * Tests use compiled sample classes from {@code src/test/java/spring/twin/testee/}.
 */
class FieldTypeExtractorDetailsTest {

    private final FieldTypeExtractor extractor = new FieldTypeExtractor();

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
    void testExtractDetails_fieldWithType_returnsFieldLink() throws IOException {
        // ServiceHolder has field OrderService orderService -> map contains key spring.twin.testee.service.OrderService
        // with value {LinkDetails.of(LinkType.FIELD, "orderService")}
        byte[] classBytes = loadClassBytes("ServiceHolder");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("spring.twin.testee.service.OrderService"));
        Set<LinkDetails> links = result.get("spring.twin.testee.service.OrderService");
        assertNotNull(links);
        assertEquals(1, links.size());
        assertTrue(links.contains(LinkDetails.of(LinkType.FIELD, "orderService")));
    }

    @Test
    void testExtractDetails_multipleFields_returnsAllFieldLinks() throws IOException {
        // FieldHolder has multiple fields -> each field gives a separate record with correct name
        byte[] classBytes = loadClassBytes("FieldHolder");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        // simpleField: String
        assertTrue(result.containsKey("java.lang.String"));
        Set<LinkDetails> stringLinks = result.get("java.lang.String");
        assertNotNull(stringLinks);
        assertTrue(stringLinks.contains(LinkDetails.of(LinkType.FIELD, "simpleField")));

        // genericField: List<String>
        assertTrue(result.containsKey("java.util.List"));
        Set<LinkDetails> listLinks = result.get("java.util.List");
        assertNotNull(listLinks);
        assertTrue(listLinks.contains(LinkDetails.of(LinkType.FIELD, "genericField")));

        // arrayField: String[]
        // Base type String already checked above, verify arrayField is included
        assertTrue(stringLinks.contains(LinkDetails.of(LinkType.FIELD, "arrayField")));

        // mapField: Map<String, Integer>
        assertTrue(result.containsKey("java.util.Map"));
        Set<LinkDetails> mapLinks = result.get("java.util.Map");
        assertNotNull(mapLinks);
        assertTrue(mapLinks.contains(LinkDetails.of(LinkType.FIELD, "mapField")));

        // Integer from Map value type
        assertTrue(result.containsKey("java.lang.Integer"));
        Set<LinkDetails> integerLinks = result.get("java.lang.Integer");
        assertNotNull(integerLinks);
        assertTrue(integerLinks.contains(LinkDetails.of(LinkType.FIELD, "mapField")));
    }

    @Test
    void testExtractDetails_genericField_returnsFieldAndGenericTypes() throws IOException {
        // FieldHolder has field List<String> genericField -> contains FQCN List with
        // LinkDetails.of(LinkType.FIELD, "genericField") and FQCN String with
        // LinkDetails.of(LinkType.FIELD, "genericField")
        byte[] classBytes = loadClassBytes("FieldHolder");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        // Check List type
        assertTrue(result.containsKey("java.util.List"));
        Set<LinkDetails> listLinks = result.get("java.util.List");
        assertNotNull(listLinks);
        assertTrue(listLinks.contains(LinkDetails.of(LinkType.FIELD, "genericField")));

        // Check String generic parameter
        assertTrue(result.containsKey("java.lang.String"));
        Set<LinkDetails> stringLinks = result.get("java.lang.String");
        assertNotNull(stringLinks);
        assertTrue(stringLinks.contains(LinkDetails.of(LinkType.FIELD, "genericField")));
    }

    @Test
    void testExtractDetails_arrayField_returnsBaseType() throws IOException {
        // FieldHolder has field String[] arrayField -> contains FQCN of base type (String)
        // with LinkDetails.of(LinkType.FIELD, "arrayField")
        byte[] classBytes = loadClassBytes("FieldHolder");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("java.lang.String"));
        Set<LinkDetails> links = result.get("java.lang.String");
        assertNotNull(links);
        assertTrue(links.contains(LinkDetails.of(LinkType.FIELD, "arrayField")));
    }

    @Test
    void testExtractDetails_primitiveField_notIncluded() throws IOException {
        // FieldHolder has primitive field int primitiveField -> primitive fields are not included
        byte[] classBytes = loadClassBytes("FieldHolder");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertFalse(result.containsKey("int"));
    }

    @Test
    void testExtractDetails_nullBytes_throwsException() {
        // null -> should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> extractor.extractDetails(null));
    }

}