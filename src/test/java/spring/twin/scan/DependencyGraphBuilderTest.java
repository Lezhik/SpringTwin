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
 * Unit tests for {@link DependencyGraphBuilder}.
 * Tests use compiled sample classes from {@code src/test/java/spring/twin/testee/}.
 */
class DependencyGraphBuilderTest {

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
    void testBuild_scansDirectoryAndBuildsGraph() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        List<String> includeMasks = List.of("*.testee.*");
        List<String> excludeMasks = List.of();

        Map<String, Set<String>> result = builder.build(dir, includeMasks, excludeMasks);

        // Graph should contain entries for testee classes
        assertFalse(result.isEmpty());

        // Should contain ComplexService
        assertTrue(result.containsKey("spring.twin.testee.ComplexService"));

        // Should contain FieldHolder
        assertTrue(result.containsKey("spring.twin.testee.FieldHolder"));
    }

    @Test
    void testBuild_emptyMasks_includesAllClasses() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of();

        Map<String, Set<String>> result = builder.build(dir, includeMasks, excludeMasks);

        // All classes should be included
        assertTrue(result.containsKey("spring.twin.testee.ComplexService"));
        assertTrue(result.containsKey("spring.twin.testee.FieldHolder"));
        assertTrue(result.containsKey("spring.twin.testee.InheritanceBase"));
        assertTrue(result.containsKey("spring.twin.testee.InheritanceChild"));
    }

    @Test
    void testBuild_includeMask_filtersClasses() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        List<String> includeMasks = List.of("*.testee.*");
        List<String> excludeMasks = List.of();

        Map<String, Set<String>> result = builder.build(dir, includeMasks, excludeMasks);

        // All keys should contain "testee" in their package name
        for (String key : result.keySet()) {
            assertTrue(key.contains("testee"), "Key " + key + " should contain 'testee'");
        }
    }

    @Test
    void testBuild_excludeMask_excludesClasses() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of("*FieldHolder*");

        Map<String, Set<String>> result = builder.build(dir, includeMasks, excludeMasks);

        // FieldHolder should be excluded
        assertFalse(result.containsKey("spring.twin.testee.FieldHolder"));

        // Other classes should still be included
        assertTrue(result.containsKey("spring.twin.testee.ComplexService"));
    }

    @Test
    void testBuild_includeAndExclude_combined() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        List<String> includeMasks = List.of("*.testee.*");
        List<String> excludeMasks = List.of("*FieldHolder*");

        Map<String, Set<String>> result = builder.build(dir, includeMasks, excludeMasks);

        // Should only have testee package classes
        for (String key : result.keySet()) {
            assertTrue(key.contains("testee"));
        }

        // FieldHolder should be excluded even though it matches include
        assertFalse(result.containsKey("spring.twin.testee.FieldHolder"));

        // ComplexService should be included
        assertTrue(result.containsKey("spring.twin.testee.ComplexService"));
    }

    @Test
    void testBuild_dependenciesFilteredByMasks() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        // Include only testee package classes
        List<String> includeMasks = List.of("spring.twin.testee.*");
        List<String> excludeMasks = List.of();

        Map<String, Set<String>> result = builder.build(dir, includeMasks, excludeMasks);

        // All dependencies should also be from testee package
        for (Map.Entry<String, Set<String>> entry : result.entrySet()) {
            for (String dependency : entry.getValue()) {
                assertTrue(dependency.startsWith("spring.twin.testee."),
                        "Dependency " + dependency + " of " + entry.getKey() + " should start with 'spring.twin.testee.'");
            }
        }
    }

    @Test
    void testBuild_emptyDirectory_returnsEmptyMap() {
        Path emptyDir = testClassesDir.resolve("spring/twin/testee/service");
        // service directory exists but we use a mask that won't match anything
        List<String> includeMasks = List.of("*.nonexistent.*");
        List<String> excludeMasks = List.of();

        Map<String, Set<String>> result = builder.build(emptyDir, includeMasks, excludeMasks);

        assertTrue(result.isEmpty());
    }

    @Test
    void testBuild_sortedKeys() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of();

        Map<String, Set<String>> result = builder.build(dir, includeMasks, excludeMasks);

        // Extract keys and verify they are sorted
        List<String> keys = new ArrayList<>(result.keySet());
        List<String> sortedKeys = new ArrayList<>(keys);
        sortedKeys.sort(String::compareTo);

        assertEquals(sortedKeys, keys, "Keys should be sorted alphabetically");
    }

    @Test
    void testBuild_nonExistingDirectory_throwsException() {
        Path nonExistent = Path.of("C:/non/existent/directory/that/does/not/exist");
        List<String> includeMasks = List.of();
        List<String> excludeMasks = List.of();

        assertThrows(UncheckedIOException.class, () -> builder.build(nonExistent, includeMasks, excludeMasks));
    }
}