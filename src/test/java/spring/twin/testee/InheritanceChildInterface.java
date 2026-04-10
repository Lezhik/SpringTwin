package spring.twin.testee;

import java.io.Serializable;

/**
 * Интерфейс, расширяющий другой интерфейс (Serializable).
 * Используется для тестирования InheritanceExtractor.
 */
public interface InheritanceChildInterface extends Serializable {
    // Интерфейс расширяет Serializable
}