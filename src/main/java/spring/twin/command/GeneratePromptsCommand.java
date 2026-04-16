package spring.twin.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * Spring Shell command for generating refactoring prompts and TODO lists from pik_full.json dependencies.
 */
@ShellComponent
@Slf4j
public class GeneratePromptsCommand {

    private static final String PROMPT_TEMPLATE = """
Замени в {FILE_NAME} использование типа @/some/class/to/Replace.java
на новую компоненту @/some/class/to/ReplaceWith.java
нужно заменить само поле, аргументы конструктора,
убрать из конструктора аргументы messageSource и localeProvider, если они не используются кроме как для создания i18n
после этого удали неиспользуемые импорты
если будут ошибки компиляции, связанные с передачей сервиса в другие классы вместо Replace, то игнорируй их, другие классы будут исправлены в рамках отдельных задач            
    """;

    private final ObjectMapper objectMapper;

    public GeneratePromptsCommand(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Generates refactoring prompts and TODO lists from pik_full.json.
     *
     * @param input       path to pik_full.json file
     * @param sourceRoot  root directory for source files
     * @param outputDir   output directory for generated files (default "ref")
     * @return success message or error
     */
    @ShellMethod(key = "generate-prompts", value = "Generate refactoring prompts and TODO lists from dependencies")
    public String generatePrompts(
            @ShellOption(value = "--input", help = "Path to pik_full.json file") String input,
            @ShellOption(value = "--source-root", help = "Root directory for Java source files") String sourceRoot,
            @ShellOption(value = "--output-dir", help = "Output directory for prompts and TODOs", defaultValue = "ref") String outputDir) {
        try {
            // 1. Read and transform JSON
            Map<String, Map<String, Object>> rawData = objectMapper.readValue(
                Path.of(input).toFile(),
                new TypeReference<Map<String, Map<String, Object>>>() {}
            );

            Map<String, Set<String>> dependencies = new HashMap<>();
            for (Map.Entry<String, Map<String, Object>> entry : rawData.entrySet()) {
                String clazz = entry.getKey();
                Set<String> uses = new HashSet<>(entry.getValue().keySet());
                if (uses.contains("some.class.to.Replace")) {
                    dependencies.put(clazz, uses);
                }
            }

            // 2. Find source file paths recursively
            Map<String, String> sourcePaths = new HashMap<>();
            Path sourceRootPath = Path.of(sourceRoot);
            var files = Files.walk(sourceRootPath)
                    .filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString().endsWith(".java"))
                    .toList();
            for (String fqcn : dependencies.keySet()) {
                String fileName = fqcn.replace('.', '\\') + ".java";
                Path foundFile = files.stream()
                    .filter(path -> path.toString().endsWith(fileName))
                    .findFirst()
                    .orElse(null);
                if (foundFile != null) {
                    String relativePath = sourceRootPath.relativize(foundFile).toString().replace('\\', '/');
                    sourcePaths.put(fqcn, relativePath);
                }
            }

            // 3. Generate prompts
            List<String> promptPaths = new ArrayList<>();
            int counter = 1;
            Path promptsDir = Path.of(outputDir, "prompts");
            Files.createDirectories(promptsDir);

            for (String fqcn : dependencies.keySet()) {
                String filePath = sourcePaths.get(fqcn);
                if (filePath != null) {
                    String prompt = PROMPT_TEMPLATE.replace("{FILE_NAME}", "@/" + filePath);
                    String promptFile = String.format("%03d-prompt.txt", counter);
                    Path promptPath = promptsDir.resolve(promptFile);
                    Files.writeString(promptPath, prompt);
                    promptPaths.add("@/ref/prompts/" + promptFile);
                    counter++;
                }
            }

            // 4. Generate TODO lists
            int todoCounter = 1;
            for (int i = 0; i < promptPaths.size(); i += 20) {
                String todoFile = String.format("%03d-todo-list.txt", todoCounter);
                Path todoPath = Path.of(outputDir, todoFile);
                StringBuilder sb = new StringBuilder("TODO:\n");
                for (int j = i; j < Math.min(i + 20, promptPaths.size()); j++) {
                    sb.append("- выполни задачу ").append(promptPaths.get(j)).append("\n");
                }
                Files.writeString(todoPath, sb.toString());
                todoCounter++;
            }

            return "Prompts and TODO lists generated successfully in: " + outputDir;
        } catch (IOException e) {
            log.error("Error generating prompts: ", e);
            return "Error: " + e.getMessage();
        }
    }
}