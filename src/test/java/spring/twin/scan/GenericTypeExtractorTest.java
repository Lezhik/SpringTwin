package spring.twin.scan;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GenericTypeExtractor}.
 * Tests use real JVM bytecode signature formats.
 */
class GenericTypeExtractorTest {

    // ========================================================================
    // extractTypes() tests
    // ========================================================================

    @Test
    void testExtractTypes_nullSignature_returnsEmptySet() {
        Set<String> result = GenericTypeExtractor.extractTypes(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractTypes_emptyString_returnsEmptySet() {
        Set<String> result = GenericTypeExtractor.extractTypes("");
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractTypes_simpleObjectType_returnsFqcn() {
        Set<String> result = GenericTypeExtractor.extractTypes("Ljava/lang/String;");
        assertEquals(Set.of("java.lang.String"), result);
    }

    @Test
    void testExtractTypes_genericList_returnsListAndParameter() {
        Set<String> result = GenericTypeExtractor.extractTypes("Ljava/util/List<Ljava/lang/String;>;");
        assertEquals(Set.of("java.util.List", "java.lang.String"), result);
    }

    @Test
    void testExtractTypes_genericMap_returnsMapAndBothParameters() {
        Set<String> result = GenericTypeExtractor.extractTypes("Ljava/util/Map<Ljava/lang/String;Ljava/lang/Integer;>;");
        assertEquals(Set.of("java.util.Map", "java.lang.String", "java.lang.Integer"), result);
    }

    @Test
    void testExtractTypes_nestedGenerics_returnsAllTypes() {
        Set<String> result = GenericTypeExtractor.extractTypes("Ljava/util/Map<Ljava/lang/String;Ljava/util/List<Ljava/lang/Integer;>;>;");
        assertEquals(Set.of("java.util.Map", "java.lang.String", "java.util.List", "java.lang.Integer"), result);
    }

    @Test
    void testExtractTypes_primitiveOnly_returnsEmptySet() {
        Set<String> result = GenericTypeExtractor.extractTypes("I");
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractTypes_arrayOfObject_returnsBaseType() {
        Set<String> result = GenericTypeExtractor.extractTypes("[Ljava/lang/String;");
        assertEquals(Set.of("java.lang.String"), result);
    }

    @Test
    void testExtractTypes_classSignatureWithFormalTypeParameter() {
        // <T:Ljava/lang/Object;>Ljava/lang/Object; - T is a formal type parameter, should not be extracted
        Set<String> result = GenericTypeExtractor.extractTypes("<T:Ljava/lang/Object;>Ljava/lang/Object;");
        assertEquals(Set.of("java.lang.Object"), result);
    }

    @Test
    void testExtractTypes_wildcardExtends_returnsBoundType() {
        // List<? extends Number> in JVM format
        Set<String> result = GenericTypeExtractor.extractTypes("Ljava/util/List<+Ljava/lang/Number;>;");
        assertEquals(Set.of("java.util.List", "java.lang.Number"), result);
    }

    @Test
    void testExtractTypes_wildcardSuper_returnsBoundType() {
        // List<? super Number> in JVM format
        Set<String> result = GenericTypeExtractor.extractTypes("Ljava/util/List<-Ljava/lang/Number;>;");
        assertEquals(Set.of("java.util.List", "java.lang.Number"), result);
    }

    // ========================================================================
    // extractTypeNames() tests
    // ========================================================================

    @Test
    void testExtractTypeNames_simpleObjectType_returnsFqcn() {
        Set<String> result = GenericTypeExtractor.extractTypeNames("Ljava/lang/String;");
        assertEquals(Set.of("java.lang.String"), result);
    }

    @Test
    void testExtractTypeNames_genericWithNested_returnsAllTypes() {
        // Map<String, List<Integer>> in JVM format
        Set<String> result = GenericTypeExtractor.extractTypeNames("Ljava/util/Map<Ljava/lang/String;Ljava/util/List<Ljava/lang/Integer;>;>;");
        assertEquals(Set.of("java.util.Map", "java.lang.String", "java.util.List", "java.lang.Integer"), result);
    }

    @Test
    void testExtractTypeNames_primitiveDescriptor_returnsEmptySet() {
        Set<String> result = GenericTypeExtractor.extractTypeNames("I");
        assertTrue(result.isEmpty());
    }
}