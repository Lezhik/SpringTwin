package spring.twin.scan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DependencyGraphBuilder#buildDetails}.
 * Tests use compiled sample classes from {@code src/test/java/spring/twin/testee/}.
 */
class DependencyGraphBuilderDetailsTest {

    private DependencyGraphBuilder builder;
    private Path testClassesDir;

    @BeforeEach
    void setUp() {
        ClassFileScanner classFileScanner = new ClassFileScanner();
        BytecodeClassAnalyzer bytecodeClassAnalyzer = new BytecodeClassAnalyzer(
                new InheritanceExtractor(),
                new FieldTypeExtractor(),
                new MethodTypeExtractor(),
                new AnnotationTypeExtractor(),
                new CodeUsageExtractor()
        );
        builder = new DependencyGraphBuilder(classFileScanner, bytecodeClassAnalyzer);
        testClassesDir = Path.of("build/classes/java/test");
    }

    @Test
    void testBuildDetails_scansDirectory_returnsDetailedGraph() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        List<String> includeMasks = List.of("*.testee.*");
        List<String> excludeMasks = List.of();

        Map<String, Map<String, Set<LinkDetails>>> result = builder.buildDetails(dir, includeMasks, excludeMasks);

        // Graph should contain entries for testee classes
        assertFalse(result.isEmpty());

        // Should contain ComplexService with detailed dependencies
        assertTrue(result.containsKey("spring.twin.testee.ComplexService"));
        Map<String, Set<LinkDetails>> complexServiceDeps = result.get("spring.twin.testee.ComplexService");
        assertNotNull(complexServiceDeps);

