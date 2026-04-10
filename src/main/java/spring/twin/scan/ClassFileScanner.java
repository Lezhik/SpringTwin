package spring.twin.scan;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Recursively scans a directory for .class files.
 * 
 * <p>This class provides functionality to traverse a directory tree and find all
 * Java class files (.class extension). It is designed to be used as a Spring bean
 * with constructor injection.
 */
public class ClassFileScanner {

    private static final String CLASS_FILE_EXTENSION = ".class";
    private static final PathMatcher CLASS_FILE_MATCHER = path -> path.getFileName().toString().endsWith(CLASS_FILE_EXTENSION);

    /**
     * Recursively scans the specified directory for .class files.
     *
     * @param classesDir the root directory to scan for class files
     * @return a list of paths to all .class files found in the directory tree
     * @throws UncheckedIOException if the directory does not exist, is not accessible, 
     *                              or cannot be read
     */
    public List<Path> scan(Path classesDir) {
        if (!Files.exists(classesDir) || !Files.isDirectory(classesDir)) {
            throw new UncheckedIOException(
                new IOException("Directory does not exist or is not accessible: " + classesDir)
            );
        }
        
        try (Stream<Path> walk = Files.walk(classesDir)) {
            return walk
                .filter(Files::isRegularFile)
                .filter(CLASS_FILE_MATCHER::matches)
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Converts a .class file path to its Fully Qualified Class Name (FQCN).
     *
     * <p>Given the root classes directory and a path to a .class file within it,
     * this method extracts the FQCN by removing the .class extension and replacing
     * directory separators with dots.
     *
     * <p>Example:
     * <ul>
     *   <li>{@code classesDir/com/example/Service.class} → {@code com.example.Service}</li>
     *   <li>{@code classesDir/com/example/Inner$Nested.class} → {@code com.example.Inner$Nested}</li>
     * </ul>
     *
     * @param classesDir the root classes directory
     * @param classFile the path to the .class file
     * @return the FQCN of the class, or {@code Optional.empty()} if the path cannot be converted
     */
    public Optional<String> toClassName(Path classesDir, Path classFile) {
        if (classesDir == null || classFile == null) {
            return Optional.empty();
        }
        
        try {
            // Get the relative path from classesDir to classFile
            Path relativePath = classesDir.relativize(classFile);
            
            // Convert path separators to dots and remove .class extension
            String fqcn = relativePath.toString()
                .replace('\\', '.')
                .replace('/', '.');
            
            // Remove .class extension if present
            if (fqcn.endsWith(CLASS_FILE_EXTENSION)) {
                fqcn = fqcn.substring(0, fqcn.length() - CLASS_FILE_EXTENSION.length());
            }
            
            return Optional.of(fqcn);
        } catch (IllegalArgumentException e) {
            // thrown by relativize if paths are on different drives or not related
            return Optional.empty();
        }
    }
}