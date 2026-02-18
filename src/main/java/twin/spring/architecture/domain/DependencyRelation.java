package twin.spring.architecture.domain;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * Связь зависимости между классами через Dependency Injection.
 *
 * @since 1.0.0
 */
@RelationshipProperties
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DependencyRelation {

    /**
     * Уникальный идентификатор связи.
     */
    @Id
    @GeneratedValue
    private String id;

    /**
     * Целевой класс зависимости.
     */
    @TargetNode
    private ClassNode targetClass;

    /**
     * Имя поля для инъекции.
     */
    @Property("fieldName")
    private String fieldName;

    /**
     * Тип инъекции (constructor, setter, field).
     */
    @Property("injectionType")
    private String injectionType;
}