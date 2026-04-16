package spring.twin.scan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.springframework.stereotype.Component;

/**
 * Extracts type dependencies from method code bodies and static initializers.
 *
 * <p>This class analyzes bytecode instructions within method bodies to extract all types
 * that are used in code execution, including object creation, method invocations,
 * field accesses, type casts, and array operations.
 *
 * <p>Uses ASM's MethodVisitor for bytecode instruction analysis.
 */
@Component
public class CodeUsageExtractor {

    /**
     * Analyzes class bytecode and extracts all types used in method bodies and static initializers.
     *
     * <p>This method extracts types from the following bytecode instructions:
     * <ul>
     *   <li>{@code NEW} - object creation</li>
     *   <li>{@code NEWARRAY} - primitive array creation</li>
     *   <li>{@code ANEWARRAY} - object array creation</li>
     *   <li>{@code CHECKCAST} - type casting</li>
     *   <li>{@code INSTANCEOF} - type checking</li>
     *   <li>{@code GETSTATIC}/{@code PUTSTATIC} - static field access</li>
     *   <li>{@code GETFIELD}/{@code PUTFIELD} - instance field access</li>
     *   <li>{@code INVOKEVIRTUAL}/{@code INVOKESTATIC}/{@code INVOKESPECIAL}/{@code INVOKEINTERFACE} - method invocations</li>
     *   <li>{@code LDC} with Class value - class literal loading</li>
     * </ul>
     *
     * <p>All extracted types are returned as Fully Qualified Class Names (FQCN).
     *
     * @param classBytes the bytecode of the class to analyze
     * @return a set of FQCN strings representing all types used in code bodies;
     *         empty set if no types are referenced in method code
     * @throws IllegalArgumentException if classBytes is null
     */
    public Set<String> extract(byte[] classBytes) {
        if (classBytes == null) {
            throw new IllegalArgumentException("classBytes must not be null");
        }

        Set<String> types = new HashSet<>();

        ClassReader classReader = new ClassReader(classBytes);
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor,
                                             String signature, String[] exceptions) {
                return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public void visitTypeInsn(int opcode, String type) {
                        // NEW, ANEWARRAY, CHECKCAST, INSTANCEOF - type is internal name
                        FqcnNormalizer.fromInternalName(type).ifPresent(types::add);
                    }

                    @Override
                    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                        // GETSTATIC, PUTSTATIC, GETFIELD, PUTFIELD - owner is the class containing the field
                        FqcnNormalizer.fromInternalName(owner).ifPresent(types::add);
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name,
                                                String descriptor, boolean isInterface) {
                        // INVOKEVIRTUAL, INVOKESTATIC, INVOKESPECIAL, INVOKEINTERFACE
                        // owner is the class/interface containing the method
                        FqcnNormalizer.fromInternalName(owner).ifPresent(types::add);
                    }

