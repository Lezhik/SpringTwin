package spring.twin.scan;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Utility class for merging inner classes with their parent classes.
 * When the --merge-inner-classes option is enabled (default), entries like
 * com.example.Outer$Inner are merged with com.example.Outer: inner class
 * dependencies are added to outer class dependencies, and the inner class
 * itself is removed from the graph keys.
 */
public final class InnerClassMerger {

    private InnerClassMerger() {
        // Utility class, prevent instantiation
    }

    /**
     * Determines if the given FQCN is an inner class.
     * Returns true if FQCN contains '$' that is not the first character.
     *
     * @param fqcn the fully qualified class name
     * @return true if the FQCN represents an inner class
     */
    public static boolean isInnerClass(String fqcn) {
        if (fqcn == null || fqcn.isEmpty()) {
            return false;
        }
        int dollarIndex = fqcn.indexOf('$');
        return dollarIndex > 0;
    }

    /**
     * Extracts the outer class name from an inner class FQCN.
     * For com.example.Outer$Inner returns com.example.Outer.
     * For com.example.Outer$Inner$Deep returns com.example.Outer.
     * If FQCN is not an inner class, returns the original FQCN unchanged.
     *
     * @param fqcn the fully qualified class name
     * @return the outer class name, or the original FQCN if not an inner class
     */
    public static String getOuterClassName(String fqcn) {
        if (fqcn == null) {
            return null;
        }
        if (!isInnerClass(fqcn)) {
            return fqcn;
        }
        int dollarIndex = fqcn.indexOf('$');
        return fqcn.substring(0, dollarIndex);
    }

    /**
     * Merges inner classes with their outer classes in the dependency graph.
     * For each key that is an inner class: removes it from keys, adds its
     * dependencies to the outer class dependencies. If outer class is not
     * in the graph, creates a new entry with inner class dependencies.
     * Returns a new LinkedHashMap with sorted keys and values.
     *
     * @param graph the dependency graph to process
     * @return a new graph with inner classes merged
     */
    public static Map<String, Set<String>> mergeInnerClasses(Map<String, Set<String>> graph) {
        TreeMap<String, TreeSet<String>> result = new TreeMap<>();
        
        // Step 1 & 2: Process keys - merge inner classes to outer classes
        for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
            String key = entry.getKey();
            Set<String> dependencies = entry.getValue();
            
            String targetKey;
            if (isInnerClass(key)) {
                targetKey = getOuterClassName(key);
            } else {
                targetKey = key;
            }
            
            TreeSet<String> targetSet = result.computeIfAbsent(targetKey, k -> new TreeSet<>());
            if (dependencies != null) {
                targetSet.addAll(dependencies);
            }
        }
        
        // Step 3: Replace inner classes in values with outer classes, remove self-references
        for (Map.Entry<String, TreeSet<String>> entry : result.entrySet()) {
            String key = entry.getKey();
            TreeSet<String> dependencies = entry.getValue();
            TreeSet<String> newDependencies = new TreeSet<>();
            
            for (String dep : dependencies) {
                String mappedDep;
                if (isInnerClass(dep)) {
                    mappedDep = getOuterClassName(dep);
                } else {
                    mappedDep = dep;
                }
                // Remove self-references
                if (!mappedDep.equals(key)) {
                    newDependencies.add(mappedDep);
                }
            }
            
            entry.setValue(newDependencies);
        }
        
        // Step 4: Return LinkedHashMap for deterministic order
        return new LinkedHashMap<>(result);
    }

    /**
     * Conditionally merges inner classes with their outer classes.
     * If merge == true, delegates to mergeInnerClasses(graph).
     * If merge == false, returns the original graph unchanged.
     *
     * @param graph the dependency graph to process
     * @param merge whether to perform the merge
     * @return the processed graph or the original graph
     */
    public static Map<String, Set<String>> mergeInnerClasses(Map<String, Set<String>> graph, boolean merge) {
        if (merge) {
            return mergeInnerClasses(graph);
        } else {
            return graph;
        }
    }
}