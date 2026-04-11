package spring.twin.scan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ScanBytecodeService}.
 */
class ScanBytecodeServiceTest {

    private DependencyGraphBuilder dependencyGraphBuilder;
    private DependencyJsonWriter dependencyJsonWriter;
    private ScanBytecodeService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        dependencyGraphBuilder = mock(DependencyGraphBuilder.class);
        dependencyJsonWriter = mock(DependencyJsonWriter.class);
        service = new ScanBytecodeService(dependencyGraphBuilder, dependencyJsonWriter);
    }

    /**
     * Test: execute() вызывает builder и writer, файл создан
     */
    @Test
    void testExecute_buildsGraphAndWritesFile() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of();
        ScanBytecodeParams params = new ScanBytecodeParams(classesDir, outputFile, includeMasks, excludeMasks);

        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("com.example.Service", Set.of("com.example.Dependency"));
        when(dependencyGraphBuilder.build(any(), any(), any())).thenReturn(graph);

        service.execute(params);

        verify(dependencyGraphBuilder).build(classesDir, includeMasks, excludeMasks);
        verify(dependencyJsonWriter).write(graph, outputFile);
    }

    /**
     * Test: содержимое файла соответствует ожидаемому графу
     */
    @Test
    void testExecute_correctGraphWritten() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of();
        ScanBytecodeParams params = new ScanBytecodeParams(classesDir, outputFile, includeMasks, excludeMasks);

        Map<String, Set<String>> expectedGraph = new LinkedHashMap<>();
        expectedGraph.put("com.example.OrderService", Set.of("com.example.PaymentClient", "com.example.OrderRepository"));
        expectedGraph.put("com.example.OrderRepository", Set.of("com.example.OrderModel"));
        when(dependencyGraphBuilder.build(any(), any(), any())).thenReturn(expectedGraph);

        service.execute(params);

        ArgumentCaptor<Map<String, Set<String>>> graphCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
        verify(dependencyJsonWriter).write(graphCaptor.capture(), pathCaptor.capture());

        assertEquals(expectedGraph, graphCaptor.getValue());
        assertEquals(outputFile, pathCaptor.getValue());
    }

    /**
     * Test: analyze() возвращает граф без создания файла
     */
    @Test
    void testAnalyze_returnsGraphWithoutWriting() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of();
        ScanBytecodeParams params = new ScanBytecodeParams(classesDir, outputFile, includeMasks, excludeMasks);

        Map<String, Set<String>> expectedGraph = new HashMap<>();
        expectedGraph.put("com.example.Service", Set.of("com.example.Dependency"));
        when(dependencyGraphBuilder.build(any(), any(), any())).thenReturn(expectedGraph);

        Map<String, Set<String>> result = service.analyze(params);

        verify(dependencyGraphBuilder).build(classesDir, includeMasks, excludeMasks);
        verify(dependencyJsonWriter, never()).write(any(), any());
        assertEquals(expectedGraph, result);
    }

    /**
     * Test: маски применяются корректно
     */
    @Test
    void testExecute_withMasks_filtersCorrectly() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        List<String> includeMasks = List.of("com.example.*");
        List<String> excludeMasks = List.of("*.internal.*");
        ScanBytecodeParams params = new ScanBytecodeParams(classesDir, outputFile, includeMasks, excludeMasks);

        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("com.example.Service", Set.of("com.example.Dependency"));
        when(dependencyGraphBuilder.build(any(), any(), any())).thenReturn(graph);

        service.execute(params);

        verify(dependencyGraphBuilder).build(classesDir, includeMasks, excludeMasks);
        verify(dependencyJsonWriter).write(graph, outputFile);
    }

    /**
     * Test: пустая директория → '{}'
     */
    @Test
    void testExecute_emptyDirectory_createsEmptyJson() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of();
        ScanBytecodeParams params = new ScanBytecodeParams(classesDir, outputFile, includeMasks, excludeMasks);

        Map<String, Set<String>> emptyGraph = new HashMap<>();
        when(dependencyGraphBuilder.build(any(), any(), any())).thenReturn(emptyGraph);

        service.execute(params);

        verify(dependencyGraphBuilder).build(classesDir, includeMasks, excludeMasks);
        verify(dependencyJsonWriter).write(emptyGraph, outputFile);
    }

    /**
     * Test: пустая директория → пустой Map
     */
    @Test
    void testAnalyze_emptyDirectory_returnsEmptyMap() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of();
        ScanBytecodeParams params = new ScanBytecodeParams(classesDir, outputFile, includeMasks, excludeMasks);

        Map<String, Set<String>> emptyGraph = new HashMap<>();
        when(dependencyGraphBuilder.build(any(), any(), any())).thenReturn(emptyGraph);

        Map<String, Set<String>> result = service.analyze(params);

        assertTrue(result.isEmpty());
        assertEquals(emptyGraph, result);
        verify(dependencyJsonWriter, never()).write(any(), any());
    }
}