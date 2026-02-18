package twin.spring.architecture.api;

import lombok.*;

import java.util.Set;

/**
 * DTO запроса для создания или обновления узла MethodNode.
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodNodeRequest {

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
     * Параметры метода в виде строки.
     */
    private String parameters;

    /**
     * ID родительского класса.
     */
    private String classId;
}