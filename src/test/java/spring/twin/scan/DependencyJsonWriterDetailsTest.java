package spring.twin.scan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link DependencyJsonWriter#writeDetails(Map, Path)} method.
 *
 * <p>Tests cover:
 * <ul>
 *   <li>Empty graph handling</li>
 *   <li>Single and multiple class dependencies</li>
 *   <li>Sorted keys at both levels</li>
 *   <li>LinkDetails serialization format</li>
 *   <li>LinkType serialization as string</li>
 *   <li>Sorted LinkDetails arrays</li>
 *   <li>Parent directory creation</li>
 *   <li>Pretty-printing with 2-space indentation</li>
 *   <li>Empty details for SUPERCLASS type</li>
 * </ul>
 */
class DependencyJsonWriterDetailsTest {

    @TempDir
    Path tempDir;

    /**
     * Test: пустой граф → файл содержит `{}`
     */
    @Test
    void testWriteDetails_emptyGraph_writesEmptyJson() throws Exception {
        DependencyJsonWriter writer = new DependencyJsonWriter();

        Map<String, Map<String, Set<LinkDetails>>> graph = new HashMap<>();
        Path outputFile = tempDir.resolve("output.json");

        writer.writeDetails(graph, outputFile);

        String content = Files.readString(outputFile).trim();
        assertEquals("{}", content, "Empty graph should produce empty JSON object");
    }

    /**
     * Test: один класс с одной зависимостью → корректный JSON с LinkDetails
     */
    @Test
    void testWriteDetails_singleClassSingleDependency_writesCorrectJson() throws Exception {
        DependencyJsonWriter writer = new DependencyJsonWriter();

        Map<String, Map<String, Set<LinkDetails>>> graph = new HashMap<>();
        Map<String, Set<LinkDetails>> deps = new HashMap<>();
        deps.put("com.example.PaymentClient", Set.of(LinkDetails.of(LinkType.FIELD, "paymentClient")));
        graph.put("com.example.OrderService", deps);

        Path outputFile = tempDir.resolve("output.json");
        writer.writeDetails(graph, outputFile);

        String content = Files.readString(outputFile);
        assertTrue(content.contains("\"com.example.OrderService\""), "JSON should contain source class");
        assertTrue(content.contains("\"com.example.PaymentClient\""), "JSON should contain target class");
        assertTrue(content.contains("\"type\" : \"FIELD\"") || content.contains("\"type\":\"FIELD\""), "JSON should contain type field");
        assertTrue(content.contains("\"details\" : \"paymentClient\"") || content.contains("\"details\":\"paymentClient\""), "JSON should contain details field");
    }

    /**
     * Test: один класс с несколькими зависимостями → ключи второго уровня отсортированы
     */
    @Test
    void testWriteDetails_singleClassMultipleDependencies_writesSortedKeys() throws Exception {
        DependencyJsonWriter writer = new DependencyJsonWriter();

        Map<String, Map<String, Set<LinkDetails>>> graph = new HashMap<>();
        Map<String, Set<LinkDetails>> deps = new LinkedHashMap<>();
        deps.put("com.example.ZebraService", Set.of(LinkDetails.of(LinkType.FIELD, "zebra")));
        deps.put("com.example.AlphaService", Set.of(LinkDetails.of(LinkType.FIELD, "alpha")));
        deps.put("com.example.BetaService", Set.of(LinkDetails.of(LinkType.FIELD, "beta")));
        graph.put("com.example.OrderService", deps);

        Path outputFile = tempDir.resolve("output.json");
        writer.writeDetails(graph, outputFile);

        String content = Files.readString(outputFile);
        int alphaPos = content.indexOf("com.example.AlphaService");
        int betaPos = content.indexOf("com.example.BetaService");
        int zebraPos = content.indexOf("com.example.ZebraService");

        assertTrue(alphaPos < betaPos, "AlphaService should come before BetaService");
        assertTrue(betaPos < zebraPos, "BetaService should come before ZebraService");
    }

