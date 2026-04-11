package spring.twin.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import spring.twin.scan.AnnotationTypeExtractor;
import spring.twin.scan.BytecodeClassAnalyzer;
import spring.twin.scan.ClassFileScanner;
import spring.twin.scan.CodeUsageExtractor;
import spring.twin.scan.DependencyGraphBuilder;
import spring.twin.scan.DependencyJsonWriter;
import spring.twin.scan.FieldTypeExtractor;
import spring.twin.scan.InheritanceExtractor;
import spring.twin.scan.MethodTypeExtractor;
import spring.twin.scan.ScanBytecodeParams;
import spring.twin.scan.ScanBytecodeService;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link ScanBytecodeCommand}.
 */
class ScanBytecodeCommandTest {

    private ScanBytecodeService scanBytecodeService;
    private ScanBytecodeCommand command;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        scanBytecodeService = mock(ScanBytecodeService.class);
        command = new ScanBytecodeCommand(scanBytecodeService);
    }

    /**
     * Test: корректные параметры → сообщение об успехе с путём к файлу
     */
    @Test
    void testScanBytecode_validParams_returnsSuccessMessage() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        String include = "com.example.*";
        String exclude = "*.internal.*";

        String result = command.scanBytecode(classesDir.toString(), outputFile.toString(), include, exclude);

        assertTrue(result.contains("Dependencies written to"));
        assertTrue(result.contains(outputFile.toString()));
    }

    /**
     * Test: проверяет что scanBytecodeService.execute() вызывается с правильными параметрами
     */
    @Test
    void testScanBytecode_callsServiceExecute() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        String include = "com.example.*;com.demo.*";
        String exclude = "*.internal.*";

        command.scanBytecode(classesDir.toString(), outputFile.toString(), include, exclude);

        ArgumentCaptor<ScanBytecodeParams> paramsCaptor = ArgumentCaptor.forClass(ScanBytecodeParams.class);
        verify(scanBytecodeService).execute(paramsCaptor.capture());
        ScanBytecodeParams capturedParams = paramsCaptor.getValue();

        assertEquals(classesDir, capturedParams.classesDir());
        assertEquals(outputFile, capturedParams.outputFile());
    }

    /**
     * Test: пустые include/exclude → сервис вызывается с пустыми списками масок
     */
    @Test
    void testScanBytecode_emptyIncludeAndExclude() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        String include = "";
        String exclude = "";

        command.scanBytecode(classesDir.toString(), outputFile.toString(), include, exclude);

        ArgumentCaptor<ScanBytecodeParams> paramsCaptor = ArgumentCaptor.forClass(ScanBytecodeParams.class);
        verify(scanBytecodeService).execute(paramsCaptor.capture());
        ScanBytecodeParams capturedParams = paramsCaptor.getValue();

        assertEquals(List.of(), capturedParams.includeMasks());
        assertEquals(List.of(), capturedParams.excludeMasks());
    }

    /**
     * Test: несуществующая директория → исключение или сообщение об ошибке
     */
    @Test
    void testScanBytecode_nonExistingClassesDir_throwsException() {
        // Create real service with all real dependencies to test actual error handling
        ClassFileScanner classFileScanner = new ClassFileScanner();
        BytecodeClassAnalyzer bytecodeClassAnalyzer = new BytecodeClassAnalyzer(
                new InheritanceExtractor(),
                new FieldTypeExtractor(),
                new MethodTypeExtractor(),
                new AnnotationTypeExtractor(),
                new CodeUsageExtractor()
        );
        DependencyGraphBuilder dependencyGraphBuilder = new DependencyGraphBuilder(
                classFileScanner,
                bytecodeClassAnalyzer
        );
        DependencyJsonWriter dependencyJsonWriter = new DependencyJsonWriter();
        ScanBytecodeService realService = new ScanBytecodeService(
                dependencyGraphBuilder,
                dependencyJsonWriter
        );
        ScanBytecodeCommand realCommand = new ScanBytecodeCommand(realService);

        String nonExistingPath = "non_existing_directory";
        Path outputFile = tempDir.resolve("output.json");

        String result = realCommand.scanBytecode(nonExistingPath, outputFile.toString(), "", "");

        assertTrue(result.startsWith("Error:"));
    }

    /**
     * Test: проверяет корректность создания ScanBytecodeParams из строковых аргументов
     */
    @Test
    void testScanBytecode_createsCorrectParams() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        String include = "com.example.*;org.test.*";
        String exclude = "*.internal.*;*.temp.*";

        command.scanBytecode(classesDir.toString(), outputFile.toString(), include, exclude);

        ArgumentCaptor<ScanBytecodeParams> paramsCaptor = ArgumentCaptor.forClass(ScanBytecodeParams.class);
        verify(scanBytecodeService).execute(paramsCaptor.capture());
        ScanBytecodeParams capturedParams = paramsCaptor.getValue();

        assertEquals(classesDir, capturedParams.classesDir());
        assertEquals(outputFile, capturedParams.outputFile());
        assertEquals(List.of("com.example.*", "org.test.*"), capturedParams.includeMasks());
        assertEquals(List.of("*.internal.*", "*.temp.*"), capturedParams.excludeMasks());
    }
}