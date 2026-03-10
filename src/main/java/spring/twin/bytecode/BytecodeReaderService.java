package spring.twin.bytecode;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service for reading bytecode information from .class files.
 * <p>
 * This service provides low-level bytecode parsing capabilities
 * for extracting class metadata without external dependencies.
 */
@Slf4j
public class BytecodeReaderService {

    /**
     * Reads the constant pool from class file bytes.
     * <p>
     * Parses the constant pool and extracts UTF-8 strings and Class entry indices.
     *
     * @param classBytes the class file bytes
     * @return constant pool data containing UTF-8 strings and class name indices, or null if parsing fails
     */
    public ConstantPoolData readConstantPool(byte[] classBytes) {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(classBytes))) {
            // Read magic number
            int magic = dis.readInt();
            if (magic != 0xCAFEBABE) {
                log.warn("Invalid class file magic number: {}", Integer.toHexString(magic));
                return null;
            }

            // Skip minor and major version
            dis.skipBytes(4);

            // Read constant pool count
            int constantPoolCount = dis.readUnsignedShort();

            // Collect all UTF-8 strings and track Class entries
            String[] utf8Strings = new String[constantPoolCount];
            int[] classNameIndices = new int[constantPoolCount];

            for (int i = 1; i < constantPoolCount; i++) {
                int tag = dis.readUnsignedByte();

                switch (tag) {
                    case 1: // UTF-8
                        utf8Strings[i] = dis.readUTF();
                        break;
                    case 3: // Integer
                    case 4: // Float
                        dis.skipBytes(4);
                        break;
                    case 5: // Long
                    case 6: // Double
                        dis.skipBytes(8);
                        i++; // Long and Double take two slots
                        break;
                    case 7: // Class
                        classNameIndices[i] = dis.readUnsignedShort();
                        break;
                    case 8: // String
                    case 16: // MethodType
                    case 19: // Module
                    case 20: // Package
                        dis.skipBytes(2);
                        break;
                    case 9: // Fieldref
                    case 10: // Methodref
                    case 11: // InterfaceMethodref
                        dis.skipBytes(4);
                        break;
                    case 12: // NameAndType
                        dis.skipBytes(4);
                        break;
                    case 15: // MethodHandle
                        dis.skipBytes(3);
                        break;
                    case 18: // InvokeDynamic
                        dis.skipBytes(4);
                        break;
                    default:
                        log.warn("Unknown constant pool tag: {} at index {}", tag, i);
                        return null;
                }
            }

            return new ConstantPoolData(utf8Strings, classNameIndices, dis);
        } catch (IOException e) {
            log.warn("Error parsing class file bytes", e);
            return null;
        }
    }

    /**
     * Extracts the class name from constant pool data.
     *
     * @param poolData the constant pool data
     * @return the fully qualified class name, or null if extraction fails
     */
    public String extractClassName(ConstantPoolData poolData) {
        if (poolData == null) {
            return null;
        }

        try {
            // Skip access flags
            poolData.inputStream().skipBytes(2);

            // Read this_class index
            int thisClassIndex = poolData.inputStream().readUnsignedShort();

            // Get the UTF-8 string index from the Class entry
            int utf8Index = poolData.classNameIndices()[thisClassIndex];
            String className = poolData.utf8Strings()[utf8Index];

            // Convert internal format (slashes) to dot notation
            if (className != null) {
                return className.replace('/', '.');
            }

            return null;
        } catch (IOException e) {
            log.warn("Error extracting class name", e);
            return null;
        }
    }

    /**
     * Extracts inheritance information from constant pool data.
     *
     * @param poolData the constant pool data
     * @return inheritance info containing superclass and interfaces, or null if extraction fails
     */
    public InheritanceInfo extractInheritanceInfo(ConstantPoolData poolData) {
        if (poolData == null) {
            return null;
        }

        try {
            DataInputStream dis = poolData.inputStream();

            // Skip access flags
            dis.skipBytes(2);

            // Read this_class index (skip, we already know the class name)
            dis.skipBytes(2);

            // Read superclass index
            int superClassIndex = dis.readUnsignedShort();
            String superClassName = null;
            if (superClassIndex > 0) {
                int superClassUtf8Index = poolData.classNameIndices()[superClassIndex];
                superClassName = convertToDotNotation(poolData.utf8Strings()[superClassUtf8Index]);
            }

            // Read interfaces count
            int interfacesCount = dis.readUnsignedShort();

            // Read all implemented interfaces
            List<String> interfaceNames = new ArrayList<>();
            for (int i = 0; i < interfacesCount; i++) {
                int interfaceIndex = dis.readUnsignedShort();
                int interfaceUtf8Index = poolData.classNameIndices()[interfaceIndex];
                String interfaceName = convertToDotNotation(poolData.utf8Strings()[interfaceUtf8Index]);
                interfaceNames.add(interfaceName);
            }

            return new InheritanceInfo(superClassName, interfaceNames);
        } catch (IOException e) {
            log.warn("Error extracting inheritance info", e);
            return null;
        }
    }

    /**
     * Converts internal class name format (slashes and $) to dot notation.
     *
     * @param internalName class name with slashes and/or $
     * @return class name with dots, or null if input is null
     */
    private String convertToDotNotation(String internalName) {
        if (internalName == null) {
            return null;
        }
        return internalName.replace('/', '.').replace('$', '.');
    }

    /**
     * Parses class-level annotations from bytecode.
     *
     * @param classBytes the class file bytes
     * @return set of annotation internal names
     */
    public Set<String> parseClassAnnotations(byte[] classBytes) {
        Set<String> annotations = new HashSet<>();

        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(classBytes))) {
            // Skip magic, version, constant pool
            dis.skipBytes(8);

            int constantPoolCount = dis.readUnsignedShort();

            // Skip constant pool
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
                        log.warn("Unknown tag {} at index {}", tag, i);
                        return annotations;
                    }
                }
            }

            // Skip access flags, this_class, superclass
            dis.skipBytes(6);

            // Skip interfaces
            int interfacesCount = dis.readUnsignedShort();
            dis.skipBytes(interfacesCount * 2);

            // Skip fields
            int fieldsCount = dis.readUnsignedShort();
            for (int i = 0; i < fieldsCount; i++) {
                skipFieldOrMethod(dis);
            }

            // Skip methods
            int methodsCount = dis.readUnsignedShort();
            for (int i = 0; i < methodsCount; i++) {
                skipFieldOrMethod(dis);
            }

            // Read class attributes (this is where RuntimeVisibleAnnotations is)
            int attributesCount = dis.readUnsignedShort();
            for (int i = 0; i < attributesCount; i++) {
                String attrName = readUtf8FromPool(dis, classBytes);
                int attrLength = dis.readInt();

                if ("RuntimeVisibleAnnotations".equals(attrName)) {
                    annotations.addAll(parseRuntimeVisibleAnnotations(dis, classBytes));
                } else {
                    dis.skipBytes(attrLength);
                }
            }

        } catch (IOException e) {
            log.warn("Error parsing class annotations", e);
        }

        return annotations;
    }

    /**
     * Reads a UTF-8 string from the constant pool using the index from the stream.
     */
    private String readUtf8FromPool(DataInputStream dis, byte[] classBytes) throws IOException {
        int index = dis.readUnsignedShort();
        return getUtf8FromPool(classBytes, index);
    }

    /**
     * Extracts a UTF-8 string from the constant pool at the given index.
     */
    private String getUtf8FromPool(byte[] classBytes, int index) throws IOException {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(classBytes))) {
            dis.skipBytes(8); // magic + version
            int count = dis.readUnsignedShort();

            for (int i = 1; i < count && i <= index; i++) {
                int tag = dis.readUnsignedByte();
                if (i == index && tag == 1) {
                    return dis.readUTF();
                }

                switch (tag) {
                    case 1 -> dis.skipBytes(dis.readUnsignedShort());
                    case 3, 4 -> dis.skipBytes(4);
                    case 5, 6 -> {
                        dis.skipBytes(8);
                        i++;
                    }
                    case 7, 8, 16, 19, 20 -> dis.skipBytes(2);
                    case 9, 10, 11, 12, 18 -> dis.skipBytes(4);
                    case 15 -> dis.skipBytes(3);
                    default -> throw new IOException("Unknown tag: " + tag);
                }
            }
        }
        return null;
    }

    /**
     * Parses RuntimeVisibleAnnotations attribute to extract annotation type descriptors.
     */
    private Set<String> parseRuntimeVisibleAnnotations(DataInputStream dis, byte[] classBytes) throws IOException {
        Set<String> annotations = new HashSet<>();
        int numAnnotations = dis.readUnsignedShort();

        for (int i = 0; i < numAnnotations; i++) {
            int typeIndex = dis.readUnsignedShort();
            String typeDescriptor = getUtf8FromPool(classBytes, typeIndex);
            if (typeDescriptor != null) {
                annotations.add(typeDescriptor);
            }

            // Skip annotation element-value pairs
            int numElementValuePairs = dis.readUnsignedShort();
            for (int j = 0; j < numElementValuePairs; j++) {
                dis.skipBytes(2); // element_name_index
                skipElementValue(dis);
            }
        }

        return annotations;
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
     * Skips a field or method structure in the class file.
     */
    private void skipFieldOrMethod(DataInputStream dis) throws IOException {
        dis.skipBytes(6); // access_flags, name_index, descriptor_index
        int attributesCount = dis.readUnsignedShort();
        for (int i = 0; i < attributesCount; i++) {
            dis.skipBytes(2); // attribute_name_index
            int attributeLength = dis.readInt();
            dis.skipBytes(attributeLength);
        }
    }

    /**
     * Record holding constant pool data.
     *
     * @param utf8Strings array of UTF-8 strings from the constant pool
     * @param classNameIndices array mapping Class entry indices to UTF-8 string indices
     * @param inputStream the remaining input stream positioned after the constant pool
     */
    public record ConstantPoolData(String[] utf8Strings, int[] classNameIndices, DataInputStream inputStream) {
    }

    /**
     * Record holding inheritance information.
     *
     * @param superClassName the fully qualified superclass name, or null if extends Object
     * @param interfaceNames list of fully qualified interface names
     */
    public record InheritanceInfo(String superClassName, List<String> interfaceNames) {
    }
}