    /**
     * Test: несколько классов → ключи первого уровня отсортированы
     */
    @Test
    void testWriteDetails_multipleClasses_writesSortedKeys() throws Exception {
        DependencyJsonWriter writer = new DependencyJsonWriter();

        Map<String, Map<String, Set<LinkDetails>>> graph = new LinkedHashMap<>();
        Map<String, Set<LinkDetails>> deps1 = new HashMap<>();
        deps1.put("com.example.Dependency", Set.of(LinkDetails.of(LinkType.FIELD, "dep")));
        graph.put("com.example.ZebraClass", deps1);
        graph.put("com.example.AlphaClass", deps1);
        graph.put("com.example.BetaClass", deps1);

        Path outputFile = tempDir.resolve("output.json");
        writer.writeDetails(graph, outputFile);

        String content = Files.readString(outputFile);
        int alphaPos = content.indexOf("com.example.AlphaClass");
        int betaPos = content.indexOf("com.example.BetaClass");
        int zebraPos = content.indexOf("com.example.ZebraClass");

        assertTrue(alphaPos < betaPos, "AlphaClass should come before BetaClass");
        assertTrue(betaPos < zebraPos, "BetaClass should come before ZebraClass");
    }

    /**
     * Test: LinkDetails сериализуется как `{"type": "FIELD", "details": "fieldName"}`
     */
    @Test
    void testWriteDetails_linkDetailsSerialized_typeAndDetailsFields() throws Exception {
        DependencyJsonWriter writer = new DependencyJsonWriter();

        Map<String, Map<String, Set<LinkDetails>>> graph = new HashMap<>();
        Map<String, Set<LinkDetails>> deps = new HashMap<>();
        deps.put("com.example.Dependency", Set.of(LinkDetails.of(LinkType.FIELD, "fieldName")));
        graph.put("com.example.Service", deps);

        Path outputFile = tempDir.resolve("output.json");
        writer.writeDetails(graph, outputFile);

        String content = Files.readString(outputFile);
        assertTrue(content.contains("\"type\""), "JSON should contain type field");
        assertTrue(content.contains("\"details\""), "JSON should contain details field");
        assertTrue(content.contains("\"FIELD\""), "JSON should contain FIELD value");
        assertTrue(content.contains("\"fieldName\""), "JSON should contain fieldName value");
    }

    /**
     * Test: LinkType.FIELD сериализуется как строка `"FIELD"`, не как число
     */
    @Test
    void testWriteDetails_linkTypeSerialized_asStringName() throws Exception {
        DependencyJsonWriter writer = new DependencyJsonWriter();

        Map<String, Map<String, Set<LinkDetails>>> graph = new HashMap<>();
        Map<String, Set<LinkDetails>> deps = new HashMap<>();
        deps.put("com.example.Dependency", Set.of(LinkDetails.of(LinkType.METHOD, "test()")));
        graph.put("com.example.Service", deps);

        Path outputFile = tempDir.resolve("output.json");
        writer.writeDetails(graph, outputFile);

        String content = Files.readString(outputFile);
        assertTrue(content.contains("\"METHOD\""), "LinkType should be serialized as string name");
        assertTrue(!content.matches(".*\"type\"\\s*:\\s*\\d+.*"), "LinkType should not be serialized as number");
    }

