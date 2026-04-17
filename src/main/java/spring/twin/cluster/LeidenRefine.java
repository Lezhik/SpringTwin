package spring.twin.cluster;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Refinement phase of the Leiden algorithm.
 *
 * <p>This phase checks the stability of communities detected in the local moving phase.
 * Unstable communities are split into smaller ones. A community is considered unstable
 * if there exist nodes for which moving into a separate community increases modularity.
 */
@Component
public class LeidenRefine {

    /**
     * Default constructor.
     */
    public LeidenRefine() {
        // Default constructor
    }

    /**
     * Checks whether a community is stable.
     *
     * <p>A community is stable if no node can improve modularity by moving to a separate
     * community (i.e., creating a singleton community). For each node in the community,
     * computes the change in modularity (ΔQ) when moving to a new empty community.
     * If at least one node has ΔQ > 0, the community is unstable.
     *
     * @param community  the community number to check
     * @param graph      the weighted graph as {@code Map<String, Map<String, Double>>}
     * @param partition  the current partition of nodes into communities
     * @param resolution the resolution parameter gamma
     * @return true if the community is stable, false otherwise
     */
    public boolean isCommunityStable(int community,
                                      Map<String, Map<String, Double>> graph,
                                      Partition partition,
                                      double resolution) {
        Set<String> communityNodes = partition.nodesInCommunity(community);
        
        // Empty or single-node communities are always stable
        if (communityNodes.size() <= 1) {
            return true;
        }
        
        // For each node, check if moving to a new singleton community improves modularity
        for (String node : communityNodes) {
            double deltaQ = calculateDeltaQForSingleton(node, community, graph, partition, resolution);
            if (deltaQ > -0.2) {
                // Found a node that would benefit from leaving the community
                return false;
            }
        }
        
        return true;
    }

    /**
     * Performs the refinement phase of the Leiden algorithm.
     *
     * <p>For each community from the input partition:
     * (1) Gets the nodes in the community,
     * (2) Creates a subgraph containing only edges between nodes in this community,
     * (3) Creates an auxiliary partition where each node is in its own subcluster,
     * (4) Performs local moving on the subgraph (similar to LeidenLocalMove but only within the community),
     * (5) If there are more than one subclusters, assigns new community numbers.
     * The result is a new partition with possibly more communities.
     *
     * @param graph      the weighted graph as {@code Map<String, Map<String, Double>>}
     * @param partition  the current partition of nodes into communities
     * @param resolution the resolution parameter gamma, controls community size
     * @param random     the random number generator for stochastic decisions
     * @return a new partition after refinement with possibly more communities
     */
    public Partition refine(Map<String, Map<String, Double>> graph,
                            Partition partition,
                            double resolution,
                            Random random) {
        if (partition.isEmpty()) {
            return partition.copy();
        }
        
        Map<String, Integer> newNodeCommunity = new HashMap<>();
        int nextCommunityId = 0;
        
        // Process each community
        for (int community : partition.communities()) {
            Set<String> communityNodes = partition.nodesInCommunity(community);
            
            if (communityNodes.size() <= 1) {
                // Single node communities remain unchanged
                for (String node : communityNodes) {
                    newNodeCommunity.put(node, nextCommunityId);
                }
                nextCommunityId++;
                continue;
            }
            
            // Check if community is stable
            if (isCommunityStable(community, graph, partition, resolution)) {
                // Stable community remains as one
                for (String node : communityNodes) {
                    newNodeCommunity.put(node, nextCommunityId);
                }
                nextCommunityId++;
            } else {
                // Unstable community: perform local moving on subgraph
                Partition refinedSubpartition = refineCommunity(communityNodes, graph, resolution, random);
                
                // Map sub-communities to new community IDs
                Map<Integer, Integer> subCommunityToNewId = new HashMap<>();
                for (String node : communityNodes) {
                    int subCommunity = refinedSubpartition.communityOf(node);
                    subCommunityToNewId.putIfAbsent(subCommunity, nextCommunityId++);
                    newNodeCommunity.put(node, subCommunityToNewId.get(subCommunity));
                }
            }
        }
        
        return new Partition(newNodeCommunity, nextCommunityId);
    }

    /**
     * Refines an unstable community by performing local moving on its subgraph.
     *
     * <p>Creates a subgraph containing only nodes from the community and edges between them,
     * then performs iterative local moving where nodes are moved between subclusters
     * to maximize modularity.
     *
     * @param communityNodes the set of nodes in the community
     * @param graph          the full weighted graph
     * @param resolution     the resolution parameter
     * @param random         the random number generator
     * @return a partition of the community nodes into subclusters
     */
    private Partition refineCommunity(Set<String> communityNodes,
                                      Map<String, Map<String, Double>> graph,
                                      double resolution,
                                      Random random) {
        // Create subgraph with only edges between community nodes
        Map<String, Map<String, Double>> subgraph = createSubgraph(communityNodes, graph);
        
        // Create initial partition: each node in its own community
        Partition subpartition = new Partition(communityNodes);
        
        // Perform local moving on the subgraph
        boolean moved;
        do {
            moved = false;
            
            // Get list of nodes and shuffle
            List<String> nodes = new ArrayList<>(communityNodes);
            Collections.shuffle(nodes, random);
            
            for (String node : nodes) {
                // Get current community and neighboring communities
                int currentCommunity = subpartition.communityOf(node);
                Set<Integer> neighborCommunities = getNeighborCommunities(node, subgraph, subpartition);
                
                double maxDeltaQ = 0.0;
                int bestCommunity = currentCommunity;
                
                // Evaluate each neighboring community
                for (int targetCommunity : neighborCommunities) {
                    if (targetCommunity != currentCommunity) {
                        double deltaQ = ModularityCalculator.deltaModularity(
                            node, targetCommunity, subgraph, subpartition, resolution);
                        if (deltaQ > maxDeltaQ) {
                            maxDeltaQ = deltaQ;
                            bestCommunity = targetCommunity;
                        }
                    }
                }
                
                // Move if improvement found
                if (maxDeltaQ > 0.001) {
                    subpartition.moveNode(node, bestCommunity);
                    moved = true;
                }
            }
        } while (moved);
        
        return subpartition;
    }

