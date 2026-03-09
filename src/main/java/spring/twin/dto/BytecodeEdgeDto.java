package spring.twin.dto;

import spring.twin.dto.types.EdgeType;

/**
 * Represents an edge in the bytecode dependency graph.
 * <p>
 * Describes structural dependencies extracted from bytecode analysis,
 * including method calls, field accesses, and instantiations.
 *
 * @param type      the edge type (CALLS, ACCESSES_FIELD, INSTANTIATES)
 * @param fromClass the source class name
 * @param toClass   the target class name
 * @param method    the method name (for CALLS edges)
 * @param field     the field name (for ACCESSES_FIELD edges)
 * @see BytecodeGraphDto
 */
public record BytecodeEdgeDto(
    EdgeType type,
    String fromClass,
    String toClass,
    String method,
    String field
) {
}