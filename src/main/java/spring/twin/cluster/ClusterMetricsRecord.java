package spring.twin.cluster;

/**
 * Immutable record holding cohesion and coupling metrics for a cluster.
 *
 * <p>Cohesion represents the proportion of internal dependencies within a cluster
 * relative to the total number of dependencies. Coupling represents the proportion
 * of external dependencies relative to the total number of dependencies.
 *
 * <p>Both values are in the range 0.0 to 1.0 and are complementary:
 * cohesion + coupling = 1.0 (when there are dependencies).
 *
 * @param cohesion the proportion of internal links (0.0–1.0)
 * @param coupling the proportion of external links (0.0–1.0)
 */
public record ClusterMetricsRecord(double cohesion, double coupling) {
}