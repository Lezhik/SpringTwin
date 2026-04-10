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
        // Filter primitive types
        if (isPrimitiveInternalName(internalName)) {
            return Optional.empty();
        }
        return Optional.of(internalToFqcn(internalName));
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
        if (descriptor == null || descriptor.isEmpty()) {
            return Optional.empty();
        }
        // Strip array brackets
        String remaining = descriptor;
        while (remaining.startsWith("[")) {
            remaining = remaining.substring(1);
        }
        
        // Check for primitive after stripping arrays
        if (remaining.length() == 1 && isPrimitiveDescriptorChar(remaining.charAt(0))) {
            return Optional.empty();
        }
        
        // Handle object types L...;
        if (remaining.startsWith("L") && remaining.endsWith(";")) {
            String internalName = remaining.substring(1, remaining.length() - 1);
            return Optional.of(internalToFqcn(internalName));
        }
        
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
        return internalName.replace('/', '.');
    }
    
    /**
     * Checks if an internal name represents a primitive type.
     */
    private static boolean isPrimitiveInternalName(String name) {
        return "int".equals(name) || "long".equals(name) || "boolean".equals(name)
                || "byte".equals(name) || "short".equals(name) || "char".equals(name)
                || "float".equals(name) || "double".equals(name) || "void".equals(name);
    }
    
    /**
     * Checks if a character represents a primitive type in descriptor notation.
     */
    private static boolean isPrimitiveDescriptorChar(char c) {
        return c == 'I' || c == 'J' || c == 'Z' || c == 'B'
                || c == 'S' || c == 'C' || c == 'F' || c == 'D' || c == 'V';
    }
}