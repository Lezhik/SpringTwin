package spring.twin.scan;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Writes a dependency graph to a JSON file in the format specified by the SPEC.
 *
 * <p>This class serializes a map of class dependencies into a JSON file with
 * the following characteristics:
 * <ul>
 *   <li>Keys are sorted alphabetically</li>
 *   <li>Values (dependency arrays) are sorted alphabetically</li>
 *   <li>Pretty-printed with 2-space indentation</li>
 *   <li>UTF-8 encoded</li>
 * </ul>
 *
 * <p>Parent directories are created automatically if they do not exist.
 */
@Component
public class DependencyJsonWriter {

    /**
     * Constructs a new DependencyJsonWriter.
     * This constructor is kept for backward compatibility with tests.
     *
     * @param objectMapper the Jackson ObjectMapper (not used, ObjectMapper is created internally)
     */
    public DependencyJsonWriter(ObjectMapper objectMapper) {
        // ObjectMapper is created internally in write() method
    }

    /**
     * Constructs a new DependencyJsonWriter with no dependencies.
     */
    public DependencyJsonWriter() {
        // Default constructor
    }

    /**
     * Writes the dependency graph to a JSON file.
     *
     * <p>The graph is written with sorted keys and sorted dependency arrays.
     * Parent directories are created if they do not exist.
     *
     * @param graph      the dependency graph as a map from FQCN to set of dependency FQCNs
     * @param outputFile the path to the output JSON file
     * @throws UncheckedIOException if the file cannot be written
     */
    public void write(Map<String, Set<String>> graph, Path outputFile) {
        // Create ObjectMapper with INDENT_OUTPUT
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Convert Map<String, Set<String>> to Map<String, List<String>> with sorted values
        Map<String, List<String>> sortedMap = graph.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().stream()
                    .sorted()
                    .collect(Collectors.toList()),
                (a, b) -> a,
                TreeMap::new
            ));

        try {
            // Create parent directories if they don't exist
            var path = outputFile.getParent();
            if (path != null) {
                Files.createDirectories(path);
            }

            // Write JSON - for empty graph, write compact form to match test expectations
            if (sortedMap.isEmpty()) {
                Files.writeString(outputFile, "{}");
            } else {
                objectMapper.writeValue(outputFile.toFile(), sortedMap);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write dependency graph to: " + outputFile, e);
        }
    }

    /**
     * Writes the detailed dependency graph to a JSON file.
     *
     * <p>This method writes a detailed graph containing {@link LinkDetails} information
     * for each dependency. The output format includes link types and additional details
     * about each dependency.
     *
     * <p>The output JSON format follows the SPEC:
     * <ul>
     *   <li>First-level keys (source classes) are sorted alphabetically</li>
     *   <li>Second-level keys (target classes) are sorted alphabetically</li>
     *   <li>LinkDetails arrays are sorted by type, then by details</li>
     *   <li>Pretty-printed with 2-space indentation</li>
     *   <li>UTF-8 encoded</li>
     *   <li>Empty graph is written as {@code {}}</li>
     * </ul>
     *
     * <p>Example output:
     * <pre>
     * {
     *   "com.example.OrderService": {
     *     "com.example.PaymentClient": [
     *       {"type": "FIELD", "details": "paymentClient"}
     *     ],
     *     "com.example.repository.OrderRepository": [
     *       {"type": "FIELD", "details": "orderRepository"}
     *     ]
     *   }
     * }
     * </pre>
     *
     * @param graph      the detailed dependency graph as a map from FQCN to a map of
     *                   dependency FQCNs to set of {@link LinkDetails}
     * @param outputFile the path to the output JSON file
     * @throws UncheckedIOException if the file cannot be written
     */
    public void writeDetails(Map<String, Map<String, Set<LinkDetails>>> graph, Path outputFile) {
        // Create ObjectMapper with INDENT_OUTPUT
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Convert Map<String, Map<String, Set<LinkDetails>>> to sorted structure:
        // - Outer TreeMap for first-level key sorting
        // - Inner TreeMap for second-level key sorting
        // - Set<LinkDetails> -> sorted List (by type, then by details)
        Map<String, Map<String, List<LinkDetails>>> sortedMap = new TreeMap<>();

        for (Map.Entry<String, Map<String, Set<LinkDetails>>> outerEntry : graph.entrySet()) {
            String outerKey = outerEntry.getKey();
            Map<String, Set<LinkDetails>> innerMap = outerEntry.getValue();

            Map<String, List<LinkDetails>> sortedInnerMap = new TreeMap<>();

            for (Map.Entry<String, Set<LinkDetails>> innerEntry : innerMap.entrySet()) {
                String innerKey = innerEntry.getKey();
                Set<LinkDetails> linkDetailsSet = innerEntry.getValue();

                // Convert Set to sorted List by type, then by details
                List<LinkDetails> sortedList = linkDetailsSet.stream()
                        .sorted(Comparator.comparing(LinkDetails::type)
                                .thenComparing(LinkDetails::details))
                        .collect(Collectors.toList());

                sortedInnerMap.put(innerKey, sortedList);
            }

            sortedMap.put(outerKey, sortedInnerMap);
        }

        try {
            // Create parent directories if they don't exist
            var path = outputFile.getParent();
            if (path != null) {
                Files.createDirectories(path);
            }

            // Write JSON
            if (sortedMap.isEmpty()) {
                Files.writeString(outputFile, "{}");
            } else {
                objectMapper.writeValue(outputFile.toFile(), sortedMap);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write dependency graph to: " + outputFile, e);
        }
    }
}