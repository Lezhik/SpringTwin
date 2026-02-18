package twin.spring.architecture.api;

import lombok.*;

import java.util.Set;

/**
 * DTO запроса для создания или обновления узла ClassNode.
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassNodeRequest {

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
}