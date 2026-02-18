package twin.spring.architecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import twin.spring.architecture.domain.ClassNode;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ClassNode model.
 */
@DisplayName("ClassNode Model Tests")
class ClassNodeModelTest {

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("UT-B1.4-01: Should create ClassNode using builder")
        void shouldCreateClassNodeUsingBuilder() {
            // Given
            String id = UUID.randomUUID().toString();
            String name = "UserService";
            String fullName = "com.example.service.UserService";
            String packageName = "com.example.service";
            List<String> labels = List.of("Service");
            List<String> modifiers = List.of("public");

            // When
            ClassNode classNode = ClassNode.builder()
                    .id(id)
                    .name(name)
                    .fullName(fullName)
                    .packageName(packageName)
                    .labels(labels)
                    .modifiers(modifiers)
                    .build();

            // Then
            assertEquals(id, classNode.getId());
            assertEquals(name, classNode.getName());
            assertEquals(fullName, classNode.getFullName());
            assertEquals(packageName, classNode.getPackageName());
            assertEquals(labels, classNode.getLabels());
            assertEquals(modifiers, classNode.getModifiers());
        }

        @Test
        @DisplayName("Should create ClassNode with default values")
        void shouldCreateClassNodeWithDefaultValues() {
            // When
            ClassNode classNode = ClassNode.builder()
                    .name("TestService")
                    .fullName("com.example.TestService")
                    .build();

            // Then
            assertEquals("TestService", classNode.getName());
            assertEquals("com.example.TestService", classNode.getFullName());
            assertNotNull(classNode.getLabels());
            assertTrue(classNode.getLabels().isEmpty());
            assertNotNull(classNode.getModifiers());
            assertTrue(classNode.getModifiers().isEmpty());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("UT-B1.4-04: Should have equals for ClassNode")
        void shouldHaveEqualsForClassNode() {
            // Given
            ClassNode classNode1 = ClassNode.builder()
                    .id("test-id-1")
                    .name("UserService")
                    .fullName("com.example.service.UserService")
                    .packageName("com.example.service")
                    .labels(List.of("Service"))
                    .build();

            ClassNode classNode2 = ClassNode.builder()
                    .id("test-id-1")
                    .name("UserService")
                    .fullName("com.example.service.UserService")
                    .packageName("com.example.service")
                    .labels(List.of("Service"))
                    .build();

            ClassNode classNode3 = ClassNode.builder()
                    .id("test-id-2")
                    .name("UserService")
                    .fullName("com.example.service.UserService")
                    .packageName("com.example.service")
                    .labels(List.of("Service"))
                    .build();

            // Then
            assertEquals(classNode1, classNode2);
            assertNotEquals(classNode1, classNode3);
            assertEquals(classNode1.hashCode(), classNode2.hashCode());
        }

        @Test
        @DisplayName("Should have correct equals with null values")
        void shouldHaveCorrectEqualsWithNullValues() {
            // Given
            ClassNode classNode1 = ClassNode.builder()
                    .name("UserService")
                    .build();

            ClassNode classNode2 = ClassNode.builder()
                    .name("UserService")
                    .build();

            // Then
            assertEquals(classNode1, classNode2);
            assertEquals(classNode1.hashCode(), classNode2.hashCode());
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString")
        void shouldGenerateToString() {
            // Given
            ClassNode classNode = ClassNode.builder()
                    .id("test-id")
                    .name("UserService")
                    .fullName("com.example.service.UserService")
                    .packageName("com.example.service")
                    .labels(List.of("Service"))
                    .modifiers(List.of("public"))
                    .build();

            // When
            String toStringResult = classNode.toString();

            // Then
            assertTrue(toStringResult.contains("ClassNode"));
            assertTrue(toStringResult.contains("UserService"));
            assertTrue(toStringResult.contains("com.example.service.UserService"));
        }
    }
}