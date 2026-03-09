package spring.twin.dto;

import java.util.List;

/**
 * DTO for the bytecode dependency graph produced by {@code scan-bytecode} command.
 * <p>
 * Represents the result of analyzing compiled .class files to extract
 * structural dependencies between classes.
 *
 * @param edges list of dependency edges extracted from bytecode
 * @see BytecodeEdgeDto
 */
public record BytecodeGraphDto(
    List<BytecodeEdgeDto> edges
) {
}