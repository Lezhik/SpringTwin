package twin.spring.architecture.domain;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * Связь вызова одного метода другим.
 *
 * @since 1.0.0
 */
@RelationshipProperties
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallsRelation {

    /**
     * Уникальный идентификатор связи.
     */
    @Id
    @GeneratedValue
    private String id;

    /**
     * Целевой метод (который вызывается).
     */
    @TargetNode
    private MethodNode targetMethod;

    /**
     * Позиция в исходном коде (номер строки).
     */
    @Property("lineNumber")
    private Integer lineNumber;
}