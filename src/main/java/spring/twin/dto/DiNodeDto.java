package spring.twin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents a node in the Spring DI dependency graph.
 * <p>
 * Corresponds to a class analyzed during source code scanning.
 *
 * @param type    the node type (e.g., "Class")
 * @param name    the simple class name
 * @param packageName the package name of the class
 * @param labels  Spring stereotypes and other labels (e.g., "Service", "Controller")
 * @see DiGraphDto
 */
public record DiNodeDto(
    String type,
    String name,
    @JsonProperty("package") String packageName,
    List<String> labels
) {
}