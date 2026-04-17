package spring.twin.cluster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import spring.twin.scan.LinkDetails;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ClusterService}.
 *
 * <p>Tests verify that the service correctly orchestrates the clustering pipeline
 * by calling all dependencies in the proper order.
 */
class ClusterServiceTest {

    @Test
    @DisplayName("execute calls DependencyReader.read() with correct path")
    void testExecute_callsDependencyReader() {
        // Given
        DependencyReader dependencyReader = mock(DependencyReader.class);
        GraphConverter graphConverter = mock(GraphConverter.class);
        LeidenAlgorithm leidenAlgorithm = mock(LeidenAlgorithm.class);
        ClusterMetricsCalculator metricsCalculator = mock(ClusterMetricsCalculator.class);
        PenaltyEdgeDetector penaltyDetector = mock(PenaltyEdgeDetector.class);
        ClusterResultBuilder resultBuilder = mock(ClusterResultBuilder.class);
        ClusterJsonWriter jsonWriter = mock(ClusterJsonWriter.class);

        ClusterService service = new ClusterService(
                dependencyReader,
                graphConverter,
                leidenAlgorithm,
                metricsCalculator,
                penaltyDetector,
                resultBuilder,
                jsonWriter
        );

        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        when(dependencyReader.read(any())).thenReturn(dependencyGraph);

        Map<String, Map<String, Double>> weightedGraph = new HashMap<>();
        when(graphConverter.toUndirectedWeightedGraph(any())).thenReturn(weightedGraph);

        Partition partition = new Partition(new HashSet<>());
        when(leidenAlgorithm.cluster(any(), anyDouble())).thenReturn(partition);

        Map<String, Set<String>> penaltyEdges = new HashMap<>();
        ClusterResult clusterResult = new ClusterResult(new java.util.ArrayList<>(), penaltyEdges);
        when(resultBuilder.build(any(), any(), any(), any())).thenReturn(clusterResult);

        Path depsFile = Paths.get("dependencies.json");
        Path outputFile = Paths.get("clusters.json");
        ClusterParams params = new ClusterParams(depsFile, outputFile, 1.5);

        // When
        service.execute(params);

        // Then
        verify(dependencyReader).read(depsFile);
    }

    @Test
    @DisplayName("execute calls GraphConverter.toUndirectedWeightedGraph()")
    void testExecute_callsGraphConverter() {
        // Given
        DependencyReader dependencyReader = mock(DependencyReader.class);
        GraphConverter graphConverter = mock(GraphConverter.class);
        LeidenAlgorithm leidenAlgorithm = mock(LeidenAlgorithm.class);
        ClusterMetricsCalculator metricsCalculator = mock(ClusterMetricsCalculator.class);
        PenaltyEdgeDetector penaltyDetector = mock(PenaltyEdgeDetector.class);
        ClusterResultBuilder resultBuilder = mock(ClusterResultBuilder.class);
        ClusterJsonWriter jsonWriter = mock(ClusterJsonWriter.class);

        ClusterService service = new ClusterService(
                dependencyReader,
                graphConverter,
                leidenAlgorithm,
                metricsCalculator,
                penaltyDetector,
                resultBuilder,
                jsonWriter
        );

        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        dependencyGraph.put("com.example.ClassA", new HashMap<>());
        when(dependencyReader.read(any())).thenReturn(dependencyGraph);

        Map<String, Map<String, Double>> weightedGraph = new HashMap<>();
        when(graphConverter.toUndirectedWeightedGraph(any())).thenReturn(weightedGraph);

        Partition partition = new Partition(new HashSet<>());
        when(leidenAlgorithm.cluster(any(), anyDouble())).thenReturn(partition);

        Map<String, Set<String>> penaltyEdges = new HashMap<>();
        ClusterResult clusterResult = new ClusterResult(new java.util.ArrayList<>(), penaltyEdges);
        when(resultBuilder.build(any(), any(), any(), any())).thenReturn(clusterResult);

        ClusterParams params = new ClusterParams(
                Paths.get("dependencies.json"),
                Paths.get("clusters.json"),
                1.5
        );

        // When
        service.execute(params);

        // Then
        verify(graphConverter).toUndirectedWeightedGraph(dependencyGraph);
    }

