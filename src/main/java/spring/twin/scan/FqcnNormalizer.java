package spring.twin.scan;

import java.util.Optional;

/**
 * Utility class for normalizing class names from JVM internal representations
 * to Fully Qualified Class Names (FQCN).
 * 
 * <p>Handles JVM descriptors, internal names, and extracts base types from arrays.
 * Primitive types are filtered out as they don't represent class dependencies.
 */
public final class FqcnNormalizer {

    private FqcnNormalizer() {
        // Utility class, no instantiation
    }

    /**
     * Converts a JVM internal name to FQCN.
     *
     * @param internalName JVM internal name (e.g., {@code com/example/OrderService})
     * @return FQCN with dots (e.g., {@code com.example.OrderService}),
     *         or {@code Optional.empty()} if input is null or represents a primitive
     */
    public static Optional<String> fromInternalName(String internalName) {
        if (internalName == null) {
            return Optional.empty();
        }
        // TODO: implement conversion logic
        return Optional.empty();
    }

    /**
     * Extracts FQCN from a JVM type descriptor.
     *
     * <p>Examples:
     * <ul>
     *   <li>{@code Lcom/example/OrderService;} → {@code com.example.OrderService}</li>
     *   <li>{@code [Lcom/example/OrderService;} → {@code com.example.OrderService}</li>
     *   <li>{@code I} → {@code Optional.empty()} (primitive)</li>
     *   <li>{@code [I} → {@code Optional.empty()} (primitive array)</li>
     * </ul>
     *
     * @param descriptor JVM type descriptor (e.g., {@code Lcom/example/OrderService;})
     * @return FQCN of the base type, or {@code Optional.empty()} for primitives and primitive arrays
     */
    public static Optional<String> fromDescriptor(String descriptor) {
        if (descriptor == null) {
            return Optional.empty();
        }
        // TODO: implement descriptor parsing
        return Optional.empty();
    }

    /**
     * Directly converts JVM internal name to FQCN by replacing slashes with dots.
     * Does not filter primitives.
     *
     * @param internalName JVM internal name (e.g., {@code com/example/OrderService})
     * @return FQCN with dots (e.g., {@code com.example.OrderService})
     */
    public static String internalToFqcn(String internalName) {
        if (internalName == null) {
            return null;
        }
        // TODO: implement conversion
        return internalName;
    }
}