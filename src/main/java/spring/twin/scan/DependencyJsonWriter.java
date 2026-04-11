package spring.twin.scan;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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
 *
 * <p>Acts as a Spring bean with constructor injection for the ObjectMapper.
 */
@Component
public class DependencyJsonWriter {

    private final ObjectMapper objectMapper;

    /**
     * Constructs a new DependencyJsonWriter with the required ObjectMapper.
     *
     * @param objectMapper the Jackson ObjectMapper for JSON serialization
     */
    public DependencyJsonWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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
        // Sort keys alphabetically and convert sets to sorted lists
        TreeMap<String, TreeSet<String>> sortedGraph = new TreeMap<>();
        for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
            sortedGraph.put(entry.getKey(), new TreeSet<>(entry.getValue()));
        }

        // Convert TreeSet values to List for JSON array output
        Map<String, java.util.List<String>> outputMap = sortedGraph.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> new ArrayList<>(e.getValue()),
                (a, b) -> a,
                TreeMap::new
            ));

        try {
            // Create parent directories if they don't exist
            Path parentDir = outputFile.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }

            // Write JSON with pretty print (2 spaces)
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(outputFile.toFile(), outputMap);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write dependency graph to: " + outputFile, e);
        }
    }
}