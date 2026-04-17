package spring.twin.cluster;

import org.springframework.stereotype.Component;
import spring.twin.scan.LinkDetails;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builder for constructing {@link ClusterResult} from a partition.
 *
 * <p>This component orchestrates the creation of cluster results by:
 * <ol>
 *   <li>Extracting community assignments from the partition</li>
 *   <li>Calculating metrics for each cluster using {@link ClusterMetricsCalculator}</li>
 *   <li>Detecting penalty edges using {@link PenaltyEdgeDetector}</li>
 *   <li>Creating {@link ClusterRecord} instances for each community</li>
 *   <li>Assembling the final {@link ClusterResult}</li>
 * </ol>
 *
 * <p>The builder ensures that:
 * <ul>
 *   <li>Cluster records are sorted by cluster ID</li>
 *   <li>Class lists within each cluster are sorted alphabetically</li>
 *   <li>All dependencies are properly injected via Spring</li>
 * </ul>
 */
@Component
public class ClusterResultBuilder {

    /**
     * Default constructor.
     *
     * <p>Required for Spring component scanning and dependency injection.
     */
    public ClusterResultBuilder() {
    }

    /**
     * Builds a {@link ClusterResult} from the given partition and dependency graph.
     *
     * <p>This method performs the following steps:
     * <ol>
     *   <li>Extracts the community map from the partition using {@link Partition#toCommunityMap()}</li>
     *   <li>Calculates metrics for each cluster using the provided {@link ClusterMetricsCalculator}</li>
     *   <li>Detects penalty edges using the provided {@link PenaltyEdgeDetector}</li>
     *   <li>Creates a {@link ClusterRecord} for each community with sorted class lists</li>
     *   <li>Assembles and returns the final {@link ClusterResult} with sorted clusters</li>
     * </ol>
     *
     * @param partition          the partition of nodes into communities (clusters)
     * @param dependencyGraph    the dependency graph as Map<String, Map<String, Set<LinkDetails>>>,
     *                           where the first key is the source class, the second key is the target class,
     *                           and the value is a set of link details describing the relationship
     * @param metricsCalculator  the calculator for computing cluster cohesion and coupling metrics
     * @param penaltyDetector    the detector for identifying cross-cluster penalty edges
     * @return a complete {@link ClusterResult} containing all clusters and penalty edges
     */
    public ClusterResult build(Partition partition,
                               Map<String, Map<String, Set<LinkDetails>>> dependencyGraph,
                               ClusterMetricsCalculator metricsCalculator,
                               PenaltyEdgeDetector penaltyDetector) {
        // 1. Extract community map from partition
        Map<Integer, Set<String>> communityMap = partition.toCommunityMap();

        // 2. Calculate metrics for each cluster
        Map<Integer, ClusterMetricsRecord> metricsMap = metricsCalculator.calculateAll(communityMap, dependencyGraph);

        // 3. Detect penalty edges
        Map<String, Set<String>> penaltyEdges = penaltyDetector.detect(partition, dependencyGraph);

        // 4. Create ClusterRecord for each community
        List<ClusterRecord> clusters = new java.util.ArrayList<>();
        for (Map.Entry<Integer, Set<String>> entry : communityMap.entrySet()) {
            int communityId = entry.getKey();
            Set<String> classSet = entry.getValue();

            // Sort classes alphabetically
            List<String> sortedClasses = new java.util.ArrayList<>(classSet);
            java.util.Collections.sort(sortedClasses);

            // Create cluster id
            String clusterId = "cluster-" + communityId;

            // Get metrics for this cluster
            ClusterMetricsRecord metrics = metricsMap.getOrDefault(communityId, new ClusterMetricsRecord(1.0, 0.0));

            clusters.add(new ClusterRecord(clusterId, sortedClasses, metrics));
        }

        // 5. Sort clusters by numeric id (extract number from "cluster-{number}")
        clusters.sort(java.util.Comparator.comparingInt(c -> {
            String id = c.id();
            String numberPart = id.substring("cluster-".length());
            return Integer.parseInt(numberPart);
        }));

        // 6. Return ClusterResult
        return new ClusterResult(clusters, penaltyEdges);
    }
}