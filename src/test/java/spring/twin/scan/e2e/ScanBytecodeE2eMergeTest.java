package spring.twin.scan.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import spring.twin.scan.ScanBytecodeParams;
import spring.twin.scan.ScanBytecodeService;

import java.nio.file.Path;

/**
 * End-to-End tests for the --merge-inner-classes option.
 * <p>
 * Tests call ScanBytecodeService.execute() with various mergeInnerClasses values
 * and verify the output JSON file.
 */
@SpringBootTest
class ScanBytecodeE2eMergeTest {

    @TempDir
    Path tempDir;

    @Autowired
    ScanBytecodeService scanBytecodeService;

    Path classesDir;

    @BeforeEach
    void setUp() {
        classesDir = getTesteeClassesDir();
    }

    /**
     * Returns the path to compiled testee classes directory.
     *
     * @return path to build/classes/java/test/spring/twin/testee/
     */
    Path getTesteeClassesDir() {
        return Path.of("build/classes/java/test/spring/twin/testee/");
    }

    /**
     * Tests that with mergeInnerClasses=true, inner classes are merged into outer classes.
     * Outer$Inner should be absent from keys, and Outer should contain dependencies of the inner class.
     */
    @Test
    void e2e_mergeInnerClassesTrue_innerClassesMergedIntoOuter() {
        // TODO: Implement test
    }

    /**
     * Tests that with mergeInnerClasses=false, inner classes appear as separate entries in the graph.
     * Outer$Inner should be present as a separate key.
     */
    @Test
    void e2e_mergeInnerClassesFalse_innerClassesSeparate() {
        // TODO: Implement test
    }

    /**
     * Tests that the 4-parameter constructor of ScanBytecodeParams defaults mergeInnerClasses to true.
     */
    @Test
    void e2e_mergeInnerClassesDefault_isTrue() {
        // TODO: Implement test
    }

    /**
     * Tests that after merge, the outer class does not contain a self-reference.
     */
    @Test
    void e2e_mergeInnerClassesTrue_noSelfReference() {
        // TODO: Implement test
    }

    /**
     * Tests that when another class depends on Outer$Inner, after merge the dependency
     * is remapped to Outer.
     */
    @Test
    void e2e_mergeInnerClassesTrue_dependencyOnInnerClass_remappedToOuter() {
        // TODO: Implement test
    }

    /**
     * Tests that keys and values in the output JSON are sorted after merge.
     */
    @Test
    void e2e_mergeInnerClassesTrue_sortedOutput() {
        // TODO: Implement test
    }
}