package twin.spring.unit;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Lombok model annotations: @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor.
 * 
 * @see <a href="https://projectlombok.org/features/Data">Lombok @Data</a>
 * @see <a href="https://projectlombok.org/features/builder">Lombok @Builder</a>
 * @see <a href="https://projectlombok.org/features/constructor">Lombok Constructors</a>
 */
@DisplayName("Lombok Model Annotations Tests")
class LombokModelAnnotationsTest {

    /**
     * Test model class with all Lombok model annotations.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    static class TestModel {
        private String name;
        private Integer value;
        private Boolean active;
    }

    @Nested
    @DisplayName("@Data Annotation Tests")
    class DataAnnotationTests {

        @Test
        @DisplayName("Should generate getters for all fields")
        void shouldGenerateGetters() {
            TestModel model = new TestModel();
            model.setName("test");
            model.setValue(42);
            model.setActive(true);

            assertEquals("test", model.getName());
            assertEquals(42, model.getValue());
            assertTrue(model.getActive());
        }

        @Test
        @DisplayName("Should generate setters for all fields")
        void shouldGenerateSetters() {
            TestModel model = new TestModel();
            model.setName("updated");
            model.setValue(100);
            model.setActive(false);

            assertEquals("updated", model.getName());
            assertEquals(100, model.getValue());
            assertFalse(model.getActive());
        }

        @Test
        @DisplayName("Should generate equals and hashCode")
        void shouldGenerateEqualsAndHashCode() {
            TestModel model1 = new TestModel("test", 42, true);
            TestModel model2 = new TestModel("test", 42, true);
            TestModel model3 = new TestModel("different", 42, true);

            assertEquals(model1, model2);
            assertNotEquals(model1, model3);
            assertEquals(model1.hashCode(), model2.hashCode());
        }

        @Test
        @DisplayName("Should generate toString")
        void shouldGenerateToString() {
            TestModel model = new TestModel("test", 42, true);
            String toStringResult = model.toString();

            assertTrue(toStringResult.contains("TestModel"));
            assertTrue(toStringResult.contains("name=test"));
            assertTrue(toStringResult.contains("value=42"));
            assertTrue(toStringResult.contains("active=true"));
        }
    }

    @Nested
    @DisplayName("@Builder Annotation Tests")
    class BuilderAnnotationTests {

        @Test
        @DisplayName("Should create object using builder")
        void shouldCreateObjectUsingBuilder() {
            TestModel model = TestModel.builder()
                    .name("builderTest")
                    .value(123)
                    .active(false)
                    .build();

            assertEquals("builderTest", model.getName());
            assertEquals(123, model.getValue());
            assertFalse(model.getActive());
        }

        @Test
        @DisplayName("Should create object using builder with partial fields")
        void shouldCreateObjectWithPartialFields() {
            TestModel model = TestModel.builder()
                    .name("partial")
                    .build();

            assertEquals("partial", model.getName());
            assertNull(model.getValue());
            assertNull(model.getActive());
        }
    }

    @Nested
    @DisplayName("@NoArgsConstructor Annotation Tests")
    class NoArgsConstructorTests {

        @Test
        @DisplayName("Should create object with no-args constructor")
        void shouldCreateObjectWithNoArgsConstructor() {
            TestModel model = new TestModel();

            assertNull(model.getName());
            assertNull(model.getValue());
            assertNull(model.getActive());
        }
    }

    @Nested
    @DisplayName("@AllArgsConstructor Annotation Tests")
    class AllArgsConstructorTests {

        @Test
        @DisplayName("Should create object with all-args constructor")
        void shouldCreateObjectWithAllArgsConstructor() {
            TestModel model = new TestModel("full", 999, true);

            assertEquals("full", model.getName());
            assertEquals(999, model.getValue());
            assertTrue(model.getActive());
        }
    }
}