    @Test
    @DisplayName("execute calls LeidenAlgorithm.cluster() with correct resolution")
    void testExecute_callsLeidenAlgorithm() {
        // Given
        DependencyReader dependencyReader = mock(DependencyReader.class);
        GraphConverter graphConverter = mock(GraphConverter.class);
        LeidenAlgorithm leidenAlgorithm = mock(LeidenAlgorithm.class);
        ClusterMetricsCalculator metricsCalculator = mock(ClusterMetricsCalculator.class);
        PenaltyEdgeDetector penaltyDetector = mock(PenaltyEdgeDetector.class);
        ClusterResultBuilder resultBuilder = mock(ClusterResultBuilder.class);
        ClusterJsonWriter jsonWriter = mock(ClusterJsonWriter.class);

        ClusterService service = new ClusterService(
                dependencyReader,
                graphConverter,
                leidenAlgorithm,
                metricsCalculator,
                penaltyDetector,
                resultBuilder,
                jsonWriter
        );

        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        when(dependencyReader.read(any())).thenReturn(dependencyGraph);

        Map<String, Map<String, Double>> weightedGraph = new HashMap<>();
        weightedGraph.put("com.example.ClassA", new HashMap<>());
        when(graphConverter.toUndirectedWeightedGraph(any())).thenReturn(weightedGraph);

        Partition partition = new Partition(new HashSet<>());
        when(leidenAlgorithm.cluster(any(), anyDouble())).thenReturn(partition);

        Map<String, Set<String>> penaltyEdges = new HashMap<>();
        ClusterResult clusterResult = new ClusterResult(new java.util.ArrayList<>(), penaltyEdges);
        when(resultBuilder.build(any(), any(), any(), any())).thenReturn(clusterResult);

        double expectedResolution = 2.5;
        ClusterParams params = new ClusterParams(
                Paths.get("dependencies.json"),
                Paths.get("clusters.json"),
                expectedResolution
        );

        // When
        service.execute(params);

        // Then
        verify(leidenAlgorithm).cluster(weightedGraph, expectedResolution);
    }

    @Test
    @DisplayName("execute calls ClusterResultBuilder.build()")
    void testExecute_callsResultBuilder() {
        // Given
        DependencyReader dependencyReader = mock(DependencyReader.class);
        GraphConverter graphConverter = mock(GraphConverter.class);
        LeidenAlgorithm leidenAlgorithm = mock(LeidenAlgorithm.class);
        ClusterMetricsCalculator metricsCalculator = mock(ClusterMetricsCalculator.class);
        PenaltyEdgeDetector penaltyDetector = mock(PenaltyEdgeDetector.class);
        ClusterResultBuilder resultBuilder = mock(ClusterResultBuilder.class);
        ClusterJsonWriter jsonWriter = mock(ClusterJsonWriter.class);

        ClusterService service = new ClusterService(
                dependencyReader,
                graphConverter,
                leidenAlgorithm,
                metricsCalculator,
                penaltyDetector,
                resultBuilder,
                jsonWriter
        );

        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        dependencyGraph.put("com.example.ClassA", new HashMap<>());
        when(dependencyReader.read(any())).thenReturn(dependencyGraph);

        Map<String, Map<String, Double>> weightedGraph = new HashMap<>();
        when(graphConverter.toUndirectedWeightedGraph(any())).thenReturn(weightedGraph);

        Partition partition = new Partition(new HashSet<>());
        when(leidenAlgorithm.cluster(any(), anyDouble())).thenReturn(partition);

        Map<String, Set<String>> penaltyEdges = new HashMap<>();
        ClusterResult clusterResult = new ClusterResult(new java.util.ArrayList<>(), penaltyEdges);
        when(resultBuilder.build(any(), any(), any(), any())).thenReturn(clusterResult);

        ClusterParams params = new ClusterParams(
                Paths.get("dependencies.json"),
                Paths.get("clusters.json"),
                1.5
        );

        // When
        service.execute(params);

        // Then
        verify(resultBuilder).build(eq(partition), eq(dependencyGraph), eq(metricsCalculator), eq(penaltyDetector));
    }

