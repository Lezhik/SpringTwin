package spring.twin.cluster.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spring.twin.cluster.ClusterParams;
import spring.twin.cluster.ClusterService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-End tests for the cluster command.
 * <p>
 * Tests accept parameters for the main method and verify the output JSON file.
 */
@SpringBootTest
class ClusterE2eTest {

    @TempDir
    Path tempDir;

    @Autowired
    ClusterService clusterService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    /**
     * Scenario: Clustering a simple graph with 3 classes in one cluster.
     * Input dependencies.json contains 3 classes with strong internal links.
     * Verification: output JSON contains 1 cluster, all classes in it, cohesion > 0.5, penaltyEdges empty.
     */
    @Test
    void shouldClusterSimpleGraph() throws IOException {
        // Given: dependencies.json with 3 strongly connected classes
        Map<String, Map<String, List<Map<String, String>>>> deps = new LinkedHashMap<>();

        Map<String, List<Map<String, String>>> classAEdges = new LinkedHashMap<>();
        classAEdges.put("com.example.ClassB", List.of(Map.of("type", "FIELD", "details", "classB")));
        classAEdges.put("com.example.ClassC", List.of(Map.of("type", "FIELD", "details", "classC")));
        deps.put("com.example.ClassA", classAEdges);

        Map<String, List<Map<String, String>>> classBEdges = new LinkedHashMap<>();
        classBEdges.put("com.example.ClassA", List.of(Map.of("type", "FIELD", "details", "classA")));
        classBEdges.put("com.example.ClassC", List.of(Map.of("type", "FIELD", "details", "classC")));
        deps.put("com.example.ClassB", classBEdges);

        Map<String, List<Map<String, String>>> classCEdges = new LinkedHashMap<>();
        classCEdges.put("com.example.ClassA", List.of(Map.of("type", "FIELD", "details", "classA")));
        classCEdges.put("com.example.ClassB", List.of(Map.of("type", "FIELD", "details", "classB")));
        deps.put("com.example.ClassC", classCEdges);

        Path depsFile = tempDir.resolve("dependencies.json");
        Path outputFile = tempDir.resolve("clusters.json");
        objectMapper.writeValue(depsFile.toFile(), deps);

        // When: run clustering
        ClusterParams params = new ClusterParams(depsFile, outputFile, 1.5);
        clusterService.execute(params);

        // Then: verify output
        Map<String, Object> result = objectMapper.readValue(outputFile.toFile(), Map.class);

        List<Map<String, Object>> clusters = (List<Map<String, Object>>) result.get("clusters");
        Map<String, Set<String>> penaltyEdges = (Map<String, Set<String>>) result.get("penaltyEdges");

        assertNotNull(clusters);
        assertEquals(1, clusters.size(), "Should have exactly 1 cluster");

        Map<String, Object> cluster = clusters.get(0);
        List<String> classes = (List<String>) cluster.get("classes");
        assertNotNull(classes);
        assertEquals(3, classes.size(), "Cluster should contain all 3 classes");
        assertTrue(classes.contains("com.example.ClassA"));
        assertTrue(classes.contains("com.example.ClassB"));
        assertTrue(classes.contains("com.example.ClassC"));

        Map<String, Object> metrics = (Map<String, Object>) cluster.get("metrics");
        assertNotNull(metrics);
        double cohesion = ((Number) metrics.get("cohesion")).doubleValue();
        assertTrue(cohesion > 0.5, "Cohesion should be > 0.5, but was: " + cohesion);

        assertNotNull(penaltyEdges);
        assertTrue(penaltyEdges.isEmpty(), "Penalty edges should be empty for single cluster");
    }

