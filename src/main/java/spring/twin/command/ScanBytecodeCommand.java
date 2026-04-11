package spring.twin.command;

import java.nio.file.Path;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.stereotype.Component;

import spring.twin.scan.ScanBytecodeParams;
import spring.twin.scan.ScanBytecodeService;

/**
 * Spring Shell command for scanning bytecode and extracting dependencies.
 * <p>
 * This command accepts parameters for the classes directory, output file path,
 * and optional include/exclude masks for filtering classes by their fully qualified names.
 */
@Component
@Command
public class ScanBytecodeCommand {

    private final ScanBytecodeService scanBytecodeService;

    /**
     * Constructs a new ScanBytecodeCommand with the required service.
     *
     * @param scanBytecodeService the service for executing the scan-bytecode pipeline
     */
    public ScanBytecodeCommand(ScanBytecodeService scanBytecodeService) {
        this.scanBytecodeService = scanBytecodeService;
    }

    /**
     * Scans bytecode and extracts dependencies from .class files.
     * <p>
     * This command analyzes the specified directory containing .class files,
     * builds a dependency graph, and writes the result to a JSON file.
     * <p>
     * Include and exclude masks can be used to filter classes by their fully qualified
     * class names (FQCN). Multiple masks can be specified separated by semicolons.
     *
     * @param classes path to directory containing .class files
     * @param output  path to output JSON file
     * @param include FQCN include masks separated by semicolon (optional)
     * @param exclude FQCN exclude masks separated by semicolon (optional)
     * @return a message indicating success (output file path) or error
     */
    @Command(command = "scan-bytecode", description = "Scan bytecode and extract dependencies")
    public String scanBytecode(
            @Option(required = true, description = "Path to directory with .class files") String classes,
            @Option(required = true, description = "Path to output JSON file") String output,
            @Option(defaultValue = "", description = "FQCN include masks separated by ;") String include,
            @Option(defaultValue = "", description = "FQCN exclude masks separated by ;") String exclude) {
        try {
            ScanBytecodeParams params = ScanBytecodeParams.of(
                    Path.of(classes),
                    Path.of(output),
                    include,
                    exclude
            );
            scanBytecodeService.execute(params);
            return "Dependencies written to: " + output;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}