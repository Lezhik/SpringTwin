package spring.twin.cluster;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Component that performs the local move phase of the Leiden algorithm.
 * In this phase, nodes are moved between communities to maximize modularity.
 * The process repeats until stabilization (no node is moved in a pass).
 */
@Component
public class LeidenLocalMove {

    /**
     * Default constructor.
     */
    public LeidenLocalMove() {
    }

    /**
     * Returns the set of communities neighboring the given node.
     *
     * <p>Gets neighbors from the graph, determines their community through
     * {@link Partition#communityOf(String)}, and collects them in a Set.
     * Includes the current node's community if it has no neighbors (isolated node).
     *
     * @param node      the node name
     * @param graph     the weighted graph as {@code Map<String, Map<String, Double>>}
     * @param partition the current partition of nodes into communities
     * @return the set of neighboring community numbers
     */
    public Set<Integer> neighborCommunities(String node,
                                            Map<String, Map<String, Double>> graph,
                                            Partition partition) {
        Set<Integer> communities = new HashSet<>();
        
        Map<String, Double> neighbors = graph.get(node);
        if (neighbors != null) {
            for (String neighbor : neighbors.keySet()) {
                communities.add(partition.communityOf(neighbor));
            }
        }
        
        // For isolated nodes (no neighbors), include own community
        if (communities.isEmpty()) {
            communities.add(partition.communityOf(node));
        }
        
        return communities;
    }

    /**
     * Performs the local move phase of the Leiden algorithm.
     *
     * <p>Main loop:
     * (1) Creates a copy of partition,
     * (2) Gets list of nodes and shuffles via {@link Collections#shuffle(List, Random)},
     * (3) For each node computes neighboring communities,
     * (4) For each neighboring community computes ΔQ via {@link ModularityCalculator#deltaModularity(String, int, Map, Partition, double)},
     * (5) If maximum ΔQ > 0, moves the node,
     * (6) Repeats while at least one node was moved in a pass.
     *
     * @param graph      the weighted graph as {@code Map<String, Map<String, Double>>}
     * @param partition  the current partition of nodes into communities
     * @param resolution the resolution parameter (gamma), controls community size
     * @param random     random number generator for node ordering
     * @return the updated partition after local move phase
     */
    public Partition move(Map<String, Map<String, Double>> graph,
                          Partition partition,
                          double resolution,
                          Random random) {
        Partition currentPartition = partition.copy();
        
        boolean moved;
        do {
            moved = false;
            
            // Get list of nodes and shuffle
            List<String> nodes = new ArrayList<>(currentPartition.nodes());
            Collections.shuffle(nodes, random);
            
            // Process each node
            for (String node : nodes) {
                // Get neighboring communities
                Set<Integer> neighborCommunities = neighborCommunities(node, graph, currentPartition);
                
                int currentCommunity = currentPartition.communityOf(node);
                double maxDeltaQ = 0.0;
                int bestCommunity = currentCommunity;
                
                // Evaluate each neighboring community
                for (int community : neighborCommunities) {
                    double deltaQ = ModularityCalculator.deltaModularity(
                        node, community, graph, currentPartition, resolution);
                    
                    if (deltaQ > maxDeltaQ) {
                        maxDeltaQ = deltaQ;
                        bestCommunity = community;
                    }
                }
                
                // Move node if improvement found
                if (maxDeltaQ > 0 && bestCommunity != currentCommunity) {
                    currentPartition.moveNode(node, bestCommunity);
                    moved = true;
                }
            }
        } while (moved);
        
        return currentPartition;
    }
}