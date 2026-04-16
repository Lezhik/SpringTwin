package spring.twin.scan;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MethodTypeExtractor#extractDetails(byte[])}.
 * Tests use compiled sample classes from {@code src/test/java/spring/twin/testee/}.
 */
class MethodTypeExtractorDetailsTest {

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
    // extractDetails() tests
    // ========================================================================

    @Test
    void testExtractDetails_methodWithReturnType_returnsMethodLink() throws IOException {
        // MethodHolder has method getName() returning String -> map contains key java.lang.String
        // with value {LinkDetails.of(LinkType.METHOD, signature)}
        byte[] classBytes = loadClassBytes("MethodHolder");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("java.lang.String"));
        Set<LinkDetails> links = result.get("java.lang.String");
        assertNotNull(links);
        // Signature format: getName() - only method name and arguments, no class name, no return type
        assertTrue(links.stream().anyMatch(link ->
            link.type() == LinkType.METHOD &&
            link.details().equals("getName()")
        ), "Expected signature to be 'getName()' but got: " +
            links.stream().filter(link -> link.type() == LinkType.METHOD).map(LinkDetails::details).toList());
    }

    @Test
    void testExtractDetails_methodWithParameterType_returnsMethodLink() throws IOException {
        // MethodHolder has method setName(String name) with String parameter -> map contains key java.lang.String
        // with value {LinkDetails.of(LinkType.METHOD, signature)}
        byte[] classBytes = loadClassBytes("MethodHolder");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("java.lang.String"));
        Set<LinkDetails> links = result.get("java.lang.String");
        assertNotNull(links);
        // Signature format: setName(Ljava/lang/String;) - method name and arguments only
        assertTrue(links.stream().anyMatch(link ->
            link.type() == LinkType.METHOD &&
            link.details().equals("setName(Ljava/lang/String;)")
        ), "Expected signature to be 'setName(Ljava/lang/String;)' but got: " +
            links.stream().filter(link -> link.type() == LinkType.METHOD).map(LinkDetails::details).toList());
    }

    @Test
    void testExtractDetails_methodWithGenericReturnType_returnsMethodAndGenericLinks() throws IOException {
        // MethodHolder has method getItems() returning List<String> -> contains FQCN List and String,
        // both with LinkType.METHOD and method signature
        byte[] classBytes = loadClassBytes("MethodHolder");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        // Check List type
        assertTrue(result.containsKey("java.util.List"));
        Set<LinkDetails> listLinks = result.get("java.util.List");
        assertNotNull(listLinks);
        assertTrue(listLinks.stream().anyMatch(link ->
            link.type() == LinkType.METHOD &&
            link.details().equals("getItems()")
        ), "Expected signature to be 'getItems()' but got: " +
            listLinks.stream().filter(link -> link.type() == LinkType.METHOD).map(LinkDetails::details).toList());

        // Check String generic parameter
        assertTrue(result.containsKey("java.lang.String"));
        Set<LinkDetails> stringLinks = result.get("java.lang.String");
        assertNotNull(stringLinks);
        assertTrue(stringLinks.stream().anyMatch(link ->
            link.type() == LinkType.METHOD &&
            link.details().equals("getItems()")
        ), "Expected signature to be 'getItems()' but got: " +
            stringLinks.stream().filter(link -> link.type() == LinkType.METHOD).map(LinkDetails::details).toList());
    }

    @Test
    void testExtractDetails_constructorWithParameter_returnsMethodLink() throws IOException {
        // MethodHolder has constructor with String parameter -> contains FQCN of parameter type
        // with LinkType.METHOD and constructor signature
        byte[] classBytes = loadClassBytes("MethodHolder");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertTrue(result.containsKey("java.lang.String"));
        Set<LinkDetails> links = result.get("java.lang.String");
        assertNotNull(links);
        // Constructor signature uses <init> as method name, with arguments only
        assertTrue(links.stream().anyMatch(link ->
            link.type() == LinkType.METHOD &&
            link.details().equals("<init>(Ljava/lang/String;)")
        ), "Expected signature to be '<init>(Ljava/lang/String;)' but got: " +
            links.stream().filter(link -> link.type() == LinkType.METHOD).map(LinkDetails::details).toList());
    }

    @Test
    void testExtractDetails_voidMethod_noReturnTypeLink() throws IOException {
        // MethodHolder has void method setName(String) and calculate() returning int (primitive)
        // void methods should not add entries for void type
        byte[] classBytes = loadClassBytes("MethodHolder");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        // void should not be in the result
        assertFalse(result.containsKey("void"));
    }

    @Test
    void testExtractDetails_primitiveParams_notIncluded() throws IOException {
        // MethodHolder has method calculate() returning int (primitive)
        // and process methods may have primitive handling
        // Primitive types should not be included in the result
        byte[] classBytes = loadClassBytes("MethodHolder");
        Map<String, Set<LinkDetails>> result = extractor.extractDetails(classBytes);

        assertFalse(result.containsKey("int"));
    }

    @Test
    void testExtractDetails_nullBytes_throwsException() {
        // null -> should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> extractor.extractDetails(null));
    }

}