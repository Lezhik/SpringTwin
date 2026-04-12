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
import java.util.TreeSet;

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
     * @return a map from FQCN to set of dependency FQCNs, sorted by key and values
     * @throws UncheckedIOException if class files cannot be read
     */
    public Map<String, Set<String>> build(Path classesDir, List<String> includeMasks, List<String> excludeMasks) {
        // 1. Scan the directory to get a list of .class files
        List<Path> classFiles = classFileScanner.scan(classesDir);

        // 7. Use TreeMap for sorted keys and TreeSet for sorted values
        TreeMap<String, TreeSet<String>> graph = new TreeMap<>();

        for (Path classFile : classFiles) {
            try {
                // 2. Read bytecode via Files.readAllBytes()
                byte[] classBytes = Files.readAllBytes(classFile);

                // 3. Extract FQCN via bytecodeClassAnalyzer.extractClassName(bytes)
                String fqcn = bytecodeClassAnalyzer.extractClassName(classBytes);

                // 4. Check FQCN via MaskMatcher.shouldInclude(fqcn, includeMasks, excludeMasks)
                if (!MaskMatcher.shouldInclude(fqcn, includeMasks, excludeMasks)) {
                    continue;
                }

                // 5. Extract dependencies via bytecodeClassAnalyzer.extractDependencies(bytes)
                Set<String> dependencies = bytecodeClassAnalyzer.extractDependencies(classBytes);

                // 6. Filter dependencies via MaskMatcher.shouldInclude()
                TreeSet<String> filteredDependencies = new TreeSet<>();
                for (String dep : dependencies) {
                    if (!PRIMITIVE_TYPES.contains(dep) && MaskMatcher.shouldInclude(dep, includeMasks, excludeMasks)) {
                        filteredDependencies.add(dep);
                    }
                }

                // 7. Add entry to TreeMap for sorting
                graph.put(fqcn, filteredDependencies);

            } catch (IOException e) {
                throw new UncheckedIOException("Failed to read class file: " + classFile, e);
            }
        }

        // 8. Return LinkedHashMap with sorted keys and values
        return new LinkedHashMap<>(graph);
    }

    /**
     * Builds a dependency graph from .class files in the specified directory
     * with optional merging of inner classes.
     *
     * <p>This method:
     * <ul>
     *   <li>Calls the existing build method to construct the initial graph</li>
     *   <li>Conditionally merges inner classes with their outer classes based on the flag</li>
     *   <li>Returns the resulting graph</li>
     * </ul>
     *
     * @param classesDir        the directory containing .class files
     * @param includeMasks      list of masks for including classes (empty = include all)
     * @param excludeMasks      list of masks for excluding classes (empty = exclude none)
     * @param mergeInnerClasses whether to merge inner classes with their outer classes
     * @return a map from FQCN to set of dependency FQCNs, sorted by key and values
     * @throws UncheckedIOException if class files cannot be read
     */
    public Map<String, Set<String>> build(Path classesDir, List<String> includeMasks, List<String> excludeMasks, boolean mergeInnerClasses) {
        // 1. Call existing build method to build the original graph
        Map<String, Set<String>> graph = build(classesDir, includeMasks, excludeMasks);

        // 2. Apply inner class merge based on the flag
        return InnerClassMerger.mergeInnerClasses(graph, mergeInnerClasses);
    }

    /**
     * Builds a detailed dependency graph from .class files in the specified directory.
     *
     * <p>This method:
     * <ul>
     *   <li>Scans the directory for all .class files</li>
     *   <li>Extracts the FQCN from each class file</li>
     *   <li>Filters classes based on include/exclude masks</li>
     *   <li>Extracts detailed dependencies for each included class with link information</li>
     *   <li>Filters out primitive types and excluded dependencies</li>
     *   <li>Returns a sorted map for deterministic ordering</li>
     * </ul>
     *
     * <p>The returned structure is {@code Map<String, Map<String, Set<LinkDetails>>>} where:
     * <ul>
     *   <li>Outer key - FQCN of the analyzed class</li>
     *   <li>Inner key - FQCN of a dependency class</li>
     *   <li>Value - set of LinkDetails describing the relationship</li>
     * </ul>
     *
     * @param classesDir   the directory containing .class files
     * @param includeMasks list of masks for including classes (empty = include all)
     * @param excludeMasks list of masks for excluding classes (empty = exclude none)
     * @return a two-level map from FQCN to dependency FQCN to set of LinkDetails, sorted by keys
     * @throws UncheckedIOException if class files cannot be read
     */
    public Map<String, Map<String, Set<LinkDetails>>> buildDetails(Path classesDir, List<String> includeMasks, List<String> excludeMasks) {
        throw new UnsupportedOperationException("buildDetails() is not yet implemented");
    }

    /**
     * Builds a detailed dependency graph from .class files in the specified directory
     * with optional merging of inner classes.
     *
     * <p>This method:
     * <ul>
     *   <li>Calls the buildDetails method to construct the initial detailed graph</li>
     *   <li>Conditionally merges inner classes with their outer classes based on the flag</li>
     *   <li>Returns the resulting detailed graph</li>
     * </ul>
     *
     * <p>The returned structure is {@code Map<String, Map<String, Set<LinkDetails>>>} where:
     * <ul>
     *   <li>Outer key - FQCN of the analyzed class</li>
     *   <li>Inner key - FQCN of a dependency class</li>
     *   <li>Value - set of LinkDetails describing the relationship</li>
     * </ul>
     *
     * @param classesDir        the directory containing .class files
     * @param includeMasks      list of masks for including classes (empty = include all)
     * @param excludeMasks      list of masks for excluding classes (empty = exclude none)
     * @param mergeInnerClasses whether to merge inner classes with their outer classes
     * @return a two-level map from FQCN to dependency FQCN to set of LinkDetails, sorted by keys
     * @throws UncheckedIOException if class files cannot be read
     */
    public Map<String, Map<String, Set<LinkDetails>>> buildDetails(Path classesDir, List<String> includeMasks, List<String> excludeMasks, boolean mergeInnerClasses) {
        throw new UnsupportedOperationException("buildDetails() with mergeInnerClasses is not yet implemented");
    }
}