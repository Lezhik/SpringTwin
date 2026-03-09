package spring.twin.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import spring.twin.dto.types.EdgeType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for BytecodeGraphDto JSON serialization and deserialization.
 */
class BytecodeGraphDtoSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSerializeAndDeserializeBytecodeGraphDto() throws Exception {
        // Given: create a BytecodeGraphDto with sample data
        BytecodeEdgeDto callEdge = new BytecodeEdgeDto(
            EdgeType.CALLS,
            "OrderService",
            "PaymentClient",
            "processPayment",
            null
        );

        BytecodeEdgeDto fieldAccessEdge = new BytecodeEdgeDto(
            EdgeType.ACCESSES_FIELD,
            "OrderService",
            "OrderRepository",
            null,
            "repository"
        );

        BytecodeEdgeDto instantiateEdge = new BytecodeEdgeDto(
            EdgeType.INSTANTIATES,
            "OrderService",
            "Order",
            null,
            null
        );

        BytecodeGraphDto original = new BytecodeGraphDto(
            List.of(callEdge, fieldAccessEdge, instantiateEdge)
        );

        // When: serialize to JSON
        String json = objectMapper.writeValueAsString(original);
        System.out.println("Serialized JSON: " + json);

        // Then: deserialize back to DTO
        BytecodeGraphDto deserialized = objectMapper.readValue(json, BytecodeGraphDto.class);

        // And: verify objects are equal
        assertThat(deserialized).isEqualTo(original);
        assertThat(deserialized.edges()).hasSize(3);

        // Verify CALLS edge
        assertThat(deserialized.edges().getFirst().type()).isEqualTo(EdgeType.CALLS);
        assertThat(deserialized.edges().getFirst().fromClass()).isEqualTo("OrderService");
        assertThat(deserialized.edges().getFirst().toClass()).isEqualTo("PaymentClient");
        assertThat(deserialized.edges().getFirst().method()).isEqualTo("processPayment");

        // Verify ACCESSES_FIELD edge
        assertThat(deserialized.edges().get(1).type()).isEqualTo(EdgeType.ACCESSES_FIELD);
        assertThat(deserialized.edges().get(1).field()).isEqualTo("repository");

        // Verify INSTANTIATES edge
        assertThat(deserialized.edges().get(2).type()).isEqualTo(EdgeType.INSTANTIATES);
    }

    @Test
    void shouldDeserializeFromCliSpecExample() throws Exception {
        // Given: JSON from CliSpec.md example
        String json = """
            {
              "edges": [
                {
                  "type": "CALLS",
                  "fromClass": "OrderService",
                  "toClass": "PaymentClient",
                  "method": "processPayment"
                },
                {
                  "type": "ACCESSES_FIELD",
                  "fromClass": "OrderService",
                  "toClass": "OrderRepository",
                  "field": "repository"
                },
                {
                  "type": "INSTANTIATES",
                  "fromClass": "OrderService",
                  "toClass": "Order"
                }
              ]
            }
            """;

        // When: deserialize
        BytecodeGraphDto dto = objectMapper.readValue(json, BytecodeGraphDto.class);

        // Then: verify structure
        assertThat(dto.edges()).hasSize(3);
        assertThat(dto.edges().get(0).type()).isEqualTo(EdgeType.CALLS);
        assertThat(dto.edges().get(0).method()).isEqualTo("processPayment");
        assertThat(dto.edges().get(1).type()).isEqualTo(EdgeType.ACCESSES_FIELD);
        assertThat(dto.edges().get(1).field()).isEqualTo("repository");
        assertThat(dto.edges().get(2).type()).isEqualTo(EdgeType.INSTANTIATES);
    }
}