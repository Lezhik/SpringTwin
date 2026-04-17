package spring.twin.cluster;

import java.util.List;

/**
 * Immutable record representing a single cluster of related classes.
 *
 * <p>A cluster is a group of classes that are strongly connected to each other
 * based on their structural dependencies. Each cluster has a unique identifier,
 * a list of classes belonging to it, and metrics describing its internal cohesion
 * and external coupling.
 *
 * <p>The list of classes is sorted alphabetically for consistent serialization.
 *
 * @param id      the unique cluster identifier (e.g., "cluster-1")
 * @param classes the sorted list of fully qualified class names in the cluster
 * @param metrics the cohesion and coupling metrics for this cluster
 */
public record ClusterRecord(String id, List<String> classes, ClusterMetricsRecord metrics) {
}