package spring.twin.scan;

import java.util.Set;

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
        throw new UnsupportedOperationException("Not implemented yet");
    }
}