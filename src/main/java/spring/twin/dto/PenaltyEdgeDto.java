package spring.twin.dto;

import spring.twin.dto.types.EdgeType;

/**
 * Represents an edge that crosses cluster boundaries.
 * <p>
 * These edges indicate coupling between different clusters,
 * which is undesirable from an architectural perspective.
 *
 * @param from the source class name
 * @param to   the target class name
 * @param type the type of the dependency
 * @see ClustersDto
 */
public record PenaltyEdgeDto(
    String from,
    String to,
    EdgeType type
) {
}