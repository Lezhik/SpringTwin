package spring.twin.scan;

import java.util.HashSet;
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
}