package spring.twin.dto;

import java.util.List;

/**
 * DTO for the clustering result produced by {@code cluster} command.
 * <p>
 * Represents the result of combining DI and bytecode graphs and
 * performing architectural clustering analysis.
 *
 * @param clusters    list of identified clusters
 * @param penaltyEdges list of edges that cross cluster boundaries
 * @see ClusterDto
 * @see PenaltyEdgeDto
 */
public record ClustersDto(
    List<ClusterDto> clusters,
    List<PenaltyEdgeDto> penaltyEdges
) {
}