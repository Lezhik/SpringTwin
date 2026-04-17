package spring.twin.cluster;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Component that performs the aggregation phase of the Leiden algorithm.
 * In this phase, nodes within the same community are merged into super-nodes.
 */
@Component
public class LeidenAggregate {

    /**
     * Default constructor.
     */
    public LeidenAggregate() {
    }

    /**
     * Creates an aggregated graph from the given graph and partition.
     *
     * <p>For each community, a super-node is created with name "community-{id}".
     * Edges between super-nodes have weight equal to the sum of weights of edges
     * between nodes of the corresponding communities. Edges within a community
     * become self-loops on the super-node.
     *
     * @param graph     the weighted graph as {@code Map<String, Map<String, Double>>}
     * @param partition the current partition of nodes into communities
     * @return the aggregated graph with super-nodes
     */
    public Map<String, Map<String, Double>> aggregate(Map<String, Map<String, Double>> graph, Partition partition) {
        Map<String, Map<String, Double>> aggregatedGraph = new HashMap<>();
        
        // Initialize all communities as super-nodes (even those without outgoing edges)
        for (Integer community : partition.communities()) {
            String superNode = toSuperNodeName(community);
            aggregatedGraph.put(superNode, new HashMap<>());
        }
        
        for (Map.Entry<String, Map<String, Double>> nodeEntry : graph.entrySet()) {
            String sourceNode = nodeEntry.getKey();
            Map<String, Double> edges = nodeEntry.getValue();
            
            int sourceCommunity = partition.communityOf(sourceNode);
            String sourceSuperNode = toSuperNodeName(sourceCommunity);
            
            for (Map.Entry<String, Double> edgeEntry : edges.entrySet()) {
                String targetNode = edgeEntry.getKey();
                Double weight = edgeEntry.getValue();
                
                int targetCommunity = partition.communityOf(targetNode);
                String targetSuperNode = toSuperNodeName(targetCommunity);
                
                aggregatedGraph.get(sourceSuperNode).merge(targetSuperNode, weight, Double::sum);
            }
        }
        
        return aggregatedGraph;
    }

    /**
     * Creates a partition for the aggregated graph where each super-node
     * is in its own community.
     *
     * @param aggregatedGraph the aggregated graph with super-nodes
     * @return a new partition where each super-node forms its own community
     */
    public Partition createAggregatePartition(Map<String, Map<String, Double>> aggregatedGraph) {
        return new Partition(aggregatedGraph.keySet());
    }

    /**
     * Converts a community ID to a super-node name.
     *
     * @param communityId the community ID
     * @return the super-node name in format "community-{id}"
     */
    public String toSuperNodeName(int communityId) {
        return "community-" + communityId;
    }
}