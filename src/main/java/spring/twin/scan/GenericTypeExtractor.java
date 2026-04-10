package spring.twin.scan;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for extracting types from JVM generic signatures.
 *
 * <p>This class parses generic signatures in JVM format and extracts all referenced
 * types as Fully Qualified Class Names (FQCN). It handles class, method, and field
 * generic parameters, including nested generics.
 */
public final class GenericTypeExtractor {

    private GenericTypeExtractor() {
        // Utility class, no instantiation
    }

    /**
     * Parses a generic signature in JVM format and extracts all mentioned types as FQCN.
     *
     * <p>This method handles various signature formats including class signatures,
     * field signatures, and method signatures. It extracts the base type, its generic
     * parameters, and any nested generics.
     *
     * <p>Scanning rules:
     * <ul>
     *   <li>On 'L' - collects characters until ';' as internal class name</li>
     *   <li>On '<' - starts generic parameter region, types inside also extracted</li>
     *   <li>On 'T' - formal type parameter (like T:), skips until ';'</li>
     *   <li>Primitive descriptors (I, J, B, S, C, F, D, Z, V) are skipped</li>
     *   <li>Arrays ([) are skipped, but base type is extracted</li>
     * </ul>
     *
     * @param signature the JVM generic signature to parse (e.g., {@code Ljava/util/List<Ljava/lang/String;>;})
     * @return a set of all extracted FQCN types, or an empty set if signature is null or empty
     */
    public static Set<String> extractTypes(String signature) {
        if (signature == null || signature.isEmpty()) {
            return Set.of();
        }

        Set<String> types = new HashSet<>();
        scanSignature(signature, types, true);
        return types;
    }

    /**
     * Extracts type names from a single type signature.
     *
     * <p>This method parses a type signature and extracts the base type along with
     * any generic type arguments. Array types are unwrapped to their base component type.
     *
     * <p>Example:
     * <ul>
     *   <li>{@code Ljava/util/List<Ljava/lang/String;>;} → {@code [java.util.List, java.lang.String]}</li>
     *   <li>{@code Ljava/util/Map<Ljava/lang/String;Ljava/lang/Integer;>;} → {@code [java.util.Map, java.lang.String, java.lang.Integer]}</li>
     * </ul>
     *
     * <p>Scanning rules:
     * <ul>
     *   <li>On 'L' - collects characters until ';' as internal class name</li>
     *   <li>On '<' - starts generic parameter region, types inside also extracted</li>
     *   <li>On 'T' - formal type parameter, skips until ';'</li>
     *   <li>Primitive descriptors (I, J, B, S, C, F, D, Z, V) are skipped</li>
     *   <li>Arrays ([) are skipped, but base type is extracted</li>
     * </ul>
     *
     * @param typeSignature the JVM type signature to parse (e.g., {@code Ljava/util/List<Ljava/lang/String;>;})
     * @return a set of all extracted FQCN types from the signature, or an empty set if signature is null or empty
     */
    public static Set<String> extractTypeNames(String typeSignature) {
        if (typeSignature == null || typeSignature.isEmpty()) {
            return Set.of();
        }

        Set<String> types = new HashSet<>();
        scanSignature(typeSignature, types, false);
        return types;
    }

