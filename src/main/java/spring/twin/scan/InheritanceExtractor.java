package spring.twin.scan;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

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
        if (classBytes == null) {
            throw new IllegalArgumentException("classBytes must not be null");
        }

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(classBytes);
        classReader.accept(classNode, 0);

        Set<String> result = new HashSet<>();

        // Extract superclass (excluding java/lang/Object)
        if (classNode.superName != null && !"java/lang/Object".equals(classNode.superName)) {
            FqcnNormalizer.fromInternalName(classNode.superName).ifPresent(result::add);
        }

        // Extract implemented interfaces
        if (classNode.interfaces != null) {
            for (String iface : classNode.interfaces) {
                FqcnNormalizer.fromInternalName(iface).ifPresent(result::add);
            }
        }

        // Extract generic types from signature
        if (classNode.signature != null) {
            Set<String> genericTypes = GenericTypeExtractor.extractTypes(classNode.signature);
            result.addAll(genericTypes);
        }

        return result;
    }
}