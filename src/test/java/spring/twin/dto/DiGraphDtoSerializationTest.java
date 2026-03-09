package spring.twin.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import spring.twin.dto.types.EdgeType;
import spring.twin.dto.types.InjectionType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DiGraphDto JSON serialization and deserialization.
 */
class DiGraphDtoSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSerializeAndDeserializeDiGraphDto() throws Exception {
        // Given: create a DiGraphDto with sample data
        DiNodeDto node1 = new DiNodeDto(
            "Class",
            "OrderService",
            "com.example.service",
            List.of("Service")
        );

        DiNodeDto node2 = new DiNodeDto(
            "Class",
            "OrderController",
            "com.example.controller",
            List.of("Controller", "RestController")
        );

        DiEdgeDetailsDto edgeDetails = new DiEdgeDetailsDto(
            InjectionType.CONSTRUCTOR,
            0,
            null,
            null
        );

        DiEdgeDto edge = new DiEdgeDto(
            EdgeType.DEPENDS_ON,
            "OrderController",
            "OrderService",
            edgeDetails
        );

        DiGraphDto original = new DiGraphDto(
            List.of(node1, node2),
            List.of(edge)
        );

        // When: serialize to JSON
        String json = objectMapper.writeValueAsString(original);
        System.out.println("Serialized JSON: " + json);

        // Then: deserialize back to DTO
        DiGraphDto deserialized = objectMapper.readValue(json, DiGraphDto.class);

        // And: verify objects are equal
        assertThat(deserialized).isEqualTo(original);
        assertThat(deserialized.nodes()).hasSize(2);
        assertThat(deserialized.edges()).hasSize(1);

        // Verify node details
        assertThat(deserialized.nodes().getFirst().name()).isEqualTo("OrderService");
        assertThat(deserialized.nodes().getFirst().packageName()).isEqualTo("com.example.service");
        assertThat(deserialized.nodes().getFirst().labels()).containsExactly("Service");

        // Verify edge details
        assertThat(deserialized.edges().getFirst().type()).isEqualTo(EdgeType.DEPENDS_ON);
        assertThat(deserialized.edges().getFirst().from()).isEqualTo("OrderController");
        assertThat(deserialized.edges().getFirst().to()).isEqualTo("OrderService");
        assertThat(deserialized.edges().getFirst().details().injectionType()).isEqualTo(InjectionType.CONSTRUCTOR);
        assertThat(deserialized.edges().getFirst().details().parameterIndex()).isEqualTo(0);
    }

    @Test
    void shouldDeserializeFromCliSpecExample() throws Exception {
        // Given: JSON from CliSpec.md example (using "package" field name as per spec)
        String json = """
            {
              "nodes": [
                {
                  "type": "Class",
                  "name": "OrderService",
                  "package": "com.example.service",
                  "labels": ["Service"]
                }
              ],
              "edges": [
                {
                  "type": "DEPENDS_ON",
                  "from": "OrderController",
                  "to": "OrderService",
                  "details": {
                    "injectionType": "CONSTRUCTOR",
                    "parameterIndex": 0
                  }
                }
              ]
            }
            """;

        // When: deserialize
        DiGraphDto dto = objectMapper.readValue(json, DiGraphDto.class);

        // Then: verify structure
        assertThat(dto.nodes()).hasSize(1);
        assertThat(dto.edges()).hasSize(1);
        assertThat(dto.nodes().getFirst().name()).isEqualTo("OrderService");
        assertThat(dto.nodes().getFirst().packageName()).isEqualTo("com.example.service");
        assertThat(dto.edges().getFirst().details().injectionType()).isEqualTo(InjectionType.CONSTRUCTOR);
    }
}