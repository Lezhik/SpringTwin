package spring.twin.scanner;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import spring.twin.bytecode.BytecodeReaderService;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Service for scanning and extracting class files from directories and archives.
 * <p>
 * This service scans a given path for .class files, including those inside
 * archive files (zip, jar, war, ear). Found classes are extracted to a temporary
 * directory for further processing.
 * <p>
 * The service ensures each unique class is extracted only once, even if found
 * in multiple locations. Classes are stored with UUID-based filenames in the
 * temporary directory.
 * <p>
 * This class is thread-safe.
 *
 * @see IncludeExcludeFilter
 */
@Getter
@Slf4j
public class ClassScanningService {

    /**
     * Hardcoded path to the temporary directory for extracted classes.
     */
    private static final String TMP_DIR = "classes";

    /**
     * Archive extensions that are supported for scanning.
     */
    private static final Set<String> ARCHIVE_EXTENSIONS = Set.of("zip", "jar", "war", "ear");

    /**
     * -- GETTER --
     *  Returns the path to the tmp directory.
     *
     * @return the tmp directory path
     */
    private final Path tmpDirectory;

    private final BytecodeReaderService bytecodeReader;

    /**
     * Creates a new ClassScanningService with the default tmp directory.
     */
    public ClassScanningService() {
        this.tmpDirectory = Path.of(TMP_DIR);
        this.bytecodeReader = new BytecodeReaderService();
    }

    /**
     * Creates a new ClassScanningService with a custom tmp directory.
     * Used primarily for testing.
     *
     * @param tmpDirectory the temporary directory path
     */
    public ClassScanningService(Path tmpDirectory) {
        this.tmpDirectory = tmpDirectory;
        this.bytecodeReader = new BytecodeReaderService();
    }

    /**
     * Scans the given path for .class files and extracts them to the tmp directory.
     * <p>
     * The scan includes:
     * <ul>
     *   <li>Direct .class files in the directory and subdirectories</li>
     *   <li>.class files inside archive files (zip, jar, war, ear)</li>
     * </ul>
     * <p>
     * If a class is found in multiple locations, only the first occurrence is extracted.
     * Subsequent occurrences are skipped and logged at debug level.
     *
     * @param scanPath the path to scan (directory or archive file)
     * @return a map of fully qualified class names to their extracted file paths
     * @throws IOException if an I/O error occurs during scanning
     * @throws IllegalArgumentException if scanPath is null
     */
    public Map<String, Path> scan(Path scanPath) throws IOException {
        if (scanPath == null) {
            throw new IllegalArgumentException("Scan path must not be null");
        }

        log.info("Starting class scan at: {}", scanPath);

        // Ensure tmp directory exists
        Files.createDirectories(tmpDirectory);

        Map<String, Path> foundClasses = new HashMap<>();

        if (Files.isDirectory(scanPath)) {
            scanDirectory(scanPath, foundClasses);
        } else if (isArchiveFile(scanPath)) {
            scanArchive(scanPath, foundClasses);
        } else {
            log.warn("Path is neither a directory nor a supported archive: {}", scanPath);
        }

        log.info("Class scan completed. Found {} unique classes.", foundClasses.size());
        return Collections.unmodifiableMap(foundClasses);
    }

    /**
     * Recursively scans a directory for .class files and archives.
     *
     * @param directory the directory to scan
     * @param foundClasses the map to populate with found classes
     * @throws IOException if an I/O error occurs
     */
    private void scanDirectory(Path directory, Map<String, Path> foundClasses) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileName = file.getFileName().toString();
                
