package twin.spring.architecture.api;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO ответа для узла ClassNode.
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassNodeResponse {

    /**
     * Уникальный идентификатор узла.
     */
    private String id;

    /**
     * Простое имя класса.
     */
    private String name;

    /**
     * Полное имя класса с пакетом.
     */
    private String fullName;

    /**
     * Имя пакета.
     */
    private String packageName;

    /**
     * Метки: Controller, Service, Repository, Component, Configuration.
     */
    private Set<String> labels;

    /**
     * Модификаторы доступа: public, private, static, etc.
     */
    private Set<String> modifiers;

    /**
     * Дата создания узла.
     */
    private LocalDateTime createdAt;
}