package spring.twin.cluster;

import org.springframework.stereotype.Service;
import spring.twin.scan.LinkDetails;

import java.util.Map;
import java.util.Set;

/**
 * Main service orchestrating the entire cluster pipeline.
 *
 * <p>This service coordinates the full clustering workflow:
 * <ol>
 *   <li>Reading dependencies.json via {@link DependencyReader}</li>
 *   <li>Converting to an undirected graph via {@link GraphConverter}</li>
 *   <li>Clustering via {@link LeidenAlgorithm}</li>
 *   <li>Building {@link ClusterResult} via {@link ClusterResultBuilder}</li>
 *   <li>Writing output via {@link ClusterJsonWriter}</li>
 * </ol>
 *
 * <p>The service is designed to be used by the CLI command and encapsulates
 * all business logic of the clustering stage.
 */
@Service
public class ClusterService {

    private final DependencyReader dependencyReader;
    private final GraphConverter graphConverter;
    private final LeidenAlgorithm leidenAlgorithm;
    private final ClusterMetricsCalculator metricsCalculator;
    private final PenaltyEdgeDetector penaltyDetector;
    private final ClusterResultBuilder resultBuilder;
    private final ClusterJsonWriter jsonWriter;

    /**
     * Constructs a new ClusterService with all required dependencies.
     *
     * @param dependencyReader   the reader for dependencies.json files
     * @param graphConverter     the converter for transforming dependency graphs to undirected weighted graphs
     * @param leidenAlgorithm    the Leiden clustering algorithm implementation
     * @param metricsCalculator  the calculator for cluster cohesion and coupling metrics
     * @param penaltyDetector    the detector for cross-cluster penalty edges
     * @param resultBuilder      the builder for constructing cluster results
     * @param jsonWriter         the writer for serializing results to JSON
     */
    public ClusterService(DependencyReader dependencyReader,
                          GraphConverter graphConverter,
                          LeidenAlgorithm leidenAlgorithm,
                          ClusterMetricsCalculator metricsCalculator,
                          PenaltyEdgeDetector penaltyDetector,
                          ClusterResultBuilder resultBuilder,
                          ClusterJsonWriter jsonWriter) {
        this.dependencyReader = dependencyReader;
        this.graphConverter = graphConverter;
        this.leidenAlgorithm = leidenAlgorithm;
        this.metricsCalculator = metricsCalculator;
        this.penaltyDetector = penaltyDetector;
        this.resultBuilder = resultBuilder;
        this.jsonWriter = jsonWriter;
    }

    /**
     * Executes the full clustering pipeline and writes the result to a file.
     *
     * <p>This method performs the complete workflow:
     * <ol>
     *   <li>Reads the dependency graph from the file specified in params</li>
     *   <li>Converts the directed dependency graph to an undirected weighted graph</li>
     *   <li>Runs the Leiden clustering algorithm with the specified resolution</li>
     *   <li>Builds a {@link ClusterResult} containing clusters and penalty edges</li>
     *   <li>Writes the result to the output file specified in params</li>
     * </ol>
     *
     * @param params the clustering parameters including input/output paths and resolution
     */
    public void execute(ClusterParams params) {
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = dependencyReader.read(params.depsFile());
        Map<String, Map<String, Double>> undirectedGraph = graphConverter.toUndirectedWeightedGraph(dependencyGraph);
        Partition partition = leidenAlgorithm.cluster(undirectedGraph, params.resolution());
        ClusterResult clusterResult = resultBuilder.build(partition, dependencyGraph, metricsCalculator, penaltyDetector);
        jsonWriter.write(clusterResult, params.outputFile());
    }

    /**
     * Executes the clustering pipeline without writing to a file.
     *
     * <p>This method performs the same workflow as {@link #execute(ClusterParams)}
     * but returns the {@link ClusterResult} directly instead of writing to a file.
     * Useful for testing and programmatic access.
     *
     * @param params the clustering parameters including input path and resolution
     * @return the clustering result containing all clusters and penalty edges
     */
    public ClusterResult analyze(ClusterParams params) {
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = dependencyReader.read(params.depsFile());
        Map<String, Map<String, Double>> undirectedGraph = graphConverter.toUndirectedWeightedGraph(dependencyGraph);
        Partition partition = leidenAlgorithm.cluster(undirectedGraph, params.resolution());
        return resultBuilder.build(partition, dependencyGraph, metricsCalculator, penaltyDetector);
    }
}