    /**
     * Scenario: Clustering a graph with two clear communities.
     * Input dependencies.json contains 2 groups of 3 classes each with internal links
     * and 1 link between groups.
     * Verification: output JSON contains 2 clusters, penaltyEdges contains the cross-cluster link.
     */
    @Test
    void shouldClusterTwoCommunities() throws IOException {
        // Given: dependencies.json with 2 communities (3 classes each) and 1 cross-link
        Map<String, Map<String, List<Map<String, String>>>> deps = new LinkedHashMap<>();

        // Community 1: A, B, C (strongly connected)
        Map<String, List<Map<String, String>>> classAEdges = new LinkedHashMap<>();
        classAEdges.put("com.example.ClassB", List.of(Map.of("type", "FIELD", "details", "classB")));
        classAEdges.put("com.example.ClassC", List.of(Map.of("type", "FIELD", "details", "classC")));
        deps.put("com.example.ClassA", classAEdges);

        Map<String, List<Map<String, String>>> classBEdges = new LinkedHashMap<>();
        classBEdges.put("com.example.ClassA", List.of(Map.of("type", "FIELD", "details", "classA")));
        classBEdges.put("com.example.ClassC", List.of(Map.of("type", "FIELD", "details", "classC")));
        deps.put("com.example.ClassB", classBEdges);

        Map<String, List<Map<String, String>>> classCEdges = new LinkedHashMap<>();
        classCEdges.put("com.example.ClassA", List.of(Map.of("type", "FIELD", "details", "classA")));
        classCEdges.put("com.example.ClassB", List.of(Map.of("type", "FIELD", "details", "classB")));
        // Cross-community link: C -> D
        classCEdges.put("com.example.ClassD", List.of(Map.of("type", "FIELD", "details", "classD")));
        deps.put("com.example.ClassC", classCEdges);

        // Community 2: D, E, F (strongly connected)
        Map<String, List<Map<String, String>>> classDEdges = new LinkedHashMap<>();
        classDEdges.put("com.example.ClassE", List.of(Map.of("type", "FIELD", "details", "classE")));
        classDEdges.put("com.example.ClassF", List.of(Map.of("type", "FIELD", "details", "classF")));
        deps.put("com.example.ClassD", classDEdges);

        Map<String, List<Map<String, String>>> classEEdges = new LinkedHashMap<>();
        classEEdges.put("com.example.ClassD", List.of(Map.of("type", "FIELD", "details", "classD")));
        classEEdges.put("com.example.ClassF", List.of(Map.of("type", "FIELD", "details", "classF")));
        deps.put("com.example.ClassE", classEEdges);

        Map<String, List<Map<String, String>>> classFEdges = new LinkedHashMap<>();
        classFEdges.put("com.example.ClassD", List.of(Map.of("type", "FIELD", "details", "classD")));
        classFEdges.put("com.example.ClassE", List.of(Map.of("type", "FIELD", "details", "classE")));
        deps.put("com.example.ClassF", classFEdges);

        Path depsFile = tempDir.resolve("dependencies.json");
        Path outputFile = tempDir.resolve("clusters.json");
        objectMapper.writeValue(depsFile.toFile(), deps);

        // When: run clustering
        ClusterParams params = new ClusterParams(depsFile, outputFile, 1.5);
        clusterService.execute(params);

        // Then: verify output
        Map<String, Object> result = objectMapper.readValue(outputFile.toFile(), Map.class);

        List<Map<String, Object>> clusters = (List<Map<String, Object>>) result.get("clusters");
        Map<String, List<String>> penaltyEdges = (Map<String, List<String>>) result.get("penaltyEdges");

        assertNotNull(clusters);
        assertEquals(2, clusters.size(), "Should have exactly 2 clusters");

        // Verify penaltyEdges contains cross-cluster link
        assertNotNull(penaltyEdges);
        assertFalse(penaltyEdges.isEmpty(), "Penalty edges should contain cross-cluster links");
        assertTrue(penaltyEdges.containsKey("com.example.ClassC"), "ClassC should have penalty edges");
        assertTrue(penaltyEdges.get("com.example.ClassC").contains("com.example.ClassD"),
                "ClassC should have penalty edge to ClassD");
    }

