package spring.twin.dto;

import java.util.List;

/**
 * Represents a cluster of related classes.
 * <p>
 * A cluster groups classes that have high internal cohesion and
 * low external coupling, representing a potential architectural module.
 *
 * @param id      the unique identifier of the cluster
 * @param classes list of class names belonging to this cluster
 * @param metrics cohesion and coupling metrics for the cluster
 * @see ClustersDto
 */
public record ClusterDto(
    String id,
    List<String> classes,
    ClusterMetricsDto metrics
) {
}