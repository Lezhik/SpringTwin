package spring.twin.bytecode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link BytecodeReaderService}.
 * <p>
 * Tests use real compiled class files from build/classes/java/test directory.
 */
class BytecodeReaderServiceTest {

    private static final String BUILD_CLASSES_DIR = "build/classes/java/test";
    private static final String BUILD_MAIN_CLASSES_DIR = "build/classes/java/main";

    private BytecodeReaderService bytecodeReader;

    @BeforeEach
    void setUp() {
        bytecodeReader = new BytecodeReaderService();
    }

    // ========================================================================
    // Tests for readConstantPool method
    // ========================================================================

    @Test
    void shouldReadConstantPoolForGrandchildClass() throws IOException {
        // Given
        byte[] classBytes = loadClassBytes("spring/twin/analysis/fixtures/GrandchildClass.class");

        // When
        BytecodeReaderService.ConstantPoolData poolData = bytecodeReader.readConstantPool(classBytes);

        // Then
        assertThat(poolData).isNotNull();
        assertThat(poolData.inputStream()).isNotNull();

        // Collect non-null UTF-8 strings (array contains nulls for non-UTF8 entries)
        var utf8List = java.util.Arrays.stream(poolData.utf8Strings())
            .filter(Objects::nonNull)
            .toList();

        // Verify specific classes from constant pool
        // GrandchildClass extends ChildClass - both must be in constant pool
        assertThat(utf8List)
            .contains(
                "spring/twin/analysis/fixtures/GrandchildClass",
                "spring/twin/analysis/fixtures/ChildClass"
            );

        // Verify class name indices are populated for expected classes
        assertThat(poolData.classNameIndices()).isNotEmpty();
        // At least 2 class entries (this class, superclass ChildClass)
        long classEntryCount = java.util.Arrays.stream(poolData.classNameIndices())
            .filter(idx -> idx > 0)
            .count();
        assertThat(classEntryCount).isGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldReadConstantPoolForTestInterface() throws IOException {
        // Given
        byte[] classBytes = loadClassBytes("spring/twin/analysis/fixtures/TestInterface.class");

        // When
        BytecodeReaderService.ConstantPoolData poolData = bytecodeReader.readConstantPool(classBytes);

        // Then
        assertThat(poolData).isNotNull();
        assertThat(poolData.inputStream()).isNotNull();

        // Collect non-null UTF-8 strings (array contains nulls for non-UTF8 entries)
        var utf8List = java.util.Arrays.stream(poolData.utf8Strings())
            .filter(Objects::nonNull)
            .toList();

        // Verify specific class names from constant pool
        assertThat(utf8List)
            .contains("spring/twin/analysis/fixtures/TestInterface");

        // Verify class name indices are populated
        assertThat(poolData.classNameIndices()).isNotEmpty();
        long classEntryCount = java.util.Arrays.stream(poolData.classNameIndices())
            .filter(idx -> idx > 0)
            .count();
        assertThat(classEntryCount).isGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldReadConstantPoolForIncludeExcludeFilterTest() throws IOException {
        // Given
        byte[] classBytes = loadClassBytes("spring/twin/scanner/IncludeExcludeFilterTest.class");

        // When
        BytecodeReaderService.ConstantPoolData poolData = bytecodeReader.readConstantPool(classBytes);

        // Then
        assertThat(poolData).isNotNull();
        assertThat(poolData.inputStream()).isNotNull();

        // Collect non-null UTF-8 strings (array contains nulls for non-UTF8 entries)
        var utf8List = java.util.Arrays.stream(poolData.utf8Strings())
            .filter(Objects::nonNull)
            .toList();

        // Verify specific class names from constant pool
        // The test class itself and classes it uses
        assertThat(utf8List)
            .contains(
                "spring/twin/scanner/IncludeExcludeFilterTest",
                "spring/twin/scanner/IncludeExcludeFilter"
            );

        // Verify class name indices are populated
        assertThat(poolData.classNameIndices()).isNotEmpty();
        long classEntryCount = java.util.Arrays.stream(poolData.classNameIndices())
            .filter(idx -> idx > 0)
            .count();
        // IncludeExcludeFilterTest references many classes (assertj, junit, etc.)
        assertThat(classEntryCount).isGreaterThanOrEqualTo(5);
    }

    @Test
    void shouldReturnNullForInvalidClassBytes() {
        // Given
        byte[] invalidBytes = new byte[]{0x00, 0x01, 0x02, 0x03};

        // When
        BytecodeReaderService.ConstantPoolData poolData = bytecodeReader.readConstantPool(invalidBytes);

        // Then
        assertThat(poolData).isNull();
    }

    @Test
    void shouldReadConstantPoolForMainIncludeExcludeFilter() throws IOException {
        // Given - use main class (not test class) to verify references from method calls, fields, etc.
        byte[] classBytes = loadClassBytesFromMain("spring/twin/scanner/IncludeExcludeFilter.class");

        // When
        BytecodeReaderService.ConstantPoolData poolData = bytecodeReader.readConstantPool(classBytes);

        // Then
        assertThat(poolData).isNotNull();
        assertThat(poolData.inputStream()).isNotNull();

        // Collect non-null UTF-8 strings
        var utf8List = java.util.Arrays.stream(poolData.utf8Strings())
            .filter(Objects::nonNull)
            .toList();

        // Verify this class is in constant pool
        assertThat(utf8List).contains("spring/twin/scanner/IncludeExcludeFilter");

        // Verify references to field types (List, ArrayList, etc.)
        assertThat(utf8List).contains("java/util/List");

        // Verify references from method signatures - constructor and methods
        assertThat(utf8List)
            .contains(
                "<init>",                    // Constructor name
                "matches",                   // Method name
                "getIncludeFilters",         // Getter method name
                "getExcludeFilters",         // Getter method name
                "()Z",                       // boolean return type descriptor
                "(Ljava/lang/String;)Z"      // matches(String) method descriptor
            );

        // Verify class name indices are populated
        assertThat(poolData.classNameIndices()).isNotEmpty();
        long classEntryCount = java.util.Arrays.stream(poolData.classNameIndices())
            .filter(idx -> idx > 0)
            .count();
        // IncludeExcludeFilter references many classes: List, ArrayList, FqcnFilter, Collections, etc.
        assertThat(classEntryCount).isGreaterThanOrEqualTo(5);
    }

    // ========================================================================
    // Tests for extractClassName method
    // ========================================================================

    @Test
    void shouldExtractClassNameForParentClass() throws IOException {
        // Given
        byte[] classBytes = loadClassBytes("spring/twin/analysis/fixtures/ParentClass.class");
        BytecodeReaderService.ConstantPoolData poolData = bytecodeReader.readConstantPool(classBytes);

        // When
        String className = bytecodeReader.extractClassName(poolData);

        // Then
        assertThat(className).isEqualTo("spring.twin.analysis.fixtures.ParentClass");
    }

    @Test
    void shouldExtractClassNameForTasksDtoSerializationTest() throws IOException {
        // Given
        byte[] classBytes = loadClassBytes("spring/twin/dto/TasksDtoSerializationTest.class");
        BytecodeReaderService.ConstantPoolData poolData = bytecodeReader.readConstantPool(classBytes);

        // When
        String className = bytecodeReader.extractClassName(poolData);

        // Then
        assertThat(className).isEqualTo("spring.twin.dto.TasksDtoSerializationTest");
    }

    @Test
    void shouldReturnNullForNullPoolData() {
        // When
        String className = bytecodeReader.extractClassName(null);

        // Then
        assertThat(className).isNull();
    }

    // ========================================================================
    // Tests for extractInheritanceInfo method
    // ========================================================================

    @Test
    void shouldExtractInheritanceInfoForGrandchildClass() throws IOException {
        // Given
        byte[] classBytes = loadClassBytes("spring/twin/analysis/fixtures/GrandchildClass.class");
        BytecodeReaderService.ConstantPoolData poolData = bytecodeReader.readConstantPool(classBytes);

        // When
        BytecodeReaderService.InheritanceInfo info = bytecodeReader.extractInheritanceInfo(poolData);

        // Then
        assertThat(info).isNotNull();
        assertThat(info.superClassName()).isEqualTo("spring.twin.analysis.fixtures.ChildClass");
        assertThat(info.interfaceNames()).isEmpty();
    }

    @Test
    void shouldExtractInheritanceInfoForChildClass() throws IOException {
        // Given
        byte[] classBytes = loadClassBytes("spring/twin/analysis/fixtures/ChildClass.class");
        BytecodeReaderService.ConstantPoolData poolData = bytecodeReader.readConstantPool(classBytes);

        // When
        BytecodeReaderService.InheritanceInfo info = bytecodeReader.extractInheritanceInfo(poolData);

        // Then
        assertThat(info).isNotNull();
        assertThat(info.superClassName()).isEqualTo("spring.twin.analysis.fixtures.ParentClass");
        assertThat(info.interfaceNames()).isEmpty();
    }

    @Test
    void shouldExtractInheritanceInfoForTestInterface() throws IOException {
        // Given
        byte[] classBytes = loadClassBytes("spring/twin/analysis/fixtures/TestInterface.class");
        BytecodeReaderService.ConstantPoolData poolData = bytecodeReader.readConstantPool(classBytes);

        // When
        BytecodeReaderService.InheritanceInfo info = bytecodeReader.extractInheritanceInfo(poolData);

        // Then
        assertThat(info).isNotNull();
        // Interfaces have java.lang.Object as superclass in bytecode (index 0 means no superclass,
        // but in practice interfaces often reference Object)
        assertThat(info.superClassName()).isIn(null, "java.lang.Object");
        // Simple interface with no extends clause has no superinterfaces
        assertThat(info.interfaceNames()).isEmpty();
    }

    @Test
    void shouldExtractInheritanceInfoForImplementingClass() throws IOException {
        // Given
        byte[] classBytes = loadClassBytes("spring/twin/analysis/fixtures/ImplementingClass.class");
        BytecodeReaderService.ConstantPoolData poolData = bytecodeReader.readConstantPool(classBytes);

        // When
        BytecodeReaderService.InheritanceInfo info = bytecodeReader.extractInheritanceInfo(poolData);

        // Then
        assertThat(info).isNotNull();
        assertThat(info.superClassName()).isEqualTo("java.lang.Object");
        assertThat(info.interfaceNames()).containsExactly("spring.twin.analysis.fixtures.TestInterface");
    }

    @Test
    void shouldReturnNullInheritanceInfoForNullPoolData() {
        // When
        BytecodeReaderService.InheritanceInfo info = bytecodeReader.extractInheritanceInfo(null);

        // Then
        assertThat(info).isNull();
    }

    // ========================================================================
    // Tests for parseClassAnnotations method
    // ========================================================================

    @Test
    void shouldReturnEmptyAnnotationsForBaseNonComponentClass() throws IOException {
        // Given - class without any Spring annotations
        byte[] classBytes = loadClassBytes("spring/twin/analysis/fixtures/BaseNonComponentClass.class");

        // When
        Set<String> annotations = bytecodeReader.parseClassAnnotations(classBytes);

        // Then
        assertThat(annotations).isEmpty();
    }

    @Test
    void shouldReturnComponentAnnotationForConcreteComponentClass() throws IOException {
        // Given - class with @Component annotation
        byte[] classBytes = loadClassBytes("spring/twin/analysis/fixtures/ConcreteComponentClass.class");

        // When
        Set<String> annotations = bytecodeReader.parseClassAnnotations(classBytes);

        // Then
        assertThat(annotations).hasSize(1);
        assertThat(annotations).contains("Lorg/springframework/stereotype/Component;");
    }

    // ========================================================================
    // Helper methods
    // ========================================================================

    /**
     * Loads class bytes from the build classes directory.
     *
     * @param relativePath the relative path to the .class file (using / as separator)
     * @return the class file bytes
     * @throws IOException if the file cannot be read
     */
    private byte[] loadClassBytes(String relativePath) throws IOException {
        Path classFilePath = Paths.get(BUILD_CLASSES_DIR, relativePath);
        return Files.readAllBytes(classFilePath);
    }

    /**
     * Loads class bytes from the build main classes directory.
     *
     * @param relativePath the relative path to the .class file (using / as separator)
     * @return the class file bytes
     * @throws IOException if the file cannot be read
     */
    private byte[] loadClassBytesFromMain(String relativePath) throws IOException {
        Path classFilePath = Paths.get(BUILD_MAIN_CLASSES_DIR, relativePath);
        return Files.readAllBytes(classFilePath);
    }
}