    /**
     * Scenario: Verify the structure of the output JSON.
     * Verification: presence of fields 'clusters' and 'penaltyEdges',
     * each cluster contains 'id', 'classes', 'metrics' with 'cohesion' and 'coupling'.
     */
    @Test
    void shouldProduceValidClustersJsonStructure() throws IOException {
        // Given: simple dependencies
        Map<String, Map<String, List<Map<String, String>>>> deps = new LinkedHashMap<>();
        deps.put("com.example.ClassA", Map.of());

        Path depsFile = tempDir.resolve("dependencies.json");
        Path outputFile = tempDir.resolve("clusters.json");
        objectMapper.writeValue(depsFile.toFile(), deps);

        // When: run clustering
        ClusterParams params = new ClusterParams(depsFile, outputFile, 1.5);
        clusterService.execute(params);

        // Then: verify structure
        Map<String, Object> result = objectMapper.readValue(outputFile.toFile(), Map.class);

        assertTrue(result.containsKey("clusters"), "Result should contain 'clusters' field");
        assertTrue(result.containsKey("penaltyEdges"), "Result should contain 'penaltyEdges' field");

        List<Map<String, Object>> clusters = (List<Map<String, Object>>) result.get("clusters");
        assertNotNull(clusters);

        for (Map<String, Object> cluster : clusters) {
            assertTrue(cluster.containsKey("id"), "Each cluster should have 'id' field");
            assertTrue(cluster.containsKey("classes"), "Each cluster should have 'classes' field");
            assertTrue(cluster.containsKey("metrics"), "Each cluster should have 'metrics' field");

            Map<String, Object> metrics = (Map<String, Object>) cluster.get("metrics");
            assertNotNull(metrics);
            assertTrue(metrics.containsKey("cohesion"), "Metrics should contain 'cohesion'");
            assertTrue(metrics.containsKey("coupling"), "Metrics should contain 'coupling'");
        }
    }

    /**
     * Scenario: Run with different resolution parameters (0.5 and 5.0).
     * With low resolution there should be fewer clusters, with high - more.
     * Verification: cluster count at resolution=5.0 >= cluster count at resolution=0.5.
     */
    @Test
    void shouldRespectResolutionParameter() throws IOException {
        // Given: dependencies with multiple potential communities
        Map<String, Map<String, List<Map<String, String>>>> deps = new LinkedHashMap<>();

        // Create a graph with 6 classes in 2 loose groups
        for (int i = 1; i <= 6; i++) {
            Map<String, List<Map<String, String>>> edges = new LinkedHashMap<>();
            // Each class connects to the next one (forming a chain)
            if (i < 6) {
                edges.put("com.example.Class" + (i + 1), List.of(Map.of("type", "FIELD", "details", "next")));
            }
            if (i > 1) {
                edges.put("com.example.Class" + (i - 1), List.of(Map.of("type", "FIELD", "details", "prev")));
            }
            deps.put("com.example.Class" + i, edges);
        }

        Path depsFile = tempDir.resolve("dependencies.json");
        objectMapper.writeValue(depsFile.toFile(), deps);

        // When: run with low resolution (0.5) - should produce fewer/larger clusters
        Path outputFileLow = tempDir.resolve("clusters_low.json");
        ClusterParams paramsLow = new ClusterParams(depsFile, outputFileLow, 0.5);
        clusterService.execute(paramsLow);

        Map<String, Object> resultLow = objectMapper.readValue(outputFileLow.toFile(), Map.class);
        List<Map<String, Object>> clustersLow = (List<Map<String, Object>>) resultLow.get("clusters");
        int clusterCountLow = clustersLow.size();

        // When: run with high resolution (5.0) - should produce more/smaller clusters
        Path outputFileHigh = tempDir.resolve("clusters_high.json");
        ClusterParams paramsHigh = new ClusterParams(depsFile, outputFileHigh, 5.0);
        clusterService.execute(paramsHigh);

        Map<String, Object> resultHigh = objectMapper.readValue(outputFileHigh.toFile(), Map.class);
        List<Map<String, Object>> clustersHigh = (List<Map<String, Object>>) resultHigh.get("clusters");
        int clusterCountHigh = clustersHigh.size();

        // Then: high resolution should produce >= clusters than low resolution
        assertTrue(clusterCountHigh >= clusterCountLow,
                "Resolution 5.0 should produce >= clusters than resolution 0.5, " +
                        "but got " + clusterCountHigh + " vs " + clusterCountLow);
    }

