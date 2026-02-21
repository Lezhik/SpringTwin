# AGENTS.md: Тестовые профили

Данный документ содержит правила и стандарты для создания и использования тестовых профилей в проекте SpringTwin.

---

## Назначение

Тестовые профили - это классы с фабричными методами для создания тестовых данных. Они обеспечивают:

- **Унификацию** - одинаковые тестовые данные между уровнями тестов
- **Быстроту** - простое создание тестовых сущностей
- **Централизацию** - управление тестовыми данными в одном месте
- **Реактивность** - поддержка реактивных типов Mono/Flux

---

## Структура

### Организация по модулям

```
src/test/java/twin/spring/profiles/
├── AGENTS.md                           # Этот файл
├── app/
│   └── AppTestProfile.java
├── project/
│   └── ProjectTestProfile.java
├── architecture/
│   ├── ClassNodeTestProfile.java
│   ├── MethodNodeTestProfile.java
│   ├── EndpointNodeTestProfile.java
│   └── FieldNodeTestProfile.java
├── analysis/
│   └── AnalysisTestProfile.java
├── report/
│   └── ReportTestProfile.java
├── migration/
│   └── MigrationTestProfile.java
└── mcp/
    └── McpTestProfile.java
```

---

## Принципы создания профилей

### Именование

| Класс | Описание |
|-------|----------|
| `<Module>TestProfile` | Профиль для модуля (например, `ProjectTestProfile`) |
| `<Entity>TestProfile` | Профиль для сущности (например, `ClassNodeTestProfile`) |

### Структура класса профиля

```java
package twin.spring.profiles.project;

import twin.spring.project.domain.Project;
import twin.spring.project.api.CreateProjectRequest;
import twin.spring.project.api.UpdateProjectRequest;
import twin.spring.project.api.ProjectResponse;

/**
 * Тестовый профиль для модуля Project.
 * Предоставляет фабричные методы для создания тестовых данных.
 */
public final class ProjectTestProfile {
    
    // Константы для повторного использования
    public static final String DEFAULT_PROJECT_ID = "test-project-id";
    public static final String DEFAULT_PROJECT_NAME = "Test Project";
    public static final String DEFAULT_PROJECT_PATH = "/path/to/project";
    
    // Приватный конструктор - утилитный класс
    private ProjectTestProfile() {
    }
    
    // Фабричные методы...
}
```

---

## Фабричные методы

### Типы методов

| Метод | Возвращаемый тип | Назначение |
|-------|------------------|------------|
| `createDefault<Entity>()` | Entity | Создание сущности с дефолтными значениями |
| `create<Entity>()` | Entity | Создание сущности с кастомными параметрами |
| `create<Entity>List()` | List<Entity> | Создание списка сущностей |
| `create<Entity>Request()` | Request | Создание DTO для запроса |
| `create<Entity>Response()` | Response | Создание DTO для ответа |
| `seed<Entity>()` | Mono/Flux | Заполнение БД тестовыми данными |

### Примеры методов

#### Создание сущности

```java
/**
 * Создает тестовый проект с базовыми данными.
 */
public static Project createDefaultProject() {
    return Project.builder()
        .id(DEFAULT_PROJECT_ID)
        .name(DEFAULT_PROJECT_NAME)
        .path(DEFAULT_PROJECT_PATH)
        .includePackages(List.of("com.example"))
        .excludePackages(List.of("com.example.config"))
        .build();
}

/**
 * Создает тестовый проект с кастомными данными.
 */
public static Project createProject(String name, String path) {
    return Project.builder()
        .id(UUID.randomUUID().toString())
        .name(name)
        .path(path)
        .includePackages(List.of("com.example"))
        .excludePackages(List.of())
        .build();
}

/**
 * Создает тестовый проект с кастомным ID.
 */
public static Project createProjectWithId(String id) {
    return Project.builder()
        .id(id)
        .name(DEFAULT_PROJECT_NAME)
        .path(DEFAULT_PROJECT_PATH)
        .includePackages(List.of("com.example"))
        .excludePackages(List.of())
        .build();
}
```

#### Создание списка

```java
/**
 * Создает список тестовых проектов.
 */
public static List<Project> createProjectList(int count) {
    return IntStream.range(0, count)
        .mapToObj(i -> createProject("Project " + i, "/path/" + i))
        .collect(Collectors.toList());
}
```

