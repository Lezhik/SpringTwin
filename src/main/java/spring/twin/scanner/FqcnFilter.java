package spring.twin.scanner;

import lombok.Getter;

import java.util.regex.Pattern;

/**
 * Filter for Fully Qualified Class Names (FQCN) using simplified wildcard patterns.
 * <p>
 * Supports the following pattern syntax:
 * <ul>
 *   <li>{@code *} - matches any sequence of characters (zero or more)</li>
 *   <li>{@code ?} - matches exactly one character</li>
 * </ul>
 * <p>
 * The pattern is matched against the entire FQCN string. If the pattern does not
 * contain wildcards, it is treated as a substring match (the FQCN must contain
 * the pattern).
 * <p>
 * This class is immutable and thread-safe.
 *
 * @see IncludeExcludeFilter
 */
@Getter
public class FqcnFilter {

    private final String pattern;
    private final Pattern regexPattern;

    /**
     * Creates a new FQCN filter with the specified pattern.
     *
     * @param pattern the pattern to match against FQCN strings;
     *                may contain {@code *} and {@code ?} wildcards
     * @throws IllegalArgumentException if pattern is null or empty
     */
    public FqcnFilter(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern must not be null or empty");
        }
        this.pattern = pattern;
        this.regexPattern = compilePattern(pattern);
    }

    /**
     * Tests whether the given FQCN matches this filter's pattern.
     *
     * @param fqcn the fully qualified class name to test; must not be null
     * @return {@code true} if the FQCN matches the pattern, {@code false} otherwise
     * @throws IllegalArgumentException if fqcn is null
     */
    public boolean matches(String fqcn) {
        if (fqcn == null) {
            throw new IllegalArgumentException("FQCN must not be null");
        }
        return regexPattern.matcher(fqcn).matches();
    }

    /**
     * Compiles the simplified wildcard pattern into a Java regex Pattern.
     * <p>
     * The compilation process:
     * <ul>
     *   <li>If pattern contains no wildcards ({@code *} or {@code ?}), treats it as
     *       a substring match (equivalent to {@code *pattern*})</li>
     *   <li>Escapes regex special characters (except * and ?)</li>
     *   <li>Converts {@code *} to {@code .+} (one or more chars) - empty string won't match</li>
     *   <li>Converts {@code ?} to {@code .} (exactly one char)</li>
     *   <li>Wraps the pattern to match the entire string</li>
     * </ul>
     *
     * @param pattern the wildcard pattern to compile
     * @return a compiled Pattern for matching
     */
    private static Pattern compilePattern(String pattern) {
        // If pattern has no wildcards, treat it as substring match (like *pattern*)
        boolean hasWildcards = pattern.indexOf('*') >= 0 || pattern.indexOf('?') >= 0;

        StringBuilder regex = new StringBuilder();
        regex.append("^");

        if (!hasWildcards) {
            regex.append(".*");
        }

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            switch (c) {
                case '*':
                    // Use .+ instead of .* so that * requires at least one character
                    // This ensures empty string won't match "*"
                    regex.append(".+");
                    break;
                case '?':
                    regex.append(".");
                    break;
                case '.':
                case '$':
                case '^':
                case '{':
                case '}':
                case '[':
                case ']':
                case '(':
                case ')':
                case '+':
                case '|':
                case '\\':
                    regex.append('\\').append(c);
                    break;
                default:
                    regex.append(c);
                    break;
            }
        }

        if (!hasWildcards) {
            regex.append(".*");
        }

        regex.append("$");
        return Pattern.compile(regex.toString());
    }

}