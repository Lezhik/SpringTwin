package twin.spring.architecture.service.mapper;

import twin.spring.architecture.api.ClassNodeRequest;
import twin.spring.architecture.api.ClassNodeResponse;
import twin.spring.architecture.domain.ClassNode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Маппер для преобразования между ClassNode и DTO.
 *
 * @since 1.0.0
 */
@Component
public class ClassNodeMapper {

    /**
     * Преобразовать ClassNodeRequest в ClassNode.
     *
     * @param request DTO запроса
     * @return доменная модель
     */
    public ClassNode toEntity(ClassNodeRequest request) {
        if (request == null) {
            return null;
        }
        return ClassNode.builder()
                .name(request.getName())
                .fullName(request.getFullName())
                .packageName(request.getPackageName())
                .labels(request.getLabels() != null ? new ArrayList<>(request.getLabels()) : null)
                .modifiers(request.getModifiers() != null ? new ArrayList<>(request.getModifiers()) : null)
                .build();
    }

    /**
     * Преобразовать ClassNode в ClassNodeResponse.
     *
     * @param entity доменная модель
     * @return DTO ответа
     */
    public ClassNodeResponse toResponse(ClassNode entity) {
        if (entity == null) {
            return null;
        }
        return ClassNodeResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .fullName(entity.getFullName())
                .packageName(entity.getPackageName())
                .labels(entity.getLabels() != null ? java.util.Set.copyOf(entity.getLabels()) : null)
                .modifiers(entity.getModifiers() != null ? java.util.Set.copyOf(entity.getModifiers()) : null)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Обновить ClassNode из ClassNodeRequest.
     *
     * @param entity доменная модель для обновления
     * @param request DTO запроса
     */
    public void updateEntity(ClassNode entity, ClassNodeRequest request) {
        if (request == null || entity == null) {
            return;
        }
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getFullName() != null) {
            entity.setFullName(request.getFullName());
        }
        if (request.getPackageName() != null) {
            entity.setPackageName(request.getPackageName());
        }
        if (request.getLabels() != null) {
            entity.setLabels(new ArrayList<>(request.getLabels()));
        }
        if (request.getModifiers() != null) {
            entity.setModifiers(new ArrayList<>(request.getModifiers()));
        }
    }
}