package spring.twin.scan;

import java.nio.file.Path;
import java.util.List;

/**
 * DTO for scan-bytecode command parameters.
 *
 * @param classesDir path to directory containing .class files
 * @param outputFile path to output JSON file
 * @param includeMasks list of FQCN include masks (empty = any class)
 * @param excludeMasks list of FQCN exclude masks (empty = exclude nothing)
 * @param mergeInnerClasses flag to merge inner classes with their parents (default true)
 */
public record ScanBytecodeParams(
        Path classesDir,
        Path outputFile,
        List<String> includeMasks,
        List<String> excludeMasks,
        boolean mergeInnerClasses) {

    /**
     * Compact constructor that wraps lists in {@code List.of()} for immutability.
     */
    public ScanBytecodeParams {
        includeMasks = List.copyOf(includeMasks);
        excludeMasks = List.copyOf(excludeMasks);
    }

    /**
     * Constructor for backward compatibility (defaults mergeInnerClasses to true).
     *
     * @param classesDir path to directory containing .class files
     * @param outputFile path to output JSON file
     * @param includeMasks list of FQCN include masks
     * @param excludeMasks list of FQCN exclude masks
     */
    public ScanBytecodeParams(Path classesDir, Path outputFile, List<String> includeMasks, List<String> excludeMasks) {
        this(classesDir, outputFile, includeMasks, excludeMasks, true);
    }

    /**
     * Factory method to create ScanBytecodeParams from raw mask strings with mergeInnerClasses flag.
     *
     * @param classesDir path to directory with .class files
     * @param outputFile path to output JSON file
     * @param includeRaw raw include masks string (semicolon-separated)
     * @param excludeRaw raw exclude masks string (semicolon-separated)
     * @param mergeInnerClasses flag to merge inner classes with their parents
     * @return new ScanBytecodeParams instance
     */
    public static ScanBytecodeParams of(Path classesDir, Path outputFile, String includeRaw, String excludeRaw, boolean mergeInnerClasses) {
        List<String> includeMasks = MaskParser.parseMasks(includeRaw);
        List<String> excludeMasks = MaskParser.parseMasks(excludeRaw);
        return new ScanBytecodeParams(classesDir, outputFile, includeMasks, excludeMasks, mergeInnerClasses);
    }

    /**
     * Factory method for backward compatibility (defaults mergeInnerClasses to true).
     *
     * @param classesDir path to directory with .class files
     * @param outputFile path to output JSON file
     * @param includeRaw raw include masks string (semicolon-separated)
     * @param excludeRaw raw exclude masks string (semicolon-separated)
     * @return new ScanBytecodeParams instance
     */
    public static ScanBytecodeParams of(Path classesDir, Path outputFile, String includeRaw, String excludeRaw) {
        return of(classesDir, outputFile, includeRaw, excludeRaw, true);
    }
}