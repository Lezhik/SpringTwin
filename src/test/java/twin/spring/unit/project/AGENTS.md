# AGENTS.md: Unit тестирование модуля Project (Backend)

Правила и структура unit тестирования для модуля project.

---

## Структура тестов

```
src/test/java/twin/spring/unit/project/
├── service/
│   ├── ProjectServiceTest.java
│   └── PackageConfigServiceTest.java
└── mapper/
    └── ProjectMapperTest.java
```

---

## Тестовые профили

### Использование ProjectTestProfile

```java
import twin.spring.profiles.project.ProjectTestProfile;

@Test
void findById_existingId_returnsProject() {
    // Arrange - используем профиль для создания тестовых данных
    Project project = ProjectTestProfile.createProject();
    ProjectResponse response = ProjectTestProfile.createProjectResponse();
    
    // ...
}
```

Профиль находится в `src/test/java/twin/spring/profiles/project/ProjectTestProfile.java`

---

## Unit тесты

### ProjectServiceTest.java

```java
/**
 * Unit тесты для ProjectService.
 * Тестирует бизнес-логику в изоляции с использованием Mockito.
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    
    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private ProjectMapper projectMapper;
    
    @InjectMocks
    private ProjectService projectService;
    
    @Test
    void findById_existingId_returnsProject() {
        // Arrange
        Project project = ProjectTestProfile.createProject();
        ProjectResponse expected = ProjectResponse.builder()
            .id(project.getId())
            .name(project.getName())
            .path(project.getPath())
            .build();
            
        when(projectRepository.findById(project.getId()))
            .thenReturn(Mono.just(project));
        when(projectMapper.toResponse(project))
            .thenReturn(expected);
        
        // Act & Assert
        StepVerifier.create(projectService.findById(project.getId()))
            .expectNext(expected)
            .verifyComplete();
    }
    
    @Test
    void findById_nonExistingId_returnsEmpty() {
        // Arrange
        String id = UUID.randomUUID().toString();
        when(projectRepository.findById(id)).thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(projectService.findById(id))
            .verifyComplete();
    }
    
    @Test
    void create_validRequest_returnsCreatedProject() {
        // Arrange
        CreateProjectRequest request = ProjectTestProfile.createProjectRequest();
        Project project = ProjectTestProfile.createProject("New Project");
        ProjectResponse expected = ProjectResponse.builder()
            .id(project.getId())
            .name(project.getName())
            .path(project.getPath())
            .build();
            
        when(projectMapper.toEntity(request)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(Mono.just(project));
        when(projectMapper.toResponse(project)).thenReturn(expected);
        
        // Act & Assert
        StepVerifier.create(projectService.create(request))
            .expectNext(expected)
            .verifyComplete();
    }
    
    @Test
    void update_existingProject_returnsUpdatedProject() {
        // Arrange
        Project existingProject = ProjectTestProfile.createProject();
        UpdateProjectRequest request = ProjectTestProfile.updateProjectRequest();
        Project updatedProject = Project.builder()
            .id(existingProject.getId())
            .name(request.getName())
            .path(existingProject.getPath())
            .includePackages(request.getIncludePackages())
            .excludePackages(request.getExcludePackages())
            .build();
        ProjectResponse expected = ProjectResponse.builder()
            .id(updatedProject.getId())
            .name(updatedProject.getName())
            .build();
            
        when(projectRepository.findById(existingProject.getId()))
            .thenReturn(Mono.just(existingProject));
        when(projectRepository.save(updatedProject))
            .thenReturn(Mono.just(updatedProject));
        when(projectMapper.toResponse(updatedProject)).thenReturn(expected);
        
        // Act & Assert
        StepVerifier.create(projectService.update(existingProject.getId(), request))
            .expectNext(expected)
            .verifyComplete();
    }
    
    @Test
    void delete_existingProject_deletesSuccessfully() {
        // Arrange
        Project project = ProjectTestProfile.createProject();
        when(projectRepository.findById(project.getId()))
            .thenReturn(Mono.just(project));
        when(projectRepository.delete(project))
            .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(projectService.delete(project.getId()))
            .verifyComplete();
    }
    
    @Test
    void findAll_shouldReturnAllProjects() {
        // Arrange
        List<Project> projects = ProjectTestProfile.createProjectList();
        when(projectRepository.findAll())
            .thenReturn(Flux.fromIterable(projects));
        
        // Act & Assert
        StepVerifier.create(projectService.findAll())
            .expectNextCount(3)
            .verifyComplete();
    }
}
```

### ProjectMapperTest.java

```java
/**
 * Unit тесты для ProjectMapper.
 * Тестирует преобразование между DTO и Entity.
 */
class ProjectMapperTest {
    
    private ProjectMapper mapper = new ProjectMapper();
    
    @Test
    void toEntity_validRequest_returnsEntity() {
        // Arrange
        CreateProjectRequest request = ProjectTestProfile.createProjectRequest();
        
        // Act
        Project result = mapper.toEntity(request);
        
        // Assert
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(request.getName());
        assertThat(result.getPath()).isEqualTo(request.getPath());
    }
    
    @Test
    void toResponse_validEntity_returnsResponse() {
        // Arrange
        Project project = ProjectTestProfile.createProject();
        
        // Act
        ProjectResponse result = mapper.toResponse(project);
        
        // Assert
        assertThat(result.getId()).isEqualTo(project.getId());
        assertThat(result.getName()).isEqualTo(project.getName());
        assertThat(result.getPath()).isEqualTo(project.getPath());
    }
    
    @Test
    void updateEntity_validRequest_updatesEntity() {
        // Arrange
        Project project = ProjectTestProfile.createProject();
        UpdateProjectRequest request = ProjectTestProfile.updateProjectRequest();
        
        // Act
        mapper.updateEntity(project, request);
        
        // Assert
        assertThat(project.getName()).isEqualTo(request.getName());
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| Сервисы | Все публичные методы покрыты |
| Мапперы | Все методы преобразования покрыты |
| Изоляция | Зависимости замоканы |
| Профили | Тестовые данные вынесены |

---

## Запуск тестов

```bash
# Unit тесты модуля project
gradlew.bat test --tests "twin.spring.unit.project.*"