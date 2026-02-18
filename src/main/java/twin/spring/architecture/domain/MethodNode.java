package twin.spring.architecture.domain;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

/**
 * Метод Java класса.
 *
 * @since 1.0.0
 */
@Node("Method")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodNode {

    /**
     * Уникальный идентификатор узла.
     */
    @Id
    @GeneratedValue
    private String id;

    /**
     * Имя метода.
     */
    @Property("name")
    private String name;

    /**
     * Сигнатура метода.
     */
    @Property("signature")
    private String signature;

    /**
     * Возвращаемый тип.
     */
    @Property("returnType")
    private String returnType;

    /**
     * Модификаторы (public, private, static, etc.).
     */
    @Property("modifiers")
    @Builder.Default
    private List<String> modifiers = new ArrayList<>();

    /**
     * Параметры метода в виде строки (для простоты хранения).
     */
    @Property("parameters")
    private String parameters;

    /**
     * Родительский класс.
     */
    @Relationship(type = "HAS_METHOD", direction = Relationship.Direction.INCOMING)
    private ClassNode parentClass;

    /**
     * Вызываемые методы (связи CALLS).
     */
    @Relationship(type = "CALLS", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<CallsRelation> calledMethods = new ArrayList<>();

    /**
     * Экспонируемые endpoints (связи EXPOSES_ENDPOINT).
     */
    @Relationship(type = "EXPOSES_ENDPOINT", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<ExposesEndpointRelation> exposedEndpoints = new ArrayList<>();
}