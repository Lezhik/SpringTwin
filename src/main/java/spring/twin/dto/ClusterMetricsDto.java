package spring.twin.dto;

/**
 * Metrics for a cluster in the architecture.
 * <p>
 * Contains cohesion and coupling measurements that indicate
 * the quality of the cluster boundaries.
 *
 * @param cohesion the internal cohesion of the cluster (0.0 to 1.0)
 * @param coupling the external coupling of the cluster (0.0 to 1.0)
 * @see ClusterDto
 */
public record ClusterMetricsDto(
    Double cohesion,
    Double coupling
) {
}