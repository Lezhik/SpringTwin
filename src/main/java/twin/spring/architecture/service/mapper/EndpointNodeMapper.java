package twin.spring.architecture.service.mapper;

import twin.spring.architecture.api.EndpointNodeRequest;
import twin.spring.architecture.api.EndpointNodeResponse;
import twin.spring.architecture.domain.EndpointNode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Маппер для преобразования между EndpointNode и DTO.
 *
 * @since 1.0.0
 */
@Component
public class EndpointNodeMapper {

    /**
     * Преобразовать EndpointNodeRequest в EndpointNode.
     *
     * @param request DTO запроса
     * @return доменная модель
     */
    public EndpointNode toEntity(EndpointNodeRequest request) {
        if (request == null) {
            return null;
        }
        return EndpointNode.builder()
                .path(request.getPath())
                .httpMethod(request.getHttpMethod())
                .produces(request.getProduces())
                .consumes(request.getConsumes())
                .build();
    }

    /**
     * Преобразовать EndpointNode в EndpointNodeResponse.
     *
     * @param entity доменная модель
     * @return DTO ответа
     */
    public EndpointNodeResponse toResponse(EndpointNode entity) {
        if (entity == null) {
            return null;
        }
        return EndpointNodeResponse.builder()
                .id(entity.getId())
                .path(entity.getPath())
                .httpMethod(entity.getHttpMethod())
                .produces(entity.getProduces())
                .consumes(entity.getConsumes())
                .methodId(entity.getExposingMethod() != null ? entity.getExposingMethod().getId() : null)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Обновить EndpointNode из EndpointNodeRequest.
     *
     * @param entity доменная модель для обновления
     * @param request DTO запроса
     */
    public void updateEntity(EndpointNode entity, EndpointNodeRequest request) {
        if (request == null || entity == null) {
            return;
        }
        if (request.getPath() != null) {
            entity.setPath(request.getPath());
        }
        if (request.getHttpMethod() != null) {
            entity.setHttpMethod(request.getHttpMethod());
        }
        if (request.getProduces() != null) {
            entity.setProduces(request.getProduces());
        }
        if (request.getConsumes() != null) {
            entity.setConsumes(request.getConsumes());
        }
    }
}