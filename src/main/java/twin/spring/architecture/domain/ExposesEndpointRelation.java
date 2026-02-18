package twin.spring.architecture.domain;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * Связь метода с экспонируемым REST endpoint.
 *
 * @since 1.0.0
 */
@RelationshipProperties
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExposesEndpointRelation {

    /**
     * Уникальный идентификатор связи.
     */
    @Id
    @GeneratedValue
    private String id;

    /**
     * Endpoint, который экспонируется методом.
     */
    @TargetNode
    private EndpointNode endpoint;
}