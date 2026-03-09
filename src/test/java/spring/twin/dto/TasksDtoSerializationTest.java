package spring.twin.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for TasksDto JSON serialization and deserialization.
 */
class TasksDtoSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSerializeAndDeserializeTasksDto() throws Exception {
        // Given: create a TasksDto with sample data
        TaskDto task = new TaskDto(
            "split-class",
            "OrderService",
            "Uses classes from multiple clusters",
            List.of("order-domain", "payment-integration")
        );

        TasksDto original = new TasksDto(List.of(task));

        // When: serialize to JSON
        String json = objectMapper.writeValueAsString(original);
        System.out.println("Serialized JSON: " + json);

        // Then: deserialize back to DTO
        TasksDto deserialized = objectMapper.readValue(json, TasksDto.class);

        // And: verify objects are equal
        assertThat(deserialized).isEqualTo(original);
        assertThat(deserialized.tasks()).hasSize(1);

        // Verify task details
        assertThat(deserialized.tasks().getFirst().taskType()).isEqualTo("split-class");
        assertThat(deserialized.tasks().getFirst().className()).isEqualTo("OrderService");
        assertThat(deserialized.tasks().getFirst().reason()).isEqualTo("Uses classes from multiple clusters");
        assertThat(deserialized.tasks().getFirst().suggestedModules()).containsExactly(
            "order-domain", "payment-integration"
        );
    }

    @Test
    void shouldDeserializeFromCliSpecExample() throws Exception {
        // Given: JSON from CliSpec.md example (using "class" field name as per spec)
        String json = """
            {
              "tasks": [
                {
                  "taskType": "split-class",
                  "class": "OrderService",
                  "reason": "Uses classes from multiple clusters",
                  "suggestedModules": [
                    "order-domain",
                    "payment-integration"
                  ]
                }
              ]
            }
            """;

        // When: deserialize
        TasksDto dto = objectMapper.readValue(json, TasksDto.class);

        // Then: verify structure
        assertThat(dto.tasks()).hasSize(1);
        assertThat(dto.tasks().getFirst().taskType()).isEqualTo("split-class");
        assertThat(dto.tasks().getFirst().className()).isEqualTo("OrderService");
        assertThat(dto.tasks().getFirst().suggestedModules()).containsExactly(
            "order-domain", "payment-integration"
        );
    }
}