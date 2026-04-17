package spring.twin.cluster;

import java.util.Map;
import java.util.Set;

/**
 * Utility class for calculating modularity metrics for graph partitioning.
 *
 * <p>Modularity is a metric that measures the quality of a division of a network into communities.
 * This class implements CPM (Constant Potts Model) modularity with a configurable resolution parameter.
 *
 * <p>CPM modularity formula: Q = Σ_ij [A_ij - γ * k_i * k_j / (2m)] * δ(c_i, c_j)
 * where:
 * <ul>
 *   <li>A_ij - weight of edge between nodes i and j</li>
 *   <li>k_i - degree of node i (sum of weights of all edges connected to i)</li>
 *   <li>m - total sum of all edge weights</li>
 *   <li>γ (gamma) - resolution parameter</li>
 *   <li>δ(c_i, c_j) - indicator function (1 if nodes i and j are in the same community, 0 otherwise)</li>
 * </ul>
 */
public final class ModularityCalculator {
    
    private ModularityCalculator() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Calculates the degree of a node (sum of weights of all edges connected to the node).
     * If node is not present in graph, returns 0.
     * Sums outgoing edges from the node (directed graph semantics).
     *
     * @param node the node name
     * @param graph the weighted graph as {@code Map<String, Map<String, Double>>}
     * @return the degree of the node (sum of weights of all edges connected to the node)
     */
    public static double nodeDegree(String node, Map<String, Map<String, Double>> graph) {
        Map<String, Double> edges = graph.get(node);
        if (edges == null) {
            return 0.0;
        }
        double degree = 0.0;
        for (double weight : edges.values()) {
            degree += weight;
        }
        return degree;
    }
    
    /**
     * Calculates the sum of degrees of all nodes in a community.
     *
     * @param community the community number
     * @param graph the undirected weighted graph as {@code Map<String, Map<String, Double>>}
     * @param partition the current partition of nodes into communities
     * @return the sum of degrees of all nodes in the community
     */
    public static double communityDegree(int community,
                                          Map<String, Map<String, Double>> graph,
                                          Partition partition) {
        double sum = 0.0;
        for (String node : partition.nodesInCommunity(community)) {
            sum += nodeDegree(node, graph);
        }
        return sum;
    }
    
    /**
     * Calculates the sum of edge weights inside a community.
     * Each undirected edge is counted exactly once.
     *
     * @param community the community number
     * @param graph the weighted graph as {@code Map<String, Map<String, Double>>}
     * @param partition the current partition of nodes into communities
     * @return the sum of edge weights inside the community
     */
    public static double edgesInsideCommunity(int community,
                                               Map<String, Map<String, Double>> graph,
                                               Partition partition) {
        double sum = 0.0;
        Set<String> communityNodes = partition.nodesInCommunity(community);
        for (String node : communityNodes) {
            Map<String, Double> edges = graph.get(node);
            if (edges != null) {
                for (Map.Entry<String, Double> edge : edges.entrySet()) {
                    String neighbor = edge.getKey();
                    double weight = edge.getValue();
                    // Count each edge only once by checking node < neighbor (lexicographically)
                    if (communityNodes.contains(neighbor) && node.compareTo(neighbor) < 0) {
                        sum += weight;
                    }
                }
            }
        }
        return sum;
    }
    
    /**
     * Calculates the sum of edge weights from a node to nodes in a specific community.
     * Counts outgoing edges from node to community members (directed graph semantics).
     *
     * @param node the source node
     * @param community the target community
     * @param graph the weighted graph as {@code Map<String, Map<String, Double>>}
     * @param partition the current partition of nodes into communities
     * @return the sum of edge weights from the node to the community
     */
    public static double edgesToCommunity(String node,
                                           int community,
                                           Map<String, Map<String, Double>> graph,
                                           Partition partition) {
        double sum = 0.0;
        Map<String, Double> edges = graph.get(node);
        if (edges == null) {
            return 0.0;
        }
        for (Map.Entry<String, Double> edge : edges.entrySet()) {
            String neighbor = edge.getKey();
            double weight = edge.getValue();
            if (partition.communityOf(neighbor) == community) {
                sum += weight;
            }
        }
        return sum;
    }
    
