package spring.twin.cluster;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Component that orchestrates the complete Leiden clustering algorithm.
 * The algorithm consists of three phases: local move, refinement, and aggregation.
 * These phases are iterated until convergence (no changes in the partition).
 */
@Component
public class LeidenAlgorithm {

    private final LeidenLocalMove localMove;
    private final LeidenRefine refine;
    private final LeidenAggregate aggregate;

    /**
     * Constructor with dependency injection of the three algorithm phases.
     *
     * @param localMove the local move phase component
     * @param refine    the refinement phase component
     * @param aggregate the aggregation phase component
     */
    public LeidenAlgorithm(LeidenLocalMove localMove, LeidenRefine refine, LeidenAggregate aggregate) {
        this.localMove = localMove;
        this.refine = refine;
        this.aggregate = aggregate;
    }

    /**
     * Executes the complete Leiden clustering algorithm with a random seed.
     *
     * <p>Creates an initial partition where each node is in its own community,
     * then iteratively performs: (1) local move, (2) refinement, (3) aggregation.
     * If no changes occur during local move, the algorithm has converged and stops.
     * Otherwise, it continues on the aggregated graph. After convergence,
     * the results are mapped back to the original nodes.
     *
     * <p>The seed is generated randomly using {@code new Random()}.
     *
     * @param graph      the weighted graph as {@code Map<String, Map<String, Double>>}
     * @param resolution the resolution parameter (gamma), controls community size
     * @return the final partition of nodes into communities
     */
    public Partition cluster(Map<String, Map<String, Double>> graph, double resolution) {
        return cluster(graph, resolution, new Random().nextLong());
    }

    /**
     * Executes the complete Leiden clustering algorithm with a fixed seed.
     *
     * <p>This variant allows reproducible results by using a specific random seed.
     * Creates an initial partition where each node is in its own community,
     * then iteratively performs: (1) local move, (2) refinement, (3) aggregation.
     * If no changes occur during local move, the algorithm has converged and stops.
     * Otherwise, it continues on the aggregated graph. After convergence,
     * the results are mapped back to the original nodes.
     *
     * @param graph      the weighted graph as {@code Map<String, Map<String, Double>>}
     * @param resolution the resolution parameter (gamma), controls community size
     * @param seed       the random seed for reproducibility
     * @return the final partition of nodes into communities
     */
    public Partition cluster(Map<String, Map<String, Double>> graph, double resolution, long seed) {
        Random random = new Random(seed);
        
        // Handle empty graph
        if (graph.isEmpty()) {
            return new Partition(new HashSet<>());
        }
        
        // Save original nodes for final result
        Set<String> originalNodes = new HashSet<>(graph.keySet());
        
        // Step 1: Create initial partition where each node is in its own community
        Partition currentPartition = new Partition(graph.keySet());
        
        // Step 2: Save initial super-node mapping (each node maps to itself initially)
        Map<String, Set<String>> superNodeToOriginalNodes = new HashMap<>();
        for (String node : graph.keySet()) {
            Set<String> originalSet = new HashSet<>();
            originalSet.add(node);
            superNodeToOriginalNodes.put(node, originalSet);
        }
        
        // Step 3: Iterative process
        while (true) {
            // Local move phase
            Partition newPartition = localMove.move(graph, currentPartition, resolution, random);
            
            // Check convergence - compare using node names from the partition
            boolean partitionChanged = false;
            for (String node : newPartition.nodes()) {
                int newCommunity = newPartition.communityOf(node);
                int oldCommunity = currentPartition.communityOf(node);
                if (newCommunity != oldCommunity) {
                    partitionChanged = true;
                    break;
                }
            }
            if (!partitionChanged) {
                // Algorithm has converged, flatten the result back to original nodes
                return flattenToOriginalNodes(newPartition, superNodeToOriginalNodes, originalNodes);
            }
            currentPartition = newPartition;
            
            // Refinement phase
            Partition refinedPartition = refine.refine(graph, currentPartition, resolution, random);
            currentPartition = refinedPartition;
            
            // Aggregation phase
            Map<String, Map<String, Double>> aggregatedGraph = aggregate.aggregate(graph, currentPartition);
            
            // Build new superNodeToOriginalNodes mapping for aggregated graph
            Map<String, Set<String>> newSuperNodeToOriginalNodes = new HashMap<>();
            for (Integer community : currentPartition.communities()) {
                String superNode = aggregate.toSuperNodeName(community);
                Set<String> mergedOriginalNodes = new HashSet<>();
                for (String node : currentPartition.nodesInCommunity(community)) {
                    Set<String> originalNodesForThisNode = superNodeToOriginalNodes.get(node);
                    if (originalNodesForThisNode != null) {
                        mergedOriginalNodes.addAll(originalNodesForThisNode);
                    }
                }
                newSuperNodeToOriginalNodes.put(superNode, mergedOriginalNodes);
            }
            superNodeToOriginalNodes = newSuperNodeToOriginalNodes;
            
            // Create new partition for aggregated graph (each super-node in its own community)
            Partition aggregatePartition = aggregate.createAggregatePartition(aggregatedGraph);
            
            // Continue with aggregated graph
            graph = aggregatedGraph;
            currentPartition = aggregatePartition;
        }
    }
    
