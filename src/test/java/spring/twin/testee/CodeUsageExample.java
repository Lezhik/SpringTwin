package spring.twin.testee;

import java.util.ArrayList;

/**
 * Test class with various code usage patterns for CodeUsageExtractor tests.
 */
public class CodeUsageExample {

    static {
        // Static initializer block
        System.setProperty("test", "value");
    }

    /**
     * Contains new ArrayList<>()
     */
    public void createObject() {
        new ArrayList<>();
    }

    /**
     * Contains String.valueOf(42)
     */
    public void callMethod() {
        String.valueOf(42);
    }

    /**
     * Contains (String) obj
     */
    public void castType(Object obj) {
        String s = (String) obj;
    }

    /**
     * Contains obj instanceof String
     */
    public void instanceCheck(Object obj) {
        boolean result = obj instanceof String;
    }

    /**
     * Contains System.out
     */
    public void accessField() {
        System.out.println("test");
    }

    /**
     * Static method for static initialization test
     */
    public static void staticInit() {
        // Method referenced from static block
    }
}