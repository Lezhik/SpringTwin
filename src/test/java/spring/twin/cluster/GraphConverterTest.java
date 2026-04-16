package spring.twin.cluster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spring.twin.scan.LinkDetails;
import spring.twin.scan.LinkType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GraphConverter}.
 *
 * <p>Tests conversion of directed dependency graphs to undirected weighted graphs,
 * node collection, and edge weight calculations.
 */
class GraphConverterTest {

    private GraphConverter converter;

    @BeforeEach
    void setUp() {
        converter = new GraphConverter();
    }

    // ========================================
    // toUndirectedWeightedGraph tests
    // ========================================

    @Test
    void testToUndirectedWeightedGraph_emptyGraph_returnsEmptyMap() {
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();

        Map<String, Map<String, Double>> result = converter.toUndirectedWeightedGraph(dependencyGraph);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testToUndirectedWeightedGraph_singleNodeNoEdges_returnsNodeWithEmptyNeighbors() {
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        dependencyGraph.put("com.example.ClassA", new HashMap<>());

        Map<String, Map<String, Double>> result = converter.toUndirectedWeightedGraph(dependencyGraph);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("com.example.ClassA"));
        assertTrue(result.get("com.example.ClassA").isEmpty());
    }

    @Test
    void testToUndirectedWeightedGraph_oneDirectionEdge_createsUndirectedEdge() {
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        Map<String, Set<LinkDetails>> innerMap = new HashMap<>();
        Set<LinkDetails> links = new HashSet<>();
        links.add(LinkDetails.of(LinkType.FIELD));
        innerMap.put("com.example.ClassB", links);
        dependencyGraph.put("com.example.ClassA", innerMap);

        Map<String, Map<String, Double>> result = converter.toUndirectedWeightedGraph(dependencyGraph);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("com.example.ClassA"));
        assertTrue(result.containsKey("com.example.ClassB"));
        
