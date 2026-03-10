package spring.twin.analysis;

import lombok.extern.slf4j.Slf4j;
import spring.twin.bytecode.BytecodeReaderService;
import spring.twin.dto.DiEdgeDetailsDto;
import spring.twin.dto.DiEdgeDto;
import spring.twin.dto.DiNodeDto;
import spring.twin.dto.types.EdgeType;
import spring.twin.dto.types.InjectionType;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Service for analyzing Spring Dependency Injection from bytecode.
 * <p>
 * This service analyzes compiled .class files to extract Spring components
 * and their dependency injection relationships, including:
 * <ul>
 *   <li>Component detection (@Component, @Service, @Repository, @Controller, @Configuration)</li>
 *   <li>Field injection (@Autowired on fields)</li>
 *   <li>Constructor injection (including generated constructors from Lombok)</li>
 * </ul>
 * <p>
 * The service resolves dependencies to all possible implementations found in the classpath,
 * using inheritance information to find concrete classes for interface/abstract dependencies.
 *
 * @see BytecodeReaderService
 * @see InheritanceTreeService
 */
@Slf4j
public class SpringDiAnalyzerService {

    private final BytecodeReaderService bytecodeReader;
    private final InheritanceTreeService inheritanceTreeService;

    // Spring stereotype annotations (internal names with slashes)
    private static final String COMPONENT_ANNOTATION = "Lorg/springframework/stereotype/Component;";
    private static final String SERVICE_ANNOTATION = "Lorg/springframework/stereotype/Service;";
    private static final String REPOSITORY_ANNOTATION = "Lorg/springframework/stereotype/Repository;";
    private static final String CONTROLLER_ANNOTATION = "Lorg/springframework/stereotype/Controller;";
    private static final String CONFIGURATION_ANNOTATION = "Lorg/springframework/context/annotation/Configuration;";
    private static final String AUTOWIRED_ANNOTATION = "Lorg/springframework/beans/factory/annotation/Autowired;";

    // Map of annotation internal names to label names
    private static final Map<String, String> ANNOTATION_TO_LABEL = Map.of(
        COMPONENT_ANNOTATION, "Component",
        SERVICE_ANNOTATION, "Service",
        REPOSITORY_ANNOTATION, "Repository",
        CONTROLLER_ANNOTATION, "Controller",
        CONFIGURATION_ANNOTATION, "Configuration"
    );

    /**
     * Creates a new SpringDiAnalyzerService.
     */
    public SpringDiAnalyzerService() {
        this.bytecodeReader = new BytecodeReaderService();
        this.inheritanceTreeService = new InheritanceTreeService();
    }

    /**
     * Analyzes a single class file to determine if it's a Spring component and extract its DI dependencies.
     * <p>
     * The method checks if the class is annotated with Spring stereotype annotations.
     * If not a component, returns null. If it is a component, returns a {@link ComponentAnalysisResult}
     * containing the node information and all DI edges.
     * <p>
     * Dependencies are resolved to all possible implementations found in the classpath.
     * If a dependency is specified as an interface or abstract class, all concrete implementations
     * from the classpath that are also Spring components are included as dependency targets.
     *
     * @param classBytes the class file bytes to analyze
     * @param className the fully qualified class name
     * @param classpath map of all class names in the project to their file paths
     * @param inheritanceTree the pre-built inheritance tree for the classpath
     * @return analysis result containing node and edges, or null if not a Spring component
     */
    private ComponentAnalysisResult analyzeClass(byte[] classBytes, String className, Map<String, Path> classpath,
                                                  Map<String, Set<String>> inheritanceTree) {
        log.debug("Analyzing class: {}", className);

        // Parse class file to extract annotations and members
        ClassInfo classInfo = parseClassInfo(classBytes);
        if (classInfo == null) {
            log.warn("Failed to parse class: {}", className);
            return null;
        }

        // Check if this is a Spring component
        String componentLabel = detectSpringComponent(classInfo.annotations());
        if (componentLabel == null) {
            log.debug("Class {} is not a Spring component", className);
            return null;
        }

        log.debug("Found Spring component: {} with label {}", className, componentLabel);

        // Create node
        DiNodeDto node = createNode(className, componentLabel);

        // Extract DI dependencies
        List<DiEdgeDto> edges = extractDependencies(className, classInfo, inheritanceTree, classpath);

        return new ComponentAnalysisResult(node, edges);
    }

