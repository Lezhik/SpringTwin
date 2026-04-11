package spring.twin.testee;

/**
 * Test class with method annotation for AnnotationTypeExtractor tests.
 */
public class MethodAnnotatedClass {

    @CustomAnnotation
    public int annotatedMethod() {
        return 0;
    }
}