                if (fileName.endsWith(".class")) {
                    processClassFile(file, foundClasses);
                } else if (isArchiveFile(file)) {
                    scanArchive(file, foundClasses);
                }
                
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                log.warn("Failed to access file: {} - {}", file, exc.getMessage());
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Scans an archive file for .class entries.
     *
     * @param archivePath the path to the archive file
     * @param foundClasses the map to populate with found classes
     * @throws IOException if an I/O error occurs
     */
    private void scanArchive(Path archivePath, Map<String, Path> foundClasses) throws IOException {
        log.debug("Scanning archive: {}", archivePath);

        try (InputStream is = Files.newInputStream(archivePath);
             ZipInputStream zis = new ZipInputStream(is)) {
            
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                
                if (entryName.endsWith(".class") && !entry.isDirectory()) {
                    // Read class bytes into memory
                    byte[] classBytes = zis.readAllBytes();
                    processClassBytes(classBytes, foundClasses);
                }
                
                zis.closeEntry();
            }
        } catch (IOException e) {
            log.error("Error reading archive: {}", archivePath, e);
            throw e;
        }
    }

    /**
     * Processes a standalone .class file.
     *
     * @param classFile the path to the .class file
     * @param foundClasses the map to populate with found classes
     * @throws IOException if an I/O error occurs
     */
    private void processClassFile(Path classFile, Map<String, Path> foundClasses) throws IOException {
        log.debug("Processing class file: {}", classFile);
        
        byte[] classBytes = Files.readAllBytes(classFile);
        String className = extractClassName(classBytes);
        
        if (className != null) {
            addClassIfNotExists(className, classFile, foundClasses);
        }
    }

    /**
     * Processes class bytes from an archive entry.
     *
     * @param classBytes the class file bytes
     * @param foundClasses the map to populate with found classes
     * @throws IOException if an I/O error occurs
     */
    private void processClassBytes(byte[] classBytes, Map<String, Path> foundClasses) throws IOException {
        String className = extractClassName(classBytes);
        
        if (className != null) {
            Path extractedPath = extractToTmp(classBytes);
            addClassIfNotExists(className, extractedPath, foundClasses);
        }
    }

    /**
     * Adds a class to the map if it hasn't been found before.
     * If the class already exists, logs a debug message and skips.
     *
     * @param className the fully qualified class name
     * @param path the path to the class file
     * @param foundClasses the map of found classes
     */
    private void addClassIfNotExists(String className, Path path, Map<String, Path> foundClasses) {
        if (foundClasses.containsKey(className)) {
            log.debug("Class {} already found at {}, skipping duplicate at {}", 
                className, foundClasses.get(className), path);
            return;
        }
        
        foundClasses.put(className, path);
        log.debug("Found class: {} at {}", className, path);
    }

    /**
     * Extracts class bytes to a temporary file with a UUID-based name.
     *
     * @param classBytes the class file content
     * @return the path to the extracted file
     * @throws IOException if an I/O error occurs
     */
    private Path extractToTmp(byte[] classBytes) throws IOException {
        String fileName = UUID.randomUUID() + ".class";
        Path targetPath = tmpDirectory.resolve(fileName);
        
        Files.write(targetPath, classBytes);
        log.debug("Extracted class to: {}", targetPath);
        
        return targetPath;
    }

    /**
     * Checks if a file is a supported archive type.
     *
     * @param path the file path to check
     * @return true if the file is an archive, false otherwise
     */
    private boolean isArchiveFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        int dotIndex = fileName.lastIndexOf('.');
        
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            String extension = fileName.substring(dotIndex + 1);
            return ARCHIVE_EXTENSIONS.contains(extension);
        }
        
        return false;
    }

    /**
     * Extracts the fully qualified class name from class file bytes.
     * <p>
     * This method parses the class file format to read the class name from
     * the constant pool without requiring external dependencies.
     *
     * @param classBytes the class file bytes
     * @return the fully qualified class name, or null if parsing fails
     */
    String extractClassName(byte[] classBytes) {
        var poolData = bytecodeReader.readConstantPool(classBytes);
        return bytecodeReader.extractClassName(poolData);
    }

    /**
     * Cleans the tmp directory by deleting all files and subdirectories.
     * <p>
     * If the tmp directory doesn't exist, it will be created as an empty directory.
     * After cleaning, the tmp directory exists and is empty.
     *
     * @throws IOException if an I/O error occurs during cleanup
     */
    public void cleanTmp() throws IOException {
        log.info("Cleaning tmp directory: {}", tmpDirectory);

        if (!Files.exists(tmpDirectory)) {
            log.debug("Tmp directory does not exist, creating empty directory");
            Files.createDirectories(tmpDirectory);
            return;
        }

        // Walk the directory tree and delete all files and directories
        Files.walkFileTree(tmpDirectory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                log.debug("Deleted file: {}", file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (!dir.equals(tmpDirectory)) {
                    Files.delete(dir);
                    log.debug("Deleted directory: {}", dir);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        log.info("Tmp directory cleaned successfully");
    }

}