    @Test
    @DisplayName("execute calls ClusterJsonWriter.write() with correct path")
    void testExecute_callsJsonWriter() {
        // Given
        DependencyReader dependencyReader = mock(DependencyReader.class);
        GraphConverter graphConverter = mock(GraphConverter.class);
        LeidenAlgorithm leidenAlgorithm = mock(LeidenAlgorithm.class);
        ClusterMetricsCalculator metricsCalculator = mock(ClusterMetricsCalculator.class);
        PenaltyEdgeDetector penaltyDetector = mock(PenaltyEdgeDetector.class);
        ClusterResultBuilder resultBuilder = mock(ClusterResultBuilder.class);
        ClusterJsonWriter jsonWriter = mock(ClusterJsonWriter.class);

        ClusterService service = new ClusterService(
                dependencyReader,
                graphConverter,
                leidenAlgorithm,
                metricsCalculator,
                penaltyDetector,
                resultBuilder,
                jsonWriter
        );

        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        when(dependencyReader.read(any())).thenReturn(dependencyGraph);

        Map<String, Map<String, Double>> weightedGraph = new HashMap<>();
        when(graphConverter.toUndirectedWeightedGraph(any())).thenReturn(weightedGraph);

        Partition partition = new Partition(new HashSet<>());
        when(leidenAlgorithm.cluster(any(), anyDouble())).thenReturn(partition);

        Map<String, Set<String>> penaltyEdges = new HashMap<>();
        ClusterResult clusterResult = new ClusterResult(new java.util.ArrayList<>(), penaltyEdges);
        when(resultBuilder.build(any(), any(), any(), any())).thenReturn(clusterResult);

        Path outputFile = Paths.get("clusters.json");
        ClusterParams params = new ClusterParams(
                Paths.get("dependencies.json"),
                outputFile,
                1.5
        );

        // When
        service.execute(params);

        // Then
        verify(jsonWriter).write(clusterResult, outputFile);
    }

    @Test
    @DisplayName("analyze returns ClusterResult without writing to file")
    void testAnalyze_returnsClusterResult() {
        // Given
        DependencyReader dependencyReader = mock(DependencyReader.class);
        GraphConverter graphConverter = mock(GraphConverter.class);
        LeidenAlgorithm leidenAlgorithm = mock(LeidenAlgorithm.class);
        ClusterMetricsCalculator metricsCalculator = mock(ClusterMetricsCalculator.class);
        PenaltyEdgeDetector penaltyDetector = mock(PenaltyEdgeDetector.class);
        ClusterResultBuilder resultBuilder = mock(ClusterResultBuilder.class);
        ClusterJsonWriter jsonWriter = mock(ClusterJsonWriter.class);

        ClusterService service = new ClusterService(
                dependencyReader,
                graphConverter,
                leidenAlgorithm,
                metricsCalculator,
                penaltyDetector,
                resultBuilder,
                jsonWriter
        );

        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        dependencyGraph.put("com.example.ClassA", new HashMap<>());
        when(dependencyReader.read(any())).thenReturn(dependencyGraph);

        Map<String, Map<String, Double>> weightedGraph = new HashMap<>();
        when(graphConverter.toUndirectedWeightedGraph(any())).thenReturn(weightedGraph);

        Partition partition = new Partition(new HashSet<>());
        when(leidenAlgorithm.cluster(any(), anyDouble())).thenReturn(partition);

        java.util.List<ClusterRecord> clusters = new java.util.ArrayList<>();
        clusters.add(new ClusterRecord("cluster-0", java.util.List.of("com.example.ClassA"), new ClusterMetricsRecord(1.0, 0.0)));
        Map<String, Set<String>> penaltyEdges = new HashMap<>();
        ClusterResult expectedResult = new ClusterResult(clusters, penaltyEdges);
        when(resultBuilder.build(any(), any(), any(), any())).thenReturn(expectedResult);

        ClusterParams params = new ClusterParams(
                Paths.get("dependencies.json"),
                Paths.get("clusters.json"),
                1.5
        );

        // When
        ClusterResult result = service.analyze(params);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        assertEquals(1, result.clusters().size());
        assertEquals("cluster-0", result.clusters().get(0).id());

        // Verify JSON writer was NOT called
        verify(jsonWriter, never()).write(any(), any());
    }