                    @Override
                    public void visitLdcInsn(Object value) {
                        // LDC with Type value - class literal loading
                        if (value instanceof Type) {
                            Type type = (Type) value;
                            FqcnNormalizer.fromDescriptor(type.getDescriptor())
                                    .ifPresent(types::add);
                        }
                    }
                };
            }
        };

        classReader.accept(classVisitor, 0);
        return types;
    }

    /**
     * Analyzes class bytecode and extracts all types used in method bodies and static initializers
     * with detailed link information.
     *
     * <p>This method extracts types from bytecode instructions within method bodies and static
     * initializers, returning a map where each key is a Fully Qualified Class Name (FQCN) of a
     * dependent class, and the value is a set of {@link LinkDetails} describing the usage context.
     *
     * <p>The method signature format follows ASM convention: {@code ownerClassName.methodName(descriptor)}
     *
     * <p>LinkDetails formation rules:
     * <ul>
     *   <li>Usage in static initializer ({@code <clinit>}) → {@code LinkDetails.of(LinkType.STATIC_BLOCK)}
     *       (empty details)</li>
     *   <li>Usage in method (method call, variable declaration, object creation, etc.) →
     *       {@code LinkDetails.of(LinkType.METHOD, methodSignature)} where methodSignature is the
     *       signature of the method containing the usage</li>
     * </ul>
     *
     * @param classBytes the bytecode of the class to analyze
     * @return a map where key is FQCN of dependent class and value is a set of LinkDetails
     *         describing the usage context; empty map if no types are referenced in method code
     * @throws IllegalArgumentException if classBytes is null
     */
    public Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes) {
        if (classBytes == null) {
            throw new IllegalArgumentException("classBytes must not be null");
        }

        Map<String, Set<LinkDetails>> result = new HashMap<>();
        ClassReader classReader = new ClassReader(classBytes);

        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM9) {
            private String className;

            @Override
            public void visit(int version, int access, String name, String signature,
                              String superName, String[] interfaces) {
                this.className = name;
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor,
                                             String signature, String[] exceptions) {
                LinkType linkType = "<clinit>".equals(name) ? LinkType.STATIC_BLOCK : LinkType.METHOD;
                String methodSignature = extractMethodSignature(name, descriptor);

                return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public void visitTypeInsn(int opcode, String type) {
                        FqcnNormalizer.fromInternalName(type).ifPresent(fqcn -> {
                            LinkDetails detail = linkType == LinkType.STATIC_BLOCK
                                    ? LinkDetails.of(linkType)
                                    : LinkDetails.of(linkType, methodSignature);
                            addDetail(result, fqcn, detail);
                        });
                    }

                    @Override
                    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                        FqcnNormalizer.fromInternalName(owner).ifPresent(fqcn -> {
                            LinkDetails detail = linkType == LinkType.STATIC_BLOCK
                                    ? LinkDetails.of(linkType)
                                    : LinkDetails.of(linkType, methodSignature);
                            addDetail(result, fqcn, detail);
                        });
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name,
                                                String descriptor, boolean isInterface) {
                        FqcnNormalizer.fromInternalName(owner).ifPresent(fqcn -> {
                            LinkDetails detail = linkType == LinkType.STATIC_BLOCK
                                    ? LinkDetails.of(linkType)
                                    : LinkDetails.of(linkType, methodSignature);
                            addDetail(result, fqcn, detail);
                        });
                    }

                    @Override
                    public void visitLdcInsn(Object value) {
                        if (value instanceof Type) {
                            Type type = (Type) value;
                            FqcnNormalizer.fromDescriptor(type.getDescriptor()).ifPresent(fqcn -> {
                                LinkDetails detail = linkType == LinkType.STATIC_BLOCK
                                        ? LinkDetails.of(linkType)
                                        : LinkDetails.of(linkType, methodSignature);
                                addDetail(result, fqcn, detail);
                            });
                        }
                    }
                };
            }
        };

        classReader.accept(classVisitor, 0);
        return result;
    }

    /**
     * Adds a LinkDetails entry to the set for the specified FQCN.
     *
     * <p>If the FQCN is not yet present in the map, a new HashSet is created.
     * The LinkDetails is then added to the set associated with the FQCN.
     *
     * @param map    the result map to add to
     * @param fqcn   the Fully Qualified Class Name
     * @param detail the LinkDetails to add
     */
    private void addDetail(Map<String, Set<LinkDetails>> map, String fqcn, LinkDetails detail) {
        map.computeIfAbsent(fqcn, k -> new HashSet<>()).add(detail);
    }

    /**
     * Extracts method signature in the format: methodName(argumentDescriptor).
     *
     * <p>The signature includes only the method name and argument types (descriptor),
     * without the class name and without the return type.
     *
     * <p>Examples:
     * <ul>
     *   <li>{@code getName()} for method with no arguments</li>
     *   <li>{@code setName(Ljava/lang/String;)} for method with String argument</li>
     *   <li>{@code <init>(Ljava/lang/String;)} for constructor with String argument</li>
     * </ul>
     *
     * @param methodName the method name (or {@code <init>} for constructors)
     * @param methodDesc the method descriptor in ASM format (e.g., {@code ()Ljava/lang/String;})
     * @return the method signature with name and arguments only
     */
    private String extractMethodSignature(String methodName, String methodDesc) {
        // Extract argument part from descriptor: (args)returnType -> (args)
        int argsEnd = methodDesc.lastIndexOf(')');
        if (argsEnd == -1) {
            argsEnd = methodDesc.length();
        }
        String argumentPart = methodDesc.substring(0, argsEnd + 1);
        return methodName + argumentPart;
    }

}