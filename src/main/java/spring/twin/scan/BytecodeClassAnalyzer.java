package spring.twin.scan;

import java.util.HashSet;
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
     * @return a set of FQCN strings representing all dependencies of the class;
     *         empty set if classBytes is null
     */
    public Set<String> extractDependencies(byte[] classBytes) {
        return new HashSet<>();
    }

    /**
     * Extracts the Fully Qualified Class Name (FQCN) of the analyzed class from its bytecode.
     *
     * <p>Uses ASM's {@link ClassReader#getClassName()} to obtain the internal class name,
     * then converts it to FQCN using {@link FqcnNormalizer#fromInternalName(String)}.
     *
     * @param classBytes the bytecode of the class to analyze
     * @return the FQCN of the class, or null if classBytes is null or class name cannot be determined
     */
    public String extractClassName(byte[] classBytes) {
        return null;
    }
}