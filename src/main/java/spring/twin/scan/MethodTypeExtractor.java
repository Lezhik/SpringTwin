package spring.twin.scan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.springframework.stereotype.Component;

/**
 * Extracts method and constructor type dependencies from Java bytecode.
 *
 * <p>This class analyzes class bytecode to extract all types that are referenced
 * by method and constructor signatures, including parameter types, return types,
 * and generic type parameters from method signatures.
 *
 * <p>Handles regular methods, constructors, arrays (references base type), and generic methods.
 * Uses ASM's ClassNode for bytecode parsing.
 */
@Component
public class MethodTypeExtractor {

    /**
     * Analyzes class bytecode and extracts all method-related types.
     *
     * <p>This method extracts:
     * <ul>
     *   <li>Parameter types from method descriptors (from {@code MethodNode.desc})</li>
     *   <li>Return types from method descriptors</li>
     *   <li>Generic type parameters from method signatures (from {@code MethodNode.signature})</li>
     * </ul>
     *
     * <p>All extracted types are returned as Fully Qualified Class Names (FQCN).
     *
     * @param classBytes the bytecode of the class to analyze
     * @return a set of FQCN strings representing all types that this class references through methods,
     *         including types from generic signatures; empty set if no methods reference non-primitive types
     * @throws IllegalArgumentException if classBytes is null
     */
    public Set<String> extract(byte[] classBytes) {
        if (classBytes == null) {
            throw new IllegalArgumentException("classBytes cannot be null");
        }

        ClassReader classReader = new ClassReader(classBytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        Set<String> types = new HashSet<>();

        for (MethodNode method : classNode.methods) {
            // Extract types from method descriptor
            Type[] argumentTypes = Type.getArgumentTypes(method.desc);
            for (Type argType : argumentTypes) {
                FqcnNormalizer.fromDescriptor(argType.getDescriptor())
                        .ifPresent(types::add);
            }

            // Extract return type from method descriptor
            Type returnType = Type.getReturnType(method.desc);
            FqcnNormalizer.fromDescriptor(returnType.getDescriptor())
                    .ifPresent(types::add);

            // Extract types from generic signature if present
            if (method.signature != null) {
                types.addAll(GenericTypeExtractor.extractTypes(method.signature));
            }
        }

        return types;
    }

    /**
     * Analyzes class bytecode and extracts all method-related types with detailed link information.
     *
     * <p>This method extracts:
     * <ul>
     *   <li>Parameter types from method descriptors (from {@code MethodNode.desc})</li>
     *   <li>Return types from method descriptors</li>
     *   <li>Generic type parameters from method signatures (from {@code MethodNode.signature})</li>
     * </ul>
     *
     * <p>All extracted types are returned as Fully Qualified Class Names (FQCN) mapped to
     * {@link LinkDetails} sets with type {@link LinkType#METHOD} and method signature in details.
     *
     * <p>Method signature format follows ASM convention: {@code ownerClassName.methodName(descriptor)}.
     * For example:
     * <ul>
     *   <li>{@code com/example/OrderService.add(Lcom/example/model/OrderModel;)}</li>
     *   <li>For constructors: {@code com/example/OrderService.<init>(Lcom/example/model/OrderModel;)}</li>
     * </ul>
     *
     * @param classBytes the bytecode of the class to analyze
     * @return a map where keys are FQCN strings of types referenced through methods,
     *         and values are sets of {@link LinkDetails} with type METHOD and method signature;
     *         empty map if no methods reference non-primitive types
     * @throws IllegalArgumentException if classBytes is null
     */
    public Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes) {
        if (classBytes == null) {
            throw new IllegalArgumentException("classBytes cannot be null");
        }

        ClassReader classReader = new ClassReader(classBytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        Map<String, Set<LinkDetails>> result = new HashMap<>();

        for (MethodNode method : classNode.methods) {
            // Form signature: methodName(argumentDescriptor) - only method name and arguments, no class name, no return type
            String signature = extractMethodSignature(method.name, method.desc);

            // Extract types from method descriptor arguments
            Type[] argumentTypes = Type.getArgumentTypes(method.desc);
            for (Type argType : argumentTypes) {
                String fqcn = FqcnNormalizer.fromDescriptor(argType.getDescriptor()).orElse(null);
                if (fqcn != null) {
                    addDetail(result, fqcn, LinkDetails.of(LinkType.METHOD, signature));
                }
            }

            // Extract return type from method descriptor
            Type returnType = Type.getReturnType(method.desc);
            String returnFqcn = FqcnNormalizer.fromDescriptor(returnType.getDescriptor()).orElse(null);
            if (returnFqcn != null) {
                addDetail(result, returnFqcn, LinkDetails.of(LinkType.METHOD, signature));
            }

            // Extract types from generic signature if present
            if (method.signature != null) {
                Set<String> genericTypes = GenericTypeExtractor.extractTypes(method.signature);
                for (String genericFqcn : genericTypes) {
                    addDetail(result, genericFqcn, LinkDetails.of(LinkType.METHOD, signature));
                }
            }
        }

        return result;
    }

    /**
     * Adds a LinkDetails to the Set for the specified FQCN in the map.
     *
     * <p>If the FQCN is not yet present in the map, a new HashSet is created.
     * The LinkDetails is then added to the set associated with the FQCN.
     *
     * @param map the map to add the detail to
     * @param fqcn the Fully Qualified Class Name to use as the key
     * @param detail the LinkDetails to add to the set
     */
    private void addDetail(Map<String, Set<LinkDetails>> map, String fqcn, LinkDetails detail) {
        map.computeIfAbsent(fqcn, k -> new HashSet<>()).add(detail);
    }

    /**
     * Extracts method signature in the format: methodName(argumentDescriptor).
     *
     * <p>The signature includes only the method name and argument types (descriptor),
     * without the class name and without the return type.
     *
     * <p>Examples:
     * <ul>
     *   <li>{@code getName()} for method with no arguments</li>
     *   <li>{@code setName(Ljava/lang/String;)} for method with String argument</li>
     *   <li>{@code <init>(Ljava/lang/String;)} for constructor with String argument</li>
     * </ul>
     *
     * @param methodName the method name (or {@code <init>} for constructors)
     * @param methodDesc the method descriptor in ASM format (e.g., {@code ()Ljava/lang/String;})
     * @return the method signature with name and arguments only
     */
    private String extractMethodSignature(String methodName, String methodDesc) {
        // Extract argument part from descriptor: (args)returnType -> (args)
        int argsEnd = methodDesc.lastIndexOf(')');
        if (argsEnd == -1) {
            argsEnd = methodDesc.length();
        }
        String argumentPart = methodDesc.substring(0, argsEnd + 1);
        return methodName + argumentPart;
    }
}