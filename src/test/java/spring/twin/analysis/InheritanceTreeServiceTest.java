package spring.twin.analysis;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link InheritanceTreeService}.
 */
class InheritanceTreeServiceTest {

    private final InheritanceTreeService service = new InheritanceTreeService();

    /**
     * Array of all fixture classes used in tests.
     */
    private static final Class<?>[] FIXTURE_CLASSES = {
        spring.twin.analysis.fixtures.ParentClass.class,
        spring.twin.analysis.fixtures.ChildClass.class,
        spring.twin.analysis.fixtures.GrandchildClass.class,
        spring.twin.analysis.fixtures.TestInterface.class,
        spring.twin.analysis.fixtures.ImplementingClass.class,
        spring.twin.analysis.fixtures.OuterClass.class,
        spring.twin.analysis.fixtures.OuterClass.InnerClass.class
    };

    /**
     * Builds a map of class names to their file paths from the fixture classes array.
     *
     * @return map of fully qualified class names to Path objects
     */
    private Map<String, Path> buildFixtureClassMap() {
        return Arrays.stream(FIXTURE_CLASSES)
            .collect(Collectors.toMap(
                Class::getName,
                this::getClassFilePath
            ));
    }

    @Test
    void buildTree_shouldReturnEmptyMap_forEmptyInput() {
        Map<String, Set<String>> result = service.buildTree(Map.of());

        assertThat(result).isEmpty();
    }

    @Test
    void buildTree_shouldBuildTree_forClassHierarchy() {
        Map<String, Path> input = buildFixtureClassMap();
        Map<String, Set<String>> result = service.buildTree(input);

        // ParentClass is extended by ChildClass
        assertThat(result)
            .containsKey("spring.twin.analysis.fixtures.ParentClass")
            .containsEntry("spring.twin.analysis.fixtures.ParentClass",
                Set.of("spring.twin.analysis.fixtures.ChildClass"));
    }

    @Test
    void buildTree_shouldBuildTree_forInterfaceImplementation() {
        Map<String, Path> input = buildFixtureClassMap();
        Map<String, Set<String>> result = service.buildTree(input);

        // TestInterface is implemented by ImplementingClass
        assertThat(result)
            .containsKey("spring.twin.analysis.fixtures.TestInterface")
            .containsEntry("spring.twin.analysis.fixtures.TestInterface",
                Set.of("spring.twin.analysis.fixtures.ImplementingClass"));
    }

    @Test
    void buildTree_shouldHandleInnerClasses() {
        Map<String, Path> input = buildFixtureClassMap();
        Map<String, Set<String>> result = service.buildTree(input);

        // Inner class name should be normalized with dots
        assertThat(result)
            .containsKey("spring.twin.analysis.fixtures.OuterClass")
            .containsEntry("spring.twin.analysis.fixtures.OuterClass",
                Set.of("spring.twin.analysis.fixtures.OuterClass.InnerClass"));
    }

    @Test
    void buildTree_shouldSkipNonExistentFiles() {
        Map<String, Path> input = Map.of(
            "com.example.NonExistent", Path.of("/nonexistent/path/NonExistent.class")
        );

        Map<String, Set<String>> result = service.buildTree(input);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllDescendants_shouldReturnAllDescendantsRecursively() {
        Map<String, Path> input = buildFixtureClassMap();
        Map<String, Set<String>> tree = service.buildTree(input);

        Set<String> descendants = service.findAllDescendants("spring.twin.analysis.fixtures.ParentClass", tree);

        // Parent -> Child -> Grandchild
        assertThat(descendants)
            .containsExactlyInAnyOrder(
                "spring.twin.analysis.fixtures.ChildClass",
                "spring.twin.analysis.fixtures.GrandchildClass"
            );
    }

    @Test
    void findAllDescendants_shouldReturnEmptySet_forLeafClass() {
        Map<String, Path> input = buildFixtureClassMap();
        Map<String, Set<String>> tree = service.buildTree(input);

        // GrandchildClass is a leaf (no children in the fixture set)
        Set<String> descendants = service.findAllDescendants("spring.twin.analysis.fixtures.GrandchildClass", tree);

        assertThat(descendants).isEmpty();
    }

    @Test
    void findAllDescendants_shouldReturnEmptySet_forUnknownClass() {
        Map<String, Set<String>> tree = Map.of(
            "some.ExistingClass", Set.of("some.ChildClass")
        );

        Set<String> descendants = service.findAllDescendants("nonexistent.Class", tree);

        assertThat(descendants).isEmpty();
    }

    @Test
    void parseClassBytes_shouldReturnNull_forInvalidMagicNumber() {
        byte[] invalidBytes = new byte[] { 0, 0, 0, 0 };

        var info = service.parseClassBytes(invalidBytes);

        assertThat(info).isNull();
    }

    /**
     * Gets the filesystem path to a compiled class file.
     *
     * @param clazz the class
     * @return path to the .class file
     */
    private Path getClassFilePath(Class<?> clazz) {
        String className = clazz.getName();
        String classFilePath = className.replace('.', '/') + ".class";

        // Get the classpath root for test classes
        Path testClassesDir = Path.of("build/classes/java/test");
        return testClassesDir.resolve(classFilePath);
    }
}