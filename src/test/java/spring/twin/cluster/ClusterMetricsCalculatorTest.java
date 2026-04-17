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
 * Unit tests for {@link ClusterMetricsCalculator} and {@link ClusterMetricsRecord}.
 */
class ClusterMetricsCalculatorTest {

    @Test
    @DisplayName("Isolated cluster returns high cohesion and zero coupling")
    void testCalculate_isolatedCluster_returnsHighCohesionZeroCoupling() {
        ClusterMetricsCalculator calculator = new ClusterMetricsCalculator();

        // Cluster with classes A and B, where A references B (internal link only)
        Set<String> clusterClasses = new HashSet<>(Set.of("A", "B"));
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        
        Map<String, Set<LinkDetails>> aDeps = new HashMap<>();
        aDeps.put("B", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "bField"))));
        dependencyGraph.put("A", aDeps);
        dependencyGraph.put("B", new HashMap<>());

        ClusterMetricsRecord result = calculator.calculate(clusterClasses, dependencyGraph);

        assertEquals(1.0, result.cohesion(), 0.001, "Isolated cluster should have cohesion = 1.0");
        assertEquals(0.0, result.coupling(), 0.001, "Isolated cluster should have coupling = 0.0");
    }

    @Test
    @DisplayName("Cluster with only external links returns zero cohesion and high coupling")
    void testCalculate_clusterWithOnlyExternalLinks_returnsZeroCohesionHighCoupling() {
        ClusterMetricsCalculator calculator = new ClusterMetricsCalculator();

        // Cluster with class A, where A references C (external to cluster)
        Set<String> clusterClasses = new HashSet<>(Set.of("A"));
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        
        Map<String, Set<LinkDetails>> aDeps = new HashMap<>();
        aDeps.put("C", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "cField"))));
        dependencyGraph.put("A", aDeps);

        ClusterMetricsRecord result = calculator.calculate(clusterClasses, dependencyGraph);

        assertEquals(0.0, result.cohesion(), 0.001, "Cluster with only external links should have cohesion = 0.0");
        assertEquals(1.0, result.coupling(), 0.001, "Cluster with only external links should have coupling = 1.0");
    }

    @Test
    @DisplayName("Mixed cluster returns correct ratio of cohesion and coupling")
    void testCalculate_mixedCluster_returnsCorrectRatio() {
        ClusterMetricsCalculator calculator = new ClusterMetricsCalculator();

        // Cluster with classes A and B
        // A references B (internal) and C (external)
        Set<String> clusterClasses = new HashSet<>(Set.of("A", "B"));
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        
        Map<String, Set<LinkDetails>> aDeps = new HashMap<>();
        aDeps.put("B", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "bField"))));
        aDeps.put("C", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "cField"))));
        dependencyGraph.put("A", aDeps);
        dependencyGraph.put("B", new HashMap<>());

        ClusterMetricsRecord result = calculator.calculate(clusterClasses, dependencyGraph);

        // 1 internal link, 1 external link
        assertEquals(0.5, result.cohesion(), 0.001, "Mixed cluster should have cohesion = 0.5");
        assertEquals(0.5, result.coupling(), 0.001, "Mixed cluster should have coupling = 0.5");
    }

    @Test
    @DisplayName("Empty cluster returns default metrics")
    void testCalculate_emptyCluster_returnsDefaultMetrics() {
        ClusterMetricsCalculator calculator = new ClusterMetricsCalculator();

        // Empty cluster
        Set<String> clusterClasses = new HashSet<>();
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();

        ClusterMetricsRecord result = calculator.calculate(clusterClasses, dependencyGraph);

        assertEquals(1.0, result.cohesion(), 0.001, "Empty cluster should have default cohesion = 1.0");
        assertEquals(0.0, result.coupling(), 0.001, "Empty cluster should have default coupling = 0.0");
    }

    @Test
    @DisplayName("Cluster with no links returns default metrics")
    void testCalculate_clusterWithNoLinks_returnsDefaultMetrics() {
        ClusterMetricsCalculator calculator = new ClusterMetricsCalculator();

        // Cluster with class A but no dependencies
        Set<String> clusterClasses = new HashSet<>(Set.of("A"));
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        dependencyGraph.put("A", new HashMap<>());

        ClusterMetricsRecord result = calculator.calculate(clusterClasses, dependencyGraph);

        assertEquals(1.0, result.cohesion(), 0.001, "Cluster with no links should have default cohesion = 1.0");
        assertEquals(0.0, result.coupling(), 0.001, "Cluster with no links should have default coupling = 0.0");
    }

    @Test
    @DisplayName("CalculateAll returns metrics for all clusters")
    void testCalculateAll_multipleClusters_returnsMetricsForAll() {
        ClusterMetricsCalculator calculator = new ClusterMetricsCalculator();

        // Two clusters: cluster 0 has A and B, cluster 1 has C
        Map<Integer, Set<String>> communityMap = new HashMap<>();
        communityMap.put(0, new HashSet<>(Set.of("A", "B")));
        communityMap.put(1, new HashSet<>(Set.of("C")));

        // Dependencies: A -> B (internal to cluster 0), A -> C (external to cluster 0)
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        Map<String, Set<LinkDetails>> aDeps = new HashMap<>();
        aDeps.put("B", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "bField"))));
        aDeps.put("C", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "cField"))));
        dependencyGraph.put("A", aDeps);
        dependencyGraph.put("B", new HashMap<>());
        dependencyGraph.put("C", new HashMap<>());

        Map<Integer, ClusterMetricsRecord> result = calculator.calculateAll(communityMap, dependencyGraph);

        assertEquals(2, result.size(), "Should return metrics for all clusters");
        assertTrue(result.containsKey(0), "Should contain metrics for cluster 0");
        assertTrue(result.containsKey(1), "Should contain metrics for cluster 1");
        
        // Cluster 0 has mixed links (1 internal, 1 external)
        ClusterMetricsRecord cluster0Metrics = result.get(0);
        assertEquals(0.5, cluster0Metrics.cohesion(), 0.001);
        assertEquals(0.5, cluster0Metrics.coupling(), 0.001);
        
        // Cluster 1 has no links
        ClusterMetricsRecord cluster1Metrics = result.get(1);
        assertEquals(1.0, cluster1Metrics.cohesion(), 0.001);
        assertEquals(0.0, cluster1Metrics.coupling(), 0.001);
    }

    @Test
    @DisplayName("Cohesion and coupling sum to one for cluster with links")
    void testCohesionAndCoupling_sumToOne() {
        ClusterMetricsCalculator calculator = new ClusterMetricsCalculator();

        // Cluster with mixed internal and external links
        Set<String> clusterClasses = new HashSet<>(Set.of("A", "B", "C"));
        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        
        Map<String, Set<LinkDetails>> aDeps = new HashMap<>();
        aDeps.put("B", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "bField"))));
        aDeps.put("C", new HashSet<>(Set.of(LinkDetails.of(LinkType.METHOD, "method()"))));
        dependencyGraph.put("A", aDeps);
        
        Map<String, Set<LinkDetails>> bDeps = new HashMap<>();
        bDeps.put("D", new HashSet<>(Set.of(LinkDetails.of(LinkType.FIELD, "dField"))));
        dependencyGraph.put("B", bDeps);
        
        dependencyGraph.put("C", new HashMap<>());

        ClusterMetricsRecord result = calculator.calculate(clusterClasses, dependencyGraph);

        // Internal links: A->B, A->C (2)
        // External links: B->D (1)
        double sum = result.cohesion() + result.coupling();
        assertEquals(1.0, sum, 0.001, "Cohesion and coupling should sum to 1.0");
    }
}