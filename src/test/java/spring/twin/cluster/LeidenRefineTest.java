package spring.twin.cluster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LeidenRefine}.
 */
class LeidenRefineTest {

    @Test
    @DisplayName("isCommunityStable: stable community returns true")
    void testIsCommunityStable_stableCommunity_returnsTrue() {
        LeidenRefine refine = new LeidenRefine();

        // Complete graph with 3 nodes (triangle) - highly connected
        // Removing any node should worsen modularity
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

        // All nodes in one community
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        nodeCommunity.put("C", 0);
        Partition partition = new Partition(nodeCommunity, 1);

        boolean stable = refine.isCommunityStable(0, graph, partition, 1.0);

        assertTrue(stable, "Stable community should return true");
    }

    @Test
    @DisplayName("isCommunityStable: unstable community returns false")
    void testIsCommunityStable_unstableCommunity_returnsFalse() {
        LeidenRefine refine = new LeidenRefine();

        // Graph: A connected only to B, B strongly connected to C, C connected only to B
        // Node A might improve modularity by being separate
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 0.5);
        graph.put("A", edgesA);
        Map<String, Double> edgesB = new HashMap<>();
        edgesB.put("A", 0.5);
        edgesB.put("C", 3.0);
        graph.put("B", edgesB);
        Map<String, Double> edgesC = new HashMap<>();
        edgesC.put("B", 3.0);
        graph.put("C", edgesC);

        // All nodes in one community
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        nodeCommunity.put("C", 0);
        Partition partition = new Partition(nodeCommunity, 1);

        boolean stable = refine.isCommunityStable(0, graph, partition, 1.0);

        assertFalse(stable, "Unstable community should return false");
    }

    @Test
    @DisplayName("refine: stable community is not split")
    void testRefine_stableCommunity_noSplit() {
        LeidenRefine refine = new LeidenRefine();

        // Complete graph - stable community
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

        // All nodes in one community
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        nodeCommunity.put("C", 0);
        Partition partition = new Partition(nodeCommunity, 1);

        Random random = new Random(42);
        Partition result = refine.refine(graph, partition, 1.0, random);

        // Stable community should remain as one community
        assertEquals(1, result.communityCount());
        assertEquals(result.communityOf("A"), result.communityOf("B"));
        assertEquals(result.communityOf("B"), result.communityOf("C"));
    }

    @Test
    @DisplayName("refine: unstable community splits into subcommunities")
    void testRefine_unstableCommunity_splitsIntoSubcommunities() {
        LeidenRefine refine = new LeidenRefine();

        // Graph with two weakly connected cliques: A-B and C-D connected by weak edge B-C
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

        // All nodes initially in one community (unstable)
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        nodeCommunity.put("C", 0);
        nodeCommunity.put("D", 0);
        Partition partition = new Partition(nodeCommunity, 1);

        Random random = new Random(42);
        Partition result = refine.refine(graph, partition, 1.0, random);

        // Should split into at least 2 communities
        assertTrue(result.communityCount() > 1, "Unstable community should split");
        
        // A and B should be together (strong connection)
        assertEquals(result.communityOf("A"), result.communityOf("B"));
        
        // C and D should be together (strong connection)
        assertEquals(result.communityOf("C"), result.communityOf("D"));
    }

    @Test
    @DisplayName("refine: empty graph returns original partition")
    void testRefine_emptyGraph_returnsOriginalPartition() {
        LeidenRefine refine = new LeidenRefine();

        Map<String, Map<String, Double>> graph = new HashMap<>();
        Set<String> nodes = new HashSet<>();
        Partition partition = new Partition(nodes);

        Random random = new Random(42);
        Partition result = refine.refine(graph, partition, 1.0, random);

        assertTrue(result.isEmpty());
        assertEquals(0, result.communityCount());
    }

    @Test
    @DisplayName("refine: single node community is not split")
    void testRefine_singleNodeCommunity_noSplit() {
        LeidenRefine refine = new LeidenRefine();

        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>());

        Set<String> nodes = new HashSet<>();
        nodes.add("A");
        Partition partition = new Partition(nodes);

        Random random = new Random(42);
        Partition result = refine.refine(graph, partition, 1.0, random);

        // Single node should remain in one community
        assertEquals(1, result.communityCount());
        assertEquals(0, result.communityOf("A"));
    }

    @Test
    @DisplayName("refine: preserves all nodes")
    void testRefine_preservesAllNodes() {
        LeidenRefine refine = new LeidenRefine();

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
        edgesC.put("D", 1.0);
        graph.put("C", edgesC);
        Map<String, Double> edgesD = new HashMap<>();
        edgesD.put("C", 1.0);
        graph.put("D", edgesD);

        Set<String> originalNodes = new HashSet<>();
        originalNodes.add("A");
        originalNodes.add("B");
        originalNodes.add("C");
        originalNodes.add("D");
        Partition partition = new Partition(originalNodes);

        Random random = new Random(42);
        Partition result = refine.refine(graph, partition, 1.0, random);

        // All nodes should be preserved
        assertEquals(originalNodes, result.nodes());
        assertTrue(result.nodes().contains("A"));
        assertTrue(result.nodes().contains("B"));
        assertTrue(result.nodes().contains("C"));
        assertTrue(result.nodes().contains("D"));
        assertEquals(4, result.nodes().size());
    }

    @Test
    @DisplayName("refine: two stable communities remain unchanged")
    void testRefine_twoStableCommunities_noChange() {
        LeidenRefine refine = new LeidenRefine();

        // Two separate cliques (stable communities)
        Map<String, Map<String, Double>> graph = new HashMap<>();
        
        // Clique 1: A-B
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 2.0);
        graph.put("A", edgesA);
        Map<String, Double> edgesB = new HashMap<>();
        edgesB.put("A", 2.0);
        graph.put("B", edgesB);
        
        // Clique 2: C-D
        Map<String, Double> edgesC = new HashMap<>();
        edgesC.put("D", 2.0);
        graph.put("C", edgesC);
        Map<String, Double> edgesD = new HashMap<>();
        edgesD.put("C", 2.0);
        graph.put("D", edgesD);

        // Two separate communities
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        nodeCommunity.put("C", 1);
        nodeCommunity.put("D", 1);
        Partition partition = new Partition(nodeCommunity, 2);

        Random random = new Random(42);
        Partition result = refine.refine(graph, partition, 1.0, random);

        // Two stable communities should remain
        assertEquals(2, result.communityCount());
        
        // A and B should remain together
        assertEquals(result.communityOf("A"), result.communityOf("B"));
        
        // C and D should remain together
        assertEquals(result.communityOf("C"), result.communityOf("D"));
        
        // But A/B community should be different from C/D community
        assertNotEquals(result.communityOf("A"), result.communityOf("C"));
    }
}