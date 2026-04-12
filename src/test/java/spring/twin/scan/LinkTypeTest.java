package spring.twin.scan;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link LinkType} enum.
 */
class LinkTypeTest {

    @Test
    void testGetJsonName_superclass_returnsSUPERCLASS() {
        assertEquals("SUPERCLASS", LinkType.SUPERCLASS.getJsonName());
    }

    @Test
    void testGetJsonName_interface_returnsINTERFACE() {
        assertEquals("INTERFACE", LinkType.INTERFACE.getJsonName());
    }

    @Test
    void testGetJsonName_field_returnsFIELD() {
        assertEquals("FIELD", LinkType.FIELD.getJsonName());
    }

    @Test
    void testGetJsonName_staticBlock_returnsSTATIC_BLOCK() {
        assertEquals("STATIC_BLOCK", LinkType.STATIC_BLOCK.getJsonName());
    }

    @Test
    void testGetJsonName_method_returnsMETHOD() {
        assertEquals("METHOD", LinkType.METHOD.getJsonName());
    }

    @Test
    void testGetJsonName_classAnnotation_returnsCLASS_ANNOTATION() {
        assertEquals("CLASS_ANNOTATION", LinkType.CLASS_ANNOTATION.getJsonName());
    }

    @Test
    void testGetJsonName_fieldAnnotation_returnsFIELD_ANNOTATION() {
        assertEquals("FIELD_ANNOTATION", LinkType.FIELD_ANNOTATION.getJsonName());
    }

    @Test
    void testGetJsonName_methodAnnotation_returnsMETHOD_ANNOTATION() {
        assertEquals("METHOD_ANNOTATION", LinkType.METHOD_ANNOTATION.getJsonName());
    }

    @Test
    void testGetJsonName_methodArgAnnotation_returnsMETHOD_ARG_ANNOTATION() {
        assertEquals("METHOD_ARG_ANNOTATION", LinkType.METHOD_ARG_ANNOTATION.getJsonName());
    }

    @Test
    void testValues_count_returnsNine() {
        assertEquals(9, LinkType.values().length);
    }

    @Test
    void testValueOf_validName_returnsConstant() {
        assertEquals(LinkType.SUPERCLASS, LinkType.valueOf("SUPERCLASS"));
    }
}