    /**
     * Creates a subgraph containing only the specified nodes and edges between them.
     *
     * @param nodes the set of nodes to include in the subgraph
     * @param graph the original graph
     * @return the subgraph containing only nodes and edges within the set
     */
    private Map<String, Map<String, Double>> createSubgraph(Set<String> nodes,
                                                             Map<String, Map<String, Double>> graph) {
        Map<String, Map<String, Double>> subgraph = new HashMap<>();
        
        for (String node : nodes) {
            Map<String, Double> edges = graph.get(node);
            if (edges != null) {
                Map<String, Double> subgraphEdges = new HashMap<>();
                for (Map.Entry<String, Double> edge : edges.entrySet()) {
                    if (nodes.contains(edge.getKey())) {
                        subgraphEdges.put(edge.getKey(), edge.getValue());
                    }
                }
                // Always include the node, even if it has no internal edges
                subgraph.put(node, subgraphEdges);
            } else {
                // Node exists in graph but has no edges
                subgraph.put(node, new HashMap<>());
            }
        }
        
        return subgraph;
    }

    /**
     * Gets the set of communities neighboring the given node in the subgraph.
     *
     * @param node      the node name
     * @param subgraph  the subgraph
     * @param partition the current partition
     * @return set of neighboring community numbers
     */
    private Set<Integer> getNeighborCommunities(String node,
                                                 Map<String, Map<String, Double>> subgraph,
                                                 Partition partition) {
        Set<Integer> communities = new HashSet<>();
        
        Map<String, Double> edges = subgraph.get(node);
        if (edges != null) {
            for (String neighbor : edges.keySet()) {
                communities.add(partition.communityOf(neighbor));
            }
        }
        
        // Include own community
        communities.add(partition.communityOf(node));
        
        return communities;
    }

    /**
     * Calculates the delta Q for moving a node to a singleton community.
     *
     * <p>Uses the standard CPM modularity delta formula. When moving to a new singleton,
     * the delta is computed by considering the loss of connections to the current community.
     *
     * @param node       the node to check
     * @param community  the current community of the node
     * @param graph      the full graph
     * @param partition  the current partition
     * @param resolution the resolution parameter
     * @return the change in modularity (positive means beneficial to split)
     */
    private double calculateDeltaQForSingleton(String node,
                                                int community,
                                                Map<String, Map<String, Double>> graph,
                                                Partition partition,
                                                double resolution) {
        // Calculate the change in modularity when moving a node to a singleton community
        // by computing actual modularity before and after
        
        double ki = ModularityCalculator.nodeDegree(node, graph);
        double kiIn = ModularityCalculator.edgesToCommunity(node, community, graph, partition);
        double sigmaTot = ModularityCalculator.communityDegree(community, graph, partition);
        
        double m = calculateTotalWeight(graph);
        if (Math.abs(m) < 0.001) {
            return 0.0;
        }
        
        // When node i moves from C to singleton S:
        //
        // The standard delta modularity formula for joining target community D is:
        // ΔQ_join = k_{i,in}^D/m - γ*k_i*Σ_tot^D/(2m^2)
        //
        // For leaving source community C:
        // ΔQ_leave = -[k_{i,in}^C/m - γ*k_i*(Σ_tot^C - k_i)/(2m^2)]
        //
        // Net change = ΔQ_join + ΔQ_leave
        
        // For singleton S:
        // - k_{i,in}^S = 0 (no edges to self in new singleton)
        // - Σ_tot^S = k_i (the singleton's total degree)
        //
        // ΔQ_join_S = 0/m - γ*k_i*k_i/(2m^2) = -γ*k_i^2/(2m^2)
        
        // For leaving C:
        // ΔQ_leave_C = -k_{i,in}^C/m + γ*k_i*(Σ_tot^C - k_i)/(2m^2)
        
        // Net: -γ*k_i^2/(2m^2) - k_{i,in}^C/m + γ*k_i*(Σ_tot^C - k_i)/(2m^2)
        //     = -k_{i,in}^C/m + γ*k_i*(Σ_tot^C - 2*k_i)/(2m^2)
        
        double joinSingleton = -(resolution * ki * ki / (2.0 * m * m));
        double leaveCommunity = (kiIn / m) - (resolution * ki * (sigmaTot - ki) / (2.0 * m * m));
        
        return joinSingleton - leaveCommunity;
    }

    /**
     * Calculates the total weight of all edges in the graph.
     *
     * @param graph the weighted graph
     * @return the total weight of all edges
     */
    private double calculateTotalWeight(Map<String, Map<String, Double>> graph) {
        double total = 0.0;
        for (Map<String, Double> edges : graph.values()) {
            for (double weight : edges.values()) {
                total += weight;
            }
        }
        return total;
    }
}