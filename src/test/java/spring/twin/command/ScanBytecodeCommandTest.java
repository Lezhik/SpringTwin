package spring.twin.command;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import spring.twin.scan.LinkDetails;
import spring.twin.scan.MethodTypeExtractor;
import spring.twin.scan.ScanBytecodeParams;
import spring.twin.scan.ScanBytecodeService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        String result = command.scanBytecode(classesDir.toString(), outputFile.toString(), include, exclude, "true");

        assertTrue(result.contains("Dependencies written to"));
        assertTrue(result.contains(outputFile.toString()));
    }

    /**
     * Test: проверяет что scanBytecodeService.executeDetails() вызывается с правильными параметрами
     */
    @Test
    void testScanBytecode_callsServiceExecuteDetails() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        String include = "com.example.*;com.demo.*";
        String exclude = "*.internal.*";

        command.scanBytecode(classesDir.toString(), outputFile.toString(), include, exclude, "true");

        ArgumentCaptor<ScanBytecodeParams> paramsCaptor = ArgumentCaptor.forClass(ScanBytecodeParams.class);
        verify(scanBytecodeService).executeDetails(paramsCaptor.capture());
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

        command.scanBytecode(classesDir.toString(), outputFile.toString(), include, exclude, "true");

        ArgumentCaptor<ScanBytecodeParams> paramsCaptor = ArgumentCaptor.forClass(ScanBytecodeParams.class);
        verify(scanBytecodeService).executeDetails(paramsCaptor.capture());
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

        String result = realCommand.scanBytecode(nonExistingPath, outputFile.toString(), "", "", "true");

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

        command.scanBytecode(classesDir.toString(), outputFile.toString(), include, exclude, "true");

        ArgumentCaptor<ScanBytecodeParams> paramsCaptor = ArgumentCaptor.forClass(ScanBytecodeParams.class);
        verify(scanBytecodeService).executeDetails(paramsCaptor.capture());
        ScanBytecodeParams capturedParams = paramsCaptor.getValue();

        assertEquals(classesDir, capturedParams.classesDir());
        assertEquals(outputFile, capturedParams.outputFile());
        assertEquals(List.of("com.example.*", "org.test.*"), capturedParams.includeMasks());
        assertEquals(List.of("*.internal.*", "*.temp.*"), capturedParams.excludeMasks());
    }

    /**
     * Test: --merge-inner-classes true → params.mergeInnerClasses() == true
     */
    @Test
    void testScanBytecode_withMergeInnerClassesTrue_passesTrueToParams() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        String include = "com.example.*";
        String exclude = "*.internal.*";

        command.scanBytecode(classesDir.toString(), outputFile.toString(), include, exclude, "true");

        ArgumentCaptor<ScanBytecodeParams> paramsCaptor = ArgumentCaptor.forClass(ScanBytecodeParams.class);
        verify(scanBytecodeService).executeDetails(paramsCaptor.capture());
        ScanBytecodeParams capturedParams = paramsCaptor.getValue();

        assertEquals(true, capturedParams.mergeInnerClasses());
    }

    /**
     * Test: --merge-inner-classes false → params.mergeInnerClasses() == false
     */
    @Test
    void testScanBytecode_withMergeInnerClassesFalse_passesFalseToParams() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        String include = "com.example.*";
        String exclude = "*.internal.*";

        command.scanBytecode(classesDir.toString(), outputFile.toString(), include, exclude, "false");

        ArgumentCaptor<ScanBytecodeParams> paramsCaptor = ArgumentCaptor.forClass(ScanBytecodeParams.class);
        verify(scanBytecodeService).executeDetails(paramsCaptor.capture());
        ScanBytecodeParams capturedParams = paramsCaptor.getValue();

        assertEquals(false, capturedParams.mergeInnerClasses());
    }

    /**
     * Test: default value for --merge-inner-classes → mergeInnerClasses() == true
     */
    @Test
    void testScanBytecode_defaultMergeInnerClasses_isTrue() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        String include = "com.example.*";
        String exclude = "*.internal.*";

        // Pass "true" as default value (as specified in ShellOption defaultValue)
        command.scanBytecode(classesDir.toString(), outputFile.toString(), include, exclude, "true");

        ArgumentCaptor<ScanBytecodeParams> paramsCaptor = ArgumentCaptor.forClass(ScanBytecodeParams.class);
        verify(scanBytecodeService).executeDetails(paramsCaptor.capture());
        ScanBytecodeParams capturedParams = paramsCaptor.getValue();

        assertEquals(true, capturedParams.mergeInnerClasses());
    }

    /**
     * Test: invalid merge value → Boolean.parseBoolean("invalid") == false, mergeInnerClasses() == false
     */
    @Test
    void testScanBytecode_invalidMergeValue_treatedAsFalse() {
        Path classesDir = tempDir.resolve("classes");
        Path outputFile = tempDir.resolve("output.json");
        String include = "com.example.*";
        String exclude = "*.internal.*";

        command.scanBytecode(classesDir.toString(), outputFile.toString(), include, exclude, "invalid");

        ArgumentCaptor<ScanBytecodeParams> paramsCaptor = ArgumentCaptor.forClass(ScanBytecodeParams.class);
        verify(scanBytecodeService).executeDetails(paramsCaptor.capture());
        ScanBytecodeParams capturedParams = paramsCaptor.getValue();

        // Boolean.parseBoolean("invalid") returns false
        assertEquals(false, capturedParams.mergeInnerClasses());
    }

    /**
     * Test: verifies that output JSON contains LinkDetails structure (Map<String, Map<String, Set<LinkDetails>>>).
     * This test ensures the CLI produces the correct format with link type and details for each dependency.
     */
    @Test
    void testScanBytecode_outputJsonHasLinkDetailsStructure() throws IOException {
        // Use real service to test actual output format
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

        Path classesDir = Path.of("build/classes/java/test/spring/twin/testee");
        Path outputFile = tempDir.resolve("dependencies.json");

        // Execute the command with real service
        String result = realCommand.scanBytecode(classesDir.toString(), outputFile.toString(), "", "", "true");

        // Should succeed
        assertTrue(result.contains("Dependencies written to"), "Command should succeed: " + result);
        assertTrue(Files.exists(outputFile), "Output file should exist");

        // Parse the JSON and verify it has LinkDetails structure
        // Expected format: Map<String, Map<String, Set<LinkDetails>>>
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Try to parse as detailed format - this should work with correct implementation
        Map<String, Map<String, Set<LinkDetails>>> detailedGraph = null;
        try {
            detailedGraph = objectMapper.readValue(outputFile.toFile(), new TypeReference<>() {});
        } catch (Exception e) {
            // If parsing fails, the format is wrong (likely old format Map<String, List<String>>)
            // Read raw content to provide better error message
            String content = Files.readString(outputFile);
            assertTrue(content.contains("\"type\""),
                "Output JSON should contain 'type' field (LinkDetails format). Content: " + content);
            assertTrue(content.contains("\"details\""),
                "Output JSON should contain 'details' field (LinkDetails format). Content: " + content);
            throw new AssertionError("Output JSON is not in LinkDetails format. Content: " + content, e);
        }

        // Verify structure is correct
        assertNotNull(detailedGraph, "Parsed graph should not be null");
        assertFalse(detailedGraph.isEmpty(), "Graph should not be empty");

        // Verify each dependency has LinkDetails with type and details fields
        for (Map.Entry<String, Map<String, Set<LinkDetails>>> outerEntry : detailedGraph.entrySet()) {
            Map<String, Set<LinkDetails>> innerMap = outerEntry.getValue();
            assertNotNull(innerMap, "Inner map should not be null for " + outerEntry.getKey());
            
            for (Map.Entry<String, Set<LinkDetails>> innerEntry : innerMap.entrySet()) {
                Set<LinkDetails> linkDetailsSet = innerEntry.getValue();
                assertNotNull(linkDetailsSet, "LinkDetails set should not be null for " + innerEntry.getKey());
                assertFalse(linkDetailsSet.isEmpty(), "LinkDetails set should not be empty for " + innerEntry.getKey());
                
                // Verify each LinkDetails has type and details
                for (LinkDetails linkDetails : linkDetailsSet) {
                    assertNotNull(linkDetails.type(), "LinkDetails type should not be null");
                    assertNotNull(linkDetails.details(), "LinkDetails details should not be null");
                }
            }
        }
    }
}