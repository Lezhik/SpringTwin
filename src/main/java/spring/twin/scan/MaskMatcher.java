package spring.twin.scan;

import java.util.List;

/**
 * Utility class for matching FQCN (Fully Qualified Class Names) against include/exclude masks.
 * Supports simplified syntax: * (any characters), ? (exactly one character), and substring matching.
 */
public final class MaskMatcher {

    private MaskMatcher() {
        // Utility class, no instantiation
    }

    /**
     * Checks if the FQCN matches at least one mask from the provided list.
     * If the list of masks is empty, returns true (empty include = any class, empty exclude = exclude nothing).
     *
     * @param fqcn  the fully qualified class name to check
     * @param masks the list of masks to match against
     * @return true if FQCN matches at least one mask, or if masks list is empty; false otherwise
     */
    public static boolean matchesAny(String fqcn, List<String> masks) {
        if (masks == null || masks.isEmpty()) {
            return true;
        }
        for (String mask : masks) {
            if (matches(fqcn, mask)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the FQCN matches a single mask.
     * Supports:
     * - * → any characters (including empty)
     * - ? → exactly one character
     * - No special characters → substring containment check
     *
     * @param fqcn the fully qualified class name to check
     * @param mask the mask pattern to match against
     * @return true if FQCN matches the mask; false otherwise
     */
    public static boolean matches(String fqcn, String mask) {
        if (fqcn == null || mask == null) {
            return false;
        }

        // Check if mask contains wildcard characters
        if (mask.contains("*") || mask.contains("?")) {
            return matchesWildcard(fqcn, mask);
        }

        // No special characters - check substring containment
        return fqcn.contains(mask);
    }

    /**
     * Performs wildcard matching using * and ? patterns.
     * Converts the pattern to a regex for matching.
     *
     * @param fqcn the fully qualified class name
     * @param mask the wildcard pattern
     * @return true if FQCN matches the pattern; false otherwise
     */
    private static boolean matchesWildcard(String fqcn, String mask) {
        // Handle empty mask (just wildcards) - matches anything
        if (mask.isEmpty()) {
            return true;
        }
        
        // Escape special regex characters except * and ?
        StringBuilder regex = new StringBuilder();
        regex.append("^");
        
        for (int i = 0; i < mask.length(); i++) {
            char c = mask.charAt(i);
            switch (c) {
                case '*':
                    regex.append(".*");
                    break;
                case '?':
                    regex.append(".");
                    break;
                case '.', '\\', '[', ']', '(', ')', '{', '}', '^', '$', '+', '|':
                    regex.append("\\").append(c);
                    break;
                default:
                    regex.append(c);
            }
        }
        regex.append("$");
        
        return fqcn.matches(regex.toString());
    }

    /**
     * Combined check: FQCN is included if it matches the include mask (or include is empty)
     * AND does NOT match the exclude mask.
     *
     * @param fqcn          the fully qualified class name to check
     * @param includeMasks  list of include masks (empty list means include all)
     * @param excludeMasks  list of exclude masks (empty list means exclude nothing)
     * @return true if FQCN should be included; false otherwise
     */
    public static boolean shouldInclude(String fqcn, List<String> includeMasks, List<String> excludeMasks) {
        // Empty include masks means include all (unless exclude matches)
        if (includeMasks == null || includeMasks.isEmpty()) {
            // Include all, unless excluded
            return !(excludeMasks != null && !excludeMasks.isEmpty() && matchesAny(fqcn, excludeMasks));
        }
        
        // Check exclude first (only if exclude is not empty)
        if (excludeMasks != null && !excludeMasks.isEmpty() && matchesAny(fqcn, excludeMasks)) {
            return false;
        }
        
        // Check include
        return matchesAny(fqcn, includeMasks);
    }
}