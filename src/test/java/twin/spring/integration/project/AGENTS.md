# AGENTS.md: Integration тестирование модуля Project

Правила и структура integration тестирования для модуля project.

---

## Структура тестов

```
src/test/java/twin/spring/integration/project/
├── api/
│   └── ProjectControllerIntegrationTest.java
└── repository/
    └── ProjectRepositoryIntegrationTest.java
```

---

## Тестовые профили

### Использование ProjectTestProfile

```java
import twin.spring.profiles.project.ProjectTestProfile;

@BeforeEach
void setUp() {
    projectTestProfile.seedTestData();
}
```

Профиль находится в `src/test/java/twin/spring/profiles/project/ProjectTestProfile.java`

---

## Integration тесты

### ProjectControllerIntegrationTest.java

```java
/**
 * Integration тесты для ProjectController.
 * Тестирует REST API с использованием WebTestClient и Neo4j embedded.
 */
@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestNeo4jConfig.class)
class ProjectControllerIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private ProjectTestProfile projectTestProfile;
    
    @BeforeEach
    void setUp() {
        projectTestProfile.seedTestData();
    }
    
    @Test
    void createProject_shouldReturnCreatedProject() {
        CreateProjectRequest request = ProjectTestProfile.createProjectRequest();
        
        webTestClient.post()
            .uri("/api/v1/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(ProjectResponse.class)
            .value(response -> {
                assertThat(response.getId()).isNotNull();
                assertThat(response.getName()).isEqualTo(request.getName());
                assertThat(response.getPath()).isEqualTo(request.getPath());
            });
    }
    
    @Test
    void getProjectById_shouldReturnProject() {
        Project project = ProjectTestProfile.createProject();
        
        webTestClient.get()
            .uri("/api/v1/projects/{id}", project.getId())
            .exchange()
            .expectStatus().isOk()
            .expectBody(ProjectResponse.class)
            .value(response -> {
                assertThat(response.getId()).isEqualTo(project.getId());
                assertThat(response.getName()).isEqualTo(project.getName());
            });
    }
    
    @Test
    void getProjectById_shouldReturn404WhenNotFound() {
        webTestClient.get()
            .uri("/api/v1/projects/{id}", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isNotFound();
    }
    
    @Test
    void getAllProjects_shouldReturnProjectList() {
        webTestClient.get()
            .uri("/api/v1/projects")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(ProjectResponse.class)
            .hasSize(3);
    }
    
    @Test
    void updateProject_shouldReturnUpdatedProject() {
        Project project = ProjectTestProfile.createProject();
        UpdateProjectRequest request = ProjectTestProfile.updateProjectRequest();
        
        webTestClient.put()
            .uri("/api/v1/projects/{id}", project.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectBody(ProjectResponse.class)
            .value(response -> {
                assertThat(response.getName()).isEqualTo(request.getName());
            });
    }
    
    @Test
    void deleteProject_shouldReturnNoContent() {
        Project project = ProjectTestProfile.createProject();
        
        webTestClient.delete()
            .uri("/api/v1/projects/{id}", project.getId())
            .exchange()
            .expectStatus().isNoContent();
    }
    
    @Test
    void updatePackageConfig_shouldUpdateConfig() {
        Project project = ProjectTestProfile.createProject();
        PackageConfig config = ProjectTestProfile.createPackageConfig();
        
        webTestClient.patch()
            .uri("/api/v1/projects/{id}/config", project.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(config)
            .exchange()
            .expectStatus().isOk()
            .expectBody(ProjectResponse.class)
            .value(response -> {
                assertThat(response.getIncludePackages()).isEqualTo(config.getIncludePackages());
                assertThat(response.getExcludePackages()).isEqualTo(config.getExcludePackages());
            });
    }
}
```

### ProjectRepositoryIntegrationTest.java

```java
/**
 * Integration тесты для ProjectRepository.
 * Тестирует запросы к Neo4j с использованием embedded базы.
 */
@SpringBootTest
@Import(TestNeo4jConfig.class)
class ProjectRepositoryIntegrationTest {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @BeforeEach
    void setUp() {
        projectRepository.deleteAll().block();
    }
    
    @Test
    void save_andFindById_returnsProject() {
        // Arrange
        Project project = ProjectTestProfile.createProject();
        
        // Act
        Project saved = projectRepository.save(project).block();
        Project found = projectRepository.findById(saved.getId()).block();
        
        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(project.getName());
    }
    
    @Test
    void findByPackageName_returnsMatchingProjects() {
        // Arrange
        Project project = ProjectTestProfile.createProject();
        projectRepository.save(project).block();
        
        // Act & Assert
        StepVerifier.create(projectRepository.findByPackageName("com.example"))
            .expectNextCount(1)
            .verifyComplete();
    }
    
    @Test
    void findAll_returnsAllProjects() {
        // Arrange
        List<Project> projects = ProjectTestProfile.createProjectList();
        projects.forEach(p -> projectRepository.save(p).block());
        
        // Act & Assert
        StepVerifier.create(projectRepository.findAll())
            .expectNextCount(3)
            .verifyComplete();
    }
    
    @Test
    void delete_shouldRemoveProject() {
        // Arrange
        Project project = ProjectTestProfile.createProject();
        Project saved = projectRepository.save(project).block();
        
        // Act
        projectRepository.delete(saved).block();
        
        // Assert
        StepVerifier.create(projectRepository.findById(saved.getId()))
            .verifyComplete();
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| REST API | Все методы контроллера покрыты |
| Репозитории | Критические запросы проверены |
| Статусы | Все HTTP статусы проверены |
| Профили | Тестовые данные вынесены |

---

## Запуск тестов

```bash
# Integration тесты модуля project
gradlew.bat test --tests "twin.spring.integration.project.*"