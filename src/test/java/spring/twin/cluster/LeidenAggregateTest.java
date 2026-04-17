package spring.twin.cluster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LeidenAggregate}.
 */
class LeidenAggregateTest {

    @Test
    @DisplayName("toSuperNodeName returns correct format")
    void testToSuperNodeName_returnsCorrectFormat() {
        LeidenAggregate aggregate = new LeidenAggregate();

        String result = aggregate.toSuperNodeName(5);

        assertEquals("community-5", result);
    }

    @Test
    @DisplayName("Aggregate: empty graph returns empty map")
    void testAggregate_emptyGraph_returnsEmptyMap() {
        LeidenAggregate aggregate = new LeidenAggregate();
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Partition partition = new Partition(Set.of());

        Map<String, Map<String, Double>> result = aggregate.aggregate(graph, partition);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Aggregate: all nodes in one community creates single super-node with self-loop")
    void testAggregate_allNodesInOneCommunity_createsSingleSuperNode() {
        LeidenAggregate aggregate = new LeidenAggregate();
        // Graph: A --1.0--> B
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>(Map.of("B", 1.0)));
        graph.put("B", new HashMap<>());
        
        // All nodes in community 0
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        Partition partition = new Partition(nodeCommunity, 1);

        Map<String, Map<String, Double>> result = aggregate.aggregate(graph, partition);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("community-0"));
        Map<String, Double> superNodeEdges = result.get("community-0");
        // Internal edge becomes self-loop with weight 1.0
        assertEquals(1, superNodeEdges.size());
        assertEquals(1.0, superNodeEdges.get("community-0"));
    }

    @Test
    @DisplayName("Aggregate: two communities creates two super-nodes with edge between them")
    void testAggregate_twoCommunities_createsTwoSuperNodes() {
        LeidenAggregate aggregate = new LeidenAggregate();
        // Graph: A (community 0) --1.0--> B (community 1)
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>(Map.of("B", 1.0)));
        graph.put("B", new HashMap<>());
        
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 1);
        Partition partition = new Partition(nodeCommunity, 2);

        Map<String, Map<String, Double>> result = aggregate.aggregate(graph, partition);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("community-0"));
        assertTrue(result.containsKey("community-1"));
        
        // Edge from community-0 to community-1 with weight 1.0
        Map<String, Double> edgesFrom0 = result.get("community-0");
        assertEquals(1, edgesFrom0.size());
        assertEquals(1.0, edgesFrom0.get("community-1"));
    }

    @Test
    @DisplayName("Aggregate: edge weight aggregated correctly as sum")
    void testAggregate_edgeWeightAggregated_correctSum() {
        LeidenAggregate aggregate = new LeidenAggregate();
        // Graph: A --1.0--> C, B --2.0--> C
        // A and B in community 0, C in community 1
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>(Map.of("C", 1.0)));
        graph.put("B", new HashMap<>(Map.of("C", 2.0)));
        graph.put("C", new HashMap<>());
        
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        nodeCommunity.put("C", 1);
        Partition partition = new Partition(nodeCommunity, 2);

        Map<String, Map<String, Double>> result = aggregate.aggregate(graph, partition);

        // Edge from community-0 to community-1 should have weight 3.0 (1.0 + 2.0)
        Map<String, Double> edgesFrom0 = result.get("community-0");
        assertEquals(1, edgesFrom0.size());
        assertEquals(3.0, edgesFrom0.get("community-1"));
    }

    @Test
    @DisplayName("Aggregate: internal edges become self-loops")
    void testAggregate_internalEdgesBecomeSelfLoops() {
        LeidenAggregate aggregate = new LeidenAggregate();
        // Graph: A --1.0--> B (both in same community)
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>(Map.of("B", 1.0)));
        graph.put("B", new HashMap<>());
        
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        Partition partition = new Partition(nodeCommunity, 1);

        Map<String, Map<String, Double>> result = aggregate.aggregate(graph, partition);

        Map<String, Double> superNodeEdges = result.get("community-0");
        // Internal edge becomes self-loop
        assertEquals(1, superNodeEdges.size());
        assertTrue(superNodeEdges.containsKey("community-0"));
        assertEquals(1.0, superNodeEdges.get("community-0"));
    }

    @Test
    @DisplayName("Aggregate: no edges between communities means no inter-community edge")
    void testAggregate_noEdgesBetweenCommunities_noInterCommunityEdge() {
        LeidenAggregate aggregate = new LeidenAggregate();
        // Graph: A --1.0--> B (both in community 0), C isolated (community 1)
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>(Map.of("B", 1.0)));
        graph.put("B", new HashMap<>());
        graph.put("C", new HashMap<>());
        
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        nodeCommunity.put("C", 1);
        Partition partition = new Partition(nodeCommunity, 2);

        Map<String, Map<String, Double>> result = aggregate.aggregate(graph, partition);

        // community-0 has self-loop from internal edge
        Map<String, Double> edgesFrom0 = result.get("community-0");
        assertEquals(1, edgesFrom0.size());
        assertTrue(edgesFrom0.containsKey("community-0"));
        
        // community-1 has no edges (no incoming or outgoing edges from other community)
        Map<String, Double> edgesFrom1 = result.get("community-1");
        assertTrue(edgesFrom1 == null || edgesFrom1.isEmpty());
    }

    @Test
    @DisplayName("CreateAggregatePartition: each super-node in its own community")
    void testCreateAggregatePartition_eachSuperNodeInOwnCommunity() {
        LeidenAggregate aggregate = new LeidenAggregate();
        // Aggregated graph with 3 super-nodes
        Map<String, Map<String, Double>> aggregatedGraph = new HashMap<>();
        aggregatedGraph.put("community-0", new HashMap<>(Map.of("community-1", 1.0)));
        aggregatedGraph.put("community-1", new HashMap<>());
        aggregatedGraph.put("community-2", new HashMap<>());

        Partition result = aggregate.createAggregatePartition(aggregatedGraph);

        assertEquals(3, result.communityCount());
        assertEquals(3, result.nodes().size());
        assertTrue(result.nodes().contains("community-0"));
        assertTrue(result.nodes().contains("community-1"));
        assertTrue(result.nodes().contains("community-2"));
        
        // Each super-node should be in its own community
        int community0 = result.communityOf("community-0");
        int community1 = result.communityOf("community-1");
        int community2 = result.communityOf("community-2");
        
        assertNotEquals(community0, community1);
        assertNotEquals(community0, community2);
        assertNotEquals(community1, community2);
    }

    @Test
    @DisplayName("Aggregate: preserves total edge weight")
    void testAggregate_preservesTotalEdgeWeight() {
        LeidenAggregate aggregate = new LeidenAggregate();
        // Graph with multiple edges
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>(Map.of("B", 1.0, "C", 2.0)));
        graph.put("B", new HashMap<>(Map.of("C", 3.0)));
        graph.put("C", new HashMap<>());
        
        // Calculate total edge weight in original graph
        double originalTotalWeight = 0.0;
        for (Map<String, Double> edges : graph.values()) {
            for (double weight : edges.values()) {
                originalTotalWeight += weight;
            }
        }
        
        // A and B in community 0, C in community 1
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        nodeCommunity.put("C", 1);
        Partition partition = new Partition(nodeCommunity, 2);

        Map<String, Map<String, Double>> result = aggregate.aggregate(graph, partition);

        // Calculate total edge weight in aggregated graph
        double aggregatedTotalWeight = 0.0;
        for (Map<String, Double> edges : result.values()) {
            for (double weight : edges.values()) {
                aggregatedTotalWeight += weight;
            }
        }
        
        assertEquals(originalTotalWeight, aggregatedTotalWeight, 0.0001);
    }
}