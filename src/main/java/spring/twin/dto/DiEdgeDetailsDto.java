package spring.twin.dto;

import spring.twin.dto.types.InjectionType;

/**
 * Detailed information about a DI dependency edge.
 * <p>
 * Contains specifics about how the dependency is injected.
 *
 * @param injectionType   the type of Spring injection used
 * @param parameterIndex  the parameter index (for constructor injection)
 * @param fieldName       the field name (for field injection)
 * @param setterName      the setter method name (for setter injection)
 * @see DiEdgeDto
 */
public record DiEdgeDetailsDto(
    InjectionType injectionType,
    Integer parameterIndex,
    String fieldName,
    String setterName
) {
}