    /**
     * Analyzes all classes in the classpath to build a complete DI graph.
     * <p>
     * This method scans all classes in the provided classpath, identifies Spring components,
     * and extracts their dependency injection relationships.
     *
     * @param classpath map of all class names to their file paths
     * @return list of component analysis results, one for each Spring component found
     */
    public List<ComponentAnalysisResult> analyzeClasspath(Map<String, Path> classpath) {
        log.info("Analyzing classpath with {} classes", classpath.size());

        // Build inheritance tree once for the entire classpath
        Map<String, Set<String>> inheritanceTree = inheritanceTreeService.buildTree(classpath);
        log.debug("Built inheritance tree with {} entries", inheritanceTree.size());

        List<ComponentAnalysisResult> results = new ArrayList<>();

        for (Map.Entry<String, Path> entry : classpath.entrySet()) {
            String className = entry.getKey();
            Path classPath = entry.getValue();

            try {
                byte[] classBytes = java.nio.file.Files.readAllBytes(classPath);
                ComponentAnalysisResult result = analyzeClass(classBytes, className, classpath, inheritanceTree);
                if (result != null) {
                    results.add(result);
                }
            } catch (IOException e) {
                log.warn("Failed to read class file: {}", classPath, e);
            }
        }

        log.info("Found {} Spring components", results.size());
        return results;
    }

    /**
     * Parses class file bytes to extract annotations, fields, and constructors.
     *
     * @param classBytes the class file bytes
     * @return parsed class information, or null if parsing fails
     */
    private ClassInfo parseClassInfo(byte[] classBytes) {
        try {
            BytecodeReaderService.ConstantPoolData poolData = bytecodeReader.readConstantPool(classBytes);
            if (poolData == null) {
                return null;
            }

            DataInputStream dis = poolData.inputStream();

            // Skip access flags, this_class, superclass
            dis.skipBytes(6);

            // Skip interfaces
            int interfacesCount = dis.readUnsignedShort();
            dis.skipBytes(interfacesCount * 2);

            // Read fields
            List<FieldInfo> fields = readFields(dis, poolData);

            // Read methods (includes constructors)
            List<MethodInfo> methods = readMethods(dis, poolData);

            // Parse annotations from the class file using BytecodeReaderService
            // Note: We need to re-read to get annotations which are in attributes
            Set<String> classAnnotations = bytecodeReader.parseClassAnnotations(classBytes);

            return new ClassInfo(classAnnotations, fields, methods);

        } catch (IOException e) {
            log.warn("Error parsing class info", e);
            return null;
        }
    }

    /**
     * Reads field information from the class file.
     */
    private List<FieldInfo> readFields(DataInputStream dis, BytecodeReaderService.ConstantPoolData poolData) throws IOException {
        List<FieldInfo> fields = new ArrayList<>();
        int fieldsCount = dis.readUnsignedShort();
        log.debug("Reading {} fields", fieldsCount);

        for (int i = 0; i < fieldsCount; i++) {
            int accessFlags = dis.readUnsignedShort();
            int nameIndex = dis.readUnsignedShort();
            int descriptorIndex = dis.readUnsignedShort();

            String fieldName = poolData.utf8Strings()[nameIndex];
            String descriptor = poolData.utf8Strings()[descriptorIndex];
            String fieldType = descriptorToClassName(descriptor);
            
            log.debug("Reading field: {} with descriptor: {}", fieldName, descriptor);

            // Skip attributes
            int attributesCount = dis.readUnsignedShort();
            log.debug("Field has {} attributes", attributesCount);
            boolean hasAutowired = false;

            for (int j = 0; j < attributesCount; j++) {
                int attrNameIndex = dis.readUnsignedShort();
                String attrName = poolData.utf8Strings()[attrNameIndex];
                int attrLength = dis.readInt();
                
                log.debug("Field attribute: {}, length: {}", attrName, attrLength);

                if ("RuntimeVisibleAnnotations".equals(attrName)) {
                    // Need to check for @Autowired
                    byte[] attrBytes = new byte[attrLength];
                    dis.readFully(attrBytes);
                    hasAutowired = hasAutowiredAnnotation(attrBytes, poolData);
                    log.debug("Checked for @Autowired: {}", hasAutowired);
                } else {
                    dis.skipBytes(attrLength);
                }
            }

            fields.add(new FieldInfo(fieldName, fieldType, hasAutowired));
        }

        return fields;
    }

