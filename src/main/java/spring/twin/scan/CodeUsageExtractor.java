package spring.twin.scan;

import java.util.Set;

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
        throw new UnsupportedOperationException("Not implemented yet");
    }

}