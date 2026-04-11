package spring.twin.scan;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * Builds a dependency graph by scanning .class files and analyzing their bytecode.
 *
 * <p>This class orchestrates the scanning of class files and extracts dependencies
 * using {@link BytecodeClassAnalyzer}. It applies include/exclude masks to filter
 * classes and their dependencies, producing a deterministic graph structure.
 *
 * <p>The resulting graph is a map where each key is a Fully Qualified Class Name (FQCN)
 * and the value is a set of FQCNs representing the classes it depends on.
 *
 * <p>Acts as a Spring bean with constructor injection for its dependencies.
 */
@Component
public class DependencyGraphBuilder {

    private final ClassFileScanner classFileScanner;
    private final BytecodeClassAnalyzer bytecodeClassAnalyzer;

    private static final Set<String> PRIMITIVE_TYPES = Set.of(
        "byte", "short", "int", "long", "float", "double", "char", "boolean", "void"
    );

    /**
     * Constructs a new DependencyGraphBuilder with the required dependencies.
     *
     * @param classFileScanner      the scanner for finding .class files
     * @param bytecodeClassAnalyzer the analyzer for extracting dependencies from bytecode
     */
    public DependencyGraphBuilder(ClassFileScanner classFileScanner, BytecodeClassAnalyzer bytecodeClassAnalyzer) {
        this.classFileScanner = classFileScanner;
        this.bytecodeClassAnalyzer = bytecodeClassAnalyzer;
    }

    /**
     * Builds a dependency graph from .class files in the specified directory.
     *
     * <p>This method:
     * <ul>
     *   <li>Scans the directory for all .class files</li>
     *   <li>Extracts the FQCN from each class file</li>
     *   <li>Filters classes based on include/exclude masks</li>
     *   <li>Extracts dependencies for each included class</li>
     *   <li>Filters out primitive types and excluded dependencies</li>
     *   <li>Returns a sorted map for deterministic ordering</li>
     * </ul>
     *
     * @param classesDir   the directory containing .class files
     * @param includeMasks list of masks for including classes (empty = include all)
     * @param excludeMasks list of masks for excluding classes (empty = exclude none)
     * @return a map from FQCN to set of dependency FQCNs, sorted by key
     * @throws UncheckedIOException if class files cannot be read
     */
    public Map<String, Set<String>> build(Path classesDir, List<String> includeMasks, List<String> excludeMasks) {
        // 1. Get list of .class files
        List<Path> classFiles = classFileScanner.scan(classesDir);

        // Build the graph
        Map<String, Set<String>> graph = new LinkedHashMap<>();

        for (Path classFile : classFiles) {
            try {
                // 2. Read bytecode
                byte[] classBytes = Files.readAllBytes(classFile);

                // 3. Extract FQCN
                String fqcn = bytecodeClassAnalyzer.extractClassName(classBytes);

                // 4. Check if class passes include/exclude filters
                if (!MaskMatcher.shouldInclude(fqcn, includeMasks, excludeMasks)) {
                    continue;
                }

                // 5. Extract dependencies
                Set<String> dependencies = bytecodeClassAnalyzer.extractDependencies(classBytes);

                // 6. Filter dependencies: remove primitives and apply masks
                Set<String> filteredDependencies = dependencies.stream()
                    .filter(dep -> !PRIMITIVE_TYPES.contains(dep))
                    .filter(dep -> MaskMatcher.shouldInclude(dep, includeMasks, excludeMasks))
                    .collect(Collectors.toSet());

                // 7. Add to graph
                graph.put(fqcn, filteredDependencies);

            } catch (IOException e) {
                throw new UncheckedIOException("Failed to read class file: " + classFile, e);
            }
        }

        // 8. Return sorted graph (LinkedHashMap with sorted keys for determinism)
        return sortByKey(graph);
    }

    /**
     * Returns a new LinkedHashMap with entries sorted by key (FQCN).
     *
     * @param graph the unsorted graph
     * @return a new LinkedHashMap with entries sorted by key
     */
    private Map<String, Set<String>> sortByKey(Map<String, Set<String>> graph) {
        TreeMap<String, Set<String>> sorted = new TreeMap<>(graph);
        return new LinkedHashMap<>(sorted);
    }
}