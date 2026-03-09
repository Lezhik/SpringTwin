package spring.twin.analysis;

import lombok.extern.slf4j.Slf4j;
import spring.twin.bytecode.BytecodeReaderService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Service for building inheritance trees from class files.
 * <p>
 * This service analyzes bytecode to extract inheritance relationships
 * including both class extension (extends) and interface implementation (implements).
 * <p>
 * The service processes .class files and builds a map of parent classes/interfaces
 * to their direct descendants.
 *
 * @see spring.twin.scanner.ClassScanningService
 */
@Slf4j
public class InheritanceTreeService {

    private final BytecodeReaderService bytecodeReader;

    /**
     * Creates a new InheritanceTreeService.
     */
    public InheritanceTreeService() {
        this.bytecodeReader = new BytecodeReaderService();
    }

    /**
     * Builds an inheritance tree from the given class files.
     * <p>
     * For each class, the result contains a set of all its direct descendants
     * (both classes that extend it and classes that implement it).
     *
     * @param classFiles map of fully qualified class names to their file paths
     * @return map where key is parent class/interface name and value is set of direct descendants
     */
    public Map<String, Set<String>> buildTree(Map<String, Path> classFiles) {
        log.info("Building inheritance tree for {} classes", classFiles.size());

        Map<String, Set<String>> inheritanceTree = new HashMap<>();

        for (Map.Entry<String, Path> entry : classFiles.entrySet()) {
            // Normalize class name (convert $ to . for inner classes)
            String className = normalizeClassName(entry.getKey());
            Path classPath = entry.getValue();

            try {
                BytecodeReaderService.InheritanceInfo info = extractInheritanceInfo(classPath);
                if (info != null) {
                    // Add this class as a child of its superclass (if not Object)
                    if (info.superClassName() != null && !"java.lang.Object".equals(info.superClassName())) {
                        inheritanceTree
                            .computeIfAbsent(info.superClassName(), k -> new HashSet<>())
                            .add(className);
                    }

                    // Add this class as a child of each implemented interface
                    for (String interfaceName : info.interfaceNames()) {
                        inheritanceTree
                            .computeIfAbsent(interfaceName, k -> new HashSet<>())
                            .add(className);
                    }
                }
            } catch (IOException e) {
                log.warn("Failed to read class file: {}", classPath, e);
            }
        }

        log.info("Inheritance tree built with {} parents", inheritanceTree.size());
        return Collections.unmodifiableMap(inheritanceTree);
    }

    /**
     * Finds all descendants of a given class recursively.
     * <p>
     * This method traverses the inheritance tree to find all descendants,
     * not just direct children. The result includes both direct and indirect
     * descendants at all levels of the hierarchy.
     *
     * @param className the fully qualified class name to find descendants for
     * @param inheritanceTree the inheritance tree built by {@link #buildTree(Map)}
     * @return set of all descendant class names (direct and indirect), empty set if none found
     */
    public Set<String> findAllDescendants(String className, Map<String, Set<String>> inheritanceTree) {
        Set<String> allDescendants = new HashSet<>();
        findDescendantsRecursive(className, inheritanceTree, allDescendants);
        return Collections.unmodifiableSet(allDescendants);
    }

    /**
     * Extracts inheritance information from a class file.
     *
     * @param classPath path to the .class file
     * @return inheritance info containing superclass and interfaces, or null if parsing fails
     * @throws IOException if an I/O error occurs
     */
    private BytecodeReaderService.InheritanceInfo extractInheritanceInfo(Path classPath) throws IOException {
        byte[] classBytes = Files.readAllBytes(classPath);
        return parseClassBytes(classBytes);
    }

    /**
     * Parses class file bytes to extract superclass and interface names.
     *
     * @param classBytes the class file bytes
     * @return inheritance info containing superclass and interfaces, or null if parsing fails
     */
    BytecodeReaderService.InheritanceInfo parseClassBytes(byte[] classBytes) {
        var poolData = bytecodeReader.readConstantPool(classBytes);
        return bytecodeReader.extractInheritanceInfo(poolData);
    }

    /**
     * Recursively collects all descendants of a class.
     *
     * @param className the class to find descendants for
     * @param inheritanceTree the inheritance tree
     * @param collected the set to collect descendants into
     */
    private void findDescendantsRecursive(String className, Map<String, Set<String>> inheritanceTree,
                                          Set<String> collected) {
        Set<String> directChildren = inheritanceTree.get(className);
        if (directChildren == null || directChildren.isEmpty()) {
            return;
        }

        for (String child : directChildren) {
            // Avoid infinite loops in case of circular references (shouldn't happen in Java)
            if (collected.add(child)) {
                // Recursively find descendants of this child
                findDescendantsRecursive(child, inheritanceTree, collected);
            }
        }
    }

    /**
     * Normalizes class name from map keys.
     * <p>
     * Converts binary names (with $ for inner classes) to canonical names (with dots).
     * This ensures consistency between input keys and bytecode-extracted names.
     *
     * @param className class name, possibly with $ for inner classes
     * @return normalized class name with dots
     */
    private String normalizeClassName(String className) {
        return className.replace('$', '.');
    }
}