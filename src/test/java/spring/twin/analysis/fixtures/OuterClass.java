package spring.twin.analysis.fixtures;

/**
 * Test fixture - outer class with inner class for testing inner class handling.
 */
public class OuterClass {

    /**
     * Inner class extending the outer class.
     */
    public static class InnerClass extends OuterClass {
    }
}