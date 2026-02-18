package twin.spring.architecture.service.mapper;

import twin.spring.architecture.api.MethodNodeRequest;
import twin.spring.architecture.api.MethodNodeResponse;
import twin.spring.architecture.domain.MethodNode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Маппер для преобразования между MethodNode и DTO.
 *
 * @since 1.0.0
 */
@Component
public class MethodNodeMapper {

    /**
     * Преобразовать MethodNodeRequest в MethodNode.
     *
     * @param request DTO запроса
     * @return доменная модель
     */
    public MethodNode toEntity(MethodNodeRequest request) {
        if (request == null) {
            return null;
        }
        return MethodNode.builder()
                .name(request.getName())
                .signature(request.getSignature())
                .returnType(request.getReturnType())
                .modifiers(request.getModifiers() != null ? new ArrayList<>(request.getModifiers()) : null)
                .parameters(request.getParameters())
                .build();
    }

    /**
     * Преобразовать MethodNode в MethodNodeResponse.
     *
     * @param entity доменная модель
     * @return DTO ответа
     */
    public MethodNodeResponse toResponse(MethodNode entity) {
        if (entity == null) {
            return null;
        }
        return MethodNodeResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .signature(entity.getSignature())
                .returnType(entity.getReturnType())
                .modifiers(entity.getModifiers() != null ? java.util.Set.copyOf(entity.getModifiers()) : null)
                .parameters(entity.getParameters())
                .classId(entity.getParentClass() != null ? entity.getParentClass().getId() : null)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Обновить MethodNode из MethodNodeRequest.
     *
     * @param entity доменная модель для обновления
     * @param request DTO запроса
     */
    public void updateEntity(MethodNode entity, MethodNodeRequest request) {
        if (request == null || entity == null) {
            return;
        }
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getSignature() != null) {
            entity.setSignature(request.getSignature());
        }
        if (request.getReturnType() != null) {
            entity.setReturnType(request.getReturnType());
        }
        if (request.getModifiers() != null) {
            entity.setModifiers(new ArrayList<>(request.getModifiers()));
        }
        if (request.getParameters() != null) {
            entity.setParameters(request.getParameters());
        }
    }
}