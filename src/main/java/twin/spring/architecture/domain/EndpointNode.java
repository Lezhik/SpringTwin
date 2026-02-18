package twin.spring.architecture.domain;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * REST endpoint, экспонируемый методом контроллера.
 *
 * @since 1.0.0
 */
@Node("Endpoint")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointNode {

    /**
     * Уникальный идентификатор узла.
     */
    @Id
    @GeneratedValue
    private String id;

    /**
     * HTTP путь.
     */
    @Property("path")
    private String path;

    /**
     * HTTP метод (GET, POST, PUT, DELETE, PATCH).
     */
    @Property("httpMethod")
    private String httpMethod;

    /**
     * Content-Type для ответа.
     */
    @Property("produces")
    private String produces;

    /**
     * Content-Type для запроса.
     */
    @Property("consumes")
    private String consumes;

    /**
     * Метод, экспонирующий endpoint.
     */
    @Relationship(type = "EXPOSES_ENDPOINT", direction = Relationship.Direction.INCOMING)
    private MethodNode exposingMethod;
}