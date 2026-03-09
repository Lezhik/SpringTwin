package spring.twin.dto.types;

/**
 * Types of edges in the dependency graph.
 * <p>
 * Defines the nature of relationships between classes in the analyzed codebase.
 *
 * @see spring.twin.dto.DiGraphDto
 * @see spring.twin.dto.BytecodeGraphDto
 * @see spring.twin.dto.ClustersDto
 */
public enum EdgeType {

    /**
     * Spring dependency injection relationship.
     * <p>
     * Indicates that a class depends on another class through Spring's DI mechanism.
     */
    DEPENDS_ON,

    /**
     * Method call relationship.
     * <p>
     * Indicates that a class calls methods on another class.
     */
    CALLS,

    /**
     * Instantiation relationship.
     * <p>
     * Indicates that a class creates instances of another class.
     */
    INSTANTIATES,

    /**
     * Field access relationship.
     * <p>
     * Indicates that a class directly accesses fields of another class.
     */
    ACCESSES_FIELD

}