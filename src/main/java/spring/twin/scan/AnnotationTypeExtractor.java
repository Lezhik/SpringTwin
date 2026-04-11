package spring.twin.scan;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.springframework.stereotype.Component;

/**
 * Extracts annotation type dependencies from Java bytecode.
 *
 * <p>This class analyzes class bytecode to extract all annotation types that are referenced
 * by class declarations, fields, methods, and method parameters.
 *
 * <p>Handles class-level annotations (runtime visible and invisible), field annotations,
 * method annotations, and parameter annotations. Uses ASM's ClassNode for bytecode parsing.
 */
@Component
public class AnnotationTypeExtractor {

    /**
     * Analyzes class bytecode and extracts all annotation-related types.
     *
     * <p>This method extracts:
     * <ul>
     *   <li>Class annotations from {@code ClassNode.visibleAnnotations} and {@code ClassNode.invisibleAnnotations}</li>
     *   <li>Field annotations from field declarations</li>
     *   <li>Method annotations from method declarations</li>
     *   <li>Method parameter annotations from method parameter declarations</li>
     * </ul>
     *
     * <p>All extracted types are returned as Fully Qualified Class Names (FQCN).
     *
     * @param classBytes the bytecode of the class to analyze
     * @return a set of FQCN strings representing all annotation types referenced by this class;
     *         empty set if no annotations are present
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

        // Extract class-level annotations
        extractAnnotations(classNode.visibleAnnotations, result);
        extractAnnotations(classNode.invisibleAnnotations, result);

        // Extract field annotations
        if (classNode.fields != null) {
            for (FieldNode field : classNode.fields) {
                extractAnnotations(field.visibleAnnotations, result);
                extractAnnotations(field.invisibleAnnotations, result);
            }
        }

        // Extract method annotations and parameter annotations
        if (classNode.methods != null) {
            for (MethodNode method : classNode.methods) {
                extractAnnotations(method.visibleAnnotations, result);
                extractAnnotations(method.invisibleAnnotations, result);

                // Extract parameter annotations
                extractParameterAnnotations(method.visibleParameterAnnotations, result);
                extractParameterAnnotations(method.invisibleParameterAnnotations, result);
            }
        }

        return result;
    }

    /**
     * Extracts FQCN from a list of annotation nodes and adds them to the result set.
     *
     * @param annotations the list of annotation nodes
     * @param result the set to collect FQCN strings into
     */
    private void extractAnnotations(List<AnnotationNode> annotations, Set<String> result) {
        if (annotations == null) {
            return;
        }
        for (AnnotationNode annotation : annotations) {
            FqcnNormalizer.fromDescriptor(annotation.desc).ifPresent(result::add);
        }
    }

    /**
     * Extracts FQCN from parameter annotations and adds them to the result set.
     *
     * @param parameterAnnotations the array of annotation lists per parameter
     * @param result the set to collect FQCN strings into
     */
    private void extractParameterAnnotations(List<AnnotationNode>[] parameterAnnotations, Set<String> result) {
        if (parameterAnnotations == null) {
            return;
        }
        for (List<AnnotationNode> annotations : parameterAnnotations) {
            extractAnnotations(annotations, result);
        }
    }
}