        // Should contain LinkDetails for dependencies
        assertFalse(complexServiceDeps.isEmpty());
    }

    @Test
    void testBuildDetails_filtersByIncludeMask_returnsOnlyMatchingClasses() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        List<String> includeMasks = List.of("*.testee.*");
        List<String> excludeMasks = List.of();

        Map<String, Map<String, Set<LinkDetails>>> result = builder.buildDetails(dir, includeMasks, excludeMasks);

        // All keys should contain "testee" in their package name
        for (String key : result.keySet()) {
            assertTrue(key.contains("testee"), "Key " + key + " should contain 'testee'");
        }
    }

    @Test
    void testBuildDetails_filtersByExcludeMask_excludesMatchingClasses() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of("*FieldHolder*");

        Map<String, Map<String, Set<LinkDetails>>> result = builder.buildDetails(dir, includeMasks, excludeMasks);

        // FieldHolder should be excluded
        assertFalse(result.containsKey("spring.twin.testee.FieldHolder"));

        // Other classes should still be included
        assertTrue(result.containsKey("spring.twin.testee.ComplexService"));
    }

    @Test
    void testBuildDetails_filtersPrimitiveTypes_notInResult() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        List<String> includeMasks = List.of("spring.twin.testee.FieldHolder");
        List<String> excludeMasks = List.of();

        Map<String, Map<String, Set<LinkDetails>>> result = builder.buildDetails(dir, includeMasks, excludeMasks);

        assertTrue(result.containsKey("spring.twin.testee.FieldHolder"));
        Map<String, Set<LinkDetails>> dependencies = result.get("spring.twin.testee.FieldHolder");

        // Primitive types should not be in dependencies
        assertFalse(dependencies.containsKey("int"), "Primitive type 'int' should not be in dependencies");
        assertFalse(dependencies.containsKey("byte"), "Primitive type 'byte' should not be in dependencies");
        assertFalse(dependencies.containsKey("short"), "Primitive type 'short' should not be in dependencies");
        assertFalse(dependencies.containsKey("long"), "Primitive type 'long' should not be in dependencies");
        assertFalse(dependencies.containsKey("float"), "Primitive type 'float' should not be in dependencies");
        assertFalse(dependencies.containsKey("double"), "Primitive type 'double' should not be in dependencies");
        assertFalse(dependencies.containsKey("char"), "Primitive type 'char' should not be in dependencies");
        assertFalse(dependencies.containsKey("boolean"), "Primitive type 'boolean' should not be in dependencies");
        assertFalse(dependencies.containsKey("void"), "Primitive type 'void' should not be in dependencies");
    }

    @Test
    void testBuildDetails_resultStructure_correctNestedMap() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        // Include InheritanceChild but exclude InheritanceChildInterface
        List<String> includeMasks = List.of("spring.twin.testee.InheritanceChild");
        List<String> excludeMasks = List.of("*InheritanceChildInterface*");

        Map<String, Map<String, Set<LinkDetails>>> result = builder.buildDetails(dir, includeMasks, excludeMasks);

        // Structure: outer key FQCN -> inner key FQCN dependency -> Set<LinkDetails>
        assertEquals(1, result.size());
        assertTrue(result.containsKey("spring.twin.testee.InheritanceChild"));

        Map<String, Set<LinkDetails>> dependencies = result.get("spring.twin.testee.InheritanceChild");
        assertNotNull(dependencies);

        // Inner keys should be FQCN format
        for (String key : dependencies.keySet()) {
            assertFalse(key.contains("/"), "Inner key should be FQCN, not internal name: " + key);
            assertTrue(key.contains("."), "Inner key should be FQCN format: " + key);
        }

        // Values should be Set<LinkDetails>
        for (Set<LinkDetails> linkDetailsSet : dependencies.values()) {
            assertNotNull(linkDetailsSet);
            // Should contain at least one LinkDetails
            assertFalse(linkDetailsSet.isEmpty(), "LinkDetails set should not be empty");
        }
    }

    @Test
    void testBuildDetails_multipleClasses_allAggregated() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        List<String> includeMasks = List.of("spring.twin.testee.ComplexService", "spring.twin.testee.FieldHolder");
        List<String> excludeMasks = List.of();

        Map<String, Map<String, Set<LinkDetails>>> result = builder.buildDetails(dir, includeMasks, excludeMasks);

        // Both classes should be in the graph
        assertTrue(result.containsKey("spring.twin.testee.ComplexService"), "ComplexService should be in the graph");
        assertTrue(result.containsKey("spring.twin.testee.FieldHolder"), "FieldHolder should be in the graph");

        // Each should have its own dependencies
        Map<String, Set<LinkDetails>> complexServiceDeps = result.get("spring.twin.testee.ComplexService");
        Map<String, Set<LinkDetails>> fieldHolderDeps = result.get("spring.twin.testee.FieldHolder");

        assertNotNull(complexServiceDeps);
        assertNotNull(fieldHolderDeps);
    }

    @Test
    void testBuildDetails_mergeInnerClasses_mergesInnerToOuter() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        List<String> includeMasks = List.of("*.testee.Outer*");
        List<String> excludeMasks = List.of();

        Map<String, Map<String, Set<LinkDetails>>> result = builder.buildDetails(dir, includeMasks, excludeMasks, true);

        // Outer$Inner key should be absent after merge
        assertFalse(result.containsKey("spring.twin.testee.Outer$Inner"), "Inner class should be merged");

        // Outer should be present
        assertTrue(result.containsKey("spring.twin.testee.Outer"), "Outer class should be present");
    }

    @Test
    void testBuildDetails_noMergeInnerClasses_separateEntries() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        List<String> includeMasks = List.of("*.testee.Outer*");
        List<String> excludeMasks = List.of();

        Map<String, Map<String, Set<LinkDetails>>> result = builder.buildDetails(dir, includeMasks, excludeMasks, false);

        // Both Outer and Outer$Inner should be present as separate keys
        assertTrue(result.containsKey("spring.twin.testee.Outer"), "Outer class should be present");
        assertTrue(result.containsKey("spring.twin.testee.Outer$Inner"), "Inner class should be separate entry");
    }
}