    /**
     * Scenario: Graph with a single class and no edges.
     * Verification: output JSON contains 1 cluster with 1 class, cohesion=1.0, coupling=0.0, penaltyEdges empty.
     */
    @Test
    void shouldHandleSingleClassGraph() throws IOException {
        // Given: single class with no dependencies
        Map<String, Map<String, List<Map<String, String>>>> deps = new LinkedHashMap<>();
        deps.put("com.example.SingleClass", Map.of());

        Path depsFile = tempDir.resolve("dependencies.json");
        Path outputFile = tempDir.resolve("clusters.json");
        objectMapper.writeValue(depsFile.toFile(), deps);

        // When: run clustering
        ClusterParams params = new ClusterParams(depsFile, outputFile, 1.5);
        clusterService.execute(params);

        // Then: verify output
        Map<String, Object> result = objectMapper.readValue(outputFile.toFile(), Map.class);

        List<Map<String, Object>> clusters = (List<Map<String, Object>>) result.get("clusters");
        Map<String, Set<String>> penaltyEdges = (Map<String, Set<String>>) result.get("penaltyEdges");

        assertNotNull(clusters);
        assertEquals(1, clusters.size(), "Should have exactly 1 cluster");

        Map<String, Object> cluster = clusters.get(0);
        List<String> classes = (List<String>) cluster.get("classes");
        assertNotNull(classes);
        assertEquals(1, classes.size(), "Cluster should contain 1 class");
        assertEquals("com.example.SingleClass", classes.get(0));

        Map<String, Object> metrics = (Map<String, Object>) cluster.get("metrics");
        assertNotNull(metrics);
        double cohesion = ((Number) metrics.get("cohesion")).doubleValue();
        double coupling = ((Number) metrics.get("coupling")).doubleValue();

        assertEquals(1.0, cohesion, 0.001, "Cohesion should be 1.0 for single class");
        assertEquals(0.0, coupling, 0.001, "Coupling should be 0.0 for single class");

        assertNotNull(penaltyEdges);
        assertTrue(penaltyEdges.isEmpty(), "Penalty edges should be empty");
    }

    /**
     * Scenario: Empty dependencies.json (no classes).
     * Verification: output JSON contains empty clusters array and empty penaltyEdges.
     */
    @Test
    void shouldHandleEmptyGraph() throws IOException {
        // Given: empty dependencies
        Map<String, Map<String, List<Map<String, String>>>> deps = new LinkedHashMap<>();

        Path depsFile = tempDir.resolve("dependencies.json");
        Path outputFile = tempDir.resolve("clusters.json");
        objectMapper.writeValue(depsFile.toFile(), deps);

        // When: run clustering
        ClusterParams params = new ClusterParams(depsFile, outputFile, 1.5);
        clusterService.execute(params);

        // Then: verify output
        Map<String, Object> result = objectMapper.readValue(outputFile.toFile(), Map.class);

        List<Map<String, Object>> clusters = (List<Map<String, Object>>) result.get("clusters");
        Map<String, Set<String>> penaltyEdges = (Map<String, Set<String>>) result.get("penaltyEdges");

        assertNotNull(clusters);
        assertTrue(clusters.isEmpty(), "Clusters should be empty for empty graph");

        assertNotNull(penaltyEdges);
        assertTrue(penaltyEdges.isEmpty(), "Penalty edges should be empty for empty graph");
    }


