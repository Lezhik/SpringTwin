package spring.twin.scan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.objectweb.asm.ClassReader;
import org.springframework.stereotype.Component;

/**
 * Orchestrates all dependency extractors for analyzing a single .class file.
 *
 * <p>This class combines results from inheritance, field types, method types,
 * annotations, and code usage extractors to produce a unified set of dependencies
 * for a given class.
 *
 * <p>Acts as a facade over individual extractors to simplify bytecode analysis.
 */
@Component
@RequiredArgsConstructor
public class BytecodeClassAnalyzer {

    private final InheritanceExtractor inheritanceExtractor;
    private final FieldTypeExtractor fieldTypeExtractor;
    private final MethodTypeExtractor methodTypeExtractor;
    private final AnnotationTypeExtractor annotationTypeExtractor;
    private final CodeUsageExtractor codeUsageExtractor;

    /**
     * Analyzes class bytecode and extracts all dependencies by combining results from all extractors.
     *
     * <p>This method delegates to:
     * <ul>
     *   <li>{@link InheritanceExtractor#extract(byte[])} for superclass and interface dependencies</li>
     *   <li>{@link FieldTypeExtractor#extract(byte[])} for field type dependencies</li>
     *   <li>{@link MethodTypeExtractor#extract(byte[])} for method signature dependencies</li>
     *   <li>{@link AnnotationTypeExtractor#extract(byte[])} for annotation type dependencies</li>
     *   <li>{@link CodeUsageExtractor#extract(byte[])} for code body usage dependencies</li>
     * </ul>
     *
     * <p>All extracted types are returned as Fully Qualified Class Names (FQCN).
     *
     * @param classBytes the bytecode of the class to analyze
     * @return a set of FQCN strings representing all dependencies of the class
     * @throws IllegalArgumentException if classBytes is null
     */
    public Set<String> extractDependencies(byte[] classBytes) {
        Set<String> dependencies = new HashSet<>();
        dependencies.addAll(inheritanceExtractor.extract(classBytes));
        dependencies.addAll(fieldTypeExtractor.extract(classBytes));
        dependencies.addAll(methodTypeExtractor.extract(classBytes));
        dependencies.addAll(annotationTypeExtractor.extract(classBytes));
        dependencies.addAll(codeUsageExtractor.extract(classBytes));
        return Set.copyOf(dependencies);
    }

    /**
     * Extracts the Fully Qualified Class Name (FQCN) of the analyzed class from its bytecode.
     *
     * <p>Uses ASM's {@link ClassReader#getClassName()} to obtain the internal class name,
     * then converts it to FQCN using {@link FqcnNormalizer#fromInternalName(String)}.
     *
     * @param classBytes the bytecode of the class to analyze
     * @return the FQCN of the class
     * @throws IllegalArgumentException if classBytes is null or class name cannot be extracted
     */
    public String extractClassName(byte[] classBytes) {
        if (classBytes == null) {
            throw new IllegalArgumentException("classBytes must not be null");
        }
        ClassReader classReader = new ClassReader(classBytes);
        String internalName = classReader.getClassName();
        return FqcnNormalizer.fromInternalName(internalName)
                .orElseThrow(() -> new IllegalArgumentException("Cannot extract class name from bytecode"));
    }

    /**
     * Analyzes class bytecode and extracts all dependencies with detailed link information.
     *
     * <p>This method combines results from all extractors to produce a detailed dependency map:
     * <ul>
     *   <li>{@link InheritanceExtractor#extractDetails(byte[])} for superclass and interface dependencies
     *       → {@link LinkType#SUPERCLASS}, {@link LinkType#INTERFACE}</li>
     *   <li>{@link FieldTypeExtractor#extractDetails(byte[])} for field type dependencies
     *       → {@link LinkType#FIELD}</li>
     *   <li>{@link MethodTypeExtractor#extractDetails(byte[])} for method signature dependencies
     *       → {@link LinkType#METHOD}</li>
     *   <li>{@link AnnotationTypeExtractor#extractDetails(byte[])} for annotation type dependencies
     *       → {@link LinkType#CLASS_ANNOTATION}, {@link LinkType#FIELD_ANNOTATION},
     *       {@link LinkType#METHOD_ANNOTATION}, {@link LinkType#METHOD_ARG_ANNOTATION}</li>
     *   <li>{@link CodeUsageExtractor#extractDetails(byte[])} for code body usage dependencies
     *       → {@link LinkType#STATIC_BLOCK}, {@link LinkType#METHOD}</li>
     * </ul>
     *
     * <p>The returned structure is {@code Map<targetFqcn, Map<sourceFqcn, Set<LinkDetails>>>} where:
     * <ul>
     *   <li>{@code targetFqcn} - the FQCN of the analyzed class (extracted from bytecode)</li>
     *   <li>{@code sourceFqcn} - the FQCN of a class that the analyzed class depends on</li>
     *   <li>{@code Set<LinkDetails>} - set of details describing each link between target and source</li>
     * </ul>
     *
     * @param classBytes the bytecode of the class to analyze
     * @return a two-level map where the outer key is the target class FQCN,
     *         the inner key is the dependency class FQCN,
     *         and the value is a set of LinkDetails describing the relationship
     * @throws IllegalArgumentException if classBytes is null
     */
    public Map<String, Map<String, Set<LinkDetails>>> extractDependenciesDetails(byte[] classBytes) {
        String targetFqcn = extractClassName(classBytes);

        Map<String, Set<LinkDetails>> combinedDetails = new HashMap<>();

        // Merge results from all extractors
        mergeDetails(combinedDetails, inheritanceExtractor.extractDetails(classBytes));
        mergeDetails(combinedDetails, fieldTypeExtractor.extractDetails(classBytes));
        mergeDetails(combinedDetails, methodTypeExtractor.extractDetails(classBytes));
        mergeDetails(combinedDetails, annotationTypeExtractor.extractDetails(classBytes));
        mergeDetails(combinedDetails, codeUsageExtractor.extractDetails(classBytes));

        Map<String, Map<String, Set<LinkDetails>>> result = new HashMap<>();
        result.put(targetFqcn, combinedDetails);
        return result;
    }

    /**
     * Merges source details into the target map.
     *
     * <p>For each entry in the source map, adds all LinkDetails to the corresponding
     * entry in the target map. Creates new sets as needed.
     *
     * @param target the map to merge into
     * @param source the map to merge from
     */
    private void mergeDetails(Map<String, Set<LinkDetails>> target, Map<String, Set<LinkDetails>> source) {
        for (Map.Entry<String, Set<LinkDetails>> entry : source.entrySet()) {
            String fqcn = entry.getKey();
            Set<LinkDetails> details = entry.getValue();
            target.computeIfAbsent(fqcn, k -> new HashSet<>()).addAll(details);
        }
    }
}