package spring.twin.dto;

import spring.twin.dto.types.EdgeType;

/**
 * Represents an edge in the Spring DI dependency graph.
 * <p>
 * Describes a dependency relationship between two classes.
 *
 * @param type    the edge type (always {@link EdgeType#DEPENDS_ON} for DI graph)
 * @param from    the source class name
 * @param to      the target class name (dependency)
 * @param details additional details about the injection
 * @see DiGraphDto
 * @see DiEdgeDetailsDto
 */
public record DiEdgeDto(
    EdgeType type,
    String from,
    String to,
    DiEdgeDetailsDto details
) {
}