package spring.twin.scan;

import java.util.Objects;

/**
 * Model for storing detailed information about a link between classes.
 *
 * <p>Contains the type of link ({@link LinkType}) and additional description
 * ({@code details}) that depends on the link type. Used to represent
 * dependencies in the dependency graph.
 *
 * <p>For example:
 * <pre>
 * LinkDetails.of(LinkType.FIELD, "orderRepository")
 * // serializes to: {"type": "FIELD", "details": "orderRepository"}
 * </pre>
 *
 * @param type    the type of link (superclass, field, method, annotation, etc.)
 * @param details additional information about the link, depends on type:
 *                <ul>
 *                <li>SUPERCLASS — empty string</li>
 *                <li>INTERFACE — empty string</li>
 *                <li>FIELD — field name</li>
 *                <li>STATIC_BLOCK — empty string</li>
 *                <li>METHOD — method signature</li>
 *                <li>CLASS_ANNOTATION — empty string</li>
 *                <li>FIELD_ANNOTATION — field name</li>
 *                <li>METHOD_ANNOTATION — method signature</li>
 *                <li>METHOD_ARG_ANNOTATION — method signature</li>
 *                </ul>
 */
public record LinkDetails(LinkType type, String details) {

    /**
     * Creates a new LinkDetails instance with the specified type and details.
     *
     * <p>This factory method is used when additional information about the link
     * is available (e.g., field name for FIELD type, method signature for METHOD type).
     *
     * <p>The returned instance is suitable for use in collections such as {@code Set<LinkDetails>}.
     *
     * @param type    the type of link, must not be null
     * @param details additional information about the link, may be empty but not null
     * @return a new LinkDetails instance
     */
    public static LinkDetails of(LinkType type, String details) {
        return new LinkDetails(type, details);
    }

    /**
     * Creates a new LinkDetails instance with the specified type and empty details.
     *
     * <p>This factory method is a convenience for link types that do not require
     * additional information (e.g., SUPERCLASS, INTERFACE, CLASS_ANNOTATION).
     *
     * <p>The returned instance is suitable for use in collections such as {@code Set<LinkDetails>}.
     *
     * @param type the type of link, must not be null
     * @return a new LinkDetails instance with empty details
     */
    public static LinkDetails of(LinkType type) {
        return new LinkDetails(type, "");
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * <p>Two LinkDetails instances are considered equal if both their {@code type}
     * and {@code details} fields are equal. This ensures that duplicate links
     * are avoided when stored in a {@code Set<LinkDetails>}.
     *
     * @param o the reference object with which to compare
     * @return true if this object is the same as the o argument; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkDetails that = (LinkDetails) o;
        return type == that.type && Objects.equals(details, that.details);
    }

    /**
     * Returns a hash code value for this LinkDetails.
     *
     * <p>The hash code is computed from both the {@code type} and {@code details}
     * fields to ensure consistent behavior with {@link #equals(Object)}.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, details);
    }
}