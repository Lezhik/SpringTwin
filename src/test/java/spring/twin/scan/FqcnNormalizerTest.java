package spring.twin.scan;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FqcnNormalizer}.
 */
class FqcnNormalizerTest {

    // Tests for fromInternalName

    @Test
    void testFromInternalName_simpleClass_returnsFqcn() {
        Optional<String> result = FqcnNormalizer.fromInternalName("com/example/OrderService");
        assertEquals(Optional.of("com.example.OrderService"), result);
    }

    @Test
    void testFromInternalName_nestedClass_returnsFqcn() {
        Optional<String> result = FqcnNormalizer.fromInternalName("com/example/Outer$Inner");
        assertEquals(Optional.of("com.example.Outer$Inner"), result);
    }

    @Test
    void testFromInternalName_primitiveInt_returnsEmpty() {
        Optional<String> result = FqcnNormalizer.fromInternalName("int");
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testFromInternalName_primitiveBoolean_returnsEmpty() {
        Optional<String> result = FqcnNormalizer.fromInternalName("boolean");
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testFromInternalName_primitiveVoid_returnsEmpty() {
        Optional<String> result = FqcnNormalizer.fromInternalName("void");
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testFromInternalName_null_returnsEmpty() {
        Optional<String> result = FqcnNormalizer.fromInternalName(null);
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testFromInternalName_allPrimitives_returnEmpty() {
        // All 8 primitive types + void
        assertEquals(Optional.empty(), FqcnNormalizer.fromInternalName("int"));
        assertEquals(Optional.empty(), FqcnNormalizer.fromInternalName("long"));
        assertEquals(Optional.empty(), FqcnNormalizer.fromInternalName("short"));
        assertEquals(Optional.empty(), FqcnNormalizer.fromInternalName("byte"));
        assertEquals(Optional.empty(), FqcnNormalizer.fromInternalName("char"));
        assertEquals(Optional.empty(), FqcnNormalizer.fromInternalName("float"));
        assertEquals(Optional.empty(), FqcnNormalizer.fromInternalName("double"));
        assertEquals(Optional.empty(), FqcnNormalizer.fromInternalName("boolean"));
        assertEquals(Optional.empty(), FqcnNormalizer.fromInternalName("void"));
    }

    // Tests for fromDescriptor

    @Test
    void testFromDescriptor_objectType_returnsFqcn() {
        Optional<String> result = FqcnNormalizer.fromDescriptor("Lcom/example/OrderService;");
        assertEquals(Optional.of("com.example.OrderService"), result);
    }

    @Test
    void testFromDescriptor_arrayOfObject_returnsBaseType() {
        Optional<String> result = FqcnNormalizer.fromDescriptor("[Lcom/example/OrderService;");
        assertEquals(Optional.of("com.example.OrderService"), result);
    }

    @Test
    void testFromDescriptor_multiDimensionalArrayOfObject_returnsBaseType() {
        Optional<String> result = FqcnNormalizer.fromDescriptor("[[Lcom/example/OrderService;");
        assertEquals(Optional.of("com.example.OrderService"), result);
    }

    @Test
    void testFromDescriptor_primitiveInt_returnsEmpty() {
        Optional<String> result = FqcnNormalizer.fromDescriptor("I");
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testFromDescriptor_arrayOfPrimitiveInt_returnsEmpty() {
        Optional<String> result = FqcnNormalizer.fromDescriptor("[I");
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testFromDescriptor_null_returnsEmpty() {
        Optional<String> result = FqcnNormalizer.fromDescriptor(null);
        assertEquals(Optional.empty(), result);
    }

    // Tests for internalToFqcn

    @Test
    void testInternalToFqcn_convertsSlashesToDots() {
        String result = FqcnNormalizer.internalToFqcn("com/example/Service");
        assertEquals("com.example.Service", result);
    }

    @Test
    void testInternalToFqcn_simpleName_noSlashes() {
        String result = FqcnNormalizer.internalToFqcn("Service");
        assertEquals("Service", result);
    }

    @Test
    void testInternalToFqcn_dollarSign_preserved() {
        String result = FqcnNormalizer.internalToFqcn("com/example/Outer$Inner");
        assertEquals("com.example.Outer$Inner", result);
    }
}