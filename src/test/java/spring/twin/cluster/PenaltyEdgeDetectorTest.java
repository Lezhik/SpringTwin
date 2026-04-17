package spring.twin.cluster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import spring.twin.scan.LinkDetails;
import spring.twin.scan.LinkType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PenaltyEdgeDetector}.
 */
class PenaltyEdgeDetectorTest {

    @Test
    @DisplayName("No cross-cluster edges returns empty map")
    void testDetect_noCrossClusterEdges_returnsEmptyMap() {
        PenaltyEdgeDetector detector = new PenaltyEdgeDetector();

        // Create partition: A and B in cluster 0
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        Partition partition = new Partition(nodeCommunity, 1);

        // Dependencies: A -> B (both in same cluster)
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        Map<String, Set<LinkDetails>> aDeps = new HashMap<>();
        aDeps.put("B", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "bField"))));
        dependencyGraph.put("A", aDeps);
        dependencyGraph.put("B", new HashMap<>());

        Map<String, Set<String>> result = detector.detect(partition, dependencyGraph);

        assertTrue(result.isEmpty(), "Should return empty map when all dependencies are within clusters");
    }

    @Test
    @DisplayName("Cross-cluster edge is detected as penalty edge")
    void testDetect_crossClusterEdges_detected() {
        PenaltyEdgeDetector detector = new PenaltyEdgeDetector();

        // Create partition: A in cluster 0, B in cluster 1
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 1);
        Partition partition = new Partition(nodeCommunity, 2);

        // Dependencies: A -> B (cross-cluster)
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        Map<String, Set<LinkDetails>> aDeps = new HashMap<>();
        aDeps.put("B", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "bField"))));
        dependencyGraph.put("A", aDeps);
        dependencyGraph.put("B", new HashMap<>());

        Map<String, Set<String>> result = detector.detect(partition, dependencyGraph);

        assertFalse(result.isEmpty(), "Should detect cross-cluster edges");
        assertTrue(result.containsKey("A"), "Should contain source class A");
        assertEquals(Set.of("B"), result.get("A"), "Should contain target class B for A");
    }

    @Test
    @DisplayName("Multiple cross-cluster edges are all detected")
    void testDetect_multipleCrossClusterEdges_allDetected() {
        PenaltyEdgeDetector detector = new PenaltyEdgeDetector();

        // Create partition: A in cluster 0, B and C in cluster 1, D in cluster 2
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 1);
        nodeCommunity.put("C", 1);
        nodeCommunity.put("D", 2);
        Partition partition = new Partition(nodeCommunity, 3);

        // Dependencies: A -> B, A -> C, A -> D (all cross-cluster from A)
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        Map<String, Set<LinkDetails>> aDeps = new HashMap<>();
        aDeps.put("B", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "bField"))));
        aDeps.put("C", new HashSet<>(Set.of(LinkDetails.of(LinkType.METHOD, "getC()"))));
        aDeps.put("D", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "dField"))));
        dependencyGraph.put("A", aDeps);
        dependencyGraph.put("B", new HashMap<>());
        dependencyGraph.put("C", new HashMap<>());
        dependencyGraph.put("D", new HashMap<>());

        Map<String, Set<String>> result = detector.detect(partition, dependencyGraph);

        assertFalse(result.isEmpty(), "Should detect cross-cluster edges");
        assertTrue(result.containsKey("A"), "Should contain source class A");
        Set<String> aTargets = result.get("A");
        assertEquals(3, aTargets.size(), "Should detect all three cross-cluster dependencies from A");
        assertTrue(aTargets.contains("B"), "Should contain target B");
        assertTrue(aTargets.contains("C"), "Should contain target C");
        assertTrue(aTargets.contains("D"), "Should contain target D");
    }

    @Test
    @DisplayName("Empty dependency graph returns empty map")
    void testDetect_emptyGraph_returnsEmptyMap() {
        PenaltyEdgeDetector detector = new PenaltyEdgeDetector();

        // Create empty partition
        Map<String, Integer> nodeCommunity = new HashMap<>();
        Partition partition = new Partition(nodeCommunity, 0);

        // Empty dependency graph
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();

        Map<String, Set<String>> result = detector.detect(partition, dependencyGraph);

        assertTrue(result.isEmpty(), "Should return empty map for empty graph");
    }

    @Test
    @DisplayName("Single cluster has no penalty edges")
    void testDetect_singleCluster_noPenaltyEdges() {
        PenaltyEdgeDetector detector = new PenaltyEdgeDetector();

        // Create partition: A, B, C all in single cluster 0
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 0);
        nodeCommunity.put("C", 0);
        Partition partition = new Partition(nodeCommunity, 1);

        // Dependencies: A -> B, A -> C, B -> C (all within same cluster)
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        
        Map<String, Set<LinkDetails>> aDeps = new HashMap<>();
        aDeps.put("B", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "bField"))));
        aDeps.put("C", new HashSet<>(Set.of(LinkDetails.of(LinkType.METHOD, "getC()"))));
        dependencyGraph.put("A", aDeps);
        
        Map<String, Set<LinkDetails>> bDeps = new HashMap<>();
        bDeps.put("C", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "cField"))));
        dependencyGraph.put("B", bDeps);
        
        dependencyGraph.put("C", new HashMap<>());

        Map<String, Set<String>> result = detector.detect(partition, dependencyGraph);

        assertTrue(result.isEmpty(), "Should return empty map when all classes are in single cluster");
    }

    @Test
    @DisplayName("Bidirectional cross-cluster edges detected in both directions")
    void testDetect_bidirectionalCrossCluster_bothDirections() {
        PenaltyEdgeDetector detector = new PenaltyEdgeDetector();

        // Create partition: A in cluster 0, B in cluster 1
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("A", 0);
        nodeCommunity.put("B", 1);
        Partition partition = new Partition(nodeCommunity, 2);

        // Dependencies: A -> B and B -> A (bidirectional cross-cluster)
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        
        Map<String, Set<LinkDetails>> aDeps = new HashMap<>();
        aDeps.put("B", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "bField"))));
        dependencyGraph.put("A", aDeps);
        
        Map<String, Set<LinkDetails>> bDeps = new HashMap<>();
        bDeps.put("A", new HashSet<>(Set.of(LinkDetails.of(LinkType.METHOD, "callA()"))));
        dependencyGraph.put("B", bDeps);

        Map<String, Set<String>> result = detector.detect(partition, dependencyGraph);

        assertFalse(result.isEmpty(), "Should detect cross-cluster edges");
        assertTrue(result.containsKey("A"), "Should contain source class A");
        assertTrue(result.containsKey("B"), "Should contain source class B");
        assertEquals(Set.of("B"), result.get("A"), "A should have B as penalty edge target");
        assertEquals(Set.of("A"), result.get("B"), "B should have A as penalty edge target");
    }
}