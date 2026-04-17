package spring.twin.cluster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LeidenAlgorithm}.
 */
class LeidenAlgorithmTest {

    @Test
    @DisplayName("Empty graph returns empty partition")
    void testCluster_emptyGraph_returnsEmptyPartition() {
        LeidenAlgorithm algorithm = new LeidenAlgorithm(
            new LeidenLocalMove(),
            new LeidenRefine(),
            new LeidenAggregate()
        );
        Map<String, Map<String, Double>> graph = new HashMap<>();

        Partition result = algorithm.cluster(graph, 1.0);

        assertTrue(result.isEmpty());
        assertEquals(0, result.nodes().size());
    }

    @Test
    @DisplayName("Single node returns single node in one community")
    void testCluster_singleNode_returnsSingleNodeInOneCommunity() {
        LeidenAlgorithm algorithm = new LeidenAlgorithm(
            new LeidenLocalMove(),
            new LeidenRefine(),
            new LeidenAggregate()
        );
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>());

        Partition result = algorithm.cluster(graph, 1.0);

        assertEquals(1, result.nodes().size());
        assertTrue(result.nodes().contains("A"));
        assertEquals(1, result.communityCount());
    }

    @Test
    @DisplayName("Two connected nodes are in same community")
    void testCluster_twoConnectedNodes_sameCommunity() {
        LeidenAlgorithm algorithm = new LeidenAlgorithm(
            new LeidenLocalMove(),
            new LeidenRefine(),
            new LeidenAggregate()
        );
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>(Map.of("B", 1.0)));
        graph.put("B", new HashMap<>());

        Partition result = algorithm.cluster(graph, 1.0);

        assertEquals(2, result.nodes().size());
        int communityA = result.communityOf("A");
        int communityB = result.communityOf("B");
        assertEquals(communityA, communityB, "Connected nodes should be in the same community");
    }

    @Test
    @DisplayName("Disconnected nodes are in different communities")
    void testCluster_disconnectedNodes_differentCommunities() {
        LeidenAlgorithm algorithm = new LeidenAlgorithm(
            new LeidenLocalMove(),
            new LeidenRefine(),
            new LeidenAggregate()
        );
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>());
        graph.put("B", new HashMap<>());

        Partition result = algorithm.cluster(graph, 1.0);

        assertEquals(2, result.nodes().size());
        int communityA = result.communityOf("A");
        int communityB = result.communityOf("B");
        assertNotEquals(communityA, communityB, "Disconnected nodes should be in different communities");
    }

    @Test
    @DisplayName("Three nodes form two clusters correctly")
    void testCluster_threeNodesTwoClusters_correctGrouping() {
        LeidenAlgorithm algorithm = new LeidenAlgorithm(
            new LeidenLocalMove(),
            new LeidenRefine(),
            new LeidenAggregate()
        );
        // A and B are connected, C is isolated
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>(Map.of("B", 1.0)));
        graph.put("B", new HashMap<>());
        graph.put("C", new HashMap<>());

        Partition result = algorithm.cluster(graph, 1.0);

        assertEquals(3, result.nodes().size());
        int communityA = result.communityOf("A");
        int communityB = result.communityOf("B");
        int communityC = result.communityOf("C");
        
        assertEquals(communityA, communityB, "A and B should be in the same community");
        assertNotEquals(communityA, communityC, "C should be in a different community from A");
    }

    @Test
    @DisplayName("All nodes are preserved in the result")
    void testCluster_preservesAllNodes() {
        LeidenAlgorithm algorithm = new LeidenAlgorithm(
            new LeidenLocalMove(),
            new LeidenRefine(),
            new LeidenAggregate()
        );
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>(Map.of("B", 1.0, "C", 0.5)));
        graph.put("B", new HashMap<>(Map.of("C", 1.0)));
        graph.put("C", new HashMap<>());
        graph.put("D", new HashMap<>(Map.of("A", 0.5)));

        Partition result = algorithm.cluster(graph, 1.0);

        assertEquals(4, result.nodes().size());
        assertTrue(result.nodes().contains("A"));
        assertTrue(result.nodes().contains("B"));
        assertTrue(result.nodes().contains("C"));
        assertTrue(result.nodes().contains("D"));
    }

    @Test
    @DisplayName("Same seed produces deterministic result")
    void testCluster_withSeed_deterministicResult() {
        LeidenAlgorithm algorithm = new LeidenAlgorithm(
            new LeidenLocalMove(),
            new LeidenRefine(),
            new LeidenAggregate()
        );
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>(Map.of("B", 1.0, "C", 0.5, "D", 0.5)));
        graph.put("B", new HashMap<>(Map.of("C", 1.0)));
        graph.put("C", new HashMap<>(Map.of("D", 0.5)));
        graph.put("D", new HashMap<>());

        Partition result1 = algorithm.cluster(graph, 1.0, 12345L);
        Partition result2 = algorithm.cluster(graph, 1.0, 12345L);

        assertEquals(result1.communityCount(), result2.communityCount());
        for (String node : graph.keySet()) {
            assertEquals(result1.communityOf(node), result2.communityOf(node), 
                "Node " + node + " should have same community with same seed");
        }
    }

    @Test
    @DisplayName("Different seeds may produce different results")
    void testCluster_differentSeeds_mayDiffer() {
        LeidenAlgorithm algorithm = new LeidenAlgorithm(
            new LeidenLocalMove(),
            new LeidenRefine(),
            new LeidenAggregate()
        );
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>(Map.of("B", 1.0, "C", 0.5, "D", 0.5)));
        graph.put("B", new HashMap<>(Map.of("C", 1.0)));
        graph.put("C", new HashMap<>(Map.of("D", 0.5)));
        graph.put("D", new HashMap<>());

        Partition result1 = algorithm.cluster(graph, 1.0, 12345L);
        Partition result2 = algorithm.cluster(graph, 1.0, 54321L);

        // We don't assert they are different (they might be the same by chance),
        // but we check both are valid partitions with all nodes
        assertEquals(4, result1.nodes().size());
        assertEquals(4, result2.nodes().size());
        assertTrue(result1.nodes().containsAll(graph.keySet()));
        assertTrue(result2.nodes().containsAll(graph.keySet()));
    }

    @Test
    @DisplayName("Build super-node mapping creates correct mapping")
    void testBuildSuperNodeMapping_correctMapping() {
        LeidenAlgorithm algorithm = new LeidenAlgorithm(
            new LeidenLocalMove(),
            new LeidenRefine(),
            new LeidenAggregate()
        );
        // Create partition: A and B in community 0, C in community 1
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        nodeCommunity.put("C", 1);
        Partition partition = new Partition(nodeCommunity, 2);

        Map<String, Set<String>> mapping = algorithm.buildSuperNodeMapping(partition);

        assertEquals(2, mapping.size());
        assertTrue(mapping.containsKey("community-0"));
        assertTrue(mapping.containsKey("community-1"));
        
        Set<String> nodesInCommunity0 = mapping.get("community-0");
        assertEquals(2, nodesInCommunity0.size());
        assertTrue(nodesInCommunity0.contains("A"));
        assertTrue(nodesInCommunity0.contains("B"));
        
        Set<String> nodesInCommunity1 = mapping.get("community-1");
        assertEquals(1, nodesInCommunity1.size());
        assertTrue(nodesInCommunity1.contains("C"));
    }

    @Test
    @DisplayName("Flatten partition maps correctly back to original nodes")
    void testFlattenPartition_correctMapping() {
        LeidenAlgorithm algorithm = new LeidenAlgorithm(
            new LeidenLocalMove(),
            new LeidenRefine(),
            new LeidenAggregate()
        );
        // Original partition: A and B in community 0, C in community 1
        Map<String, Integer> originalNodeCommunity = new HashMap<>();
        originalNodeCommunity.put("A", 0);
        originalNodeCommunity.put("B", 0);
        originalNodeCommunity.put("C", 1);
        Partition currentPartition = new Partition(originalNodeCommunity, 2);

        // Aggregated partition: community-0 and community-1 are both in community 0 (merged)
        Map<String, Integer> superNodeCommunity = new HashMap<>();
        superNodeCommunity.put("community-0", 0);
        superNodeCommunity.put("community-1", 0);
        Partition aggregatePartition = new Partition(superNodeCommunity, 1);

        // Super-node mapping
        Map<String, Set<String>> superNodeToNodes = new HashMap<>();
        superNodeToNodes.put("community-0", new HashSet<>(Set.of("A", "B")));
        superNodeToNodes.put("community-1", new HashSet<>(Set.of("C")));

        Partition result = algorithm.flattenPartition(currentPartition, aggregatePartition, superNodeToNodes);

        assertEquals(3, result.nodes().size());
        // All nodes should be in the same community after flattening
        int communityA = result.communityOf("A");
        int communityB = result.communityOf("B");
        int communityC = result.communityOf("C");
        assertEquals(communityA, communityB);
        assertEquals(communityA, communityC);
    }

    @Test
    @DisplayName("High resolution produces more clusters")
    void testCluster_highResolution_moreClusters() {
        LeidenAlgorithm algorithm = new LeidenAlgorithm(
            new LeidenLocalMove(),
            new LeidenRefine(),
            new LeidenAggregate()
        );
        // Graph with moderate connectivity
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>(Map.of("B", 1.0, "C", 0.5)));
        graph.put("B", new HashMap<>(Map.of("C", 1.0, "D", 0.5)));
        graph.put("C", new HashMap<>(Map.of("D", 0.5)));
        graph.put("D", new HashMap<>(Map.of("A", 0.5)));

        Partition lowResResult = algorithm.cluster(graph, 0.5, 12345L);
        Partition highResResult = algorithm.cluster(graph, 2.0, 12345L);

        // High resolution should tend to produce more/smaller communities
        // Note: this is probabilistic, but with fixed seed it should be consistent
        assertTrue(highResResult.communityCount() >= lowResResult.communityCount(),
            "Higher resolution should produce at least as many communities as lower resolution");
    }

    @Test
    @DisplayName("Low resolution produces fewer clusters")
    void testCluster_lowResolution_fewerClusters() {
        LeidenAlgorithm algorithm = new LeidenAlgorithm(
            new LeidenLocalMove(),
            new LeidenRefine(),
            new LeidenAggregate()
        );
        // Graph with moderate connectivity
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>(Map.of("B", 1.0, "C", 0.5)));
        graph.put("B", new HashMap<>(Map.of("C", 1.0, "D", 0.5)));
        graph.put("C", new HashMap<>(Map.of("D", 0.5)));
        graph.put("D", new HashMap<>(Map.of("A", 0.5)));

        Partition lowResResult = algorithm.cluster(graph, 0.5, 12345L);
        Partition highResResult = algorithm.cluster(graph, 2.0, 12345L);

        // Low resolution should tend to produce fewer/larger communities
        assertTrue(lowResResult.communityCount() <= highResResult.communityCount(),
            "Lower resolution should produce at most as many communities as higher resolution");
    }
}