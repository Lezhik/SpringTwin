package spring.twin.scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link FqcnFilter}.
 * <p>
 * Tests pattern matching without wildcards and with wildcards (* and ?).
 */
class FqcnFilterTest {

    private static final String TEST_FQCN = "com.example.service.OrderService";

    // ========================================================================
    // Tests for constructor validation
    // ========================================================================

    @Test
    void shouldRejectNullPattern() {
        assertThatThrownBy(() -> new FqcnFilter(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Pattern must not be null or empty");
    }

    @Test
    void shouldRejectEmptyPattern() {
        assertThatThrownBy(() -> new FqcnFilter(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Pattern must not be null or empty");
    }

    @Test
    void shouldStorePattern() {
        FqcnFilter filter = new FqcnFilter("com.example");

        assertThat(filter.getPattern()).isEqualTo("com.example");
    }

    // ========================================================================
    // Tests without wildcards - substring matching
    // ========================================================================

    @Test
    void shouldMatchPartOfPackageName() {
        FqcnFilter filter = new FqcnFilter("example");

        assertThat(filter.matches(TEST_FQCN)).isTrue();
    }

    @Test
    void shouldNotMatchWrongPartOfPackageName() {
        FqcnFilter filter = new FqcnFilter("nonexistent");

        assertThat(filter.matches(TEST_FQCN)).isFalse();
    }

    @Test
    void shouldMatchExactFqcn() {
        FqcnFilter filter = new FqcnFilter(TEST_FQCN);

        assertThat(filter.matches(TEST_FQCN)).isTrue();
    }

    @Test
    void shouldNotMatchDifferentFqcn() {
        FqcnFilter filter = new FqcnFilter("com.example.service.OrderController");

        assertThat(filter.matches(TEST_FQCN)).isFalse();
    }

    @Test
    void shouldMatchClassNamePart() {
        FqcnFilter filter = new FqcnFilter("OrderService");

        assertThat(filter.matches(TEST_FQCN)).isTrue();
    }

    @Test
    void shouldNotMatchDifferentClassName() {
        FqcnFilter filter = new FqcnFilter("PaymentService");

        assertThat(filter.matches(TEST_FQCN)).isFalse();
    }

    // ========================================================================
    // Tests with wildcard: * (matches any sequence)
    // ========================================================================

    @Test
    void shouldMatchEverythingWithAsterisk() {
        FqcnFilter filter = new FqcnFilter("*");

        assertThat(filter.matches(TEST_FQCN)).isTrue();
        assertThat(filter.matches("a")).isTrue();
        assertThat(filter.matches("")).isFalse(); // Empty string doesn't match *
    }

    @Test
    void shouldMatchContainingPartWithWildcards() {
        FqcnFilter filter = new FqcnFilter("*example*");

        assertThat(filter.matches(TEST_FQCN)).isTrue();
    }

    @Test
    void shouldNotMatchWhenContainingPartMissing() {
        FqcnFilter filter = new FqcnFilter("*nonexistent*");

        assertThat(filter.matches(TEST_FQCN)).isFalse();
    }

    @Test
    void shouldMatchPackagePrefix() {
        FqcnFilter filter = new FqcnFilter("com.example.*");

        assertThat(filter.matches(TEST_FQCN)).isTrue();
        assertThat(filter.matches("com.example.controller.OrderController")).isTrue();
    }

    @Test
    void shouldNotMatchDifferentPackagePrefix() {
        FqcnFilter filter = new FqcnFilter("com.other.*");

        assertThat(filter.matches(TEST_FQCN)).isFalse();
    }

    @Test
    void shouldMatchPackageSuffix() {
        FqcnFilter filter = new FqcnFilter("*.service.OrderService");

        assertThat(filter.matches(TEST_FQCN)).isTrue();
    }

    @Test
    void shouldNotMatchWrongSuffix() {
        FqcnFilter filter = new FqcnFilter("*.controller.OrderService");

        assertThat(filter.matches(TEST_FQCN)).isFalse();
    }

    // ========================================================================
    // Tests with wildcard: ? (matches exactly one character)
    // ========================================================================

    @Test
    void shouldMatchWithQuestionMarksForClassName() {
        // OrderService = O-r-d-e-r-S-e-r-v-i-c-e (12 chars)
        // OrderSer + vic + e = OrderService
        FqcnFilter filter = new FqcnFilter("OrderSer???e");

        assertThat(filter.matches("OrderService")).isTrue();
    }

    @Test
    void shouldNotMatchWithWrongQuestionMarkCount() {
        // Two ? instead of three - expects 11 chars but OrderService has 12
        FqcnFilter filter = new FqcnFilter("OrderSer??e");

        assertThat(filter.matches("OrderService")).isFalse();
    }

    @Test
    void shouldMatchWithWildcardPrefixAndQuestionMarks() {
        FqcnFilter filter = new FqcnFilter("*Service???");

        assertThat(filter.matches("OrderService123")).isTrue();
        assertThat(filter.matches("com.example.OrderServiceXYZ")).isTrue();
    }

    @Test
    void shouldNotMatchWithWildcardPrefixAndQuestionMarksWhenShort() {
        FqcnFilter filter = new FqcnFilter("*Service???");

        assertThat(filter.matches("OrderService12")).isFalse(); // Only 2 chars after Service
        assertThat(filter.matches("OrderService")).isFalse(); // No chars after Service
    }

    @Test
    void shouldMatchComplexPatternWithBothWildcards() {
        // *.example.????ice.*Serv???
        // Matches: com.example.service.OrderService
        // - *.example. -> com.example.
        // - ????ice -> service (s-e-r-v + ice = service)
        // - .* -> .Order (or any sequence)
        // - Serv??? -> Serv + ice = Service
        FqcnFilter filter = new FqcnFilter("*.example.????ice.*Serv???");

        assertThat(filter.matches("com.example.service.OrderService")).isTrue();
    }

    @Test
    void shouldNotMatchComplexPatternWhenSegmentMismatch() {
        // *.example.????ice.*Serv??? requires "serv" before "ice" part
        // controller doesn't match the pattern
        FqcnFilter filter = new FqcnFilter("*.example.????ice.*Serv???");

        assertThat(filter.matches("com.example.controller.OrderController")).isFalse();
    }

    // ========================================================================
    // Edge cases and validation
    // ========================================================================

    @Test
    void shouldRejectNullFqcn() {
        FqcnFilter filter = new FqcnFilter("*");

        assertThatThrownBy(() -> filter.matches(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("FQCN must not be null");
    }

    @Test
    void shouldHandleDotsInPattern() {
        FqcnFilter filter = new FqcnFilter("com.example.service.*");

        assertThat(filter.matches("com.example.service.OrderService")).isTrue();
        assertThat(filter.matches("com.example.controller.OrderController")).isFalse();
    }

    @Test
    void shouldHandlePackageNameOnly() {
        FqcnFilter filter = new FqcnFilter("service");

        assertThat(filter.matches("com.example.service.OrderService")).isTrue();
    }

    @Test
    void shouldHandleDollarSignInFqcn() {
        FqcnFilter filter = new FqcnFilter("*Inner*");

        assertThat(filter.matches("com.example.OuterClass$InnerClass")).isTrue();
    }

    @Test
    void shouldNotMatchPartialClassNameWithWildcard() {
        // Pattern with *Service should match anything ending with Service
        FqcnFilter filter = new FqcnFilter("*Service");

        assertThat(filter.matches("OrderService")).isTrue();
        assertThat(filter.matches("OrderServiceImpl")).isFalse(); // Ends with Impl, not Service
    }

    // ========================================================================
    // Parameterized tests for comprehensive coverage
    // ========================================================================

    @ParameterizedTest
    @CsvSource({
        // pattern, fqcn, expected
        "com.example,                             com.example.service.OrderService,  true",
        "service.Order,                           com.example.service.OrderService,  true",
        "OrderService,                            com.example.service.OrderService,  true",
        "com.example.service.OrderService,        com.example.service.OrderService,  true",
        "com.other,                               com.example.service.OrderService,  false",
        "PaymentService,                          com.example.service.OrderService,  false",
        "'*',                                     com.example.service.OrderService,  true",
        "'*example*',                             com.example.service.OrderService,  true",
        "'com.example.*',                         com.example.service.OrderService,  true",
        "'com.example.*',                         com.other.service.OrderService,    false",
        "'*.service.*',                           com.example.service.OrderService,  true",
        "'*.controller.*',                        com.example.service.OrderService,  false",
        "'OrderSer???e',                          OrderService,                      true",
        "'OrderSer??e',                           OrderService,                      false",
        "'*Service',                              OrderService,                      true",
        "'*Service',                              OrderServiceImpl,                  false",
        "'???',                                   abc,                               true",
        "'???',                                   ab,                                false",
        "'???',                                   abcd,                              false",
    })
    void shouldMatchCorrectly(String pattern, String fqcn, boolean expected) {
        FqcnFilter filter = new FqcnFilter(pattern);

        assertThat(filter.matches(fqcn)).isEqualTo(expected);
    }

}