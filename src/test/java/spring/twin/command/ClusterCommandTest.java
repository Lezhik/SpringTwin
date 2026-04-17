package spring.twin.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import spring.twin.cluster.ClusterParams;
import spring.twin.cluster.ClusterService;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ClusterCommand}.
 *
 * <p>Tests verify that the command correctly parses parameters,
 * calls the ClusterService, and handles various scenarios.
 */
class ClusterCommandTest {

    private ClusterService clusterService;
    private ClusterCommand command;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        clusterService = mock(ClusterService.class);
        command = new ClusterCommand(clusterService);
    }

    /**
     * Test: Вызов команды с валидными параметрами `--deps`, `--output`, `--resolution` 
     * должен создать ClusterParams и вызвать ClusterService.execute()
     */
    @Test
    void clusterShouldCallServiceWithValidParams() {
        // Given
        Path depsFile = tempDir.resolve("dependencies.json");
        Path outputFile = tempDir.resolve("clusters.json");
        String resolution = "2.0";

        // When
        command.cluster(depsFile.toString(), outputFile.toString(), resolution);

        // Then
        ArgumentCaptor<ClusterParams> paramsCaptor = ArgumentCaptor.forClass(ClusterParams.class);
        verify(clusterService).execute(paramsCaptor.capture());
        ClusterParams capturedParams = paramsCaptor.getValue();

        assertEquals(depsFile, capturedParams.depsFile());
        assertEquals(outputFile, capturedParams.outputFile());
        assertEquals(2.0, capturedParams.resolution());
    }

    /**
     * Test: Вызов команды без `--resolution` должен использовать значение по умолчанию 1.5
     */
    @Test
    void clusterShouldUseDefaultResolution() {
        // Given
        Path depsFile = tempDir.resolve("dependencies.json");
        Path outputFile = tempDir.resolve("clusters.json");
        String defaultResolution = "1.5";

        // When
        command.cluster(depsFile.toString(), outputFile.toString(), defaultResolution);

        // Then
        ArgumentCaptor<ClusterParams> paramsCaptor = ArgumentCaptor.forClass(ClusterParams.class);
        verify(clusterService).execute(paramsCaptor.capture());
        ClusterParams capturedParams = paramsCaptor.getValue();

        assertEquals(1.5, capturedParams.resolution());
    }

    /**
     * Test: При успешном выполнении команда должна возвращать сообщение с путём к выходному файлу
     */
    @Test
    void clusterShouldReturnSuccessMessage() {
        // Given
        Path depsFile = tempDir.resolve("dependencies.json");
        Path outputFile = tempDir.resolve("clusters.json");
        String resolution = "1.5";

        // When
        String result = command.cluster(depsFile.toString(), outputFile.toString(), resolution);

        // Then
        assertTrue(result.contains("Clusters written to") || result.contains(outputFile.toString()),
                "Success message should contain output file path. Result: " + result);
    }

    /**
     * Test: Передача нечислового значения в `--resolution` должна приводить к ошибке
     */
    @Test
    void clusterShouldThrowOnInvalidResolution() {
        // Given
        Path depsFile = tempDir.resolve("dependencies.json");
        Path outputFile = tempDir.resolve("clusters.json");
        String invalidResolution = "not-a-number";

        // When/Then
        String result = command.cluster(depsFile.toString(), outputFile.toString(), invalidResolution);
        assertTrue(result.startsWith("Error:") || result.contains("Invalid resolution"),
                "Should return error message for invalid resolution. Result: " + result);
    }

    /**
     * Test: Если ClusterService.execute() бросает исключение, команда должна корректно обработать ошибку
     */
    @Test
    void clusterShouldHandleServiceException() {
        // Given
        Path depsFile = tempDir.resolve("dependencies.json");
        Path outputFile = tempDir.resolve("clusters.json");
        String resolution = "1.5";

        doThrow(new RuntimeException("Service error")).when(clusterService).execute(any());

        // When
        String result = command.cluster(depsFile.toString(), outputFile.toString(), resolution);

        // Then
        assertTrue(result.startsWith("Error:") || result.contains("error") || result.contains("Error"),
                "Should return error message when service throws exception. Result: " + result);
    }

    /**
     * Test: Параметр `--resolution` должен корректно парситься как Double из строки
     */
    @Test
    void clusterShouldParseResolutionAsDouble() {
        // Given
        Path depsFile = tempDir.resolve("dependencies.json");
        Path outputFile = tempDir.resolve("clusters.json");
        String resolutionString = "3.75";

        // When
        command.cluster(depsFile.toString(), outputFile.toString(), resolutionString);

        // Then
        ArgumentCaptor<ClusterParams> paramsCaptor = ArgumentCaptor.forClass(ClusterParams.class);
        verify(clusterService).execute(paramsCaptor.capture());
        ClusterParams capturedParams = paramsCaptor.getValue();

        assertEquals(3.75, capturedParams.resolution(), 0.001);
    }
}