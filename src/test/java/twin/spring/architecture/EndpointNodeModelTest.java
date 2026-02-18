package twin.spring.architecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import twin.spring.architecture.domain.EndpointNode;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EndpointNode model.
 */
@DisplayName("EndpointNode Model Tests")
class EndpointNodeModelTest {

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("UT-B1.4-03: Should create EndpointNode using builder")
        void shouldCreateEndpointNodeUsingBuilder() {
            // Given
            String id = UUID.randomUUID().toString();
            String path = "/api/users";
            String httpMethod = "GET";
            String produces = "application/json";
            String consumes = "application/json";

            // When
            EndpointNode endpointNode = EndpointNode.builder()
                    .id(id)
                    .path(path)
                    .httpMethod(httpMethod)
                    .produces(produces)
                    .consumes(consumes)
                    .build();

            // Then
            assertEquals(id, endpointNode.getId());
            assertEquals(path, endpointNode.getPath());
            assertEquals(httpMethod, endpointNode.getHttpMethod());
            assertEquals(produces, endpointNode.getProduces());
            assertEquals(consumes, endpointNode.getConsumes());
        }

        @Test
        @DisplayName("Should create EndpointNode with default values")
        void shouldCreateEndpointNodeWithDefaultValues() {
            // When
            EndpointNode endpointNode = EndpointNode.builder()
                    .path("/api/test")
                    .httpMethod("POST")
                    .build();

            // Then
            assertEquals("/api/test", endpointNode.getPath());
            assertEquals("POST", endpointNode.getHttpMethod());
            assertNull(endpointNode.getProduces());
            assertNull(endpointNode.getConsumes());
        }

        @Test
        @DisplayName("Should create EndpointNode for DELETE method")
        void shouldCreateEndpointNodeForDeleteMethod() {
            // When
            EndpointNode endpointNode = EndpointNode.builder()
                    .path("/api/users/{id}")
                    .httpMethod("DELETE")
                    .produces("application/json")
                    .build();

            // Then
            assertEquals("/api/users/{id}", endpointNode.getPath());
            assertEquals("DELETE", endpointNode.getHttpMethod());
            assertEquals("application/json", endpointNode.getProduces());
        }
    }

    @Nested
    @DisplayName(" Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("UT-B1.4-06: Should have equals for EndpointNode")
        void shouldHaveEqualsForEndpointNode() {
            // Given
            EndpointNode endpointNode1 = EndpointNode.builder()
                    .id("test-endpoint-id-1")
                    .path("/api/users")
                    .httpMethod("GET")
                    .produces("application/json")
                    .build();

            EndpointNode endpointNode2 = EndpointNode.builder()
                    .id("test-endpoint-id-1")
                    .path("/api/users")
                    .httpMethod("GET")
                    .produces("application/json")
                    .build();

            EndpointNode endpointNode3 = EndpointNode.builder()
                    .id("test-endpoint-id-2")
                    .path("/api/users")
                    .httpMethod("GET")
                    .produces("application/json")
                    .build();

            // Then
            assertEquals(endpointNode1, endpointNode2);
            assertNotEquals(endpointNode1, endpointNode3);
            assertEquals(endpointNode1.hashCode(), endpointNode2.hashCode());
        }

        @Test
        @DisplayName("Should have correct equals with null values")
        void shouldHaveCorrectEqualsWithNullValues() {
            // Given
            EndpointNode endpointNode1 = EndpointNode.builder()
                    .path("/api/test")
                    .httpMethod("POST")
                    .build();

            EndpointNode endpointNode2 = EndpointNode.builder()
                    .path("/api/test")
                    .httpMethod("POST")
                    .build();

            // Then
            assertEquals(endpointNode1, endpointNode2);
            assertEquals(endpointNode1.hashCode(), endpointNode2.hashCode());
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString")
        void shouldGenerateToString() {
            // Given
            EndpointNode endpointNode = EndpointNode.builder()
                    .id("test-id")
                    .path("/api/users")
                    .httpMethod("GET")
                    .produces("application/json")
                    .consumes("application/json")
                    .build();

            // When
            String toStringResult = endpointNode.toString();

            // Then
            assertTrue(toStringResult.contains("EndpointNode"));
            assertTrue(toStringResult.contains("/api/users"));
            assertTrue(toStringResult.contains("GET"));
        }
    }
}