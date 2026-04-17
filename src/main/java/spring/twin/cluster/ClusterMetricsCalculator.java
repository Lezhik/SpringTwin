package spring.twin.cluster;

import org.springframework.stereotype.Component;
import spring.twin.scan.LinkDetails;

import java.util.Map;
import java.util.Set;

/**
 * Calculator for computing cohesion and coupling metrics of clusters.
 *
 * <p>Cohesion represents the internal connectivity within a cluster - the proportion
 * of dependencies that stay within the cluster versus the total dependencies.
 * Coupling represents external connectivity - the proportion of dependencies
 * that cross cluster boundaries.
 *
 * <p>This calculator works with the dependency graph produced by the bytecode scanner
 * and the community assignments produced by the Leiden clustering algorithm.
 *
 * <p>When a cluster has no dependencies at all (both internal and external are zero),
 * the calculator returns cohesion = 1.0 and coupling = 0.0 as default values.
 */
@Component
public class ClusterMetricsCalculator {

    /**
     * Constructs a new ClusterMetricsCalculator.
     *
     * <p>Default constructor required for Spring dependency injection.
     */
    public ClusterMetricsCalculator() {
    }

    /**
     * Calculates cohesion and coupling metrics for a single cluster.
     *
     * <p>Scans the dependency graph to count:
     * <ul>
     * <li>Internal links: dependencies where both source and target belong to this cluster</li>
     * <li>External links: dependencies where source belongs to this cluster but target does not</li>
     * </ul>
     *
     * <p>Formulas:
     * <ul>
     * <li>cohesion = internalLinks / (internalLinks + externalLinks)</li>
     * <li>coupling = externalLinks / (internalLinks + externalLinks)</li>
     * </ul>
     *
     * <p>If there are no links at all, returns cohesion = 1.0 and coupling = 0.0.
     *
     * @param clusterClasses   the set of fully qualified class names in the cluster
     * @param dependencyGraph  the dependency graph mapping class names to their dependencies
     * @return a record containing cohesion and coupling values (0.0–1.0)
     */
    public ClusterMetricsRecord calculate(Set<String> clusterClasses,
                                          Map<String, Map<String, Set<LinkDetails>>> dependencyGraph) {
        int internalLinks = 0;
        int externalLinks = 0;

        for (String className : clusterClasses) {
            Map<String, Set<LinkDetails>> dependencies = dependencyGraph.getOrDefault(className, Map.of());
            for (String targetClass : dependencies.keySet()) {
                Set<LinkDetails> linkDetails = dependencies.get(targetClass);
                if (linkDetails != null && !linkDetails.isEmpty()) {
                    if (clusterClasses.contains(targetClass)) {
                        internalLinks += linkDetails.size();
                    } else {
                        externalLinks += linkDetails.size();
                    }
                }
            }
        }

        int totalLinks = internalLinks + externalLinks;
        if (totalLinks == 0) {
            return new ClusterMetricsRecord(1.0, 0.0);
        }

        double cohesion = (double) internalLinks / totalLinks;
        double coupling = (double) externalLinks / totalLinks;
        return new ClusterMetricsRecord(cohesion, coupling);
    }

    /**
     * Calculates cohesion and coupling metrics for all clusters.
     *
     * <p>Iterates over all clusters in the community map and computes metrics
     * for each one using the dependency graph.
     *
     * @param communityMap    map from cluster ID to set of class names in that cluster
     * @param dependencyGraph the dependency graph mapping class names to their dependencies
     * @return a map from cluster ID to its calculated metrics
     */
    public Map<Integer, ClusterMetricsRecord> calculateAll(Map<Integer, Set<String>> communityMap,
                                                           Map<String, Map<String, Set<LinkDetails>>> dependencyGraph) {
        Map<Integer, ClusterMetricsRecord> result = new java.util.HashMap<>();
        for (Map.Entry<Integer, Set<String>> entry : communityMap.entrySet()) {
            ClusterMetricsRecord metrics = calculate(entry.getValue(), dependencyGraph);
            result.put(entry.getKey(), metrics);
        }
        return result;
    }
}