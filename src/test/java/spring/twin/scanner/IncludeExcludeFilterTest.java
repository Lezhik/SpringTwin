package spring.twin.scanner;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link IncludeExcludeFilter}.
 * <p>
 * Tests include/exclude logic with multiple patterns.
 */
class IncludeExcludeFilterTest {

    // ========================================================================
    // Filter configuration constants
    // ========================================================================
    
    // Include patterns: package substring, wildcard pattern, full class name, class with wildcard
    private static final String INCLUDE_PATTERNS = "com.example;*.service.*;com.demo.controller.UserController;*Repository";
    
    // Exclude patterns: package substring, wildcard pattern, full class name, class with wildcard  
    private static final String EXCLUDE_PATTERNS = "internal;*.test.*;com.example.security.AuthFilter;*Config";

    private IncludeExcludeFilter createFilter() {
        return new IncludeExcludeFilter(INCLUDE_PATTERNS, EXCLUDE_PATTERNS);
    }

    // ========================================================================
    // Constructor and validation tests
    // ========================================================================

    @Test
    void shouldAcceptNullPatterns() {
        IncludeExcludeFilter filter = new IncludeExcludeFilter(null, null);
        
        // With no filters, everything should be included
        assertThat(filter.matches("any.Class")).isTrue();
        assertThat(filter.matches("com.example.Service")).isTrue();
    }

    @Test
    void shouldAcceptEmptyPatterns() {
        IncludeExcludeFilter filter = new IncludeExcludeFilter("", "");
        
        // With empty filters, everything should be included
        assertThat(filter.matches("any.Class")).isTrue();
    }

