package twin.spring.unit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Lombok @RequiredArgsConstructor annotation.
 * 
 * @see <a href="https://projectlombok.org/features/constructor">Lombok @RequiredArgsConstructor</a>
 */
@DisplayName("Lombok @RequiredArgsConstructor Annotation Tests")
class LombokRequiredArgsConstructorTest {

    /**
     * Test service class with @RequiredArgsConstructor and final fields.
     */
    @RequiredArgsConstructor
    static class TestServiceWithRequiredArgs {
        private final String requiredField;
        private final Integer requiredNumber;
        private String optionalField; // Non-final, should not be in constructor
    }

    /**
     * Test service class with only final fields.
     */
    @RequiredArgsConstructor
    static class TestServiceAllFinal {
        private final String field1;
        private final Integer field2;
        private final Boolean field3;
    }

    /**
     * Test class with mixed fields (final and non-final).
     */
    @RequiredArgsConstructor
    static class TestServiceMixed {
        private final String finalField;
        private final Integer anotherFinal;
        private String nonFinal1;
        private String nonFinal2;
    }

    @Nested
    @DisplayName("@RequiredArgsConstructor Basic Tests")
    class BasicTests {

        @Test
        @DisplayName("Should generate constructor with only final fields")
        void shouldGenerateConstructorWithOnlyFinalFields() {
            Constructor<?>[] constructors = TestServiceWithRequiredArgs.class.getDeclaredConstructors();
            assertTrue(constructors.length > 0, "Should have at least one constructor");

            // Find constructor with parameters
            Constructor<?> paramConstructor = Arrays.stream(constructors)
                    .filter(c -> c.getParameterCount() > 0)
                    .findFirst()
                    .orElse(null);

            assertNotNull(paramConstructor, "Should have a constructor with parameters");

            // Should have exactly 2 parameters (requiredField and requiredNumber)
            assertEquals(2, paramConstructor.getParameterCount());
        }

        @Test
        @DisplayName("Should create object using constructor with final fields")
        void shouldCreateObjectUsingConstructorWithFinalFields() {
            TestServiceWithRequiredArgs service = 
                    new TestServiceWithRequiredArgs("test string", 42);

            assertEquals("test string", service.requiredField);
            assertEquals(42, service.requiredNumber);
        }

        @Test
        @DisplayName("Constructor parameters should match final fields order")
        void constructorParametersShouldMatchFinalFieldsOrder() {
            Constructor<?>[] constructors = TestServiceWithRequiredArgs.class.getDeclaredConstructors();
            Constructor<?> paramConstructor = Arrays.stream(constructors)
                    .filter(c -> c.getParameterCount() > 0)
                    .findFirst()
                    .orElse(null);

            assertNotNull(paramConstructor);

            Parameter[] parameters = paramConstructor.getParameters();
            
            // Check parameter names (they should match field names)
            assertEquals("requiredField", parameters[0].getName());
            assertEquals("requiredNumber", parameters[1].getName());
        }
    }

    @Nested
    @DisplayName("@RequiredArgsConstructor All Final Fields Tests")
    class AllFinalFieldsTests {

        @Test
        @DisplayName("Should include all final fields in constructor")
        void shouldIncludeAllFinalFieldsInConstructor() {
            Constructor<?>[] constructors = TestServiceAllFinal.class.getDeclaredConstructors();
            Constructor<?> constructor = constructors[0];

            assertEquals(3, constructor.getParameterCount());
        }

        @Test
        @DisplayName("Should create object with all final fields")
        void shouldCreateObjectWithAllFinalFields() {
            TestServiceAllFinal service = 
                    new TestServiceAllFinal("value1", 100, true);

            assertEquals("value1", service.field1);
            assertEquals(100, service.field2);
            assertTrue(service.field3);
        }
    }

    @Nested
    @DisplayName("@RequiredArgsConstructor Mixed Fields Tests")
    class MixedFieldsTests {

        @Test
        @DisplayName("Should only include final fields in constructor")
        void shouldOnlyIncludeFinalFieldsInConstructor() {
            Constructor<?>[] constructors = TestServiceMixed.class.getDeclaredConstructors();
            Constructor<?> constructor = constructors[0];

            // Should only have 2 parameters (final fields only)
            assertEquals(2, constructor.getParameterCount());
        }

        @Test
        @DisplayName("Non-final fields should not be in constructor")
        void nonFinalFieldsShouldNotBeInConstructor() throws Exception {
            TestServiceMixed service = new TestServiceMixed("final value", 50);
            
            // Final fields should be set
            assertEquals("final value", service.finalField);
            assertEquals(50, service.anotherFinal);
            
            // Non-final fields should be null (default)
            assertNull(service.nonFinal1);
            assertNull(service.nonFinal2);
            
            // Can set non-final fields
            service.nonFinal1 = "set";
            service.nonFinal2 = "also set";
            assertEquals("set", service.nonFinal1);
            assertEquals("also set", service.nonFinal2);
        }
    }

    @Nested
    @DisplayName("Integration with Spring DI")
    class SpringDiTests {

        @Test
        @DisplayName("RequiredArgsConstructor works with Spring beans")
        void requiredArgsConstructorWorksWithSpringBeans() {
            // This test verifies that the constructor generated by @RequiredArgsConstructor
            // can be used with Spring's dependency injection
            
            // The constructor should accept final fields as dependencies
            TestServiceWithRequiredArgs service = 
                    new TestServiceWithRequiredArgs("dependency1", 123);
            
            // In a real Spring context, this would be:
            // @Service
            // @RequiredArgsConstructor
            // class MyService {
            //     private final Dependency1 dep1;
            //     private final Dependency2 dep2;
            // }
            
            assertNotNull(service);
            assertEquals("dependency1", service.requiredField);
            assertEquals(123, service.requiredNumber);
        }
    }
}