package spring.twin.dto;

import spring.twin.dto.DiEdgeDto;
import spring.twin.dto.DiNodeDto;

import java.util.List;

/**
 * DTO for the DI dependency graph produced by {@code scan-classes} command.
 * <p>
 * Represents the result of analyzing compiled .class files to extract
 * Spring dependency injection relationships, including generated constructors
 * (e.g., from Lombok).
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