    /**
     * Flattens a partition of super-nodes back to the original nodes.
     *
     * @param partition the partition of super-nodes
     * @param superNodeToOriginalNodes mapping from super-node to original nodes
     * @param originalNodes the set of all original nodes
     * @return partition of original nodes
     */
    private Partition flattenToOriginalNodes(Partition partition, 
                                              Map<String, Set<String>> superNodeToOriginalNodes,
                                              Set<String> originalNodes) {
        Map<String, Integer> nodeCommunity = new HashMap<>();
        
        // For each super-node in the partition
        for (String superNode : partition.nodes()) {
            int community = partition.communityOf(superNode);
            Set<String> nodes = superNodeToOriginalNodes.get(superNode);
            if (nodes != null) {
                for (String node : nodes) {
                    nodeCommunity.put(node, community);
                }
            }
        }
        
        // Ensure all original nodes are in the result (for isolated nodes that might have been lost)
        int maxCommunity = partition.communityCount();
        for (String node : originalNodes) {
            if (!nodeCommunity.containsKey(node)) {
                nodeCommunity.put(node, maxCommunity++);
            }
        }
        
        return new Partition(nodeCommunity, maxCommunity);
    }

    /**
     * Maps a partition of the aggregated graph back to the original nodes.
     *
     * <p>For each super-node in the aggregated partition, retrieves its community
     * assignment and assigns the same community to all original nodes that
     * were merged into this super-node.
     *
     * @param currentPartition   the current partition before aggregation
     * @param aggregatePartition the partition of the aggregated graph (super-nodes)
     * @param superNodeToNodes   mapping from super-node name to set of original nodes
     * @return the flattened partition mapped to original nodes
     */
    public Partition flattenPartition(Partition currentPartition, Partition aggregatePartition, Map<String, Set<String>> superNodeToNodes) {
        Map<String, Integer> nodeCommunity = new HashMap<>();
        
        // For each super-node in the aggregate partition
        for (String superNode : aggregatePartition.nodes()) {
            int community = aggregatePartition.communityOf(superNode);
            Set<String> originalNodes = superNodeToNodes.get(superNode);
            
            // Assign the same community to all original nodes
            if (originalNodes != null) {
                for (String node : originalNodes) {
                    nodeCommunity.put(node, community);
                }
            }
        }
        
        return new Partition(nodeCommunity, aggregatePartition.communityCount());
    }

    /**
     * Builds a mapping from super-node names to sets of original nodes.
     *
     * <p>Uses the partition to determine which nodes belong to each community,
     * then creates super-node names using the naming convention and maps
     * each super-node to the set of its constituent original nodes.
     *
     * @param partition the partition containing community assignments
     * @return mapping from super-node name to set of original nodes in that community
     */
    public Map<String, Set<String>> buildSuperNodeMapping(Partition partition) {
        Map<String, Set<String>> superNodeToNodes = new HashMap<>();
        
        // Get community map: community ID -> set of nodes
        Map<Integer, Set<String>> communityMap = partition.toCommunityMap();
        
        // Convert to super-node mapping: "community-{id}" -> set of nodes
        for (Map.Entry<Integer, Set<String>> entry : communityMap.entrySet()) {
            String superNodeName = "community-" + entry.getKey();
            superNodeToNodes.put(superNodeName, new HashSet<>(entry.getValue()));
        }
        
        return superNodeToNodes;
    }
}