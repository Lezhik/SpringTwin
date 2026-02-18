package twin.spring.architecture.api;

import lombok.*;

/**
 * DTO запроса для создания или обновления узла EndpointNode.
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointNodeRequest {

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
}