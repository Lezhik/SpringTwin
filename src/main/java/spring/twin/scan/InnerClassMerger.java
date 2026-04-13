package spring.twin.scan;

import java.util.HashMap;
import java.util.HashSet;
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

    /**
     * Merges inner classes with their outer classes in the detailed dependency graph.
     * For each key that is an inner class: removes it from keys, adds its
     * dependencies to the outer class dependencies. If outer class is not
     * in the graph, creates a new entry with inner class dependencies.
     * Also replaces inner classes in dependency values with their outer classes
     * and removes self-references.
     *
     * @param <T> the type of the details set (LinkDetails)
     * @param graph the detailed dependency graph to process
     * @return a new graph with inner classes merged
     */
    public static <T> Map<String, Map<String, Set<T>>> mergeInnerClassesDetails(Map<String, Map<String, Set<T>>> graph) {
        TreeMap<String, Map<String, Set<T>>> result = new TreeMap<>();
        
        // Step 1: Process keys - merge inner classes to outer classes
        for (Map.Entry<String, Map<String, Set<T>>> entry : graph.entrySet()) {
            String key = entry.getKey();
            Map<String, Set<T>> dependencies = entry.getValue();
            
            String targetKey;
            if (isInnerClass(key)) {
                targetKey = getOuterClassName(key);
            } else {
                targetKey = key;
            }
            
            Map<String, Set<T>> targetMap = result.computeIfAbsent(targetKey, k -> new TreeMap<>());
            if (dependencies != null) {
                for (Map.Entry<String, Set<T>> depEntry : dependencies.entrySet()) {
                    String depKey = depEntry.getKey();
                    Set<T> depDetails = depEntry.getValue();
                    targetMap.computeIfAbsent(depKey, k -> new HashSet<>()).addAll(depDetails);
                }
            }
        }
        
        // Step 2: Replace inner classes in values with outer classes, remove self-references
        for (Map.Entry<String, Map<String, Set<T>>> entry : result.entrySet()) {
            String key = entry.getKey();
            Map<String, Set<T>> dependencies = entry.getValue();
            Map<String, Set<T>> newDependencies = new TreeMap<>();
            
            for (Map.Entry<String, Set<T>> depEntry : dependencies.entrySet()) {
                String dep = depEntry.getKey();
                Set<T> details = depEntry.getValue();
                
                // Map inner classes to their outer classes in dependencies
                String mappedDep;
                if (isInnerClass(dep)) {
                    mappedDep = getOuterClassName(dep);
                } else {
                    mappedDep = dep;
                }
                
                // Remove self-references
                if (!mappedDep.equals(key)) {
                    newDependencies.computeIfAbsent(mappedDep, k -> new HashSet<>()).addAll(details);
                }
            }
            
            entry.setValue(newDependencies);
        }
        
        // Return LinkedHashMap for deterministic order
        return new LinkedHashMap<>(result);
    }
    
    /**
     * Conditionally merges inner classes with their outer classes for detailed dependency graph.
     * If merge == true, merges inner classes (Outer$Inner) with their outer classes.
     * If merge == false, returns the original graph unchanged.
     *
     * <p>This method works with detailed dependency graphs containing LinkDetails sets:
     * {@code Map<String, Map<String, Set<LinkDetails>>>}.
     *
     * <p>Merging rules:
     * <ul>
     *   <li>Inner class keys are removed from the outer map</li>
     *   <li>Inner class dependencies are added to the parent class dependencies</li>
     *   <li>References to inner classes in values are replaced with parent class references</li>
     *   <li>Self-references (parent -> parent) are removed</li>
     *   <li>LinkDetails are preserved unchanged during merge</li>
     * </ul>
     *
     * @param <T> the type of the details set (LinkDetails)
     * @param graph the detailed dependency graph to process, containing LinkDetails sets
     * @param merge whether to perform the merge; if false, returns the original graph unchanged
     * @return the processed graph with inner classes merged, or the original graph if merge is false
     */
    public static <T> Map<String, Map<String, Set<T>>> mergeInnerClassesDetails(Map<String, Map<String, Set<T>>> graph, boolean merge) {
        if (merge) {
            return mergeInnerClassesDetails(graph);
        } else {
            return graph;
        }
    }
}