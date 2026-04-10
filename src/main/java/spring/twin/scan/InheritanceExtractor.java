package spring.twin.scan;

import java.util.Set;

/**
 * Extracts inheritance and interface implementation dependencies from Java bytecode.
 *
 * <p>This class analyzes class bytecode to extract all types that a class extends or implements,
 * including generic type parameters from inheritance signatures.
 *
 * <p>Uses ASM's ClassNode for bytecode parsing.
 */
public class InheritanceExtractor {

    /**
     * Analyzes class bytecode and extracts all inheritance-related types.
     *
     * <p>This method extracts:
     * <ul>
     *   <li>The superclass (from {@code ClassNode.superName})</li>
     *   <li>Directly implemented interfaces (from {@code ClassNode.interfaces})</li>
     *   <li>Generic type parameters from inheritance signatures (from {@code ClassNode.signature})</li>
     * </ul>
     *
     * <p>All extracted types are returned as Fully Qualified Class Names (FQCN).
     *
     * @param classBytes the bytecode of the class to analyze
     * @return a set of FQCN strings representing all types that this class extends or implements,
     *         including types from generic signatures; empty set if classBytes is null
     * @throws IllegalArgumentException if classBytes is not a valid class file
     */
    public Set<String> extract(byte[] classBytes) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}