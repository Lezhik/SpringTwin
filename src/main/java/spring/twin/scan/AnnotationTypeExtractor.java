package spring.twin.scan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
     * Analyzes class bytecode and extracts all annotation types with detailed link information.
     *
     * <p>This method extracts the same annotation types as {@link #extract(byte[])}, but provides
     * additional context about where each annotation is used:
     * <ul>
     *   <li>Class annotations → {@link LinkType#CLASS_ANNOTATION} with empty details</li>
     *   <li>Field annotations → {@link LinkType#FIELD_ANNOTATION} with field name</li>
     *   <li>Method annotations → {@link LinkType#METHOD_ANNOTATION} with method signature</li>
     *   <li>Parameter annotations → {@link LinkType#METHOD_ARG_ANNOTATION} with method signature</li>
     * </ul>
     *
     * <p>The method signature format follows ASM conventions: {@code ownerClassName.methodName(descriptor)}.
     *
     * @param classBytes the bytecode of the class to analyze
     * @return a map where key is FQCN of the annotation class and value is a set of LinkDetails
     *         describing the annotation usage context; empty map if no annotations are present
     * @throws IllegalArgumentException if classBytes is null
     */
    public Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes) {
        if (classBytes == null) {
            throw new IllegalArgumentException("classBytes must not be null");
        }

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(classBytes);
        classReader.accept(classNode, 0);

        Map<String, Set<LinkDetails>> result = new HashMap<>();
        String className = FqcnNormalizer.internalToFqcn(classNode.name);

        // Extract class-level annotations
        extractAnnotationDetails(classNode.visibleAnnotations, LinkType.CLASS_ANNOTATION, "", result);
        extractAnnotationDetails(classNode.invisibleAnnotations, LinkType.CLASS_ANNOTATION, "", result);

        // Extract field annotations
        if (classNode.fields != null) {
            for (FieldNode field : classNode.fields) {
                extractAnnotationDetails(field.visibleAnnotations, LinkType.FIELD_ANNOTATION, field.name, result);
                extractAnnotationDetails(field.invisibleAnnotations, LinkType.FIELD_ANNOTATION, field.name, result);
            }
        }

        // Extract method annotations and parameter annotations
        if (classNode.methods != null) {
            for (MethodNode method : classNode.methods) {
                String methodSignature = extractMethodSignature(method.name, method.desc);
                extractAnnotationDetails(method.visibleAnnotations, LinkType.METHOD_ANNOTATION, methodSignature, result);
                extractAnnotationDetails(method.invisibleAnnotations, LinkType.METHOD_ANNOTATION, methodSignature, result);

                // Extract parameter annotations
                extractParameterAnnotationDetails(method.visibleParameterAnnotations, methodSignature, result);
                extractParameterAnnotationDetails(method.invisibleParameterAnnotations, methodSignature, result);
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

    /**
     * Adds LinkDetails to the Set for the specified FQCN in the result map.
     *
     * <p>If the FQCN is not yet present in the map, a new Set is created.
     * The LinkDetails is then added to the Set associated with the FQCN.
     *
     * @param map    the result map to add to
     * @param fqcn   the fully qualified class name of the annotation
     * @param detail the LinkDetails to add
     */
    private void addDetail(Map<String, Set<LinkDetails>> map, String fqcn, LinkDetails detail) {
        map.computeIfAbsent(fqcn, k -> new HashSet<>()).add(detail);
    }

    /**
     * Extracts FQCN from a list of annotation nodes and adds them with the specified link type and details.
     *
     * <p>For each annotation in the list, extracts the FQCN using {@link FqcnNormalizer#fromDescriptor(String)}
     * and adds a LinkDetails entry to the result map.
     *
     * @param annotations the list of annotation nodes
     * @param linkType    the type of link to create
     * @param details     the details string for the link
     * @param result      the result map to add entries to
     */
    private void extractAnnotationDetails(List<AnnotationNode> annotations, LinkType linkType, String details, Map<String, Set<LinkDetails>> result) {
        if (annotations == null) {
            return;
        }
        for (AnnotationNode annotation : annotations) {
            FqcnNormalizer.fromDescriptor(annotation.desc).ifPresent(fqcn ->
                addDetail(result, fqcn, LinkDetails.of(linkType, details))
            );
        }
    }

    /**
     * Extracts FQCN from parameter annotations and adds them with METHOD_ARG_ANNOTATION link type.
     *
     * <p>Iterates through the array of annotation lists (one per parameter) and adds
     * each annotation with the specified method signature as details.
     *
     * @param parameterAnnotations the array of annotation lists per parameter
     * @param methodSignature      the method signature to use as details
     * @param result               the result map to add entries to
     */
    private void extractParameterAnnotationDetails(List<AnnotationNode>[] parameterAnnotations, String methodSignature, Map<String, Set<LinkDetails>> result) {
        if (parameterAnnotations == null) {
            return;
        }
        for (List<AnnotationNode> annotations : parameterAnnotations) {
            extractAnnotationDetails(annotations, LinkType.METHOD_ARG_ANNOTATION, methodSignature, result);
        }
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