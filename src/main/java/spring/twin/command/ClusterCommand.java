package spring.twin.command;

import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import spring.twin.cluster.ClusterParams;
import spring.twin.cluster.ClusterService;

/**
 * Spring Shell command for clustering the dependency graph.
 * <p>
 * This command accepts parameters for the dependencies file, output file path,
 * and optional resolution parameter for controlling cluster size.
 */
@ShellComponent
@Slf4j
public class ClusterCommand {

    private final ClusterService clusterService;

    /**
     * Constructs a new ClusterCommand with the required service.
     *
     * @param clusterService the service for executing the clustering pipeline
     */
    public ClusterCommand(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    /**
     * Performs clustering of the dependency graph.
     * <p>
     * This command reads the dependency graph from the specified JSON file,
     * clusters classes using the Leiden algorithm, and writes the result to a JSON file.
     * <p>
     * The resolution parameter controls the size of clusters: lower values produce
     * larger clusters, higher values produce smaller clusters. Valid range is 0.5–5.0.
     * <p>
     * NOTE: On Windows, paths with backslashes must be quoted, e.g.: "d:\\\\project\\\\deps.json"
     * or use forward slashes: d:/project/deps.json
     *
     * @param deps       path to dependencies.json file
     * @param output     path to output JSON file (clusters.json)
     * @param resolution clustering parameter that determines cluster size (0.5–5.0), default 1.5
     * @return a message indicating success (output file path) or error
     */
    @ShellMethod(key = "cluster", value = "Cluster dependency graph")
    public String cluster(
            @ShellOption("--deps") String deps,
            @ShellOption("--output") String output,
            @ShellOption(value = "--resolution", defaultValue = "1.5") String resolution) {
        double parsedResolution;
        try {
            parsedResolution = Double.parseDouble(resolution);
        } catch (NumberFormatException e) {
            return "Error: Invalid resolution: must be a valid number between 0.5 and 5.0";
        }
        ClusterParams params = ClusterParams.of(Path.of(deps), Path.of(output), String.valueOf(parsedResolution));
        try {
            clusterService.execute(params);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return "Clusters written to: " + params.outputFile();
    }
}