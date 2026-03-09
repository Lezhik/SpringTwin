package spring.twin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents a refactoring task for AI agents.
 * <p>
 * Describes an architectural issue that needs to be addressed
 * and provides suggestions for resolution.
 *
 * @param taskType         the type of refactoring task
 * @param className        the class that needs refactoring
 * @param reason           explanation of why refactoring is needed
 * @param suggestedModules suggested target modules for splitting or moving
 * @see TasksDto
 */
public record TaskDto(
    String taskType,
    @JsonProperty("class") String className,
    String reason,
    List<String> suggestedModules
) {
}