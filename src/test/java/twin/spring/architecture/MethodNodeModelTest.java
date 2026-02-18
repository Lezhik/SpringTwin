package twin.spring.architecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import twin.spring.architecture.domain.MethodNode;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MethodNode model.
 */
@DisplayName("MethodNode Model Tests")
class MethodNodeModelTest {

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("UT-B1.4-02: Should create MethodNode using builder")
        void shouldCreateMethodNodeUsingBuilder() {
            // Given
            String id = UUID.randomUUID().toString();
            String name = "getUsers";
            String signature = "public List<User> getUsers()";
            String returnType = "List<User>";
            List<String> modifiers = List.of("public");
            String parameters = "()";

            // When
            MethodNode methodNode = MethodNode.builder()
                    .id(id)
                    .name(name)
                    .signature(signature)
                    .returnType(returnType)
                    .modifiers(modifiers)
                    .parameters(parameters)
                    .build();

            // Then
            assertEquals(id, methodNode.getId());
            assertEquals(name, methodNode.getName());
            assertEquals(signature, methodNode.getSignature());
            assertEquals(returnType, methodNode.getReturnType());
            assertEquals(modifiers, methodNode.getModifiers());
            assertEquals(parameters, methodNode.getParameters());
        }

        @Test
        @DisplayName("Should create MethodNode with default values")
        void shouldCreateMethodNodeWithDefaultValues() {
            // When
            MethodNode methodNode = MethodNode.builder()
                    .name("doSomething")
                    .build();

            // Then
            assertEquals("doSomething", methodNode.getName());
            assertNotNull(methodNode.getModifiers());
            assertTrue(methodNode.getModifiers().isEmpty());
            assertNotNull(methodNode.getCalledMethods());
            assertTrue(methodNode.getCalledMethods().isEmpty());
            assertNotNull(methodNode.getExposedEndpoints());
            assertTrue(methodNode.getExposedEndpoints().isEmpty());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("UT-B1.4-05: Should have equals for MethodNode")
        void shouldHaveEqualsForMethodNode() {
            // Given
            MethodNode methodNode1 = MethodNode.builder()
                    .id("test-method-id-1")
                    .name("getUsers")
                    .signature("public List<User> getUsers()")
                    .returnType("List<User>")
                    .build();

            MethodNode methodNode2 = MethodNode.builder()
                    .id("test-method-id-1")
                    .name("getUsers")
                    .signature("public List<User> getUsers()")
                    .returnType("List<User>")
                    .build();

            MethodNode methodNode3 = MethodNode.builder()
                    .id("test-method-id-2")
                    .name("getUsers")
                    .signature("public List<User> getUsers()")
                    .returnType("List<User>")
                    .build();

            // Then
            assertEquals(methodNode1, methodNode2);
            assertNotEquals(methodNode1, methodNode3);
            assertEquals(methodNode1.hashCode(), methodNode2.hashCode());
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString")
        void shouldGenerateToString() {
            // Given
            MethodNode methodNode = MethodNode.builder()
                    .id("test-id")
                    .name("getUsers")
                    .signature("public List<User> getUsers()")
                    .returnType("List<User>")
                    .modifiers(List.of("public"))
                    .build();

            // When
            String toStringResult = methodNode.toString();

            // Then
            assertTrue(toStringResult.contains("MethodNode"));
            assertTrue(toStringResult.contains("getUsers"));
            assertTrue(toStringResult.contains("List<User>"));
        }
    }
}