#### Создание DTO

```java
/**
 * Создает тестовый запрос на создание проекта.
 */
public static CreateProjectRequest createProjectRequest() {
    return CreateProjectRequest.builder()
        .name("New Project")
        .path("/path/to/new/project")
        .includePackages(List.of("com.newproject"))
        .excludePackages(List.of())
        .build();
}

/**
 * Создает тестовый запрос на обновление проекта.
 */
public static UpdateProjectRequest createUpdateProjectRequest() {
    return UpdateProjectRequest.builder()
        .name("Updated Project")
        .path("/updated/path")
        .includePackages(List.of("com.updated"))
        .excludePackages(List.of())
        .build();
}

/**
 * Создает тестовый ответ проекта.
 */
public static ProjectResponse createProjectResponse() {
    return ProjectResponse.builder()
        .id(DEFAULT_PROJECT_ID)
        .name(DEFAULT_PROJECT_NAME)
        .path(DEFAULT_PROJECT_PATH)
        .includePackages(List.of("com.example"))
        .excludePackages(List.of("com.example.config"))
        .build();
}
```

#### Заполнение БД (реактивные методы)

```java
/**
 * Заполняет БД тестовыми проектами.
 */
public static Flux<Project> seedProjects(ProjectRepository repository) {
    return repository.deleteAll()
        .thenMany(Flux.fromIterable(createProjectList(3)))
        .flatMap(repository::save);
}

/**
 * Заполняет БД одним тестовым проектом.
 */
public static Mono<Project> seedProject(ProjectRepository repository) {
    return repository.deleteAll()
        .then(repository.save(createDefaultProject()));
}
```

---

## Использование профилей

### В Unit тестах

```java
import twin.spring.profiles.project.ProjectTestProfile;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    
    @Test
    void findById_existingId_returnsProject() {
        // Arrange
        Project project = ProjectTestProfile.createDefaultProject();
        ProjectResponse expected = ProjectTestProfile.createProjectResponse();
        
        when(repository.findById(any())).thenReturn(Mono.just(project));
        when(mapper.toResponse(project)).thenReturn(expected);
        
        // Act & Assert
        StepVerifier.create(service.findById(ProjectTestProfile.DEFAULT_PROJECT_ID))
            .expectNext(expected)
            .verifyComplete();
    }
}
```

### В Integration тестах

```java
import twin.spring.profiles.project.ProjectTestProfile;

@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestNeo4jConfig.class)
class ProjectControllerIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @BeforeEach
    void setUp() {
        ProjectTestProfile.seedProjects(projectRepository).blockLast();
    }
    
    @Test
    void getAllProjects_returnsProjectList() {
        webTestClient.get()
            .uri("/api/v1/projects")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(ProjectResponse.class)
            .hasSize(3);
    }
}
```

### В E2E тестах

```java
import twin.spring.profiles.project.ProjectTestProfile;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestNeo4jConfig.class)
class ProjectManagementE2ETest {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @BeforeEach
    void setUp() {
        ProjectTestProfile.seedProjects(projectRepository).blockLast();
        // ... Playwright setup
    }
}
```

---

## Профили для графа

### ClassNode профиль

