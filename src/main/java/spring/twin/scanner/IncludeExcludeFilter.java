package spring.twin.scanner;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Composite filter supporting multiple include and exclude patterns for FQCN matching.
 * <p>
 * This filter combines multiple {@link FqcnFilter} instances to implement complex
 * include/exclude logic commonly used in CLI tools:
 * <ul>
 *   <li>If no include patterns are specified, all FQCNs are considered included</li>
 *   <li>If include patterns are specified, an FQCN must match at least one</li>
 *   <li>If an FQCN matches any exclude pattern, it is rejected regardless of includes</li>
 *   <li>Exclude patterns take precedence over include patterns</li>
 * </ul>
 * <p>
 * Multiple patterns can be specified using {@code ;} as a separator, for example:
 * {@code com.example.*;com.demo.service.*}
 * <p>
 * This class is immutable and thread-safe.
 *
 * @see FqcnFilter
 */
@Getter
public class IncludeExcludeFilter {

    /**
     * -- GETTER --
     *  Returns the list of include filters.
     *
     * @return unmodifiable list of include filters; never null
     */
    private final List<FqcnFilter> includeFilters;
    /**
     * -- GETTER --
     *  Returns the list of exclude filters.
     *
     * @return unmodifiable list of exclude filters; never null
     */
    private final List<FqcnFilter> excludeFilters;

    /**
     * Creates a new filter with the specified include and exclude pattern strings.
     * <p>
     * Each string may contain multiple patterns separated by {@code ;}.
     * Empty or null pattern strings are treated as "no patterns".
     *
     * @param includePatterns the include patterns string, separated by {@code ;};
     *                        may be null or empty to include all
     * @param excludePatterns the exclude patterns string, separated by {@code ;};
     *                        may be null or empty to exclude none
     */
    public IncludeExcludeFilter(String includePatterns, String excludePatterns) {
        this.includeFilters = parsePatterns(includePatterns);
        this.excludeFilters = parsePatterns(excludePatterns);
    }

    /**
     * Tests whether the given FQCN passes both include and exclude filters.
     * <p>
     * The FQCN passes if:
     * <ul>
     *   <li>It matches at least one include filter (or no include filters are defined)</li>
     *   <li>It does not match any exclude filter</li>
     * </ul>
     *
     * @param fqcn the fully qualified class name to test; must not be null
     * @return {@code true} if the FQCN is included and not excluded
     * @throws IllegalArgumentException if fqcn is null
     */
    public boolean matches(String fqcn) {
        if (fqcn == null) {
            throw new IllegalArgumentException("FQCN must not be null");
        }

        // Check excludes first - they take precedence
        for (FqcnFilter excludeFilter : excludeFilters) {
            if (excludeFilter.matches(fqcn)) {
                return false;
            }
        }

        // If no includes specified, accept all (that weren't excluded)
        if (includeFilters.isEmpty()) {
            return true;
        }

        // Must match at least one include
        for (FqcnFilter includeFilter : includeFilters) {
            if (includeFilter.matches(fqcn)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Parses a semicolon-separated pattern string into a list of FqcnFilter instances.
     * <p>
     * Empty or null input results in an empty list. Each non-empty pattern
     * segment creates one FqcnFilter.
     *
     * @param patterns the pattern string to parse, with {@code ;} as separator
     * @return an unmodifiable list of FqcnFilter instances
     */
    private static List<FqcnFilter> parsePatterns(String patterns) {
        if (patterns == null || patterns.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String[] parts = patterns.split(";");
        List<FqcnFilter> filters = new ArrayList<>();

        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                filters.add(new FqcnFilter(trimmed));
            }
        }

        return Collections.unmodifiableList(filters);
    }

}