package spring.twin.scan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ClassFileScanner}.
 * Uses real .class files from build/classes/java/test/spring/twin/testee.
 */
class ClassFileScannerTest {

    private ClassFileScanner scanner;
    private Path testClassesDir;

    @BeforeEach
    void setUp() {
        scanner = new ClassFileScanner();
        testClassesDir = Path.of("build/classes/java/test");
    }

    // Tests for scan method

    @Test
    void testScan_singleClassFile_returnsSingletonList() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        
        List<Path> result = scanner.scan(dir);
        
        // Look for Service.class specifically
        assertTrue(result.stream().anyMatch(p -> p.getFileName().toString().equals("Service.class")));
    }

    @Test
    void testScan_multipleClassFiles_returnsAll() {
        Path dir = testClassesDir.resolve("spring/twin/testee/service");
        
        List<Path> result = scanner.scan(dir);
        
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getFileName().toString().equals("Service.class")));
        assertTrue(result.stream().anyMatch(p -> p.getFileName().toString().equals("OrderService.class")));
    }

    @Test
    void testScan_nestedDirectories_returnsAll() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        
        List<Path> result = scanner.scan(dir);
        
        // Should find Service.class, OrderService.class, Outer.class, Outer$Inner.class
        assertEquals(4, result.size());
    }

    @Test
    void testScan_ignoresNonClassFiles() {
        Path dir = testClassesDir.resolve("spring/twin/testee");
        
        List<Path> result = scanner.scan(dir);
        
        // All files should have .class extension
        assertTrue(result.stream().allMatch(p -> p.getFileName().toString().endsWith(".class")));
    }

    @Test
    void testScan_nonExistentDir_throwsUncheckedIOException() {
        Path nonExistent = Path.of("C:/non/existent/directory/that/does/not/exist");

        assertThrows(UncheckedIOException.class, () -> scanner.scan(nonExistent));
    }

    // Tests for toClassName method

    @Test
    void testToClassName_simpleClass_returnsFqcn() {
        Path classFile = testClassesDir.resolve("spring/twin/testee/service/Service.class");

        Optional<String> result = scanner.toClassName(testClassesDir, classFile);

        assertEquals(Optional.of("spring.twin.testee.service.Service"), result);
    }

    @Test
    void testToClassName_nestedPackage_returnsFqcn() {
        Path classFile = testClassesDir.resolve("spring/twin/testee/service/OrderService.class");

        Optional<String> result = scanner.toClassName(testClassesDir, classFile);

        assertEquals(Optional.of("spring.twin.testee.service.OrderService"), result);
    }

    @Test
    void testToClassName_defaultPackage_returnsSimpleClassName() {
        Path classFile = Path.of("Service.class");

        Optional<String> result = scanner.toClassName(Path.of("."), classFile);

        assertEquals(Optional.of("Service"), result);
    }

    @Test
    void testToClassName_innerClass_returnsFqcnWithDollar() {
        Path classFile = testClassesDir.resolve("spring/twin/testee/Outer$Inner.class");

        Optional<String> result = scanner.toClassName(testClassesDir, classFile);

        assertEquals(Optional.of("spring.twin.testee.Outer$Inner"), result);
    }

    @Test
    void testToClassName_nonClassFile_returnsEmpty() {
        Path nonClassFile = testClassesDir.resolve("spring/twin/testee/service/Service.java");

        Optional<String> result = scanner.toClassName(testClassesDir, nonClassFile);

        assertEquals(Optional.empty(), result);
    }
}