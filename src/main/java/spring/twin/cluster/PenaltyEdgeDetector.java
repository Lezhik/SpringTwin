package spring.twin.cluster;

import org.springframework.stereotype.Component;
import spring.twin.scan.LinkDetails;

import java.util.Map;
import java.util.Set;

/**
 * Detector for identifying penalty edges between clusters.
 *
 * <p>Penalty edges are connections between classes that belong to different clusters.
 * These represent cross-cluster dependencies that should be minimized during
 * architectural refactoring.
 */
@Component
public class PenaltyEdgeDetector {

    /**
     * Default constructor.
     */
    public PenaltyEdgeDetector() {
        // No initialization required
    }

    /**
     * Detects penalty edges in the dependency graph based on the partition.
     *
     * <p>For each class in the dependency graph, checks all its dependencies.
     * If a dependency belongs to a different cluster, it is considered a penalty edge.
     *
     * @param partition        the partition of nodes into clusters
     * @param dependencyGraph  the dependency graph as Map<String, Map<String, Set<LinkDetails>>>,
     *                         where the first key is the source class, the second key is the target class,
     *                         and the value is a set of link details describing the relationship
     * @return a map where each key is a class name and the value is a set of class names
     *         from different clusters that the key class depends on
     */
    public Map<String, Set<String>> detect(Partition partition,
                                           Map<String, Map<String, Set<LinkDetails>>> dependencyGraph) {
        Map<String, Set<String>> penaltyEdges = new java.util.TreeMap<>();

        for (Map.Entry<String, Map<String, Set<LinkDetails>>> entry : dependencyGraph.entrySet()) {
            String source = entry.getKey();
            Map<String, Set<LinkDetails>> targets = entry.getValue();

            // Skip source nodes not present in partition
            if (!partition.nodes().contains(source)) {
                continue;
            }

            int sourceCommunity = partition.communityOf(source);

            for (String target : targets.keySet()) {
                // Skip target nodes not present in partition
                if (!partition.nodes().contains(target)) {
                    continue;
                }

                int targetCommunity = partition.communityOf(target);

                // If communities differ, this is a penalty edge
                if (sourceCommunity != targetCommunity) {
                    penaltyEdges.computeIfAbsent(source, k -> new java.util.HashSet<>()).add(target);
                }
            }
        }

        return penaltyEdges;
    }
}