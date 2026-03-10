package spring.twin.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;
import spring.twin.analysis.SpringDiAnalyzerService;
import spring.twin.dto.DiEdgeDto;
import spring.twin.dto.DiGraphDto;
import spring.twin.dto.DiNodeDto;
import spring.twin.scanner.ClassScanningService;
import spring.twin.scanner.IncludeExcludeFilter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CLI command for scanning .class files and extracting Spring DI dependencies.
 * <p>
 * This command implements the {@code scan-classes} subcommand as specified in the CLI spec:
 * <pre>
 *     spring-twin scan-classes
 *       --classes <path>
 *       --output <file>
 *       --include <mask>
 *       --exclude <mask>
 * </pre>
 *
 * <p>The command delegates to:
 * <ul>
 *   <li>{@link ClassScanningService} - for scanning class files</li>
 *   <li>{@link IncludeExcludeFilter} - for FQCN filtering</li>
 *   <li>{@link SpringDiAnalyzerService} - for DI analysis</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScanClassesCommand implements CommandLineRunner, ExitCodeGenerator {

    private final SpringDiAnalyzerService analyzerService;

    private int exitCode = 0;

    @Override
    public void run(String... args) throws Exception {
        // Check if this is the scan-classes command
        if (args.length == 0 || !"scan-classes".equals(args[0])) {
            // Not our command, skip
            return;
        }

        log.info("Executing scan-classes command");

        try {
            // Parse arguments
            CommandArgs commandArgs = parseArgs(args);

            // Validate required arguments
            if (commandArgs.classesPath == null) {
                System.err.println("Error: --classes argument is required");
                exitCode = 1;
                return;
            }
            if (commandArgs.outputPath == null) {
                System.err.println("Error: --output argument is required");
                exitCode = 1;
                return;
            }

            // Execute the command
            execute(commandArgs);

        } catch (Exception e) {
            log.error("Command execution failed", e);
            System.err.println("Error: " + e.getMessage());
            exitCode = 1;
        }
    }

    /**
     * Executes the scan-classes command.
     *
     * @param args the parsed command arguments
     * @throws IOException if an I/O error occurs
     */
    private void execute(CommandArgs args) throws IOException {
        // Create filter for FQCN matching
        IncludeExcludeFilter filter = new IncludeExcludeFilter(args.includeMask, args.excludeMask);

        // Scan for class files
        ClassScanningService scanningService = new ClassScanningService();
        Map<String, Path> scannedClasses = scanningService.scan(Path.of(args.classesPath));

        // Filter classes by FQCN patterns
        Map<String, Path> filteredClasses = filterClasses(scannedClasses, filter);

        log.info("After filtering: {} classes remain for analysis", filteredClasses.size());

        // Analyze Spring DI dependencies
        var analysisResults = analyzerService.analyzeClasspath(filteredClasses);

        // Build the DI graph
        List<DiNodeDto> nodes = new ArrayList<>();
        List<DiEdgeDto> edges = new ArrayList<>();

        for (var result : analysisResults) {
            nodes.add(result.node());
            edges.addAll(result.edges());
        }

        DiGraphDto graph = new DiGraphDto(nodes, edges);

        // Write output JSON
        ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Path.of(args.outputPath).toFile(), graph);

        log.info("DI graph written to: {}", args.outputPath);
        System.out.println("Successfully analyzed " + nodes.size() + " Spring components");
        System.out.println("Found " + edges.size() + " dependency edges");
        System.out.println("Output written to: " + args.outputPath);
    }

    /**
     * Filters the scanned classes using the include/exclude patterns.
     *
     * @param classes the scanned classes map (FQCN -> path)
     * @param filter the include/exclude filter
     * @return filtered map containing only matching classes
     */
    private Map<String, Path> filterClasses(Map<String, Path> classes, IncludeExcludeFilter filter) {
        return classes.entrySet().stream()
            .filter(entry -> filter.matches(entry.getKey()))
            .collect(java.util.stream.Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
    }

    /**
     * Parses command-line arguments.
     *
     * @param args the raw command-line arguments
     * @return parsed CommandArgs
     */
    private CommandArgs parseArgs(String[] args) {
        CommandArgs result = new CommandArgs();

        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--classes" -> {
                    if (i + 1 < args.length) {
                        result.classesPath = args[++i];
                    }
                }
                case "--output" -> {
                    if (i + 1 < args.length) {
                        result.outputPath = args[++i];
                    }
                }
                case "--include" -> {
                    if (i + 1 < args.length) {
                        result.includeMask = args[++i];
                    }
                }
                case "--exclude" -> {
                    if (i + 1 < args.length) {
                        result.excludeMask = args[++i];
                    }
                }
                default -> {
                    // Unknown argument, log warning
                    log.warn("Unknown argument: {}", arg);
                }
            }
        }

        return result;
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    /**
     * Holder for parsed command arguments.
     */
    private static class CommandArgs {
        String classesPath;
        String outputPath;
        String includeMask;
        String excludeMask;
    }
}