        // Both nodes should have edge to each other
        assertTrue(result.get("com.example.ClassA").containsKey("com.example.ClassB"));
        assertTrue(result.get("com.example.ClassB").containsKey("com.example.ClassA"));
        assertEquals(1.0, result.get("com.example.ClassA").get("com.example.ClassB"));
        assertEquals(1.0, result.get("com.example.ClassB").get("com.example.ClassA"));
    }

    @Test
    void testToUndirectedWeightedGraph_bidirectionalEdges_mergesWeights() {
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        
        // A -> B with FIELD link
        Map<String, Set<LinkDetails>> innerMapA = new HashMap<>();
        Set<LinkDetails> linksA = new HashSet<>();
        linksA.add(LinkDetails.of(LinkType.FIELD));
        innerMapA.put("com.example.ClassB", linksA);
        dependencyGraph.put("com.example.ClassA", innerMapA);
        
        // B -> A with METHOD link
        Map<String, Set<LinkDetails>> innerMapB = new HashMap<>();
        Set<LinkDetails> linksB = new HashSet<>();
        linksB.add(LinkDetails.of(LinkType.METHOD));
        innerMapB.put("com.example.ClassA", linksB);
        dependencyGraph.put("com.example.ClassB", innerMapB);

        Map<String, Map<String, Double>> result = converter.toUndirectedWeightedGraph(dependencyGraph);

        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Edge weight should be 2.0 (sum of both directions)
        assertEquals(2.0, result.get("com.example.ClassA").get("com.example.ClassB"));
        assertEquals(2.0, result.get("com.example.ClassB").get("com.example.ClassA"));
    }

    @Test
    void testToUndirectedWeightedGraph_multipleLinkTypes_aggregatesWeight() {
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        
        // A -> B with multiple link types
        Map<String, Set<LinkDetails>> innerMap = new HashMap<>();
        Set<LinkDetails> links = new HashSet<>();
        links.add(LinkDetails.of(LinkType.FIELD));
        links.add(LinkDetails.of(LinkType.METHOD));
        innerMap.put("com.example.ClassB", links);
        dependencyGraph.put("com.example.ClassA", innerMap);

        Map<String, Map<String, Double>> result = converter.toUndirectedWeightedGraph(dependencyGraph);

        assertNotNull(result);
        
        // Edge weight should be 2.0 (two different link types)
        assertEquals(2.0, result.get("com.example.ClassA").get("com.example.ClassB"));
        assertEquals(2.0, result.get("com.example.ClassB").get("com.example.ClassA"));
    }

    @Test
    void testToUndirectedWeightedGraph_selfReference_notIncluded() {
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        
        // A -> A (self-reference)
        Map<String, Set<LinkDetails>> innerMap = new HashMap<>();
        Set<LinkDetails> links = new HashSet<>();
        links.add(LinkDetails.of(LinkType.FIELD));
        innerMap.put("com.example.ClassA", links);
        dependencyGraph.put("com.example.ClassA", innerMap);

        Map<String, Map<String, Double>> result = converter.toUndirectedWeightedGraph(dependencyGraph);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("com.example.ClassA"));
        
        // Self-reference should not create a loop
        assertFalse(result.get("com.example.ClassA").containsKey("com.example.ClassA"));
        assertTrue(result.get("com.example.ClassA").isEmpty());
    }

    // ========================================
    // collectNodes tests
    // ========================================

    @Test
    void testCollectNodes_emptyGraph_returnsEmptySet() {
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();

        Set<String> result = converter.collectNodes(dependencyGraph);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCollectNodes_includesKeysAndValues() {
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        
        // A -> B
        Map<String, Set<LinkDetails>> innerMapA = new HashMap<>();
        Set<LinkDetails> linksA = new HashSet<>();
        linksA.add(LinkDetails.of(LinkType.FIELD));
        innerMapA.put("com.example.ClassB", linksA);
        dependencyGraph.put("com.example.ClassA", innerMapA);
        
        // B -> C
        Map<String, Set<LinkDetails>> innerMapB = new HashMap<>();
        Set<LinkDetails> linksB = new HashSet<>();
        linksB.add(LinkDetails.of(LinkType.FIELD));
        innerMapB.put("com.example.ClassC", linksB);
        dependencyGraph.put("com.example.ClassB", innerMapB);

        Set<String> result = converter.collectNodes(dependencyGraph);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("com.example.ClassA"));
        assertTrue(result.contains("com.example.ClassB"));
        assertTrue(result.contains("com.example.ClassC"));
    }

    @Test
    void testCollectNodes_includesOnlyTargetNodes() {
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        
        // A -> B (B is only a target, never a key)
        Map<String, Set<LinkDetails>> innerMapA = new HashMap<>();
        Set<LinkDetails> linksA = new HashSet<>();
        linksA.add(LinkDetails.of(LinkType.FIELD));
        innerMapA.put("com.example.ClassB", linksA);
        dependencyGraph.put("com.example.ClassA", innerMapA);

        Set<String> result = converter.collectNodes(dependencyGraph);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("com.example.ClassA"));
        assertTrue(result.contains("com.example.ClassB"));
    }

    // ========================================
    // totalEdgeWeight tests
    // ========================================

    @Test
    void testTotalEdgeWeight_emptyGraph_returnsZero() {
        Map<String, Map<String, Double>> graph = new HashMap<>();

        double result = converter.totalEdgeWeight(graph);

        assertEquals(0.0, result, 0.0001);
    }

    @Test
    void testTotalEdgeWeight_singleEdge_returnsWeight() {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        Map<String, Double> innerMap = new HashMap<>();
        innerMap.put("com.example.ClassB", 2.5);
        graph.put("com.example.ClassA", innerMap);
        
        // Add reverse edge (undirected graph representation)
        Map<String, Double> innerMapB = new HashMap<>();
        innerMapB.put("com.example.ClassA", 2.5);
        graph.put("com.example.ClassB", innerMapB);

        double result = converter.totalEdgeWeight(graph);

        assertEquals(2.5, result, 0.0001);
    }

    @Test
    void testTotalEdgeWeight_multipleEdges_returnsSum() {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        
        // A <-> B with weight 1.0
        Map<String, Double> innerMapA = new HashMap<>();
        innerMapA.put("com.example.ClassB", 1.0);
        graph.put("com.example.ClassA", innerMapA);
        
        // B <-> A with weight 1.0
        Map<String, Double> innerMapB = new HashMap<>();
        innerMapB.put("com.example.ClassA", 1.0);
        innerMapB.put("com.example.ClassC", 2.0); // B <-> C with weight 2.0
        graph.put("com.example.ClassB", innerMapB);
        
        // C <-> B with weight 2.0
        Map<String, Double> innerMapC = new HashMap<>();
        innerMapC.put("com.example.ClassB", 2.0);
        graph.put("com.example.ClassC", innerMapC);

        double result = converter.totalEdgeWeight(graph);

        // Total should be: 1.0 (A-B) + 2.0 (B-C) = 3.0
        // Each edge counted once
        assertEquals(3.0, result, 0.0001);
    }
}