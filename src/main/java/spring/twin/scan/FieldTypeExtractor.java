package spring.twin.scan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.springframework.stereotype.Component;

/**
 * Extracts field type dependencies from Java bytecode.
 *
 * <p>This class analyzes class bytecode to extract all types that are referenced
 * by class fields, including generic type parameters from field signatures.
 *
 * <p>Handles regular fields, arrays (references base type), and generic fields.
 * Uses ASM's ClassNode for bytecode parsing.
 */
@Component
public class FieldTypeExtractor {

    /**
     * Analyzes class bytecode and extracts all field-related types.
     *
     * <p>This method extracts:
     * <ul>
     *   <li>Field types from descriptors (from {@code FieldNode.desc})</li>
     *   <li>Generic type parameters from field signatures (from {@code FieldNode.signature})</li>
     * </ul>
     *
     * <p>All extracted types are returned as Fully Qualified Class Names (FQCN).
     *
     * @param classBytes the bytecode of the class to analyze
     * @return a set of FQCN strings representing all types that this class references through fields,
     *         including types from generic signatures; empty set if no fields reference non-primitive types
     * @throws IllegalArgumentException if classBytes is null
     */
    public Set<String> extract(byte[] classBytes) {
        if (classBytes == null) {
            throw new IllegalArgumentException("classBytes must not be null");
        }

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(classBytes);
        classReader.accept(classNode, 0);

        Set<String> result = new HashSet<>();

        // Extract field types
        if (classNode.fields != null) {
            for (FieldNode field : classNode.fields) {
                // Extract type from descriptor
                FqcnNormalizer.fromDescriptor(field.desc).ifPresent(result::add);

                // Extract generic types from signature
                if (field.signature != null) {
                    Set<String> genericTypes = GenericTypeExtractor.extractTypeNames(field.signature);
                    result.addAll(genericTypes);
                }
            }
        }

        return result;
    }

    /**
     * Analyzes class bytecode and extracts all field-related types with link details.
     *
     * <p>This method extracts:
     * <ul>
     *   <li>Field types from descriptors (from {@code FieldNode.desc})</li>
     *   <li>Generic type parameters from field signatures (from {@code FieldNode.signature})</li>
     * </ul>
     *
     * <p>All extracted types are returned as Fully Qualified Class Names (FQCN) mapped to
     * {@link LinkDetails} with type {@link LinkType#FIELD} and field name in details.
     *
     * @param classBytes the bytecode of the class to analyze
     * @return a map where key is FQCN of dependent class and value is a set of LinkDetails
     *         with type FIELD and field name in details; empty map if no fields reference non-primitive types
     * @throws IllegalArgumentException if classBytes is null
     */
    public Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes) {
        // TODO: implement extraction logic
        return new HashMap<>();
    }
}