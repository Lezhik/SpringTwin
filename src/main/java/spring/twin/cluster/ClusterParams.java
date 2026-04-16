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
        throw new UnsupportedOperationException("Not implemented yet");
    }
}