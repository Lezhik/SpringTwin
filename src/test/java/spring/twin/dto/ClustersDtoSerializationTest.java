package spring.twin.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import spring.twin.dto.types.EdgeType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ClustersDto JSON serialization and deserialization.
 */
class ClustersDtoSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSerializeAndDeserializeClustersDto() throws Exception {
        // Given: create a ClustersDto with sample data
        ClusterMetricsDto metrics = new ClusterMetricsDto(0.81, 0.12);

        ClusterDto cluster = new ClusterDto(
            "cluster-1",
            List.of("OrderController", "OrderService", "OrderRepository"),
            metrics
        );

        PenaltyEdgeDto penaltyEdge = new PenaltyEdgeDto(
            "OrderService",
            "PaymentClient",
            EdgeType.CALLS
        );

        ClustersDto original = new ClustersDto(
            List.of(cluster),
            List.of(penaltyEdge)
        );

        // When: serialize to JSON
        String json = objectMapper.writeValueAsString(original);
        System.out.println("Serialized JSON: " + json);

        // Then: deserialize back to DTO
        ClustersDto deserialized = objectMapper.readValue(json, ClustersDto.class);

        // And: verify objects are equal
        assertThat(deserialized).isEqualTo(original);
        assertThat(deserialized.clusters()).hasSize(1);
        assertThat(deserialized.penaltyEdges()).hasSize(1);

        // Verify cluster details
        assertThat(deserialized.clusters().getFirst().id()).isEqualTo("cluster-1");
        assertThat(deserialized.clusters().getFirst().classes()).containsExactly(
            "OrderController", "OrderService", "OrderRepository"
        );
        assertThat(deserialized.clusters().getFirst().metrics().cohesion()).isEqualTo(0.81);
        assertThat(deserialized.clusters().getFirst().metrics().coupling()).isEqualTo(0.12);

        // Verify penalty edge details
        assertThat(deserialized.penaltyEdges().getFirst().from()).isEqualTo("OrderService");
        assertThat(deserialized.penaltyEdges().getFirst().to()).isEqualTo("PaymentClient");
        assertThat(deserialized.penaltyEdges().getFirst().type()).isEqualTo(EdgeType.CALLS);
    }

    @Test
    void shouldDeserializeFromCliSpecExample() throws Exception {
        // Given: JSON from CliSpec.md example
        String json = """
            {
              "clusters": [
                {
                  "id": "cluster-1",
                  "classes": [
                    "OrderController",
                    "OrderService",
                    "OrderRepository"
                  ],
                  "metrics": {
                    "cohesion": 0.81,
                    "coupling": 0.12
                  }
                }
              ],
              "penaltyEdges": [
                {
                  "from": "OrderService",
                  "to": "PaymentClient",
                  "type": "CALLS"
                }
              ]
            }
            """;

        // When: deserialize
        ClustersDto dto = objectMapper.readValue(json, ClustersDto.class);

        // Then: verify structure
        assertThat(dto.clusters()).hasSize(1);
        assertThat(dto.penaltyEdges()).hasSize(1);
        assertThat(dto.clusters().getFirst().id()).isEqualTo("cluster-1");
        assertThat(dto.clusters().getFirst().metrics().cohesion()).isEqualTo(0.81);
        assertThat(dto.penaltyEdges().getFirst().type()).isEqualTo(EdgeType.CALLS);
    }
}