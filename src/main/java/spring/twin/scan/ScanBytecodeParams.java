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
 */
public record ScanBytecodeParams(
        Path classesDir,
        Path outputFile,
        List<String> includeMasks,
        List<String> excludeMasks) {

    /**
     * Compact constructor that wraps lists in {@code List.of()} for immutability.
     */
    public ScanBytecodeParams {
        includeMasks = List.copyOf(includeMasks);
        excludeMasks = List.copyOf(excludeMasks);
    }

    /**
     * Factory method to create ScanBytecodeParams from raw mask strings.
     *
     * @param classesDir path to directory with .class files
     * @param outputFile path to output JSON file
     * @param includeRaw raw include masks string (semicolon-separated)
     * @param excludeRaw raw exclude masks string (semicolon-separated)
     * @return new ScanBytecodeParams instance
     */
    public static ScanBytecodeParams of(Path classesDir, Path outputFile, String includeRaw, String excludeRaw) {
        List<String> includeMasks = MaskParser.parseMasks(includeRaw);
        List<String> excludeMasks = MaskParser.parseMasks(excludeRaw);
        return new ScanBytecodeParams(classesDir, outputFile, includeMasks, excludeMasks);
    }
}