    @Test
    @DisplayName("execute with mocked dependencies runs full pipeline in correct order")
    void testExecute_withMockedDependencies_fullPipeline() {
        // Given
        DependencyReader dependencyReader = mock(DependencyReader.class);
        GraphConverter graphConverter = mock(GraphConverter.class);
        LeidenAlgorithm leidenAlgorithm = mock(LeidenAlgorithm.class);
        ClusterMetricsCalculator metricsCalculator = mock(ClusterMetricsCalculator.class);
        PenaltyEdgeDetector penaltyDetector = mock(PenaltyEdgeDetector.class);
        ClusterResultBuilder resultBuilder = mock(ClusterResultBuilder.class);
        ClusterJsonWriter jsonWriter = mock(ClusterJsonWriter.class);

        ClusterService service = new ClusterService(
                dependencyReader,
                graphConverter,
                leidenAlgorithm,
                metricsCalculator,
                penaltyDetector,
                resultBuilder,
                jsonWriter
        );

        Path depsFile = Paths.get("dependencies.json");
        Path outputFile = Paths.get("clusters.json");
        double resolution = 1.5;

        Map<String, Map<String, Set<LinkDetails>>> dependencyGraph = new HashMap<>();
        dependencyGraph.put("com.example.Service", new HashMap<>());
        dependencyGraph.put("com.example.Controller", new HashMap<>());
        when(dependencyReader.read(depsFile)).thenReturn(dependencyGraph);

        Map<String, Map<String, Double>> weightedGraph = new HashMap<>();
        weightedGraph.put("com.example.Service", new HashMap<>());
        weightedGraph.put("com.example.Controller", new HashMap<>());
        when(graphConverter.toUndirectedWeightedGraph(dependencyGraph)).thenReturn(weightedGraph);

        Partition partition = new Partition(new HashSet<>(Set.of("com.example.Service", "com.example.Controller")));
        when(leidenAlgorithm.cluster(weightedGraph, resolution)).thenReturn(partition);

        java.util.List<ClusterRecord> clusters = new java.util.ArrayList<>();
        clusters.add(new ClusterRecord("cluster-0", java.util.List.of("com.example.Controller", "com.example.Service"), new ClusterMetricsRecord(0.8, 0.1)));
        Map<String, Set<String>> penaltyEdges = new HashMap<>();
        ClusterResult clusterResult = new ClusterResult(clusters, penaltyEdges);
        when(resultBuilder.build(partition, dependencyGraph, metricsCalculator, penaltyDetector)).thenReturn(clusterResult);

        ClusterParams params = new ClusterParams(depsFile, outputFile, resolution);

        // When
        service.execute(params);

        // Then - verify all dependencies were called in correct order
        var inOrder = inOrder(dependencyReader, graphConverter, leidenAlgorithm, resultBuilder, jsonWriter);
        inOrder.verify(dependencyReader).read(depsFile);
        inOrder.verify(graphConverter).toUndirectedWeightedGraph(dependencyGraph);
        inOrder.verify(leidenAlgorithm).cluster(weightedGraph, resolution);
        inOrder.verify(resultBuilder).build(partition, dependencyGraph, metricsCalculator, penaltyDetector);
        inOrder.verify(jsonWriter).write(clusterResult, outputFile);
    }
}