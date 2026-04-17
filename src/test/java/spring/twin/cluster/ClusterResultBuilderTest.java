package spring.twin.cluster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import spring.twin.scan.LinkDetails;
import spring.twin.scan.LinkType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ClusterResultBuilder}, {@link ClusterRecord}, and {@link ClusterResult}.
 */
class ClusterResultBuilderTest {

    @Test
    @DisplayName("build creates ClusterResult with clusters")
    void testBuild_createsClusterResultWithClusters() {
        ClusterResultBuilder builder = new ClusterResultBuilder();

        // Create partition: A and B in cluster 0, C in cluster 1
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("com.example.ServiceA", 0);
        nodeCommunity.put("com.example.ServiceB", 0);
        nodeCommunity.put("com.example.Controller", 1);
        Partition partition = new Partition(nodeCommunity, 2);

        // Dependency graph
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        dependencyGraph.put("com.example.ServiceA", new HashMap<>());
        dependencyGraph.put("com.example.ServiceB", new HashMap<>());
        dependencyGraph.put("com.example.Controller", new HashMap<>());

        ClusterMetricsCalculator metricsCalculator = new ClusterMetricsCalculator();
        PenaltyEdgeDetector penaltyDetector = new PenaltyEdgeDetector();

        ClusterResult result = builder.build(partition, dependencyGraph, metricsCalculator, penaltyDetector);

        assertNotNull(result, "Result should not be null");
        assertNotNull(result.clusters(), "Clusters list should not be null");
        assertEquals(2, result.clusters().size(), "Should have 2 clusters");
    }

