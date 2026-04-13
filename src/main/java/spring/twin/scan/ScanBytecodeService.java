package spring.twin.scan;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

/**
 * Main service for the scan-bytecode pipeline.
 * Orchestrates the entire process: accepts parameters, builds the dependency graph,
 * and writes the result to a JSON file.
 */
@Service
public class ScanBytecodeService {

    private final DependencyGraphBuilder dependencyGraphBuilder;
    private final DependencyJsonWriter dependencyJsonWriter;

    /**
     * Constructs a new ScanBytecodeService with the required dependencies.
     *
     * @param dependencyGraphBuilder the builder for constructing the dependency graph
     * @param dependencyJsonWriter   the writer for serializing the graph to JSON
     */
    public ScanBytecodeService(DependencyGraphBuilder dependencyGraphBuilder, DependencyJsonWriter dependencyJsonWriter) {
        this.dependencyGraphBuilder = dependencyGraphBuilder;
        this.dependencyJsonWriter = dependencyJsonWriter;
    }

    /**
     * Executes the scan-bytecode pipeline.
     * Builds the dependency graph from .class files and writes it to a JSON file.
     *
     * <p>This method:
     * <ul>
     *   <li>Calls {@code dependencyGraphBuilder.build()} with the classes directory and masks from params</li>
     *   <li>Calls {@code dependencyJsonWriter.write()} to serialize the graph to the output file</li>
     * </ul>
     *
     * @param params the parameters for the scan-bytecode command
     */
    public void execute(ScanBytecodeParams params) {
        Map<String, Set<String>> graph = dependencyGraphBuilder.build(
                params.classesDir(),
                params.includeMasks(),
                params.excludeMasks(),
                params.mergeInnerClasses()
        );
        dependencyJsonWriter.write(graph, params.outputFile());
    }

    /**
     * Analyzes bytecode and returns the dependency graph without writing to a file.
     * Useful for programmatic usage when the graph is needed in memory.
     *
     * @param params the parameters for the scan-bytecode command
     * @return a map from FQCN to set of dependency FQCNs
     */
    public Map<String, Set<String>> analyze(ScanBytecodeParams params) {
        return dependencyGraphBuilder.build(
                params.classesDir(),
                params.includeMasks(),
                params.excludeMasks(),
                params.mergeInnerClasses()
        );
    }

    /**
     * Executes the scan-bytecode pipeline with detailed output.
     * Builds the detailed dependency graph from .class files and writes it to a JSON file.
     *
     * <p>This method:
     * <ul>
     *   <li>Calls {@code dependencyGraphBuilder.buildDetails()} with the classes directory and masks from params</li>
     *   <li>Calls {@code dependencyJsonWriter.writeDetails()} to serialize the detailed graph to the output file</li>
     * </ul>
     *
     * @param params the parameters for the scan-bytecode command
     */
    public void executeDetails(ScanBytecodeParams params) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Analyzes bytecode and returns the detailed dependency graph without writing to a file.
     * Useful for programmatic usage when the detailed graph is needed in memory.
     *
     * @param params the parameters for the scan-bytecode command
     * @return a map from FQCN to map of dependency FQCNs to set of LinkDetails
     */
    public Map<String, Map<String, Set<LinkDetails>>> analyzeDetails(ScanBytecodeParams params) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}