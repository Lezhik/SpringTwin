package spring.twin.cluster;

import java.nio.file.Path;

/**
 * DTO for cluster command parameters.
 *
 * @param depsFile path to dependencies graph file (dependencies.json)
 * @param outputFile path to output JSON file (clusters.json)
 * @param resolution clustering parameter that determines cluster size (0.5–5.0), default 1.5
 */
public record ClusterParams(
        Path depsFile,
        Path outputFile,
        double resolution) {

    /**
     * Factory method that parses resolution from string.
     *
     * @param depsFile path to dependencies graph file
     * @param outputFile path to output JSON file
     * @param resolutionRaw raw resolution string (0.5–5.0), null or empty means use default 1.5
     * @return new ClusterParams instance
     * @throws IllegalArgumentException if resolution is outside valid range 0.5–5.0
     */
    public static ClusterParams of(Path depsFile, Path outputFile, String resolutionRaw) {
        double resolution = 1.5;
        if (resolutionRaw != null && !resolutionRaw.isBlank()) {
            resolution = Double.parseDouble(resolutionRaw);
            if (resolution < 0.5 || resolution > 5.0) {
                throw new IllegalArgumentException(
                    "Resolution must be between 0.5 and 5.0, but was: " + resolution);
            }
        }
        return new ClusterParams(depsFile, outputFile, resolution);
    }
}