    /**
     * Calculates the total weight of all edges in the graph.
     * Each directed edge is counted once.
     *
     * @param graph the weighted graph as {@code Map<String, Map<String, Double>>}
     * @return the total weight of all edges
     */
    private static double totalEdgeWeight(Map<String, Map<String, Double>> graph) {
        double total = 0.0;
        for (Map.Entry<String, Map<String, Double>> entry : graph.entrySet()) {
            Map<String, Double> edges = entry.getValue();
            for (double weight : edges.values()) {
                total += weight;
            }
        }
        return total;
    }
    
    /**
     * Calculates CPM modularity for the entire graph and partition.
     *
     * <p>Formula: Q = (1/2m) * Σ_ij [A_ij - γ * k_i * k_j / (2m)] * δ(c_i, c_j)
     * where A_ij is edge weight, k_i is node degree, m is total edge weight,
     * γ is resolution, and δ is the community indicator function.
     *
     * @param graph the weighted graph as {@code Map<String, Map<String, Double>>}
     * @param partition the current partition of nodes into communities
     * @param resolution the resolution parameter γ (gamma), controls community size
     * @return the modularity value
     */
    public static double calculateModularity(Map<String, Map<String, Double>> graph,
                                              Partition partition,
                                              double resolution) {
        if (graph.isEmpty()) {
            return 0.0;
        }
        
        double m = totalEdgeWeight(graph);
        if (m == 0.0) {
            return 0.0;
        }
        
        double modularity = 0.0;
        double twoM = 2.0 * m;
        
        // Iterate over all pairs of nodes in the same community
        for (int community : partition.communities()) {
            Set<String> communityNodes = partition.nodesInCommunity(community);
            for (String nodeI : communityNodes) {
                double ki = nodeDegree(nodeI, graph);
                for (String nodeJ : communityNodes) {
                    double kj = nodeDegree(nodeJ, graph);
                    
                    // Get edge weight A_ij
                    double aij = 0.0;
                    Map<String, Double> edgesI = graph.get(nodeI);
                    if (edgesI != null && edgesI.containsKey(nodeJ)) {
                        aij = edgesI.get(nodeJ);
                    }
                    
                    // CPM modularity term: [A_ij - γ * k_i * k_j / (2m)]
                    modularity += aij - resolution * ki * kj / twoM;
                }
            }
        }
        
        // Divide by 2m
        return modularity / twoM;
    }
    
    /**
     * Calculates the change in modularity when moving a node to a target community.
     *
     * <p>Formula: ΔQ = [k_i,in / m] - [γ * Σ_tot * k_i / (2m^2)]
     * where:
     * <ul>
     *   <li>k_i,in = edgesToCommunity(node, targetCommunity)</li>
     *   <li>Σ_tot = communityDegree(targetCommunity)</li>
     *   <li>k_i = nodeDegree(node)</li>
     *   <li>m = totalEdgeWeight</li>
     *   <li>γ = resolution</li>
     * </ul>
     *
     * @param node the node to move
     * @param targetCommunity the community to move the node to
     * @param graph the weighted graph as {@code Map<String, Map<String, Double>>}
     * @param partition the current partition of nodes into communities
     * @param resolution the resolution parameter γ (gamma)
     * @return the change in modularity (delta Q)
     */
    public static double deltaModularity(String node,
                                          int targetCommunity,
                                          Map<String, Map<String, Double>> graph,
                                          Partition partition,
                                          double resolution) {
        double m = totalEdgeWeight(graph);
        if (Math.abs(m) < 0.001) {
            return 0.0;
        }
        
        int currentCommunity = partition.communityOf(node);
        
        // If moving to the same community, delta is 0
        if (currentCommunity == targetCommunity) {
            return 0.0;
        }
        
        double ki = nodeDegree(node, graph);
        double kiIn = edgesToCommunity(node, targetCommunity, graph, partition);
        double sigmaTot = communityDegree(targetCommunity, graph, partition);
        
        // Formula: ΔQ = [k_i,in / m] - [γ * Σ_tot * k_i / (2m^2)]
        return (kiIn / m) - (resolution * sigmaTot * ki / (2.0 * m * m));
    }
}