    /**
     * Scenario: Verify cohesion and coupling in output JSON have correct values (0.0-1.0),
     * and cohesion + coupling <= 1.0 for each cluster.
     */
    @Test
    void shouldCalculateMetricsCorrectly() throws IOException {
        // Given: dependencies with internal and external links
        Map<String, Map<String, List<Map<String, String>>>> deps = new LinkedHashMap<>();

        // Create a graph where metrics can be calculated
        Map<String, List<Map<String, String>>> classAEdges = new LinkedHashMap<>();
        classAEdges.put("com.example.ClassB", List.of(Map.of("type", "FIELD", "details", "classB")));
        deps.put("com.example.ClassA", classAEdges);

        Map<String, List<Map<String, String>>> classBEdges = new LinkedHashMap<>();
        classBEdges.put("com.example.ClassA", List.of(Map.of("type", "FIELD", "details", "classA")));
        deps.put("com.example.ClassB", classBEdges);

        Path depsFile = tempDir.resolve("dependencies.json");
        Path outputFile = tempDir.resolve("clusters.json");
        objectMapper.writeValue(depsFile.toFile(), deps);

        // When: run clustering
        ClusterParams params = new ClusterParams(depsFile, outputFile, 1.5);
        clusterService.execute(params);

        // Then: verify metrics
        Map<String, Object> result = objectMapper.readValue(outputFile.toFile(), Map.class);
        List<Map<String, Object>> clusters = (List<Map<String, Object>>) result.get("clusters");

        assertNotNull(clusters);

        for (Map<String, Object> cluster : clusters) {
            Map<String, Object> metrics = (Map<String, Object>) cluster.get("metrics");
            assertNotNull(metrics);

            double cohesion = ((Number) metrics.get("cohesion")).doubleValue();
            double coupling = ((Number) metrics.get("coupling")).doubleValue();

            // Verify range [0.0, 1.0]
            assertTrue(cohesion >= 0.0 && cohesion <= 1.0,
                    "Cohesion should be in range [0.0, 1.0], but was: " + cohesion);
            assertTrue(coupling >= 0.0 && coupling <= 1.0,
                    "Coupling should be in range [0.0, 1.0], but was: " + coupling);

            // Verify cohesion + coupling <= 1.0 (with small tolerance for floating point)
            double sum = cohesion + coupling;
            assertTrue(sum <= 1.0 + 0.0001,
                    "Cohesion + coupling should be <= 1.0, but was: " + sum);
        }
    }

    /**
     * Scenario: Graph with two clusters and explicit link between them.
     * Verification: penaltyEdges contains a class from first cluster with reference to class from second cluster.
     */
    @Test
    void shouldDetectPenaltyEdgesBetweenClusters() throws IOException {
        // Given: two clusters with a link between them
        Map<String, Map<String, List<Map<String, String>>>> deps = new LinkedHashMap<>();

        // Cluster 1: A and B (strongly connected)
        Map<String, List<Map<String, String>>> classAEdges = new LinkedHashMap<>();
        classAEdges.put("com.example.ClassB", List.of(Map.of("type", "FIELD", "details", "classB")));
        // Cross-cluster link: A -> C
        classAEdges.put("com.example.ClassC", List.of(Map.of("type", "FIELD", "details", "classC")));
        deps.put("com.example.ClassA", classAEdges);

        Map<String, List<Map<String, String>>> classBEdges = new LinkedHashMap<>();
        classBEdges.put("com.example.ClassA", List.of(Map.of("type", "FIELD", "details", "classA")));
        deps.put("com.example.ClassB", classBEdges);

        // Cluster 2: C and D (strongly connected)
        Map<String, List<Map<String, String>>> classCEdges = new LinkedHashMap<>();
        classCEdges.put("com.example.ClassD", List.of(Map.of("type", "FIELD", "details", "classD")));
        deps.put("com.example.ClassC", classCEdges);

        Map<String, List<Map<String, String>>> classDEdges = new LinkedHashMap<>();
        classDEdges.put("com.example.ClassC", List.of(Map.of("type", "FIELD", "details", "classC")));
        deps.put("com.example.ClassD", classDEdges);

        Path depsFile = tempDir.resolve("dependencies.json");
        Path outputFile = tempDir.resolve("clusters.json");
        objectMapper.writeValue(depsFile.toFile(), deps);

        // When: run clustering with high resolution to ensure separation
        ClusterParams params = new ClusterParams(depsFile, outputFile, 2.0);
        clusterService.execute(params);

        // Then: verify penalty edges
        Map<String, Object> result = objectMapper.readValue(outputFile.toFile(), Map.class);

        List<Map<String, Object>> clusters = (List<Map<String, Object>>) result.get("clusters");
        Map<String, List<String>> penaltyEdges = (Map<String, List<String>>) result.get("penaltyEdges");

        assertNotNull(clusters);
        assertTrue(clusters.size() >= 2, "Should have at least 2 clusters");

        // Build map of class -> cluster id
        Map<String, String> classToCluster = new HashMap<>();
        for (Map<String, Object> cluster : clusters) {
            String clusterId = (String) cluster.get("id");
            List<String> classes = (List<String>) cluster.get("classes");
            for (String cls : classes) {
                classToCluster.put(cls, clusterId);
            }
        }

        // Verify penalty edges exist and are cross-cluster
        assertNotNull(penaltyEdges);
        assertFalse(penaltyEdges.isEmpty(), "Should have penalty edges for cross-cluster dependencies");

        boolean foundCrossClusterEdge = false;
        for (Map.Entry<String, List<String>> entry : penaltyEdges.entrySet()) {
            String sourceClass = entry.getKey();
            String sourceCluster = classToCluster.get(sourceClass);
            for (String targetClass : entry.getValue()) {
                String targetCluster = classToCluster.get(targetClass);
                if (!sourceCluster.equals(targetCluster)) {
                    foundCrossClusterEdge = true;
                    break;
                }
            }
        }

        assertTrue(foundCrossClusterEdge, "Should have at least one cross-cluster penalty edge");
    }

