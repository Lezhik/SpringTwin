package spring.twin.cluster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ClusterJsonWriter}.
 */
class ClusterJsonWriterTest {

    @Test
    @DisplayName("write creates file for valid ClusterResult")
    void testWrite_validClusterResult_createsFile(@TempDir Path tempDir) throws Exception {
        ClusterJsonWriter writer = new ClusterJsonWriter();
        Path outputFile = tempDir.resolve("clusters.json");

        ClusterMetricsRecord metrics = new ClusterMetricsRecord(0.81, 0.12);
        ClusterRecord cluster = new ClusterRecord("cluster-1", List.of("com.example.Service"), metrics);
        ClusterResult result = new ClusterResult(List.of(cluster), Map.of());

        writer.write(result, outputFile);

        assertTrue(Files.exists(outputFile), "Output file should be created");
        assertTrue(Files.isRegularFile(outputFile), "Output should be a regular file");
    }

    @Test
    @DisplayName("write produces JSON with clusters and penaltyEdges fields")
    void testWrite_validClusterResult_correctJsonStructure(@TempDir Path tempDir) throws Exception {
        ClusterJsonWriter writer = new ClusterJsonWriter();
        Path outputFile = tempDir.resolve("clusters.json");

        ClusterMetricsRecord metrics = new ClusterMetricsRecord(0.81, 0.12);
        ClusterRecord cluster = new ClusterRecord("cluster-1", List.of("com.example.Service"), metrics);
        ClusterResult result = new ClusterResult(List.of(cluster), Map.of());

        writer.write(result, outputFile);

        String json = Files.readString(outputFile);
        assertTrue(json.contains("\"clusters\""), "JSON should contain 'clusters' field");
        assertTrue(json.contains("\"penaltyEdges\""), "JSON should contain 'penaltyEdges' field");
    }

    @Test
    @DisplayName("write produces clusters with id, classes, and metrics fields")
    void testWrite_clustersHaveIdClassesMetrics(@TempDir Path tempDir) throws Exception {
        ClusterJsonWriter writer = new ClusterJsonWriter();
        Path outputFile = tempDir.resolve("clusters.json");

        ClusterMetricsRecord metrics = new ClusterMetricsRecord(0.75, 0.25);
        ClusterRecord cluster = new ClusterRecord("cluster-1",
                List.of("com.example.ServiceA", "com.example.ServiceB"), metrics);
        ClusterResult result = new ClusterResult(List.of(cluster), Map.of());

        writer.write(result, outputFile);

        String json = Files.readString(outputFile);
        assertTrue(json.contains("\"id\""), "Cluster should have 'id' field");
        assertTrue(json.contains("\"cluster-1\""), "Cluster should contain id value 'cluster-1'");
        assertTrue(json.contains("\"classes\""), "Cluster should have 'classes' field");
        assertTrue(json.contains("\"metrics\""), "Cluster should have 'metrics' field");
        assertTrue(json.contains("com.example.ServiceA"), "Classes should include ServiceA");
        assertTrue(json.contains("com.example.ServiceB"), "Classes should include ServiceB");
    }

    @Test
    @DisplayName("write produces metrics with cohesion and coupling fields")
    void testWrite_metricsHaveCohesionAndCoupling(@TempDir Path tempDir) throws Exception {
        ClusterJsonWriter writer = new ClusterJsonWriter();
        Path outputFile = tempDir.resolve("clusters.json");

        ClusterMetricsRecord metrics = new ClusterMetricsRecord(0.81, 0.12);
        ClusterRecord cluster = new ClusterRecord("cluster-1", List.of("com.example.Service"), metrics);
        ClusterResult result = new ClusterResult(List.of(cluster), Map.of());

        writer.write(result, outputFile);

        String json = Files.readString(outputFile);
        assertTrue(json.contains("\"cohesion\""), "Metrics should have 'cohesion' field");
        assertTrue(json.contains("\"coupling\""), "Metrics should have 'coupling' field");
        assertTrue(json.contains("0.81"), "Cohesion value should be present");
        assertTrue(json.contains("0.12"), "Coupling value should be present");
    }

