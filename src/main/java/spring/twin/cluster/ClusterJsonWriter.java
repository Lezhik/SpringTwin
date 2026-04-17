package spring.twin.cluster;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Component responsible for serializing {@link ClusterResult} to JSON format.
 *
 * <p>Writes cluster data to a JSON file with the following structure:
 * <pre>
 * {
 *   "clusters": [...],
 *   "penaltyEdges": {...}
 * }
 * </pre>
 *
 * <p>The output is pretty-printed with 2-space indentation and UTF-8 encoding.
 * Keys in penaltyEdges are sorted alphabetically, and values (sets of class names)
 * are also sorted. Parent directories are created automatically if they don't exist.
 */
@Component
public class ClusterJsonWriter {

    /**
     * Default constructor.
     */
    public ClusterJsonWriter() {
    }

    /**
     * Writes the given cluster result to a JSON file.
     *
     * <p>The output format follows the specification in SPEC.md:
     * <ul>
     *   <li>Root object contains "clusters" array and "penaltyEdges" object</li>
     *   <li>Each cluster has "id", "classes" array, and "metrics" object</li>
     *   <li>Metrics contain "cohesion" and "coupling" double values</li>
     *   <li>Penalty edges map class names to sorted sets of dependent class names</li>
     * </ul>
     *
     * <p>Output characteristics:
     * <ul>
     *   <li>Pretty-printed with 2-space indentation</li>
     *   <li>UTF-8 encoding</li>
     *   <li>Sorted keys and values for deterministic output</li>
     *   <li>Parent directories created automatically</li>
     * </ul>
     *
     * @param clusterResult the clustering result to serialize
     * @param outputFile    the path to the output JSON file
     * @throws UncheckedIOException if an I/O error occurs during writing
     */
    public void write(ClusterResult clusterResult, Path outputFile) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        Map<String, Object> root = new HashMap<>();

        // Convert clusters to serializable structure with sorted classes
        List<Map<String, Object>> clustersList = new ArrayList<>();
        for (ClusterRecord cluster : clusterResult.clusters()) {
            Map<String, Object> clusterMap = new HashMap<>();
            clusterMap.put("id", cluster.id());

            // Sort classes alphabetically
            List<String> sortedClasses = cluster.classes().stream()
                    .sorted()
                    .collect(Collectors.toList());
            clusterMap.put("classes", sortedClasses);

            // Create metrics map
            Map<String, Object> metricsMap = new HashMap<>();
            metricsMap.put("cohesion", cluster.metrics().cohesion());
            metricsMap.put("coupling", cluster.metrics().coupling());
            clusterMap.put("metrics", metricsMap);

            clustersList.add(clusterMap);
        }
        root.put("clusters", clustersList);

        // Convert penaltyEdges to TreeMap for sorted keys and sorted values
        Map<String, List<String>> sortedPenaltyEdges = new TreeMap<>();
        for (Map.Entry<String, java.util.Set<String>> entry : clusterResult.penaltyEdges().entrySet()) {
            List<String> sortedTargets = entry.getValue().stream()
                    .sorted()
                    .collect(Collectors.toList());
            sortedPenaltyEdges.put(entry.getKey(), sortedTargets);
        }
        root.put("penaltyEdges", sortedPenaltyEdges);

        try {
            Files.createDirectories(outputFile.getParent());
            mapper.writeValue(outputFile.toFile(), root);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}