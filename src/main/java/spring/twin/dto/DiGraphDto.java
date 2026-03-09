package spring.twin.dto;

import java.util.List;

/**
 * DTO for the DI dependency graph produced by {@code scan-source} command.
 * <p>
 * Represents the result of analyzing Java source files to extract
 * Spring dependency injection relationships.
 *
 * @param nodes list of class nodes in the graph
 * @param edges list of dependency edges between classes
 * @see DiNodeDto
 * @see DiEdgeDto
 */
public record DiGraphDto(
    List<DiNodeDto> nodes,
    List<DiEdgeDto> edges
) {
}