package spring.twin.scan;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MaskParser.
 */
class MaskParserTest {

    @Test
    void testParseMasks_nullInput_returnsEmptyList() {
        List<String> result = MaskParser.parseMasks(null);
        assertEquals(List.of(), result);
    }

    @Test
    void testParseMasks_emptyString_returnsEmptyList() {
        List<String> result = MaskParser.parseMasks("");
        assertEquals(List.of(), result);
    }

    @Test
    void testParseMasks_singleMask_returnsSingletonList() {
        List<String> result = MaskParser.parseMasks("com.example.*");
        assertEquals(List.of("com.example.*"), result);
    }

    @Test
    void testParseMasks_multipleMasks_returnsList() {
        List<String> result = MaskParser.parseMasks("com.example.*;com.demo.*;*.service.*");
        assertEquals(List.of("com.example.*", "com.demo.*", "*.service.*"), result);
    }

    @Test
    void testParseMasks_trimsWhitespace() {
        List<String> result = MaskParser.parseMasks("  com.example.*  ;  *.service.* ");
        assertEquals(List.of("com.example.*", "*.service.*"), result);
    }

    @Test
    void testParseMasks_filtersEmptySegments() {
        List<String> result = MaskParser.parseMasks("a;;b");
        assertEquals(List.of("a", "b"), result);
    }

    @Test
    void testParseMasks_semicolonOnly_returnsEmptyList() {
        List<String> result = MaskParser.parseMasks(";;;");
        assertEquals(List.of(), result);
    }
}