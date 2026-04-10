package spring.twin.scan;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for parsing mask strings.
 * Masks are semicolon-separated patterns for matching FQCN (Fully Qualified Class Names).
 */
public final class MaskParser {

    private MaskParser() {
        // Utility class, no instantiation
    }

    /**
     * Parses a raw mask string into a list of trimmed, non-empty masks.
     * 
     * @param raw the raw mask string (semicolon-separated)
     * @return list of parsed masks, or empty list if raw is null or empty
     */
    public static List<String> parseMasks(String raw) {
        if (raw == null || raw.isEmpty()) {
            return List.of();
        }
        
        List<String> masks = new ArrayList<>();
        String[] parts = raw.split(";");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                masks.add(trimmed);
            }
        }
        return masks;
    }
}