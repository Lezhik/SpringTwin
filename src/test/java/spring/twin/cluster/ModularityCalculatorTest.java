package spring.twin.cluster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * Unit tests for {@link ModularityCalculator}.
 */
class ModularityCalculatorTest {

    @Test
    @DisplayName("nodeDegree: node with no edges returns 0")
    void testNodeDegree_nodeWithNoEdges_returnsZero() {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>());
        
        double degree = ModularityCalculator.nodeDegree("A", graph);
        
        assertThat(degree).isEqualTo(0.0);
    }

    @Test
    @DisplayName("nodeDegree: node with edges returns sum of weights")
    void testNodeDegree_nodeWithEdges_returnsSumOfWeights() {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 1.0);
        edgesA.put("C", 2.5);
        graph.put("A", edgesA);
        graph.put("B", new HashMap<>());
        graph.put("C", new HashMap<>());
        
        double degree = ModularityCalculator.nodeDegree("A", graph);
        
        assertThat(degree).isEqualTo(3.5);
    }

    @Test
    @DisplayName("communityDegree: single node community returns node degree")
    void testCommunityDegree_singleNodeCommunity_returnsNodeDegree() {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 1.0);
        graph.put("A", edgesA);
        graph.put("B", new HashMap<>());
        
        Set<String> nodes = new HashSet<>();
        nodes.add("A");
        nodes.add("B");
        Partition partition = new Partition(nodes);
        // A is community 0, B is community 1
        
        double degree = ModularityCalculator.communityDegree(0, graph, partition);
        
        assertThat(degree).isEqualTo(1.0);
    }

    @Test
    @DisplayName("communityDegree: multi node community returns sum of degrees")
    void testCommunityDegree_multiNodeCommunity_returnsSumOfDegrees() {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("C", 1.0);
        graph.put("A", edgesA);
        Map<String, Double> edgesB = new HashMap<>();
        edgesB.put("C", 2.0);
        graph.put("B", edgesB);
        graph.put("C", new HashMap<>());
        
        // Create partition: A and B in community 0, C in community 1
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        nodeCommunity.put("C", 1);
        Partition partition = new Partition(nodeCommunity, 2);
        
        double degree = ModularityCalculator.communityDegree(0, graph, partition);
        
        // Degree of A = 1.0, Degree of B = 2.0, Total = 3.0
        assertThat(degree).isEqualTo(3.0);
    }

    @Test
    @DisplayName("edgesInsideCommunity: no internal edges returns 0")
    void testEdgesInsideCommunity_noInternalEdges_returnsZero() {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("C", 1.0);
        graph.put("A", edgesA);
        Map<String, Double> edgesB = new HashMap<>();
        edgesB.put("C", 2.0);
        graph.put("B", edgesB);
        graph.put("C", new HashMap<>());
        
        // Create partition: A and B in community 0, C in community 1
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        nodeCommunity.put("C", 1);
        Partition partition = new Partition(nodeCommunity, 2);
        
        double edges = ModularityCalculator.edgesInsideCommunity(0, graph, partition);
        
        assertThat(edges).isEqualTo(0.0);
    }

    @Test
    @DisplayName("edgesInsideCommunity: with internal edges returns sum of weights")
    void testEdgesInsideCommunity_withInternalEdges_returnsSum() {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 1.0);
        edgesA.put("C", 3.0);
        graph.put("A", edgesA);
        Map<String, Double> edgesB = new HashMap<>();
        edgesB.put("A", 1.0);
        edgesB.put("C", 2.0);
        graph.put("B", edgesB);
        graph.put("C", new HashMap<>());
        
        // Create partition: A and B in community 0, C in community 1
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        nodeCommunity.put("C", 1);
        Partition partition = new Partition(nodeCommunity, 2);
        
        double edges = ModularityCalculator.edgesInsideCommunity(0, graph, partition);
        
        // Edge A-B with weight 1.0 is internal to community 0
        // Each edge counted once, so result is 1.0
        assertThat(edges).isEqualTo(1.0);
    }

    @Test
    @DisplayName("edgesToCommunity: node not connected to community returns 0")
    void testEdgesToCommunity_nodeNotConnectedToCommunity_returnsZero() {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        graph.put("A", new HashMap<>());
        graph.put("B", new HashMap<>());
        
        // Create partition: A in community 0, B in community 1
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 1);
        Partition partition = new Partition(nodeCommunity, 2);
        
        double edges = ModularityCalculator.edgesToCommunity("A", 1, graph, partition);
        
        assertThat(edges).isEqualTo(0.0);
    }

    @Test
    @DisplayName("edgesToCommunity: node connected to community returns sum of edge weights")
    void testEdgesToCommunity_nodeConnectedToCommunity_returnsSum() {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 1.5);
        edgesA.put("C", 2.5);
        graph.put("A", edgesA);
        graph.put("B", new HashMap<>());
        graph.put("C", new HashMap<>());
        
        // Create partition: A in community 0, B and C in community 1
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 1);
        nodeCommunity.put("C", 1);
        Partition partition = new Partition(nodeCommunity, 2);
        
        double edges = ModularityCalculator.edgesToCommunity("A", 1, graph, partition);
        
        // Edges A-B (1.5) + A-C (2.5) = 4.0
        assertThat(edges).isEqualTo(4.0);
    }

    @Test
    @DisplayName("calculateModularity: all nodes in one community returns computable value")
    void testCalculateModularity_allNodesInOneCommunity_returnsValue() {
        // Triangle graph with equal weights
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
        
        double modularity = ModularityCalculator.calculateModularity(graph, partition, 1.0);
        
        // Modularity should be a finite number (not NaN, not infinite)
        assertThat(modularity).isFinite();
    }

    @Test
    @DisplayName("calculateModularity: each node in own community returns negative value for resolution > 0")
    void testCalculateModularity_eachNodeInOwnCommunity_returnsNegativeValue() {
        // Triangle graph with equal weights
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
        
        // Each node in its own community
        Set<String> nodes = new HashSet<>();
        nodes.add("A");
        nodes.add("B");
        nodes.add("C");
        Partition partition = new Partition(nodes);
        
        double modularity = ModularityCalculator.calculateModularity(graph, partition, 1.0);
        
        // With resolution > 0, modularity should be negative when each node is isolated
        assertThat(modularity).isNegative();
    }

    @Test
    @DisplayName("calculateModularity: optimal partition has higher modularity than random")
    void testCalculateModularity_optimalPartition_higherThanRandom() {
        // Two cliques connected by one edge
        Map<String, Map<String, Double>> graph = new HashMap<>();
        
        // Clique 1: A, B, C
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
        edgesC.put("D", 1.0); // Bridge to clique 2
        graph.put("C", edgesC);
        
        // Clique 2: D, E, F
        Map<String, Double> edgesD = new HashMap<>();
        edgesD.put("C", 1.0);
        edgesD.put("E", 1.0);
        edgesD.put("F", 1.0);
        graph.put("D", edgesD);
        Map<String, Double> edgesE = new HashMap<>();
        edgesE.put("D", 1.0);
        edgesE.put("F", 1.0);
        graph.put("E", edgesE);
        Map<String, Double> edgesF = new HashMap<>();
        edgesF.put("D", 1.0);
        edgesF.put("E", 1.0);
        graph.put("F", edgesF);
        
        // Optimal partition: {A,B,C} and {D,E,F}
        Map<String, Integer> optimalCommunity = new HashMap<>();
        optimalCommunity.put("A", 0);
        optimalCommunity.put("B", 0);
        optimalCommunity.put("C", 0);
        optimalCommunity.put("D", 1);
        optimalCommunity.put("E", 1);
        optimalCommunity.put("F", 1);
        Partition optimalPartition = new Partition(optimalCommunity, 2);
        
        // Random partition: all in one community
        Map<String, Integer> randomCommunity = new HashMap<>();
        randomCommunity.put("A", 0);
        randomCommunity.put("B", 0);
        randomCommunity.put("C", 0);
        randomCommunity.put("D", 0);
        randomCommunity.put("E", 0);
        randomCommunity.put("F", 0);
        Partition randomPartition = new Partition(randomCommunity, 1);
        
        double optimalModularity = ModularityCalculator.calculateModularity(graph, optimalPartition, 1.0);
        double randomModularity = ModularityCalculator.calculateModularity(graph, randomPartition, 1.0);
        
        // Optimal partition should have higher modularity
        assertThat(optimalModularity).isGreaterThan(randomModularity);
    }

    @Test
    @DisplayName("deltaModularity: moving to better community returns positive delta")
    void testDeltaModularity_movingToBetterCommunity_positiveDelta() {
        // Two cliques connected by one edge
        Map<String, Map<String, Double>> graph = new HashMap<>();
        
        // Clique 1: A, B
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 1.0);
        graph.put("A", edgesA);
        Map<String, Double> edgesB = new HashMap<>();
        edgesB.put("A", 1.0);
        edgesB.put("C", 1.0); // Bridge
        graph.put("B", edgesB);
        
        // Clique 2: C, D
        Map<String, Double> edgesC = new HashMap<>();
        edgesC.put("B", 1.0);
        edgesC.put("D", 1.0);
        graph.put("C", edgesC);
        Map<String, Double> edgesD = new HashMap<>();
        edgesD.put("C", 1.0);
        graph.put("D", edgesD);
        
        // Initial partition: A,B in community 0; C,D in community 1
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        nodeCommunity.put("C", 1);
        nodeCommunity.put("D", 1);
        Partition partition = new Partition(nodeCommunity, 2);
        
        // Moving C to community 0 (with A and B) should have positive delta
        // since C has more connections to B than to D
        double delta = ModularityCalculator.deltaModularity("C", 0, graph, partition, 1.0);
        
        assertThat(delta).isPositive();
    }

    @Test
    @DisplayName("deltaModularity: moving to worse community returns negative delta")
    void testDeltaModularity_movingToWorseCommunity_negativeDelta() {
        // Two cliques connected by one edge
        Map<String, Map<String, Double>> graph = new HashMap<>();
        
        // Clique 1: A, B
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 1.0);
        graph.put("A", edgesA);
        Map<String, Double> edgesB = new HashMap<>();
        edgesB.put("A", 1.0);
        edgesB.put("C", 1.0); // Bridge
        graph.put("B", edgesB);
        
        // Clique 2: C, D
        Map<String, Double> edgesC = new HashMap<>();
        edgesC.put("B", 1.0);
        edgesC.put("D", 1.0);
        graph.put("C", edgesC);
        Map<String, Double> edgesD = new HashMap<>();
        edgesD.put("C", 1.0);
        graph.put("D", edgesD);
        
        // Initial partition: A in community 0; B,C,D in community 1
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 1);
        nodeCommunity.put("C", 1);
        nodeCommunity.put("D", 1);
        Partition partition = new Partition(nodeCommunity, 2);
        
        // Moving A to community 1 should have negative delta
        // since A is strongly connected to B but weakly to C
        double delta = ModularityCalculator.deltaModularity("A", 1, graph, partition, 1.0);
        
        assertThat(delta).isNegative();
    }

    @Test
    @DisplayName("deltaModularity: staying in same community returns approximately zero delta")
    void testDeltaModularity_stayingInSameCommunity_zeroDelta() {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> edgesA = new HashMap<>();
        edgesA.put("B", 1.0);
        graph.put("A", edgesA);
        Map<String, Double> edgesB = new HashMap<>();
        edgesB.put("A", 1.0);
        graph.put("B", edgesB);
        
        // Both nodes in community 0
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        Partition partition = new Partition(nodeCommunity, 1);
        
        // Moving A to community 0 (same community) should have delta ≈ 0
        double delta = ModularityCalculator.deltaModularity("A", 0, graph, partition, 1.0);
        
        assertThat(delta).isCloseTo(0.0, org.assertj.core.data.Offset.offset(1e-10));
    }

    @Test
    @DisplayName("calculateModularity: empty graph returns 0")
    void testCalculateModularity_emptyGraph_returnsZero() {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Set<String> nodes = new HashSet<>();
        Partition partition = new Partition(nodes);
        
        double modularity = ModularityCalculator.calculateModularity(graph, partition, 1.0);
        
        assertThat(modularity).isEqualTo(0.0);
    }
}