    /**
     * Scans a signature character by character and extracts all type references.
     *
     * @param signature the signature to scan
     * @param types the set to collect extracted types into
     * @param isClassSignature whether this is a class-level signature (may have formal type parameters)
     */
    private static void scanSignature(String signature, Set<String> types, boolean isClassSignature) {
        int length = signature.length();
        int i = 0;

        while (i < length) {
            char c = signature.charAt(i);

            if (c == '[') {
                // Array marker - skip it, base type will be processed next
                i++;
            } else if (c == 'L') {
                // Object type - extract internal name until ';'
                int end = findNextSemicolon(signature, i + 1);
                if (end > i) {
                    String internalName = extractInternalName(signature, i + 1, end);
                    // Check for generic parameters within this type
                    int genericStart = internalName.indexOf('<');
                    if (genericStart > 0) {
                        String baseType = internalName.substring(0, genericStart);
                        FqcnNormalizer.fromInternalName(baseType).ifPresent(types::add);
                        // Process generic arguments
                        String genericArgs = internalName.substring(genericStart);
                        scanGenericArguments(genericArgs, types);
                    } else {
                        FqcnNormalizer.fromInternalName(internalName).ifPresent(types::add);
                    }
                    i = end + 1;
                } else {
                    i++;
                }
            } else if (c == 'T') {
                // Formal type parameter - skip until ';'
                int end = findNextSemicolon(signature, i + 1);
                i = end + 1;
            } else if (c == '<') {
                // Start of generic parameter section (at class level)
                int end = findMatchingAngleBracket(signature, i);
                if (end > i) {
                    String genericSection = signature.substring(i + 1, end);
                    scanGenericArguments(genericSection, types);
                    i = end + 1;
                } else {
                    i++;
                }
            } else if (isPrimitiveDescriptor(c)) {
                // Primitive - skip
                i++;
            } else {
                // Other characters (like *, +, - for wildcards) - skip
                i++;
            }
        }
    }

    /**
     * Scans generic arguments section character by character.
     *
     * @param args the generic arguments string (without outer <>)
     * @param types the set to collect extracted types into
     */
    private static void scanGenericArguments(String args, Set<String> types) {
        int length = args.length();
        int i = 0;

        while (i < length) {
            char c = args.charAt(i);

            if (c == '[') {
                // Array marker - skip
                i++;
            } else if (c == 'L') {
                // Object type - extract until ';'
                int end = findNextSemicolon(args, i + 1);
                if (end > i) {
                    String internalName = extractInternalName(args, i + 1, end);
                    // Check for nested generics
                    int genericStart = internalName.indexOf('<');
                    if (genericStart > 0) {
                        String baseType = internalName.substring(0, genericStart);
                        FqcnNormalizer.fromInternalName(baseType).ifPresent(types::add);
                        // Process nested generic arguments
                        String nestedArgs = internalName.substring(genericStart);
                        scanGenericArguments(nestedArgs, types);
                    } else {
                        FqcnNormalizer.fromInternalName(internalName).ifPresent(types::add);
                    }
                    i = end + 1;
                } else {
                    i++;
                }
            } else if (c == 'T') {
                // Formal type parameter - skip until ';'
                int end = findNextSemicolon(args, i + 1);
                i = end + 1;
            } else if (c == '+' || c == '-' || c == '*') {
                // Wildcard markers - skip
                i++;
            } else if (isPrimitiveDescriptor(c)) {
                // Primitive - skip
                i++;
            } else {
                // Other characters
                i++;
            }
        }
    }

    /**
     * Finds the next semicolon in the string, accounting for nested angle brackets.
     *
     * @param str the string to search
     * @param start the starting position
     * @return the position of the next semicolon, or -1 if not found
     */
    private static int findNextSemicolon(String str, int start) {
        int depth = 0;
        for (int i = start; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '<') {
                depth++;
            } else if (c == '>') {
                depth--;
            } else if (c == ';' && depth == 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds the matching closing angle bracket for an opening one.
     *
     * @param str the string to search
     * @param openPos the position of the opening '<'
     * @return the position of the matching '>', or -1 if not found
     */
    private static int findMatchingAngleBracket(String str, int openPos) {
        int depth = 1;
        for (int i = openPos + 1; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '<') {
                depth++;
            } else if (c == '>') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Extracts internal name from signature, handling possible nested generics.
     *
     * @param signature the signature string
     * @param start the start position (after 'L')
     * @param end the end position (at ';')
     * @return the internal name, possibly including generic arguments
     */
    private static String extractInternalName(String signature, int start, int end) {
        return signature.substring(start, end);
    }

    /**
     * Checks if a character represents a primitive type descriptor.
     *
     * @param c the character to check
     * @return true if the character is a primitive descriptor
     */
    private static boolean isPrimitiveDescriptor(char c) {
        return c == 'I' || c == 'J' || c == 'B' || c == 'S' || c == 'C'
                || c == 'F' || c == 'D' || c == 'Z' || c == 'V';
    }
}