```java
package twin.spring.profiles.architecture;

import twin.spring.architecture.domain.ClassNode;
import java.util.List;

/**
 * Тестовый профиль для ClassNode.
 */
public final class ClassNodeTestProfile {
    
    public static final String DEFAULT_CLASS_ID = "test-class-id";
    public static final String DEFAULT_CLASS_NAME = "TestClass";
    public static final String DEFAULT_FULL_NAME = "com.example.TestClass";
    public static final String DEFAULT_PACKAGE_NAME = "com.example";
    
    private ClassNodeTestProfile() {
    }
    
    public static ClassNode createDefaultClassNode() {
        return ClassNode.builder()
            .id(DEFAULT_CLASS_ID)
            .name(DEFAULT_CLASS_NAME)
            .fullName(DEFAULT_FULL_NAME)
            .packageName(DEFAULT_PACKAGE_NAME)
            .labels(List.of("Service"))
            .build();
    }
    
    public static ClassNode createClassNode(String name, String packageName) {
        return ClassNode.builder()
            .id(UUID.randomUUID().toString())
            .name(name)
            .fullName(packageName + "." + name)
            .packageName(packageName)
            .labels(List.of("Service"))
            .build();
    }
    
    public static ClassNode createControllerClassNode() {
        return ClassNode.builder()
            .id(UUID.randomUUID().toString())
            .name("TestController")
            .fullName("com.example.controller.TestController")
            .packageName("com.example.controller")
            .labels(List.of("RestController", "Controller"))
            .build();
    }
    
    public static ClassNode createServiceClassNode() {
        return ClassNode.builder()
            .id(UUID.randomUUID().toString())
            .name("TestService")
            .fullName("com.example.service.TestService")
            .packageName("com.example.service")
            .labels(List.of("Service"))
            .build();
    }
    
    public static ClassNode createRepositoryClassNode() {
        return ClassNode.builder()
            .id(UUID.randomUUID().toString())
            .name("TestRepository")
            .fullName("com.example.repository.TestRepository")
            .packageName("com.example.repository")
            .labels(List.of("Repository"))
            .build();
    }
}
```

### MethodNode профиль

```java
package twin.spring.profiles.architecture;

import twin.spring.architecture.domain.MethodNode;
import java.util.List;

/**
 * Тестовый профиль для MethodNode.
 */
public final class MethodNodeTestProfile {
    
    public static final String DEFAULT_METHOD_ID = "test-method-id";
    public static final String DEFAULT_METHOD_NAME = "testMethod";
    public static final String DEFAULT_SIGNATURE = "testMethod(String):String";
    public static final String DEFAULT_RETURN_TYPE = "String";
    
    private MethodNodeTestProfile() {
    }
    
    public static MethodNode createDefaultMethodNode() {
        return MethodNode.builder()
            .id(DEFAULT_METHOD_ID)
            .name(DEFAULT_METHOD_NAME)
            .signature(DEFAULT_SIGNATURE)
            .returnType(DEFAULT_RETURN_TYPE)
            .modifiers(List.of("public"))
            .build();
    }
    
    public static MethodNode createMethodNode(String name, String returnType) {
        return MethodNode.builder()
            .id(UUID.randomUUID().toString())
            .name(name)
            .signature(name + "(): " + returnType)
            .returnType(returnType)
            .modifiers(List.of("public"))
            .build();
    }
}
```

---

## Best Practices

### 1. Использовать константы

```java
// Хорошо - используем константы
public static final String DEFAULT_PROJECT_ID = "test-project-id";

public static Project createDefaultProject() {
    return Project.builder()
        .id(DEFAULT_PROJECT_ID)
        .name(DEFAULT_PROJECT_NAME)
        .build();
}

// Плохо - хардкод в методах
public static Project createDefaultProject() {
    return Project.builder()
        .id("test-project-id")
        .name("Test Project")
        .build();
}
```

### 2. Создавать вариативные методы

```java
// Дефолтный
public static Project createDefaultProject() { ... }

// С кастомными параметрами
public static Project createProject(String name, String path) { ... }

// С кастомным ID
public static Project createProjectWithId(String id) { ... }

// С предустановленными типами
public static Project createMavenProject() { ... }
public static Project createGradleProject() { ... }
```

### 3. Поддерживать реактивность

```java
// Для заполнения БД
public static Flux<Project> seedProjects(ProjectRepository repository) {
    return repository.deleteAll()
        .thenMany(Flux.fromIterable(createProjectList(3)))
        .flatMap(repository::save);
}
```

### 4. Документировать методы

```java
/**
 * Создает тестовый проект с базовыми данными.
 * Используется для большинства unit тестов.
 * 
 * @return проект с дефолтными значениями
 */
public static Project createDefaultProject() { ... }
```

---

## Связанные документы

- [`src/test/java/AGENTS.md`](../../AGENTS.md) - Общие правила тестирования
- [`src/test/java/twin/spring/unit/AGENTS.md`](../unit/AGENTS.md) - Unit тесты
- [`src/test/java/twin/spring/integration/AGENTS.md`](../integration/AGENTS.md) - Integration тесты
- [`src/test/java/twin/spring/e2e/AGENTS.md`](../e2e/AGENTS.md) - E2E тесты