    @Test
    void shouldRejectNullFqcn() {
        IncludeExcludeFilter filter = createFilter();
        
        assertThatThrownBy(() -> filter.matches(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("FQCN must not be null");
    }

    @Test
    void shouldReturnIncludeFilters() {
        IncludeExcludeFilter filter = createFilter();
        
        assertThat(filter.getIncludeFilters()).hasSize(4);
        assertThat(filter.getIncludeFilters().get(0).getPattern()).isEqualTo("com.example");
        assertThat(filter.getIncludeFilters().get(1).getPattern()).isEqualTo("*.service.*");
    }

    @Test
    void shouldReturnExcludeFilters() {
        IncludeExcludeFilter filter = createFilter();
        
        assertThat(filter.getExcludeFilters()).hasSize(4);
        assertThat(filter.getExcludeFilters().get(0).getPattern()).isEqualTo("internal");
        assertThat(filter.getExcludeFilters().get(1).getPattern()).isEqualTo("*.test.*");
    }

    // ========================================================================
    // Test Pair 1: Testing with classes related to com.example package
    // ========================================================================

    /**
     * Class doesn't match any include or exclude pattern.
     */
    @Test
    void pair1_shouldRejectWhenMatchesNeitherFilter() {
        IncludeExcludeFilter filter = createFilter();
        // org.apache - doesn't match com.example include, doesn't match any exclude
        String fqcn = "org.apache.http.HttpClient";
        
        boolean result = filter.matches(fqcn);
        
        assertThat(result).isFalse(); // Not in include list
    }

    /**
     * Class matches only include pattern (com.example).
     */
    @Test
    void pair1_shouldAcceptWhenMatchesOnlyInclude() {
        IncludeExcludeFilter filter = createFilter();
        // com.example.order.OrderService - matches com.example include, no exclude
        String fqcn = "com.example.order.OrderService";
        
        boolean result = filter.matches(fqcn);
        
        assertThat(result).isTrue();
    }

    /**
     * Class matches only exclude pattern (internal).
     */
    @Test
    void pair1_shouldRejectWhenMatchesOnlyExclude() {
        IncludeExcludeFilter filter = createFilter();
        // com.company.internal.Util - matches internal exclude, not in include
        String fqcn = "com.company.internal.Util";
        
        boolean result = filter.matches(fqcn);
        
        // Even though it's not in include, exclude takes precedence if it matches
        // But actually, if it doesn't match include, it's already rejected
        assertThat(result).isFalse();
    }

    /**
     * Class matches both include (com.example) and exclude (internal).
     */
    @Test
    void pair1_shouldRejectWhenMatchesBoth_filtersOverlap() {
        IncludeExcludeFilter filter = createFilter();
        // com.example.internal.Helper - matches com.example AND internal
        String fqcn = "com.example.internal.Helper";
        
        boolean result = filter.matches(fqcn);
        
        assertThat(result).isFalse(); // Exclude takes precedence
    }

    // ========================================================================
    // Test Pair 2: Testing with service classes and wildcards
    // ========================================================================

    /**
     * Class doesn't match any include or exclude pattern.
     */
    @Test
    void pair2_shouldRejectWhenMatchesNeitherFilter() {
        IncludeExcludeFilter filter = createFilter();
        // com.dao.UserDao - doesn't match *.service.* include
        String fqcn = "com.dao.UserDao";
        
        boolean result = filter.matches(fqcn);
        
        assertThat(result).isFalse();
    }

    /**
     * Class matches only include pattern (*.service.*).
     */
    @Test
    void pair2_shouldAcceptWhenMatchesOnlyInclude() {
        IncludeExcludeFilter filter = createFilter();
        // com.example.service.OrderService - matches *.service.* include
        String fqcn = "com.example.service.OrderService";
        
        boolean result = filter.matches(fqcn);
        
        assertThat(result).isTrue();
    }

    /**
     * Class matches only exclude pattern (*.test.*).
     */
    @Test
    void pair2_shouldRejectWhenMatchesOnlyExclude() {
        IncludeExcludeFilter filter = createFilter();
        // com.example.test.IntegrationTest - matches *.test.* exclude
        String fqcn = "com.example.test.IntegrationTest";
        
        boolean result = filter.matches(fqcn);
        
        assertThat(result).isFalse();
    }

    /**
     * Class matches both include (*.service.*) and exclude (*.test.*).
     */
    @Test
    void pair2_shouldRejectWhenMatchesBoth_filtersOverlap() {
        IncludeExcludeFilter filter = createFilter();
        // com.service.test.TestService - matches *.service.* AND *.test.*
        String fqcn = "com.service.test.TestService";
        
        boolean result = filter.matches(fqcn);
        
        assertThat(result).isFalse(); // Exclude takes precedence
    }

    // ========================================================================
    // Test Pair 3: Testing with full class name matching
    // ========================================================================

    /**
     * Class doesn't match any include or exclude pattern.
     */
    @Test
    void pair3_shouldRejectWhenMatchesNeitherFilter() {
        IncludeExcludeFilter filter = createFilter();
        // Different controller class - doesn't match specific UserController include
        String fqcn = "com.demo.controller.ProductController";
        
        boolean result = filter.matches(fqcn);
        
        assertThat(result).isFalse();
    }

    /**
     * Class matches only include pattern (full class name).
     */
    @Test
    void pair3_shouldAcceptWhenMatchesOnlyInclude() {
        IncludeExcludeFilter filter = createFilter();
        // Exact match for UserController
        String fqcn = "com.demo.controller.UserController";
        
        boolean result = filter.matches(fqcn);
        
        assertThat(result).isTrue();
    }

    /**
     * Class matches only exclude pattern (full class name).
     */
    @Test
    void pair3_shouldRejectWhenMatchesOnlyExclude() {
        IncludeExcludeFilter filter = createFilter();
        // Exact match for AuthFilter exclude
        String fqcn = "com.example.security.AuthFilter";
        
        boolean result = filter.matches(fqcn);
        
        assertThat(result).isFalse(); // Matches exclude
    }

    /**
     * Class matches both include and exclude by exact name (different classes).
     * Note: A class can't be both UserController and AuthFilter simultaneously,
     * so we test with a class that matches both patterns via substring.
     */
    @Test
    void pair3_shouldRejectWhenMatchesBoth_viaSubstringOverlap() {
        IncludeExcludeFilter filter = createFilter();
        // com.demo.controller.UserControllerConfig - matches *Repository? No.
        // Let's use: contains "UserController" (include) and "Config" (exclude)
        String fqcn = "com.demo.controller.UserControllerConfig";
        
        boolean result = filter.matches(fqcn);
        
        assertThat(result).isFalse(); // Matches *Config exclude
    }

    // ========================================================================
    // Test Pair 4: Testing with class name wildcards
    // ========================================================================

    /**
     * Class doesn't match any include or exclude pattern.
     */
    @Test
    void pair4_shouldRejectWhenMatchesNeitherFilter() {
        IncludeExcludeFilter filter = createFilter();
        // Simple util class - doesn't match *Repository include or *Config exclude
        // Note: com.example.Service would match com.example include pattern
        String fqcn = "org.util.Helper";
        
        boolean result = filter.matches(fqcn);
        
        assertThat(result).isFalse();
    }

    /**
     * Class matches only include pattern (*Repository).
     */
    @Test
    void pair4_shouldAcceptWhenMatchesOnlyInclude() {
        IncludeExcludeFilter filter = createFilter();
        // OrderRepository - matches *Repository include
        String fqcn = "com.example.repository.OrderRepository";
        
        boolean result = filter.matches(fqcn);
        
        assertThat(result).isTrue();
    }

    /**
     * Class matches only exclude pattern (*Config).
     */
    @Test
    void pair4_shouldRejectWhenMatchesOnlyExclude() {
        IncludeExcludeFilter filter = createFilter();
        // DatabaseConfig - matches *Config exclude
        String fqcn = "com.example.config.DatabaseConfig";
        
        boolean result = filter.matches(fqcn);
        
        assertThat(result).isFalse();
    }

    /**
     * Class matches both include (*Repository) and exclude (*Config).
     */
    @Test
    void pair4_shouldRejectWhenMatchesBoth_filtersOverlap() {
        IncludeExcludeFilter filter = createFilter();
        // RepositoryConfig - matches *Repository AND *Config
        String fqcn = "com.example.config.RepositoryConfig";
        
        boolean result = filter.matches(fqcn);
        
        assertThat(result).isFalse(); // Exclude takes precedence
    }

    // ========================================================================
    // Additional edge case tests
    // ========================================================================

    @Test
    void shouldAcceptAllWhenNoIncludes() {
        IncludeExcludeFilter filter = new IncludeExcludeFilter(null, "internal");
        
        // Without includes, everything is considered included (unless excluded)
        assertThat(filter.matches("any.Class")).isTrue();
        assertThat(filter.matches("com.internal.Util")).isFalse(); // But this matches exclude
    }

    @Test
    void shouldAcceptWhenNoExcludes() {
        IncludeExcludeFilter filter = new IncludeExcludeFilter("com.example", null);
        
        assertThat(filter.matches("com.example.Service")).isTrue();
        assertThat(filter.matches("other.Service")).isFalse();
    }

    @Test
    void shouldHandlePatternWithSpaces() {
        // Patterns with spaces around semicolons should be trimmed
        IncludeExcludeFilter filter = new IncludeExcludeFilter(
            "com.example ; *.service.*",
            "internal ; test"
        );
        
        assertThat(filter.matches("com.example.Service")).isTrue();
        assertThat(filter.matches("org.service.Client")).isTrue();
        assertThat(filter.matches("com.internal.Test")).isFalse();
    }

    @Test
    void shouldHandleEmptyPatternSegments() {
        // Empty segments between semicolons should be ignored
        IncludeExcludeFilter filter = new IncludeExcludeFilter(
            "com.example;;*.service", 
            ";;internal"
        );
        
        assertThat(filter.getIncludeFilters()).hasSize(2);
        assertThat(filter.getExcludeFilters()).hasSize(1);
    }

}