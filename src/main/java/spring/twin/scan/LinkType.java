package spring.twin.scan;

/**
 * Enumeration of link types for classifying dependencies between classes.
 *
 * <p>Each link type defines the nature of a dependency: inheritance, field,
 * method, annotation, etc.
 *
 * <p>The JSON name of each constant matches its enum name and is used for
 * serialization in the dependencies.json output file.
 */
public enum LinkType {

    /**
     * Base class (extends). The details field is empty.
     */
    SUPERCLASS,

    /**
     * Implemented interface (implements). The details field is empty.
     */
    INTERFACE,

    /**
     * Class field. The details field contains the field name.
     */
    FIELD,

    /**
     * Usage in static initialization block. The details field is empty.
     */
    STATIC_BLOCK,

    /**
     * Usage in method (return type, argument, or method body).
     * The details field contains the method signature.
     */
    METHOD,

    /**
     * Class annotation. The details field is empty.
     */
    CLASS_ANNOTATION,

    /**
     * Field annotation. The details field contains the field name.
     */
    FIELD_ANNOTATION,

    /**
     * Method annotation. The details field contains the method signature.
     */
    METHOD_ANNOTATION,

    /**
     * Method argument annotation. The details field contains the method signature.
     */
    METHOD_ARG_ANNOTATION;

    /**
     * Returns the name of the constant for JSON serialization.
     *
     * <p>The returned value matches the enum constant name and is used
     * when writing dependency information to the output JSON file.
     *
     * @return the JSON name of this link type constant
     */
    public String getJsonName() {
        return name();
    }
}