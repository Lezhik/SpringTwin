package spring.twin.dto.types;

/**
 * Types of Spring dependency injection.
 * <p>
 * Specifies how dependencies are injected in Spring-managed beans.
 *
 * @see spring.twin.dto.DiEdgeDto
 */
public enum InjectionType {

    /**
     * Constructor-based injection.
     * <p>
     * Dependencies are provided through the class constructor.
     * Preferred method for mandatory dependencies.
     */
    CONSTRUCTOR,

    /**
     * Field-based injection.
     * <p>
     * Dependencies are injected directly into class fields using
     * {@code @Autowired} annotation.
     */
    FIELD,

    /**
     * Setter-based injection.
     * <p>
     * Dependencies are provided through setter methods.
     * Useful for optional dependencies.
     */
    SETTER,

    /**
     * Method parameter injection.
     * <p>
     * Dependencies are injected as method parameters,
     * typically in configuration classes.
     */
    METHOD_PARAMETER

}