    /**
     * Scenario: Two runs with identical input data should produce the same number of clusters
     * (algorithm is deterministic with fixed seed).
     */
    @Test
    void shouldProduceDeterministicResultsForSameSeed() throws IOException {
        // Given: same dependencies for both runs
        Map<String, Map<String, List<Map<String, String>>>> deps = new LinkedHashMap<>();

        // Create a graph with potential for multiple clusters
        Map<String, List<Map<String, String>>> classAEdges = new LinkedHashMap<>();
        classAEdges.put("com.example.ClassB", List.of(Map.of("type", "FIELD", "details", "classB")));
        deps.put("com.example.ClassA", classAEdges);

        Map<String, List<Map<String, String>>> classBEdges = new LinkedHashMap<>();
        classBEdges.put("com.example.ClassA", List.of(Map.of("type", "FIELD", "details", "classA")));
        deps.put("com.example.ClassB", classBEdges);

        Map<String, List<Map<String, String>>> classCEdges = new LinkedHashMap<>();
        classCEdges.put("com.example.ClassD", List.of(Map.of("type", "FIELD", "details", "classD")));
        deps.put("com.example.ClassC", classCEdges);

        Map<String, List<Map<String, String>>> classDEdges = new LinkedHashMap<>();
        classDEdges.put("com.example.ClassC", List.of(Map.of("type", "FIELD", "details", "classC")));
        deps.put("com.example.ClassD", classDEdges);

        Path depsFile = tempDir.resolve("dependencies.json");
        objectMapper.writeValue(depsFile.toFile(), deps);

        // When: run clustering twice with same parameters
        Path outputFile1 = tempDir.resolve("clusters1.json");
        ClusterParams params1 = new ClusterParams(depsFile, outputFile1, 1.5);
        clusterService.execute(params1);

        Path outputFile2 = tempDir.resolve("clusters2.json");
        ClusterParams params2 = new ClusterParams(depsFile, outputFile2, 1.5);
        clusterService.execute(params2);

        // Then: verify deterministic results (same number of clusters)
        Map<String, Object> result1 = objectMapper.readValue(outputFile1.toFile(), Map.class);
        Map<String, Object> result2 = objectMapper.readValue(outputFile2.toFile(), Map.class);

        List<Map<String, Object>> clusters1 = (List<Map<String, Object>>) result1.get("clusters");
        List<Map<String, Object>> clusters2 = (List<Map<String, Object>>) result2.get("clusters");

        assertEquals(clusters1.size(), clusters2.size(),
                "Two runs with same input should produce same number of clusters");
    }
}