    /**
     * Checks if a RuntimeVisibleAnnotations attribute contains @Autowired.
     */
    private boolean hasAutowiredAnnotation(byte[] attrBytes, BytecodeReaderService.ConstantPoolData poolData) {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(attrBytes))) {
            int numAnnotations = dis.readUnsignedShort();
            for (int i = 0; i < numAnnotations; i++) {
                int typeIndex = dis.readUnsignedShort();
                String typeDesc = poolData.utf8Strings()[typeIndex];
                if (AUTOWIRED_ANNOTATION.equals(typeDesc)) {
                    return true;
                }

                // Skip element-value pairs
                int numPairs = dis.readUnsignedShort();
                for (int j = 0; j < numPairs; j++) {
                    dis.skipBytes(2);
                    skipElementValue(dis);
                }
            }
        } catch (IOException e) {
            log.warn("Error checking for @Autowired", e);
        }
        return false;
    }

    /**
     * Skips an annotation element value.
     */
    private void skipElementValue(DataInputStream dis) throws IOException {
        int tag = dis.readUnsignedByte();
        switch (tag) {
            case 'B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z', 's', 'c' -> dis.skipBytes(2);
            case 'e' -> dis.skipBytes(4); // enum: type_name_index + const_name_index
            case '@' -> skipAnnotation(dis); // nested annotation
            case '[' -> {
                int numValues = dis.readUnsignedShort();
                for (int i = 0; i < numValues; i++) {
                    skipElementValue(dis);
                }
            }
            default -> dis.skipBytes(2);
        }
    }

    /**
     * Skips a nested annotation.
     */
    private void skipAnnotation(DataInputStream dis) throws IOException {
        dis.skipBytes(2); // type_index
        int numElementValuePairs = dis.readUnsignedShort();
        for (int i = 0; i < numElementValuePairs; i++) {
            dis.skipBytes(2); // element_name_index
            skipElementValue(dis);
        }
    }

    /**
     * Reads method information from the class file.
     */
    private List<MethodInfo> readMethods(DataInputStream dis, BytecodeReaderService.ConstantPoolData poolData) throws IOException {
        List<MethodInfo> methods = new ArrayList<>();
        int methodsCount = dis.readUnsignedShort();

        for (int i = 0; i < methodsCount; i++) {
            int accessFlags = dis.readUnsignedShort();
            int nameIndex = dis.readUnsignedShort();
            int descriptorIndex = dis.readUnsignedShort();

            String methodName = poolData.utf8Strings()[nameIndex];
            String descriptor = poolData.utf8Strings()[descriptorIndex];

            // Parse parameter types from descriptor
            List<String> parameterTypes = parseParameterTypes(descriptor);

            // Skip attributes
            int attributesCount = dis.readUnsignedShort();
            for (int j = 0; j < attributesCount; j++) {
                int attrNameIndex = dis.readUnsignedShort();
                int attrLength = dis.readInt();
                dis.skipBytes(attrLength);
            }

            boolean isConstructor = "<init>".equals(methodName);
            methods.add(new MethodInfo(methodName, parameterTypes, isConstructor, accessFlags));
        }

        return methods;
    }

    /**
     * Parses parameter types from a method descriptor.
     */
    private List<String> parseParameterTypes(String descriptor) {
        List<String> types = new ArrayList<>();
        int i = 1; // Skip opening '('

        while (i < descriptor.length() && descriptor.charAt(i) != ')') {
            char c = descriptor.charAt(i);
            if (c == 'L') {
                int end = descriptor.indexOf(';', i);
                String type = descriptor.substring(i + 1, end).replace('/', '.');
                types.add(type);
                i = end + 1;
            } else if (c == '[') {
                // Array type - skip for now or handle recursively
                i++;
                if (descriptor.charAt(i) == 'L') {
                    int end = descriptor.indexOf(';', i);
                    i = end + 1;
                } else {
                    i++;
                }
            } else {
                // Primitive type - skip
                i++;
            }
        }

        return types;
    }

    /**
     * Converts a field descriptor to a class name.
     */
    private String descriptorToClassName(String descriptor) {
        if (descriptor.startsWith("L") && descriptor.endsWith(";")) {
            return descriptor.substring(1, descriptor.length() - 1).replace('/', '.');
        }
        return descriptor; // Primitive or array
    }

    /**
     * Detects if the class is a Spring component and returns its label.
     *
     * @param annotations set of annotation descriptors
     * @return label name (e.g., "Service", "Controller") or null if not a component
     */
    private String detectSpringComponent(Set<String> annotations) {
        for (Map.Entry<String, String> entry : ANNOTATION_TO_LABEL.entrySet()) {
            if (annotations.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Creates a DiNodeDto from class information.
     */
    private DiNodeDto createNode(String className, String label) {
        int lastDot = className.lastIndexOf('.');
        String simpleName = lastDot >= 0 ? className.substring(lastDot + 1) : className;
        String packageName = lastDot >= 0 ? className.substring(0, lastDot) : "";

        return new DiNodeDto("Class", simpleName, packageName, List.of(label));
    }

    /**
     * Extracts all DI dependencies for a component.
     */
    private List<DiEdgeDto> extractDependencies(String className, ClassInfo classInfo,
                                                Map<String, Set<String>> inheritanceTree,
                                                Map<String, Path> classpath) {
        List<DiEdgeDto> edges = new ArrayList<>();

        // Extract field injections (@Autowired)
        for (FieldInfo field : classInfo.fields()) {
            if (field.hasAutowired()) {
                List<String> targetClasses = resolveDependencyTargets(field.fieldType(), inheritanceTree, classpath);
                for (String target : targetClasses) {
                    DiEdgeDetailsDto details = new DiEdgeDetailsDto(
                        InjectionType.FIELD,
                        null,
                        field.fieldName(),
                        null
                    );
                    edges.add(new DiEdgeDto(EdgeType.DEPENDS_ON, className, target, details));
                }
            }
        }

        // Extract constructor injections
        for (MethodInfo method : classInfo.methods()) {
            if (method.isConstructor()) {
                // Check if this is the primary constructor (public or has @Autowired)
                boolean isPrimaryConstructor = isPrimaryConstructor(method, classInfo);

                if (isPrimaryConstructor) {
                    int paramIndex = 0;
                    for (String paramType : method.parameterTypes()) {
                        List<String> targetClasses = resolveDependencyTargets(paramType, inheritanceTree, classpath);
                        for (String target : targetClasses) {
                            DiEdgeDetailsDto details = new DiEdgeDetailsDto(
                                InjectionType.CONSTRUCTOR,
                                paramIndex,
                                null,
                                null
                            );
                            edges.add(new DiEdgeDto(EdgeType.DEPENDS_ON, className, target, details));
                        }
                        paramIndex++;
                    }
                }
            }
        }

        return edges;
    }

    /**
     * Determines if a constructor is the primary one for DI.
     * <p>
     * A constructor is primary if it's public and either:
     * - It's the only public constructor
     * - It has @Autowired annotation
     * - It's the only constructor in the class
     */
    private boolean isPrimaryConstructor(MethodInfo constructor, ClassInfo classInfo) {
        // Check if public (access_flags & ACC_PUBLIC = 0x0001)
        boolean isPublic = (constructor.accessFlags() & 0x0001) != 0;

        if (!isPublic) {
            return false;
        }

        // Count public constructors
        long publicConstructorCount = classInfo.methods().stream()
            .filter(MethodInfo::isConstructor)
            .filter(m -> (m.accessFlags() & 0x0001) != 0)
            .count();

        // If only one public constructor, it's the primary one
        return publicConstructorCount == 1;
    }

    /**
     * Resolves a dependency type to all possible target implementations.
     * <p>
     * If the dependency type is a concrete Spring component that exists in the classpath,
     * returns it directly.
     * If the dependency type is an interface, abstract class, or a non-component concrete class,
     * finds all concrete implementations in the classpath that are Spring components.
     *
     * @param dependencyType the fully qualified type of the dependency
     * @param inheritanceTree the inheritance tree for finding implementations
     * @param classpath the classpath map
     * @return list of target class names
     */
    private List<String> resolveDependencyTargets(String dependencyType,
                                                   Map<String, Set<String>> inheritanceTree,
                                                   Map<String, Path> classpath) {
        List<String> targets = new ArrayList<>();

        // Check if the dependency type is in classpath
        Path dependencyPath = classpath.get(dependencyType);
        if (dependencyPath != null) {
            // Check if it's an interface or abstract class
            if (isInterfaceOrAbstract(dependencyPath)) {
                // Find all concrete implementations that are Spring components
                findConcreteSpringComponents(dependencyType, inheritanceTree, classpath, targets);
            } else {
                // It's a concrete class in our classpath
                // Check if it's a Spring component
                if (isSpringComponent(dependencyPath)) {
                    targets.add(dependencyType);
                } else {
                    // Not a Spring component, find descendants that are
                    findConcreteSpringComponents(dependencyType, inheritanceTree, classpath, targets);
                }
            }
        } else {
            // Dependency type not in classpath, try to find implementations
            findConcreteSpringComponents(dependencyType, inheritanceTree, classpath, targets);
        }

        if (targets.isEmpty()) {
            // Fallback: add the dependency type even if not found
            // This preserves the dependency information for external types
            targets.add(dependencyType);
        }

        return targets;
    }

    /**
     * Finds all concrete Spring component descendants of a given type.
     *
     * @param parentType the parent type (interface or class)
     * @param inheritanceTree the inheritance tree
     * @param classpath the classpath map
     * @param targets list to collect target class names into
     */
    private void findConcreteSpringComponents(String parentType,
                                               Map<String, Set<String>> inheritanceTree,
                                               Map<String, Path> classpath,
                                               List<String> targets) {
        Set<String> allDescendants = inheritanceTreeService.findAllDescendants(parentType, inheritanceTree);

        for (String descendant : allDescendants) {
            Path descendantPath = classpath.get(descendant);
            if (descendantPath != null && !isInterfaceOrAbstract(descendantPath)) {
                // Check if the descendant is a Spring component
                if (isSpringComponent(descendantPath)) {
                    targets.add(descendant);
                }
            }
        }
    }

    /**
     * Checks if a class file represents a Spring component by checking for Spring annotations.
     *
     * @param classPath path to the .class file
     * @return true if the class has any Spring stereotype annotation
     */
    private boolean isSpringComponent(Path classPath) {
        try {
            byte[] classBytes = java.nio.file.Files.readAllBytes(classPath);
            Set<String> annotations = bytecodeReader.parseClassAnnotations(classBytes);
            
            for (String annotation : annotations) {
                if (ANNOTATION_TO_LABEL.containsKey(annotation)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            log.warn("Failed to check if class is Spring component: {}", classPath, e);
            return false;
        }
    }

    /**
     * Checks if a class is an interface or abstract class by reading its bytecode.
     *
     * @param classPath path to the .class file
     * @return true if the class is an interface or abstract
     */
    private boolean isInterfaceOrAbstract(Path classPath) {
        try {
            byte[] classBytes = java.nio.file.Files.readAllBytes(classPath);
            try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(classBytes))) {
                // Skip magic and version
                dis.skipBytes(8);

                // Skip constant pool
                int constantPoolCount = dis.readUnsignedShort();
                for (int i = 1; i < constantPoolCount; i++) {
                    int tag = dis.readUnsignedByte();
                    switch (tag) {
                        case 1 -> dis.skipBytes(dis.readUnsignedShort()); // UTF-8
                        case 3, 4 -> dis.skipBytes(4); // Integer, Float
                        case 5, 6 -> { // Long, Double
                            dis.skipBytes(8);
                            i++;
                        }
                        case 7, 8, 16, 19, 20 -> dis.skipBytes(2); // Class, String, MethodType, Module, Package
                        case 9, 10, 11, 12, 18 -> dis.skipBytes(4); // Fieldref, Methodref, InterfaceMethodref, NameAndType, InvokeDynamic
                        case 15 -> dis.skipBytes(3); // MethodHandle
                        default -> {
                            log.warn("Unknown tag {} at index {}, assuming not interface/abstract", tag, i);
                            return false; // Unknown tag, assume not interface/abstract
                        }
                    }
                }

                // Read access flags
                int accessFlags = dis.readUnsignedShort();
                // ACC_INTERFACE = 0x0200, ACC_ABSTRACT = 0x0400
                return (accessFlags & 0x0200) != 0 || (accessFlags & 0x0400) != 0;
            }
        } catch (IOException e) {
            log.warn("Failed to check if class is interface/abstract: {}", classPath, e);
            return false;
        }
    }

    /**
     * Result of analyzing a single class for Spring DI.
     *
     * @param node the node representing this component
     * @param edges list of dependency edges from this component
     */
    public record ComponentAnalysisResult(DiNodeDto node, List<DiEdgeDto> edges) {
    }

    /**
     * Internal record for parsed class information.
     */
    private record ClassInfo(Set<String> annotations, List<FieldInfo> fields, List<MethodInfo> methods) {
    }

    /**
     * Internal record for field information.
     */
    private record FieldInfo(String fieldName, String fieldType, boolean hasAutowired) {
    }

    /**
     * Internal record for method information.
     */
    private record MethodInfo(String methodName, List<String> parameterTypes,
                              boolean isConstructor, int accessFlags) {
    }
}