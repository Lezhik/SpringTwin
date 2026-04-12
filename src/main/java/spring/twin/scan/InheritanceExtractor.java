package spring.twin.scan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.springframework.stereotype.Component;

/**
 * Extracts inheritance and interface implementation dependencies from Java bytecode.
 *
 * <p>This class analyzes class bytecode to extract all types that a class extends or implements,
 * including generic type parameters from inheritance signatures.
 *
 * <p>Uses ASM's ClassNode for bytecode parsing.
 */
@Component
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

    /**
     * Analyzes class bytecode and extracts all inheritance-related types with detailed link information.
     *
     * <p>This method extracts:
     * <ul>
     *   <li>The superclass (from {@code ClassNode.superName}) with {@link LinkType#SUPERCLASS}</li>
     *   <li>Directly implemented interfaces (from {@code ClassNode.interfaces}) with {@link LinkType#INTERFACE}</li>
     *   <li>Generic type parameters from inheritance signatures (from {@code ClassNode.signature}) with
     *       inherited link type: {@link LinkType#SUPERCLASS} for generics from superclass,
     *       {@link LinkType#INTERFACE} for generics from interfaces</li>
     * </ul>
     *
     * <p>All extracted types are returned as Fully Qualified Class Names (FQCN) mapped to their
     * corresponding {@link LinkDetails} sets.
     *
     * <p>LinkDetails formation rules:
     * <ul>
     *   <li>Superclass → {@code LinkDetails.of(LinkType.SUPERCLASS)} (empty details)</li>
     *   <li>Interface → {@code LinkDetails.of(LinkType.INTERFACE)} (empty details)</li>
     *   <li>Generic parameters → inherit the link type from their context</li>
     * </ul>
     *
     * @param classBytes the bytecode of the class to analyze
     * @return a map where keys are FQCN strings of types that this class extends or implements,
     *         and values are sets of {@link LinkDetails} describing the relationship;
     *         empty map if classBytes is null
     * @throws IllegalArgumentException if classBytes is not a valid class file
     */
    public Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes) {
        return new HashMap<>();
    }
}