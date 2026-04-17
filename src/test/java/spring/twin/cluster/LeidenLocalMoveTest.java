package spring.twin.cluster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link LeidenLocalMove}.
 */
class LeidenLocalMoveTest {

    @Test
    @DisplayName("neighborCommunities: node with neighbors returns all neighbor communities")
    void testNeighborCommunities_nodeWithNeighbors_returnsAllNeighborCommunities() {
        LeidenLocalMove localMove = new LeidenLocalMove();
        
        // Graph: A connected to B and C
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 1.0);
        edgesA.put("C", 1.0);
        graph.put("A", edgesA);
        graph.put("B", new HashMap<>());
        graph.put("C", new HashMap<>());
        
        // Partition: A in community 0, B in community 1, C in community 2
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 1);
        nodeCommunity.put("C", 2);
        Partition partition = new Partition(nodeCommunity, 3);
        
        Set<Integer> neighborCommunities = localMove.neighborCommunities("A", graph, partition);
        
        assertThat(neighborCommunities).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    @DisplayName("neighborCommunities: isolated node returns own community")
    void testNeighborCommunities_isolatedNode_returnsOwnCommunity() {
        LeidenLocalMove localMove = new LeidenLocalMove();
        
        // Graph: A isolated (no edges)
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>());
        
        // Partition: A in community 0
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        Partition partition = new Partition(nodeCommunity, 1);
        
        Set<Integer> neighborCommunities = localMove.neighborCommunities("A", graph, partition);
        
        // Isolated node should only return its own community
        assertThat(neighborCommunities).containsExactly(0);
    }

    @Test
    @DisplayName("move: single iteration moves nodes to better community")
    void testMove_singleIteration_nodesMoveToBetterCommunity() {
        LeidenLocalMove localMove = new LeidenLocalMove();
        
        // Graph: two cliques A-B and C-D connected by weak edge B-C
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 2.0);
        graph.put("A", edgesA);
        Map<String, Double> edgesB = new HashMap<>();
        edgesB.put("A", 2.0);
        edgesB.put("C", 0.5);
        graph.put("B", edgesB);
        Map<String, Double> edgesC = new HashMap<>();
        edgesC.put("B", 0.5);
        edgesC.put("D", 2.0);
        graph.put("C", edgesC);
        Map<String, Double> edgesD = new HashMap<>();
        edgesD.put("C", 2.0);
        graph.put("D", edgesD);
        
        // Initial partition: all nodes separate
        Set<String> nodes = new HashSet<>();
        nodes.add("A");
        nodes.add("B");
        nodes.add("C");
        nodes.add("D");
        Partition partition = new Partition(nodes);
        
        Random random = new Random(42);
        Partition result = localMove.move(graph, partition, 1.0, random);
        
        // After move, strongly connected nodes should be in same community
        // A and B should be together, C and D should be together
        assertEquals(result.communityOf("A"), result.communityOf("B"));
        assertEquals(result.communityOf("C"), result.communityOf("D"));
    }

    @Test
    @DisplayName("move: converges and stops when no improvement")
    void testMove_converges_stopsWhenNoImprovement() {
        LeidenLocalMove localMove = new LeidenLocalMove();
        
        // Complete graph with strong internal connections
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 1.0);
        edgesA.put("C", 1.0);
        graph.put("A", edgesA);
        Map<String, Double> edgesB = new HashMap<>();
        edgesB.put("A", 1.0);
        edgesB.put("C", 1.0);
        graph.put("B", edgesB);
        Map<String, Double> edgesC = new HashMap<>();
        edgesC.put("A", 1.0);
        edgesC.put("B", 1.0);
        graph.put("C", edgesC);
        
        // All nodes in one community - already optimal
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        nodeCommunity.put("C", 0);
        Partition partition = new Partition(nodeCommunity, 1);
        
        Random random = new Random(42);
        Partition result = localMove.move(graph, partition, 1.0, random);
        
        // Partition should remain unchanged since already optimal
        assertEquals(0, result.communityOf("A"));
        assertEquals(0, result.communityOf("B"));
        assertEquals(0, result.communityOf("C"));
        assertEquals(1, result.communityCount());
    }

    @Test
    @DisplayName("move: empty graph returns original partition")
    void testMove_emptyGraph_returnsOriginalPartition() {
        LeidenLocalMove localMove = new LeidenLocalMove();
        
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Set<String> nodes = new HashSet<>();
        Partition partition = new Partition(nodes);
        
        Random random = new Random(42);
        Partition result = localMove.move(graph, partition, 1.0, random);
        
        assertTrue(result.isEmpty());
        assertEquals(0, result.communityCount());
    }

    @Test
    @DisplayName("move: single node returns original partition")
    void testMove_singleNode_returnsOriginalPartition() {
        LeidenLocalMove localMove = new LeidenLocalMove();
        
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>());
        
        Set<String> nodes = new HashSet<>();
        nodes.add("A");
        Partition partition = new Partition(nodes);
        
        Random random = new Random(42);
        Partition result = localMove.move(graph, partition, 1.0, random);
        
        assertEquals(1, result.communityCount());
        assertEquals(0, result.communityOf("A"));
    }

    @Test
    @DisplayName("move: two connected nodes form same community")
    void testMove_twoConnectedNodes_sameCommunity() {
        LeidenLocalMove localMove = new LeidenLocalMove();
        
        // Graph: A connected to B
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 1.0);
        graph.put("A", edgesA);
        Map<String, Double> edgesB = new HashMap<>();
        edgesB.put("A", 1.0);
        graph.put("B", edgesB);
        
        // Initial partition: each node in own community
        Set<String> nodes = new HashSet<>();
        nodes.add("A");
        nodes.add("B");
        Partition partition = new Partition(nodes);
        
        Random random = new Random(42);
        Partition result = localMove.move(graph, partition, 1.0, random);
        
        // Two connected nodes should end up in same community
        assertEquals(result.communityOf("A"), result.communityOf("B"));
    }

    @Test
    @DisplayName("move: three nodes with strong internal connections form two clusters")
    void testMove_threeNodes_twoClusters() {
        LeidenLocalMove localMove = new LeidenLocalMove();
        
        // Graph: A strongly connected to B, C weakly connected to both
        // Expected: A and B in one cluster, C may be separate or with them depending on resolution
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 3.0);
        edgesA.put("C", 0.5);
        graph.put("A", edgesA);
        Map<String, Double> edgesB = new HashMap<>();
        edgesB.put("A", 3.0);
        edgesB.put("C", 0.5);
        graph.put("B", edgesB);
        Map<String, Double> edgesC = new HashMap<>();
        edgesC.put("A", 0.5);
        edgesC.put("B", 0.5);
        graph.put("C", edgesC);
        
        // Initial partition: each node in own community
        Set<String> nodes = new HashSet<>();
        nodes.add("A");
        nodes.add("B");
        nodes.add("C");
        Partition partition = new Partition(nodes);
        
        Random random = new Random(42);
        Partition result = localMove.move(graph, partition, 1.0, random);
        
        // A and B should be in same community due to strong connection
        assertEquals(result.communityOf("A"), result.communityOf("B"));
    }

    @Test
    @DisplayName("move: preserves all nodes after moving")
    void testMove_preservesAllNodes() {
        LeidenLocalMove localMove = new LeidenLocalMove();
        
        // Graph with multiple nodes
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 1.0);
        graph.put("A", edgesA);
        Map<String, Double> edgesB = new HashMap<>();
        edgesB.put("A", 1.0);
        edgesB.put("C", 1.0);
        graph.put("B", edgesB);
        Map<String, Double> edgesC = new HashMap<>();
        edgesC.put("B", 1.0);
        graph.put("C", edgesC);
        
        Set<String> originalNodes = new HashSet<>();
        originalNodes.add("A");
        originalNodes.add("B");
        originalNodes.add("C");
        Partition partition = new Partition(originalNodes);
        
        Random random = new Random(42);
        Partition result = localMove.move(graph, partition, 1.0, random);
        
        // All nodes should be preserved
        assertEquals(originalNodes, result.nodes());
        assertTrue(result.nodes().contains("A"));
        assertTrue(result.nodes().contains("B"));
        assertTrue(result.nodes().contains("C"));
        assertEquals(3, result.nodes().size());
    }

    @Test
    @DisplayName("move: deterministic with same seed produces same result")
    void testMove_deterministicWithSameSeed() {
        LeidenLocalMove localMove = new LeidenLocalMove();
        
        // Graph with multiple nodes and connections
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 1.0);
        edgesA.put("C", 1.0);
        graph.put("A", edgesA);
        Map<String, Double> edgesB = new HashMap<>();
        edgesB.put("A", 1.0);
        edgesB.put("C", 1.0);
        graph.put("B", edgesB);
        Map<String, Double> edgesC = new HashMap<>();
        edgesC.put("A", 1.0);
        edgesC.put("B", 1.0);
        graph.put("C", edgesC);
        
        Set<String> nodes = new HashSet<>();
        nodes.add("A");
        nodes.add("B");
        nodes.add("C");
        Partition partition = new Partition(nodes);
        
        // Run with same seed twice
        Random random1 = new Random(42);
        Partition result1 = localMove.move(graph, partition.copy(), 1.0, random1);
        
        Random random2 = new Random(42);
        Partition result2 = localMove.move(graph, partition.copy(), 1.0, random2);
        
        // Results should be identical
        assertEquals(result1.communityCount(), result2.communityCount());
        assertEquals(result1.communityOf("A"), result2.communityOf("A"));
        assertEquals(result1.communityOf("B"), result2.communityOf("B"));
        assertEquals(result1.communityOf("C"), result2.communityOf("C"));
    }
}