    @Test
    @DisplayName("write produces penaltyEdges in correct format")
    void testWrite_penaltyEdgesCorrectFormat(@TempDir Path tempDir) throws Exception {
        ClusterJsonWriter writer = new ClusterJsonWriter();
        Path outputFile = tempDir.resolve("clusters.json");

        ClusterMetricsRecord metrics = new ClusterMetricsRecord(0.81, 0.12);
        ClusterRecord cluster = new ClusterRecord("cluster-1", List.of("com.example.ServiceA"), metrics);

        Map<String, Set<String>> penaltyEdges = new HashMap<>();
        Set<String> targets = new HashSet<>();
        targets.add("com.example.ServiceB");
        targets.add("com.example.ServiceC");
        penaltyEdges.put("com.example.ServiceA", targets);

        ClusterResult result = new ClusterResult(List.of(cluster), penaltyEdges);

        writer.write(result, outputFile);

        String json = Files.readString(outputFile);
        assertTrue(json.contains("\"penaltyEdges\""), "JSON should contain 'penaltyEdges' field");
        assertTrue(json.contains("com.example.ServiceA"), "Penalty edges should contain source class");
        assertTrue(json.contains("com.example.ServiceB"), "Penalty edges should contain target class B");
        assertTrue(json.contains("com.example.ServiceC"), "Penalty edges should contain target class C");
    }

    @Test
    @DisplayName("write produces empty clusters and penaltyEdges for empty result")
    void testWrite_emptyClusterResult_createsEmptyJson(@TempDir Path tempDir) throws Exception {
        ClusterJsonWriter writer = new ClusterJsonWriter();
        Path outputFile = tempDir.resolve("clusters.json");

        ClusterResult result = new ClusterResult(List.of(), Map.of());

        writer.write(result, outputFile);

        String json = Files.readString(outputFile);
        assertTrue(json.contains("\"clusters\" : [ ]") || json.contains("\"clusters\": []"),
                "Empty clusters should be represented as empty array");
        assertTrue(json.contains("\"penaltyEdges\" : { }") || json.contains("\"penaltyEdges\": {}"),
                "Empty penaltyEdges should be represented as empty object");
    }

    @Test
    @DisplayName("write sorts keys and values in penaltyEdges")
    void testWrite_sortedKeys(@TempDir Path tempDir) throws Exception {
        ClusterJsonWriter writer = new ClusterJsonWriter();
        Path outputFile = tempDir.resolve("clusters.json");

        ClusterMetricsRecord metrics = new ClusterMetricsRecord(0.81, 0.12);
        ClusterRecord cluster = new ClusterRecord("cluster-1", List.of("com.example.Service"), metrics);

        // Create penalty edges with unsorted keys and values
        Map<String, Set<String>> penaltyEdges = new HashMap<>();
        Set<String> targetsForZ = new HashSet<>();
        targetsForZ.add("com.example.B");
        targetsForZ.add("com.example.A");
        penaltyEdges.put("com.example.Z", targetsForZ);

        Set<String> targetsForA = new HashSet<>();
        targetsForA.add("com.example.Y");
        targetsForA.add("com.example.X");
        penaltyEdges.put("com.example.A", targetsForA);

        ClusterResult result = new ClusterResult(List.of(cluster), penaltyEdges);

        writer.write(result, outputFile);

        String json = Files.readString(outputFile);

        // Check that keys appear in sorted order: A before Z
        int indexA = json.indexOf("com.example.A");
        int indexZ = json.indexOf("com.example.Z");
        assertTrue(indexA < indexZ, "Keys should be sorted alphabetically");

        // Check that values appear in sorted order within each key
        // X should come before Y in the output for com.example.A
        int indexX = json.indexOf("com.example.X");
        int indexY = json.indexOf("com.example.Y");
        assertTrue(indexX < indexY, "Values should be sorted alphabetically");
    }

    @Test
    @DisplayName("write creates parent directories automatically")
    void testWrite_createsParentDirectories(@TempDir Path tempDir) throws Exception {
        ClusterJsonWriter writer = new ClusterJsonWriter();
        Path nestedDir = tempDir.resolve("nested").resolve("directories");
        Path outputFile = nestedDir.resolve("clusters.json");

        assertFalse(Files.exists(nestedDir), "Parent directories should not exist initially");

        ClusterMetricsRecord metrics = new ClusterMetricsRecord(0.81, 0.12);
        ClusterRecord cluster = new ClusterRecord("cluster-1", List.of("com.example.Service"), metrics);
        ClusterResult result = new ClusterResult(List.of(cluster), Map.of());

        writer.write(result, outputFile);

        assertTrue(Files.exists(nestedDir), "Parent directories should be created");
        assertTrue(Files.exists(outputFile), "Output file should be created");
    }
}