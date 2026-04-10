package spring.twin.scan;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MaskMatcher}.
 */
class MaskMatcherTest {

    // Tests for matches() method - wildcard * (star) patterns

    @Test
    void testMatches_wildcardStar_matchesAny() {
        assertTrue(MaskMatcher.matches("com.example.OrderService", "*"));
        assertTrue(MaskMatcher.matches("java.lang.String", "*"));
        assertTrue(MaskMatcher.matches("AnyClassName", "*"));
    }

    @Test
    void testMatches_wildcardStarPrefix_matches() {
        assertTrue(MaskMatcher.matches("com.example.OrderService", "com.example.*"));
    }

    @Test
    void testMatches_wildcardStarSuffix_matches() {
        assertTrue(MaskMatcher.matches("com.example.OrderService", "*Service"));
    }

    @Test
    void testMatches_wildcardStarMiddle_matches() {
        assertTrue(MaskMatcher.matches("com.example.Service", "com.*.Service"));
    }

    // Tests for matches() method - wildcard ? (question mark) patterns

    @Test
    void testMatches_wildcardQuestionMark_singleChar() {
        assertTrue(MaskMatcher.matches("com.example.OrderService", "com.example.Order?ervice"));
    }

    @Test
    void testMatches_wildcardQuestionMark_noMatch_extraChar() {
        assertFalse(MaskMatcher.matches("com.example.OrderService", "com.example.Order?"));
    }

    // Tests for matches() method - plain substring (contains) patterns

    @Test
    void testMatches_plainSubstring_containsCheck() {
        assertTrue(MaskMatcher.matches("com.example.OrderService", "order"));
    }

    @Test
    void testMatches_plainSubstring_noMatch() {
        assertFalse(MaskMatcher.matches("com.example.OrderService", "xyz"));
    }

    @Test
    void testMatches_exactMatch_matches() {
        assertTrue(MaskMatcher.matches("com.example.OrderService", "com.example.OrderService"));
    }

    @Test
    void testMatches_multipleWildcards_matches() {
        assertTrue(MaskMatcher.matches("com.example.service.OrderService", "*.service.*"));
    }

    // Tests for matchesAny() method

    @Test
    void testMatchesAny_emptyMaskList_returnsTrue() {
        List<String> emptyMasks = Collections.emptyList();
        assertTrue(MaskMatcher.matchesAny("com.example.OrderService", emptyMasks));
    }

    @Test
    void testMatchesAny_nullMaskList_returnsTrue() {
        assertTrue(MaskMatcher.matchesAny("com.example.OrderService", null));
    }

    @Test
    void testMatchesAny_matchingMask_returnsTrue() {
        List<String> masks = Arrays.asList("xyz", "*.OrderService", "abc");
        assertTrue(MaskMatcher.matchesAny("com.example.OrderService", masks));
    }

    @Test
    void testMatchesAny_noMatchingMask_returnsFalse() {
        List<String> masks = Arrays.asList("xyz", "*.OtherClass", "abc");
        assertFalse(MaskMatcher.matchesAny("com.example.OrderService", masks));
    }

    // Tests for shouldInclude() method

    @Test
    void testShouldInclude_emptyIncludeAndExclude_returnsTrue() {
        assertTrue(MaskMatcher.shouldInclude(
                "com.example.OrderService",
                Collections.emptyList(),
                Collections.emptyList()));
    }

    @Test
    void testShouldInclude_matchingInclude_returnsTrue() {
        List<String> includeMasks = Arrays.asList("*.OrderService", "*.OrderRepository");
        List<String> excludeMasks = Collections.emptyList();
        assertTrue(MaskMatcher.shouldInclude("com.example.OrderService", includeMasks, excludeMasks));
    }

    @Test
    void testShouldInclude_notMatchingInclude_returnsFalse() {
        List<String> includeMasks = Arrays.asList("*.OtherClass", "*.OrderRepository");
        List<String> excludeMasks = Collections.emptyList();
        assertFalse(MaskMatcher.shouldInclude("com.example.OrderService", includeMasks, excludeMasks));
    }

    @Test
    void testShouldInclude_matchingExclude_returnsFalse() {
        List<String> includeMasks = Arrays.asList("*.OrderService", "*.OrderRepository");
        List<String> excludeMasks = Arrays.asList("*.OrderService", "*.PaymentClient");
        assertFalse(MaskMatcher.shouldInclude("com.example.OrderService", includeMasks, excludeMasks));
    }

    @Test
    void testShouldInclude_includeAndExclude_bothMatch_excludeWins() {
        List<String> includeMasks = Arrays.asList("*.OrderService", "*.OrderRepository");
        List<String> excludeMasks = Arrays.asList("*.Order*");
        assertFalse(MaskMatcher.shouldInclude("com.example.OrderService", includeMasks, excludeMasks));
    }

    @Test
    void testShouldInclude_nonEmptyIncludeNotMatching_returnsFalse() {
        List<String> includeMasks = Arrays.asList("*.Controller", "*.Service");
        List<String> excludeMasks = Collections.emptyList();
        assertFalse(MaskMatcher.shouldInclude("com.example.OrderService", includeMasks, excludeMasks));
    }

    // Tests for case sensitivity

    @Test
    void testMatches_caseSensitive() {
        assertFalse(MaskMatcher.matches("com.example.OrderService", "com.example.orderservice"));
        assertFalse(MaskMatcher.matches("com.example.OrderService", "COM.EXAMPLE.ORDERSERVICE"));
        assertFalse(MaskMatcher.matches("com.example.OrderService", "order"));
    }
}