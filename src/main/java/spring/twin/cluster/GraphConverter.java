package spring.twin.cluster;

import org.springframework.stereotype.Component;
import spring.twin.scan.LinkDetails;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Component for converting dependency graphs to undirected weighted graphs.
 *
 * <p>Converts a directed dependency graph (from dependencies.json) into an undirected
 * weighted graph suitable for the Leiden clustering algorithm. The conversion process
 * merges bidirectional edges and assigns weights based on the number of unique link types.
 */
@Component
public class GraphConverter {

    /**
     * Default constructor.
     */
    public GraphConverter() {
    }

    /**
     * Converts a directed dependency graph to an undirected weighted graph.
     *
     * <p>The resulting graph is represented as {@code Map<String, Map<String, Double>>}
     * where each key is a node and its value is a map of neighbors with edge weights.
     *
     * <p>For each pair of classes A and B:
     * <ul>
     *   <li>If there is a dependency A→B and/or B→A, a single undirected edge is created</li>
     *   <li>The edge weight equals the number of unique link types between the classes</li>
     * </ul>
     *
     * <p>For example, if class A references class B through both a FIELD and a METHOD,
     * the edge weight between A and B would be 2.0.
     *
     * @param dependencyGraph the directed dependency graph as {@code Map<String, Map<String, Set<LinkDetails>>>}
     * @return the undirected weighted graph as {@code Map<String, Map<String, Double>>}
     */
    public Map<String, Map<String, Double>> toUndirectedWeightedGraph(Map<String, Map<String, Set<LinkDetails>>> dependencyGraph) {
        Map<String, Map<String, Double>> result = new HashMap<>();
        
        // First pass: collect all unique nodes and initialize their neighbor maps
        Set<String> allNodes = collectNodes(dependencyGraph);
        for (String node : allNodes) {
            result.put(node, new HashMap<>());
        }
        
        // Second pass: aggregate weights for each edge
        for (Map.Entry<String, Map<String, Set<LinkDetails>>> sourceEntry : dependencyGraph.entrySet()) {
            String source = sourceEntry.getKey();
            Map<String, Set<LinkDetails>> targets = sourceEntry.getValue();
            
            for (Map.Entry<String, Set<LinkDetails>> targetEntry : targets.entrySet()) {
                String target = targetEntry.getKey();
                
                // Skip self-loops
                if (source.equals(target)) {
                    continue;
                }
                
                double weight = targetEntry.getValue().size();
                
                // Update weight in both directions (undirected graph)
                // Use getOrDefault to accumulate weights if edge already exists
                double currentWeightSourceToTarget = result.get(source).getOrDefault(target, 0.0);
                double currentWeightTargetToSource = result.get(target).getOrDefault(source, 0.0);
                
                result.get(source).put(target, currentWeightSourceToTarget + weight);
                result.get(target).put(source, currentWeightTargetToSource + weight);
            }
        }
        
        return result;
    }

    /**
     * Collects all unique nodes from the dependency graph.
     *
     * <p>A node is considered present in the graph if it appears either as:
     * <ul>
     *   <li>A key (source class) in the outer map</li>
     *   <li>A key (target class) in any of the inner maps</li>
     * </ul>
     *
     * <p>This ensures that even classes that are only referenced but never reference
     * others are included in the node set.
     *
     * @param dependencyGraph the directed dependency graph
     * @return a set of all unique class names (FQCN) present in the graph
     */
    public Set<String> collectNodes(Map<String, Map<String, Set<LinkDetails>>> dependencyGraph) {
        Set<String> nodes = new HashSet<>();
        
        for (Map.Entry<String, Map<String, Set<LinkDetails>>> entry : dependencyGraph.entrySet()) {
            // Add the source node (key of outer map)
            nodes.add(entry.getKey());
            
            // Add all target nodes (keys of inner map)
            nodes.addAll(entry.getValue().keySet());
        }
        
        return nodes;
    }

    /**
     * Calculates the total weight of all edges in the graph.
     *
     * <p>Each undirected edge is counted exactly once in the total weight calculation.
     * For a graph represented as {@code Map<String, Map<String, Double>>}, this method
     * sums all edge weights, ensuring no double counting of edges.
     *
     * <p>For example, if the graph contains edges A→B with weight 2.0 and B→A with weight 2.0,
     * only one of them contributes to the total weight.
     *
     * @param graph the undirected weighted graph
     * @return the sum of all edge weights (each edge counted once)
     */
    public double totalEdgeWeight(Map<String, Map<String, Double>> graph) {
        double totalWeight = 0.0;
        
        for (Map.Entry<String, Map<String, Double>> nodeEntry : graph.entrySet()) {
            String nodeA = nodeEntry.getKey();
            Map<String, Double> neighbors = nodeEntry.getValue();
            
            for (Map.Entry<String, Double> neighborEntry : neighbors.entrySet()) {
                String nodeB = neighborEntry.getKey();
                double weight = neighborEntry.getValue();
                
                // Count each edge only once by comparing strings lexicographically
                // Only add weight when nodeA < nodeB to avoid double counting
                if (nodeA.compareTo(nodeB) < 0) {
                    totalWeight += weight;
                }
            }
        }
        
        return totalWeight;
    }
}