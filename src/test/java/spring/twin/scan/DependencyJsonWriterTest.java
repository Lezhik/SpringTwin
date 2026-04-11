package spring.twin.scan;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link DependencyJsonWriter}.
 */
class DependencyJsonWriterTest {

    @TempDir
    Path tempDir;

    /**
     * Test: запись графа → файл существует
     */
    @Test
    void testWrite_validGraph_createsJsonFile() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        DependencyJsonWriter writer = new DependencyJsonWriter(objectMapper);

        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("com.example.OrderService", Set.of("com.example.OrderRepository"));
        Path outputFile = tempDir.resolve("output.json");

        writer.write(graph, outputFile);

        assertTrue(Files.exists(outputFile), "JSON file should be created");
    }

    /**
     * Test: запись графа → содержимое соответствует ожидаемому JSON
     */
    @Test
    void testWrite_validGraph_correctJsonContent() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        DependencyJsonWriter writer = new DependencyJsonWriter(objectMapper);

        Map<String, Set<String>> graph = new LinkedHashMap<>();
        graph.put("com.example.ServiceA", Set.of("com.example.ServiceB", "com.example.ServiceC"));
        Path outputFile = tempDir.resolve("output.json");

        writer.write(graph, outputFile);

        String content = Files.readString(outputFile);
        assertTrue(content.contains("com.example.ServiceA"), "JSON should contain ServiceA key");
        assertTrue(content.contains("com.example.ServiceB"), "JSON should contain ServiceB dependency");
        assertTrue(content.contains("com.example.ServiceC"), "JSON should contain ServiceC dependency");
    }

    /**
     * Test: ключи в JSON отсортированы по алфавиту
     */
    @Test
    void testWrite_sortedKeys() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        DependencyJsonWriter writer = new DependencyJsonWriter(objectMapper);

        Map<String, Set<String>> graph = new LinkedHashMap<>();
        graph.put("com.example.ZebraService", Set.of("com.example.XDependency"));
        graph.put("com.example.AlphaService", Set.of("com.example.YDependency"));
        graph.put("com.example.BetaService", Set.of("com.example.ZDependency"));
        Path outputFile = tempDir.resolve("output.json");

        writer.write(graph, outputFile);

        String content = Files.readString(outputFile);
        int alphaPos = content.indexOf("com.example.AlphaService");
        int betaPos = content.indexOf("com.example.BetaService");
        int zebraPos = content.indexOf("com.example.ZebraService");

        assertTrue(alphaPos < betaPos, "AlphaService should come before BetaService");
        assertTrue(betaPos < zebraPos, "BetaService should come before ZebraService");
    }

    /**
     * Test: массивы зависимостей отсортированы по алфавиту
     */
    @Test
    void testWrite_sortedValues() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        DependencyJsonWriter writer = new DependencyJsonWriter(objectMapper);

        Map<String, Set<String>> graph = new LinkedHashMap<>();
        Set<String> deps = new LinkedHashSet<>();
        deps.add("com.example.ZDependency");
        deps.add("com.example.ADependency");
        deps.add("com.example.MDependency");
        graph.put("com.example.Service", deps);
        Path outputFile = tempDir.resolve("output.json");

        writer.write(graph, outputFile);

        String content = Files.readString(outputFile);
        int aPos = content.indexOf("com.example.ADependency");
        int mPos = content.indexOf("com.example.MDependency");
        int zPos = content.indexOf("com.example.ZDependency");

        assertTrue(aPos < mPos, "ADependency should come before MDependency");
        assertTrue(mPos < zPos, "MDependency should come before ZDependency");
    }

    /**
     * Test: пустой граф → `{}`
     */
    @Test
    void testWrite_emptyGraph_createsEmptyJsonObject() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        DependencyJsonWriter writer = new DependencyJsonWriter(objectMapper);

        Map<String, Set<String>> graph = new HashMap<>();
        Path outputFile = tempDir.resolve("output.json");

        writer.write(graph, outputFile);

        String content = Files.readString(outputFile).trim();
        assertEquals("{}", content, "Empty graph should produce empty JSON object");
    }

    /**
     * Test: несуществующие родительские директории создаются
     */
    @Test
    void testWrite_createsParentDirectories() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        DependencyJsonWriter writer = new DependencyJsonWriter(objectMapper);

        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("com.example.Service", Set.of("com.example.Dependency"));
        Path nestedDir = tempDir.resolve("level1/level2/level3");
        Path outputFile = nestedDir.resolve("output.json");

        writer.write(graph, outputFile);

        assertTrue(Files.exists(outputFile), "File should be created in nested directory");
    }

    /**
     * Test: существующий файл перезаписывается
     */
    @Test
    void testWrite_overwritesExistingFile() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        DependencyJsonWriter writer = new DependencyJsonWriter(objectMapper);

        Path outputFile = tempDir.resolve("output.json");
        Files.writeString(outputFile, "old content");

        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("com.example.NewService", Set.of("com.example.NewDependency"));

        writer.write(graph, outputFile);

        String content = Files.readString(outputFile);
        assertTrue(content.contains("com.example.NewService"), "File should be overwritten with new content");
        assertTrue(!content.contains("old content"), "Old content should be removed");
    }

    /**
     * Test: класс без зависимостей → "com.example.Service": []
     */
    @Test
    void testWrite_emptyDependencySet_createsEmptyArray() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        DependencyJsonWriter writer = new DependencyJsonWriter(objectMapper);

        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("com.example.Service", Set.of());
        Path outputFile = tempDir.resolve("output.json");

        writer.write(graph, outputFile);

        String content = Files.readString(outputFile);
        assertTrue(content.contains("\"com.example.Service\""), "JSON should contain the service key");
        assertTrue(content.contains("[]"), "Empty dependency set should produce empty array");
    }

    /**
     * Test: FQCN с не-ASCII символами корректно сериализуются
     */
    @Test
    void testWrite_utf8Encoding() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        DependencyJsonWriter writer = new DependencyJsonWriter(objectMapper);

        Map<String, Set<String>> graph = new HashMap<>();
        String classWithUnicode = "com.example.Сервис";
        String dependencyWithUnicode = "com.example.Зависимость";
        graph.put(classWithUnicode, Set.of(dependencyWithUnicode));
        Path outputFile = tempDir.resolve("output.json");

        writer.write(graph, outputFile);

        String content = Files.readString(outputFile, StandardCharsets.UTF_8);
        assertTrue(content.contains(classWithUnicode), "JSON should contain class with Unicode characters");
        assertTrue(content.contains(dependencyWithUnicode), "JSON should contain dependency with Unicode characters");
    }
}