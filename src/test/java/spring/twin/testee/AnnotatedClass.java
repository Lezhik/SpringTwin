package spring.twin.testee;

/**
 * Test class with various annotations for AnnotationTypeExtractor tests.
 */
@Deprecated
public class AnnotatedClass {

    @CustomAnnotation
    private String annotatedField;

    @CustomAnnotation
    public int annotatedMethod() {
        return 0;
    }

    public void methodWithAnnotatedParameter(@CustomAnnotation String param) {
    }
}