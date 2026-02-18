package twin.spring.architecture.domain;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.ArrayList;

/**
 * Java класс в анализируемом проекте.
 * 
 * <p>Узел графа с динамическими метками на основе Spring аннотаций.</p>
 * 
 * <p>Имя ClassNode используется вместо Class, так как Class - зарезервированное слово Java.</p>
 *
 * @since 1.0.0
 */
@Node("Class")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassNode {

    /**
     * Уникальный идентификатор узла.
     */
    @Id
    @GeneratedValue
    private String id;

    /**
     * Простое имя класса.
     */
    @Property("name")
    private String name;

    /**
     * Полное имя класса с пакетом.
     */
    @Property("fullName")
    private String fullName;

    /**
     * Имя пакета.
     */
    @Property("packageName")
    private String packageName;

    /**
     * Модификаторы доступа (public, private, static, etc.).
     */
    @Property("modifiers")
    @Builder.Default
    private List<String> modifiers = new ArrayList<>();

    /**
     * Метки Neo4j (Controller, Service, Repository и т.д.).
     */
    @Property("labels")
    @Builder.Default
    private List<String> labels = new ArrayList<>();

    /**
     * Связи с другими классами (DI зависимости).
     */
    @Relationship(type = "DEPENDS_ON", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<DependencyRelation> dependencies = new ArrayList<>();

    /**
     * Методы класса.
     */
    @Relationship(type = "HAS_METHOD", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<MethodNode> methods = new ArrayList<>();
}