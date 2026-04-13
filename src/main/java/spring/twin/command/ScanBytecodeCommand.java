package spring.twin.command;

import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import spring.twin.scan.ScanBytecodeParams;
import spring.twin.scan.ScanBytecodeService;

/**
 * Spring Shell command for scanning bytecode and extracting dependencies.
 * <p>
 * This command accepts parameters for the classes directory, output file path,
 * and optional include/exclude masks for filtering classes by their fully qualified names.
 */
@ShellComponent
@Slf4j
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
     * <p>
     * NOTE: On Windows, paths with backslashes must be quoted, e.g.: "d:\\devel\\pik\\MIK"
     * or use forward slashes: d:/devel/pik/MIK
     *
     * @param classes path to directory containing .class files
     * @param output  path to output JSON file
     * @param include FQCN include masks separated by semicolon (optional)
     * @param exclude FQCN exclude masks separated by semicolon (optional)
     * @param mergeInnerClasses flag to merge inner classes with outer classes (default "true")
     * @return a message indicating success (output file path) or error
     */
    @ShellMethod(key = "scan-bytecode", value = "Scan bytecode and extract dependencies")
    public String scanBytecode(
            @ShellOption(value = "--classes", help = "Path to directory with .class files (Windows: quote or use /)") String classes,
            @ShellOption(value = "--output", help = "Path to output JSON file") String output,
            @ShellOption(value = "--include", help = "FQCN include masks separated by ;", defaultValue = "") String include,
            @ShellOption(value = "--exclude", help = "FQCN exclude masks separated by ;", defaultValue = "") String exclude,
            @ShellOption(value = "--merge-inner-classes", help = "Merge inner classes with outer classes", defaultValue = "true") String mergeInnerClasses) {
        try {
            ScanBytecodeParams params = ScanBytecodeParams.of(
                    Path.of(classes),
                    Path.of(output),
                    include,
                    exclude,
                    Boolean.parseBoolean(mergeInnerClasses)
            );
            scanBytecodeService.executeDetails(params);
            return "Dependencies written to: " + output;
        } catch (Throwable t) {
            log.error("Error: ", t);
            return "Error: " + t.getMessage();
        }
    }
}