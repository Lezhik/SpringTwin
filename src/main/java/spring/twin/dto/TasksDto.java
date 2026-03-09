package spring.twin.dto;

import java.util.List;

/**
 * DTO for the refactoring tasks produced by {@code generate-refactoring} command.
 * <p>
 * Represents a list of architectural refactoring tasks for AI agents
 * to improve the codebase structure.
 *
 * @param tasks list of refactoring tasks
 * @see TaskDto
 */
public record TasksDto(
    List<TaskDto> tasks
) {
}