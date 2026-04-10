package spring.twin.scan;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * <p>Examples of input signatures:
     * <ul>
     *   <li>Class: {@code <T:Ljava/lang/Object;>Ljava/lang/Object;}</li>
     *   <li>Field: {@code Ljava/util/List<Ljava/lang/String;>;}</li>
     *   <li>Method: {@code (Ljava/util/List<Ljava/lang/String;>;)Ljava/util/Map<Ljava/lang/String;Ljava/lang/Integer;>;;}</li>
     * </ul>
     *
     * @param signature the JVM generic signature to parse (e.g., {@code Ljava/util/List<Ljava/lang/String;>;})
     * @return a set of all extracted FQCN types, or an empty set if signature is null or contains no reference types
     */
    public static Set<String> extractTypes(String signature) {
        if (signature == null) {
            return Set.of();
        }
        
        Set<String> types = new HashSet<>();
        
        // Extract from field/method signatures (start with L or [)
        if (signature.startsWith("L") || signature.startsWith("[")) {
            types.addAll(extractTypeNames(signature));
        }
        
        // Extract from class signatures (start with <)
        if (signature.startsWith("<")) {
            Pattern typePattern = Pattern.compile("L([^;]+);");
            Matcher matcher = typePattern.matcher(signature);
            while (matcher.find()) {
                String internalName = matcher.group(1);
                // Handle type arguments within class signature
                int typeArgStart = internalName.indexOf('<');
                if (typeArgStart > 0) {
                    String baseType = internalName.substring(0, typeArgStart);
                    FqcnNormalizer.fromInternalName(baseType).ifPresent(types::add);
                    // Extract nested types from type arguments
                    extractNestedTypes(internalName.substring(typeArgStart), types);
                } else {
                    FqcnNormalizer.fromInternalName(internalName).ifPresent(types::add);
                }
            }
        }
        
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
     * @param typeSignature the JVM type signature to parse (e.g., {@code Ljava/util/List<Ljava/lang/String;>;})
     * @return a set of all extracted FQCN types from the signature
     */
    public static Set<String> extractTypeNames(String typeSignature) {
        Set<String> types = new HashSet<>();
        
        if (typeSignature == null || typeSignature.isEmpty()) {
            return types;
        }
        
        // Pattern to match: Lclass/name<arg1<arg2>;arg3;...;
        // or simple: Lclass/name;
        Pattern typePattern = Pattern.compile("L([^;]+);");
        Matcher matcher = typePattern.matcher(typeSignature);
        
        while (matcher.find()) {
            String fullType = matcher.group(1);
            extractNestedTypes(fullType, types);
        }
        
        return types;
    }

    /**
     * Extracts nested generic types from a type string that may contain type arguments.
     */
    private static void extractNestedTypes(String typeString, Set<String> types) {
        // First, extract the base type before any type arguments
        int genericStart = typeString.indexOf('<');
        String baseType;
        String remainingArgs;
        
        if (genericStart > 0) {
            baseType = typeString.substring(0, genericStart);
            remainingArgs = typeString.substring(genericStart);
            FqcnNormalizer.fromInternalName(baseType).ifPresent(types::add);
        } else {
            remainingArgs = typeString;
        }
        
        // Process remaining type arguments recursively
        if (!remainingArgs.isEmpty() && remainingArgs.startsWith("<")) {
            // Remove outer <> and process
            String args = remainingArgs.substring(1, remainingArgs.lastIndexOf('>'));
            Pattern nestedPattern = Pattern.compile("L([^;]+);");
            Matcher nestedMatcher = nestedPattern.matcher(args);
            while (nestedMatcher.find()) {
                String nestedType = nestedMatcher.group(1);
                extractNestedTypes(nestedType, types);
            }
            
            // Handle recursive nesting like List<List<String>>
            int idx = 0;
            int depth = 0;
            StringBuilder current = new StringBuilder();
            for (char c : args.toCharArray()) {
                if (c == '<') {
                    depth++;
                    if (depth == 1) {
                        // Start of nested type argument
                        current = new StringBuilder();
                    } else {
                        current.append(c);
                    }
                } else if (c == '>') {
                    depth--;
                    if (depth == 0) {
                        // End of nested type argument
                        extractNestedTypes(current.toString(), types);
                    } else {
                        current.append(c);
                    }
                } else if (depth > 0) {
                    current.append(c);
                }
            }
        }
    }
}