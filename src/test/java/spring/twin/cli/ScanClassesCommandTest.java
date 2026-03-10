package spring.twin.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spring.twin.analysis.SpringDiAnalyzerService;
import spring.twin.dto.DiGraphDto;
import spring.twin.dto.DiNodeDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ScanClassesCommand}.
 * <p>
 * These tests verify the end-to-end functionality of the scan-classes command
 * including file scanning, filtering, DI analysis, and JSON output generation.
 */
class ScanClassesCommandTest {

    private static final String TEST_CLASSES_PATH = "build/classes/java/test";
    private static final String OUTPUT_FILE = "build/reports/tests/di.json";

    private Path outputFile;
    private ScanClassesCommand command;

    @BeforeEach
    void setUp() throws IOException {
        // Use fixed output path for all tests
        outputFile = Path.of(OUTPUT_FILE);

        // Delete output file if it exists from previous runs
        if (Files.exists(outputFile)) {
            Files.delete(outputFile);
        }

        // Ensure parent directory exists
        Files.createDirectories(outputFile.getParent());

        // Create command with real analyzer service
        SpringDiAnalyzerService analyzerService = new SpringDiAnalyzerService();
        command = new ScanClassesCommand(analyzerService);
    }

    @Test
    void shouldGenerateDiJsonFile() throws Exception {
        // Given: Test classes are available in build/classes/java/test
        Path classesPath = Path.of(TEST_CLASSES_PATH);
        assertThat(classesPath).exists().isDirectory();

        // When: Execute scan-classes command
        String[] args = new String[]{
            "scan-classes",
            "--classes", classesPath.toString(),
            "--output", outputFile.toString(),
            "--include", "spring.twin.analysis.fixtures.*"
        };

        command.run(args);

        // Then: Verify output file was created
        assertThat(outputFile)
            .exists()
            .isRegularFile();

        // Parse the generated JSON
        ObjectMapper mapper = new ObjectMapper();
        DiGraphDto graph = mapper.readValue(outputFile.toFile(), DiGraphDto.class);

        // Verify graph structure
        assertThat(graph).isNotNull();
        assertThat(graph.nodes()).isNotNull();
        assertThat(graph.edges()).isNotNull();

        // Verify presence of expected Spring components
        assertThat(graph.nodes())
            .extracting(DiNodeDto::name)
            .contains(
                "ServiceClass",
                "ControllerClass",
                "RepositoryClass",
                "PaymentService",
                "InterfaceBasedService",
                "InterfaceBasedController",
                "OrderService",
                "StripePaymentProcessor",
                "ConcreteComponentClass",
                "SecondInterfaceImplementation",
                "ServiceWithBaseClassDependency",
                "ServiceInjectingParentClass"
            );

        // Verify absence of non-component classes
        assertThat(graph.nodes())
            .extracting(DiNodeDto::name)
            .doesNotContain(
                "BaseNonComponentClass",
                "ChildClass",
                "GrandchildClass",
                "ImplementingClass",
                "OuterClass",
                "ParentClass",
                "PaymentProcessor",
                "TestInterface"
            );

        // Verify presence of expected dependency edges
        assertThat(graph.edges())
            .anyMatch(edge ->
                edge.from().contains("ControllerClass") &&
                edge.to().contains("ServiceClass"));

        assertThat(graph.edges())
            .anyMatch(edge ->
                edge.from().contains("ControllerClass") &&
                edge.to().contains("RepositoryClass"));

        assertThat(graph.edges())
            .anyMatch(edge ->
                edge.from().contains("PaymentService") &&
                edge.to().contains("StripePaymentProcessor"));

        // Verify edge types are DEPENDS_ON
        assertThat(graph.edges())
            .allMatch(edge -> edge.type().toString().equals("DEPENDS_ON"));

        // Verify nodes have correct structure with labels
        assertThat(graph.nodes())
            .allMatch(node ->
                node.type().equals("Class") &&
                node.packageName().equals("spring.twin.analysis.fixtures") &&
                node.labels() != null &&
                !node.labels().isEmpty());

        // Verify specific labels for component types
        Map<String, java.util.List<String>> nodesByName = graph.nodes().stream()
            .collect(Collectors.toMap(
                    DiNodeDto::name,
                    DiNodeDto::labels
            ));

        assertThat(nodesByName.get("ServiceClass")).contains("Service");
        assertThat(nodesByName.get("ControllerClass")).contains("Controller");
        assertThat(nodesByName.get("RepositoryClass")).contains("Repository");
        assertThat(nodesByName.get("OrderService")).contains("Configuration");
    }

    @Test
    void shouldRespectIncludeExcludeFilters() throws Exception {
        // Given: Test classes are available
        Path classesPath = Path.of(TEST_CLASSES_PATH);

        // When: Execute scan-classes command with exclude filter
        // Exclude pattern matches FQCNs containing ".InterfaceBasedController" 
        String[] args = new String[]{
            "scan-classes",
            "--classes", classesPath.toString(),
            "--output", outputFile.toString(),
            "--include", "spring.twin.analysis.fixtures.*",
            "--exclude", ".InterfaceBasedController"
        };

        command.run(args);

        // Then: Verify output file was created
        assertThat(outputFile).exists();

        // Parse the generated JSON
        ObjectMapper mapper = new ObjectMapper();
        DiGraphDto graph = mapper.readValue(outputFile.toFile(), DiGraphDto.class);

        // Verify InterfaceBasedController is excluded
        assertThat(graph.nodes())
            .extracting(DiNodeDto::name)
            .doesNotContain("InterfaceBasedController");

        // Verify that InterfaceBasedService is still included (it's not excluded)
        assertThat(graph.nodes())
            .extracting(DiNodeDto::name)
            .contains("InterfaceBasedService");

        // Verify other services are still included
        assertThat(graph.nodes())
            .extracting(DiNodeDto::name)
            .contains("ServiceClass", "PaymentService");
    }

    @Test
    void shouldGenerateEmptyGraphForNonMatchingFilter() throws Exception {
        // Given: Test classes are available
        Path classesPath = Path.of(TEST_CLASSES_PATH);

        // When: Execute scan-classes command with filter that matches nothing
        String[] args = new String[]{
            "scan-classes",
            "--classes", classesPath.toString(),
            "--output", outputFile.toString(),
            "--include", "com.nonexistent.package.*"
        };

        command.run(args);

        // Then: Verify output file was created
        assertThat(outputFile).exists();

        // Parse the generated JSON
        ObjectMapper mapper = new ObjectMapper();
        DiGraphDto graph = mapper.readValue(outputFile.toFile(), DiGraphDto.class);

        // Verify graph is empty
        assertThat(graph.nodes()).isEmpty();
        assertThat(graph.edges()).isEmpty();
    }
}