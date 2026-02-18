package twin.spring.architecture.api;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO ответа для узла MethodNode.
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodNodeResponse {

    /**
     * Уникальный идентификатор узла.
     */
    private String id;

    /**
     * Имя метода.
     */
    private String name;

    /**
     * Полная сигнатура метода.
     */
    private String signature;

    /**
     * Возвращаемый тип.
     */
    private String returnType;

    /**
     * Модификаторы: public, private, static, etc.
     */
    private Set<String> modifiers;

    /**
     * Параметры метода.
     */
    private String parameters;

    /**
     * ID родительского класса.
     */
    private String classId;

    /**
     * Дата создания узла.
     */
    private LocalDateTime createdAt;
}