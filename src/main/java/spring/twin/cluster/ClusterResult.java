package spring.twin.cluster;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Immutable record representing the complete result of a clustering operation.
 *
 * <p>Contains all clusters identified by the Leiden algorithm along with
 * penalty edges that represent dependencies crossing cluster boundaries.
 * This record is the primary output of the clustering pipeline and is
 * serialized to clusters.json.
 *
 * <p>The list of clusters is sorted by cluster ID for consistent output.
 *
 * @param clusters    the sorted list of cluster records
 * @param penaltyEdges a map from class name to set of classes in different
 *                    clusters that it depends on (cross-cluster dependencies)
 */
public record ClusterResult(List<ClusterRecord> clusters, Map<String, Set<String>> penaltyEdges) {
}