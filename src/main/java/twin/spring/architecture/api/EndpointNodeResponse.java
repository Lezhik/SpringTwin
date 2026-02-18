package twin.spring.architecture.api;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO ответа для узла EndpointNode.
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointNodeResponse {

    /**
     * Уникальный идентификатор узла.
     */
    private String id;

    /**
     * URL путь endpoint.
     */
    private String path;

    /**
     * HTTP метод: GET, POST, PUT, DELETE, PATCH.
     */
    private String httpMethod;

    /**
     * Content-Type ответа.
     */
    private String produces;

    /**
     * Content-Type запроса.
     */
    private String consumes;

    /**
     * ID метода-обработчика.
     */
    private String methodId;

    /**
     * Дата создания узла.
     */
    private LocalDateTime createdAt;
}