    @Test
    @DisplayName("build creates ClusterResult with penalty edges")
    void testBuild_createsClusterResultWithPenaltyEdges() {
        ClusterResultBuilder builder = new ClusterResultBuilder();

        // Create partition: A in cluster 0, B in cluster 1 (cross-cluster dependency)
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("com.example.ServiceA", 0);
        nodeCommunity.put("com.example.ServiceB", 1);
        Partition partition = new Partition(nodeCommunity, 2);

        // Dependency graph with cross-cluster dependency: A -> B
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        Map<String, Set<LinkDetails>> aDeps = new HashMap<>();
        aDeps.put("com.example.ServiceB", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "serviceB"))));
        dependencyGraph.put("com.example.ServiceA", aDeps);
        dependencyGraph.put("com.example.ServiceB", new HashMap<>());

        ClusterMetricsCalculator metricsCalculator = new ClusterMetricsCalculator();
        PenaltyEdgeDetector penaltyDetector = new PenaltyEdgeDetector();

        ClusterResult result = builder.build(partition, dependencyGraph, metricsCalculator, penaltyDetector);

        assertNotNull(result, "Result should not be null");
        assertNotNull(result.penaltyEdges(), "Penalty edges map should not be null");
        assertFalse(result.penaltyEdges().isEmpty(), "Should have penalty edges for cross-cluster dependencies");
        assertTrue(result.penaltyEdges().containsKey("com.example.ServiceA"), "Should contain source class");
        assertTrue(result.penaltyEdges().get("com.example.ServiceA").contains("com.example.ServiceB"), "Should contain target class");
    }

    @Test
    @DisplayName("build sorts clusters by id")
    void testBuild_clustersSortedById() {
        ClusterResultBuilder builder = new ClusterResultBuilder();

        // Create partition: classes in multiple clusters with non-sequential ids
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("com.example.ZClass", 2);
        nodeCommunity.put("com.example.AClass", 0);
        nodeCommunity.put("com.example.MClass", 1);
        Partition partition = new Partition(nodeCommunity, 3);

        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        dependencyGraph.put("com.example.ZClass", new HashMap<>());
        dependencyGraph.put("com.example.AClass", new HashMap<>());
        dependencyGraph.put("com.example.MClass", new HashMap<>());

        ClusterMetricsCalculator metricsCalculator = new ClusterMetricsCalculator();
        PenaltyEdgeDetector penaltyDetector = new PenaltyEdgeDetector();

        ClusterResult result = builder.build(partition, dependencyGraph, metricsCalculator, penaltyDetector);

        List<ClusterRecord> clusters = result.clusters();
        assertEquals(3, clusters.size(), "Should have 3 clusters");

        // Verify clusters are sorted by id
        assertEquals("cluster-0", clusters.get(0).id(), "First cluster should have lowest id");
        assertEquals("cluster-1", clusters.get(1).id(), "Second cluster should have middle id");
        assertEquals("com.example.AClass", clusters.get(0).classes().get(0), "First cluster should contain AClass");
        assertEquals("com.example.MClass", clusters.get(1).classes().get(0), "Second cluster should contain MClass");
        assertEquals("com.example.ZClass", clusters.get(2).classes().get(0), "Third cluster should contain ZClass");
    }

    @Test
    @DisplayName("build sorts classes alphabetically in each cluster")
    void testBuild_classesSortedAlphabetically() {
        ClusterResultBuilder builder = new ClusterResultBuilder();

        // Create partition: multiple classes in same cluster, added in non-alphabetical order
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("com.example.Zebra", 0);
        nodeCommunity.put("com.example.Alpha", 0);
        nodeCommunity.put("com.example.Mike", 0);
        nodeCommunity.put("com.example.Beta", 0);
        Partition partition = new Partition(nodeCommunity, 1);

        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        dependencyGraph.put("com.example.Zebra", new HashMap<>());
        dependencyGraph.put("com.example.Alpha", new HashMap<>());
        dependencyGraph.put("com.example.Mike", new HashMap<>());
        dependencyGraph.put("com.example.Beta", new HashMap<>());

        ClusterMetricsCalculator metricsCalculator = new ClusterMetricsCalculator();
        PenaltyEdgeDetector penaltyDetector = new PenaltyEdgeDetector();

        ClusterResult result = builder.build(partition, dependencyGraph, metricsCalculator, penaltyDetector);

        assertEquals(1, result.clusters().size(), "Should have 1 cluster");
        List<String> classes = result.clusters().get(0).classes();
        assertEquals(4, classes.size(), "Should have 4 classes");

        // Verify classes are sorted alphabetically
        assertEquals("com.example.Alpha", classes.get(0), "First class should be Alpha");
        assertEquals("com.example.Beta", classes.get(1), "Second class should be Beta");
        assertEquals("com.example.Mike", classes.get(2), "Third class should be Mike");
        assertEquals("com.example.Zebra", classes.get(3), "Fourth class should be Zebra");
    }

    @Test
    @DisplayName("build with empty partition returns empty clusters list")
    void testBuild_emptyPartition_returnsEmptyClusters() {
        ClusterResultBuilder builder = new ClusterResultBuilder();

        // Create empty partition
        Map<String, Integer> nodeCommunity = new HashMap<>();
        Partition partition = new Partition(nodeCommunity, 0);

        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();

        ClusterMetricsCalculator metricsCalculator = new ClusterMetricsCalculator();
        PenaltyEdgeDetector penaltyDetector = new PenaltyEdgeDetector();

        ClusterResult result = builder.build(partition, dependencyGraph, metricsCalculator, penaltyDetector);

        assertNotNull(result, "Result should not be null");
        assertNotNull(result.clusters(), "Clusters list should not be null");
        assertTrue(result.clusters().isEmpty(), "Clusters list should be empty");
        assertNotNull(result.penaltyEdges(), "Penalty edges map should not be null");
        assertTrue(result.penaltyEdges().isEmpty(), "Penalty edges should be empty");
    }

    @Test
    @DisplayName("build with single community creates single cluster")
    void testBuild_singleCommunity_singleCluster() {
        ClusterResultBuilder builder = new ClusterResultBuilder();

        // Create partition: all classes in single community
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("com.example.ClassA", 0);
        nodeCommunity.put("com.example.ClassB", 0);
        nodeCommunity.put("com.example.ClassC", 0);
        Partition partition = new Partition(nodeCommunity, 1);

        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        dependencyGraph.put("com.example.ClassA", new HashMap<>());
        dependencyGraph.put("com.example.ClassB", new HashMap<>());
        dependencyGraph.put("com.example.ClassC", new HashMap<>());

        ClusterMetricsCalculator metricsCalculator = new ClusterMetricsCalculator();
        PenaltyEdgeDetector penaltyDetector = new PenaltyEdgeDetector();

        ClusterResult result = builder.build(partition, dependencyGraph, metricsCalculator, penaltyDetector);

        assertEquals(1, result.clusters().size(), "Should have exactly 1 cluster");
        assertEquals("cluster-0", result.clusters().get(0).id(), "Cluster should have id cluster-0");
        assertEquals(3, result.clusters().get(0).classes().size(), "Cluster should have 3 classes");
        assertTrue(result.penaltyEdges().isEmpty(), "No penalty edges in single cluster");
    }

    @Test
    @DisplayName("build formats cluster ids correctly as cluster-{number}")
    void testBuild_clusterIdsFormattedCorrectly() {
        ClusterResultBuilder builder = new ClusterResultBuilder();

        // Create partition with multiple clusters
        Map<String, Integer> nodeCommunity = new HashMap<>();
        nodeCommunity.put("com.example.Class0", 0);
        nodeCommunity.put("com.example.Class1", 1);
        nodeCommunity.put("com.example.Class5", 5);
        nodeCommunity.put("com.example.Class10", 10);
        Partition partition = new Partition(nodeCommunity, 11);

        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        dependencyGraph.put("com.example.Class0", new HashMap<>());
        dependencyGraph.put("com.example.Class1", new HashMap<>());
        dependencyGraph.put("com.example.Class5", new HashMap<>());
        dependencyGraph.put("com.example.Class10", new HashMap<>());

        ClusterMetricsCalculator metricsCalculator = new ClusterMetricsCalculator();
        PenaltyEdgeDetector penaltyDetector = new PenaltyEdgeDetector();

        ClusterResult result = builder.build(partition, dependencyGraph, metricsCalculator, penaltyDetector);

        List<ClusterRecord> clusters = result.clusters();
        assertEquals(4, clusters.size(), "Should have 4 clusters");

        // Verify all cluster ids follow the format "cluster-{number}"
        for (ClusterRecord cluster : clusters) {
            String id = cluster.id();
            assertNotNull(id, "Cluster id should not be null");
            assertTrue(id.startsWith("cluster-"), "Cluster id should start with 'cluster-': " + id);
            String numberPart = id.substring("cluster-".length());
            assertTrue(numberPart.matches("\\d+"), "Cluster id should end with a number: " + id);
        }

        // Verify specific expected ids
        assertEquals("cluster-0", clusters.get(0).id(), "First cluster id should be cluster-0");
        assertEquals("cluster-1", clusters.get(1).id(), "Second cluster id should be cluster-1");
        assertEquals("cluster-5", clusters.get(2).id(), "Third cluster id should be cluster-5");
        assertEquals("cluster-10", clusters.get(3).id(), "Fourth cluster id should be cluster-10");
    }
}