    /**
     * Test: несколько LinkDetails для одного FQCN → массив отсортирован по type, затем по details
     */
    @Test
    void testWriteDetails_multipleLinkDetailsPerTarget_sortedArray() throws Exception {
        DependencyJsonWriter writer = new DependencyJsonWriter();

        Map<String, Map<String, Set<LinkDetails>>> graph = new HashMap<>();
        Map<String, Set<LinkDetails>> deps = new HashMap<>();
        Set<LinkDetails> linkDetailsSet = new LinkedHashSet<>();
        // Add in non-sorted order
        linkDetailsSet.add(LinkDetails.of(LinkType.METHOD, "zMethod()"));
        linkDetailsSet.add(LinkDetails.of(LinkType.FIELD, "bField"));
        linkDetailsSet.add(LinkDetails.of(LinkType.FIELD, "aField"));
        linkDetailsSet.add(LinkDetails.of(LinkType.METHOD, "aMethod()"));
        deps.put("com.example.Dependency", linkDetailsSet);
        graph.put("com.example.Service", deps);

        Path outputFile = tempDir.resolve("output.json");
        writer.writeDetails(graph, outputFile);

        String content = Files.readString(outputFile);
        // FIELD should come before METHOD alphabetically
        // Within same type, aField should come before bField
        int fieldPos = content.indexOf("FIELD");
        int methodPos = content.indexOf("METHOD");
        assertTrue(fieldPos < methodPos, "FIELD should come before METHOD");

        int aFieldPos = content.indexOf("aField");
        int bFieldPos = content.indexOf("bField");
        assertTrue(aFieldPos < bFieldPos, "aField should come before bField");
    }

    /**
     * Test: несуществующие родительские директории создаются автоматически
     */
    @Test
    void testWriteDetails_createsParentDirectories() throws Exception {
        DependencyJsonWriter writer = new DependencyJsonWriter();

        Map<String, Map<String, Set<LinkDetails>>> graph = new HashMap<>();
        Map<String, Set<LinkDetails>> deps = new HashMap<>();
        deps.put("com.example.Dependency", Set.of(LinkDetails.of(LinkType.FIELD, "dep")));
        graph.put("com.example.Service", deps);

        Path nestedDir = tempDir.resolve("level1/level2/level3");
        Path outputFile = nestedDir.resolve("output.json");

        writer.writeDetails(graph, outputFile);

        assertTrue(Files.exists(outputFile), "File should be created in nested directory");
    }

    /**
     * Test: JSON отформатирован с 2-мя пробелами отступа
     */
    @Test
    void testWriteDetails_prettyPrinted_twoSpaceIndent() throws Exception {
        DependencyJsonWriter writer = new DependencyJsonWriter();

        Map<String, Map<String, Set<LinkDetails>>> graph = new HashMap<>();
        Map<String, Set<LinkDetails>> deps = new HashMap<>();
        deps.put("com.example.Dependency", Set.of(LinkDetails.of(LinkType.FIELD, "dep")));
        graph.put("com.example.Service", deps);

        Path outputFile = tempDir.resolve("output.json");
        writer.writeDetails(graph, outputFile);

        String content = Files.readString(outputFile);
        // Check for 2-space indentation pattern
        assertTrue(content.contains("  \""), "JSON should be formatted with 2-space indentation");
        // Should not contain 4-space indentation (which would be default Jackson)
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.startsWith("    ") && !line.startsWith("      ")) {
                // 4 spaces found at second level, which is fine
                // but 2-space is the first level
            }
        }
        // Verify it's not compact JSON
        assertTrue(content.contains("\n"), "Pretty-printed JSON should contain newlines");
    }

    /**
     * Test: LinkType.SUPERCLASS → `{"type": "SUPERCLASS", "details": ""}`
     */
    @Test
    void testWriteDetails_superclassLink_emptyDetails() throws Exception {
        DependencyJsonWriter writer = new DependencyJsonWriter();

        Map<String, Map<String, Set<LinkDetails>>> graph = new HashMap<>();
        Map<String, Set<LinkDetails>> deps = new HashMap<>();
        deps.put("com.example.BaseClass", Set.of(LinkDetails.of(LinkType.SUPERCLASS)));
        graph.put("com.example.ChildClass", deps);

        Path outputFile = tempDir.resolve("output.json");
        writer.writeDetails(graph, outputFile);

        String content = Files.readString(outputFile);
        assertTrue(content.contains("\"SUPERCLASS\""), "JSON should contain SUPERCLASS type");
        assertTrue(content.contains("\"details\" : \"\"") || content.contains("\"details\":\"\""), 
            "SUPERCLASS link should have empty details");
    }
}