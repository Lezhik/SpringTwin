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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ScanBytecodeService} detailed methods.
 */
class ScanBytecodeServiceDetailsTest {

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
     * Test: executeDetails() вызывает buildDetails() и writeDetails()
     */
    @Test
    void testExecuteDetails_callsBuildDetailsAndWriteDetails() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of();
        ScanBytecodeParams params = new ScanBytecodeParams(classesDir, outputFile, includeMasks, excludeMasks);

        Map<String, Map<String, Set<LinkDetails>>> graph = new HashMap<>();
        when(dependencyGraphBuilder.buildDetails(any(), any(), any(), anyBoolean())).thenReturn(graph);

        service.executeDetails(params);

        verify(dependencyGraphBuilder).buildDetails(classesDir, includeMasks, excludeMasks, true);
        verify(dependencyJsonWriter).writeDetails(graph, outputFile);
    }

    /**
     * Test: после выполнения executeDetails() существует выходной файл с корректным JSON
     */
    @Test
    void testExecuteDetails_writesOutputFile() throws Exception {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of();
        ScanBytecodeParams params = new ScanBytecodeParams(classesDir, outputFile, includeMasks, excludeMasks);

        Files.createDirectories(classesDir);

        Map<String, Map<String, Set<LinkDetails>>> expectedGraph = new LinkedHashMap<>();
        Map<String, Set<LinkDetails>> innerMap = new LinkedHashMap<>();
        innerMap.put("com.example.Dependency", Set.of(LinkDetails.of(LinkType.FIELD, "dependencyField")));
        expectedGraph.put("com.example.Service", innerMap);

        when(dependencyGraphBuilder.buildDetails(any(), any(), any(), anyBoolean())).thenReturn(expectedGraph);

        service.executeDetails(params);

        // Verify that writeDetails was called with the expected parameters
        ArgumentCaptor<Map<String, Map<String, Set<LinkDetails>>>> graphCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
        verify(dependencyJsonWriter).writeDetails(graphCaptor.capture(), pathCaptor.capture());

        assertEquals(expectedGraph, graphCaptor.getValue());
        assertEquals(outputFile, pathCaptor.getValue());
    }

    /**
     * Test: analyzeDetails() возвращает детализированный граф
     */
    @Test
    void testAnalyzeDetails_returnsDetailedGraph() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of();
        ScanBytecodeParams params = new ScanBytecodeParams(classesDir, outputFile, includeMasks, excludeMasks);

        Map<String, Map<String, Set<LinkDetails>>> expectedGraph = new LinkedHashMap<>();
        Map<String, Set<LinkDetails>> innerMap = new LinkedHashMap<>();
        innerMap.put("com.example.Dependency", Set.of(LinkDetails.of(LinkType.FIELD, "dependencyField")));
        expectedGraph.put("com.example.Service", innerMap);

        when(dependencyGraphBuilder.buildDetails(any(), any(), any(), anyBoolean())).thenReturn(expectedGraph);

        Map<String, Map<String, Set<LinkDetails>>> result = service.analyzeDetails(params);

        verify(dependencyGraphBuilder).buildDetails(classesDir, includeMasks, excludeMasks, true);
        verify(dependencyJsonWriter, never()).writeDetails(any(), any());
        assertNotNull(result);
        assertEquals(expectedGraph, result);
    }

    /**
     * Test: analyzeDetails() не создаёт выходной файл
     */
    @Test
    void testAnalyzeDetails_doesNotWriteFile() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of();
        ScanBytecodeParams params = new ScanBytecodeParams(classesDir, outputFile, includeMasks, excludeMasks);

        Map<String, Map<String, Set<LinkDetails>>> graph = new HashMap<>();
        when(dependencyGraphBuilder.buildDetails(any(), any(), any(), anyBoolean())).thenReturn(graph);

        service.analyzeDetails(params);

        verify(dependencyJsonWriter, never()).writeDetails(any(), any());
        assertFalse(Files.exists(outputFile));
    }

    /**
     * Test: executeDetails() с mergeInnerClasses передаёт флаг в builder
     */
    @Test
    void testExecuteDetails_withMergeInnerClasses_passesFlagToBuilder() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of();
        ScanBytecodeParams params = new ScanBytecodeParams(classesDir, outputFile, includeMasks, excludeMasks, true);

        Map<String, Map<String, Set<LinkDetails>>> graph = new HashMap<>();
        when(dependencyGraphBuilder.buildDetails(any(), any(), any(), anyBoolean())).thenReturn(graph);

        service.executeDetails(params);

        ArgumentCaptor<Boolean> mergeFlagCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(dependencyGraphBuilder).buildDetails(eq(classesDir), eq(includeMasks), eq(excludeMasks), mergeFlagCaptor.capture());
        assertTrue(mergeFlagCaptor.getValue());
    }

    /**
     * Test: analyzeDetails() с mergeInnerClasses передаёт флаг в builder
     */
    @Test
    void testAnalyzeDetails_withMergeInnerClasses_passesFlagToBuilder() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of();
        ScanBytecodeParams params = new ScanBytecodeParams(classesDir, outputFile, includeMasks, excludeMasks, true);

        Map<String, Map<String, Set<LinkDetails>>> graph = new HashMap<>();
        when(dependencyGraphBuilder.buildDetails(any(), any(), any(), anyBoolean())).thenReturn(graph);

        service.analyzeDetails(params);

        ArgumentCaptor<Boolean> mergeFlagCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(dependencyGraphBuilder).buildDetails(eq(classesDir), eq(includeMasks), eq(excludeMasks), mergeFlagCaptor.capture());
        assertTrue(mergeFlagCaptor.getValue());
    }
}