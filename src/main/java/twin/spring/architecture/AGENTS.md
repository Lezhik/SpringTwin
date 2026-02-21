# AGENTS.md: Модуль Architecture (Backend)

Центральный модуль, содержащий модели графа архитектуры проекта. Определяет узлы (ClassNode, MethodNode, EndpointNode, FieldNode) и связи между ними.

---

## Ответственность

- Определение доменных моделей графа
- Репозитории для работы с Neo4j
- Связи между архитектурными сущностями
- Метки (Labels) для классификации узлов

---

## Структура модуля

```
src/main/java/twin/spring/architecture/
├── api/
│   ├── ClassController.java            # REST контроллер для классов
│   ├── MethodController.java           # REST контроллер для методов
│   ├── EndpointController.java         # REST контроллер для endpoints
│   ├── ClassResponse.java              # DTO ответа для класса
│   ├── MethodResponse.java             # DTO ответа для метода
│   └── EndpointResponse.java           # DTO ответа для endpoint
├── domain/
│   ├── ClassNode.java                  # Узел: Java класс
│   ├── MethodNode.java                 # Узел: Метод класса
│   ├── EndpointNode.java               # Узел: REST endpoint
│   ├── FieldNode.java                  # Узел: Поле класса
│   └── relation/
│       ├── DependsOn.java              # Связь: DI зависимость
│       ├── Calls.java                  # Связь: Вызов метода
│       ├── Instantiates.java           # Связь: Создание экземпляра
│       ├── AccessesField.java          # Связь: Доступ к полю
│       └── ExposesEndpoint.java        # Связь: REST endpoint
├── service/
│   ├── ClassService.java               # Сервис для работы с классами
│   ├── MethodService.java              # Сервис для работы с методами
│   ├── EndpointService.java            # Сервис для работы с endpoints
│   ├── GraphQueryService.java          # Сервис для сложных запросов
│   └── mapper/
│       ├── ClassMapper.java            # Маппер для Class
│       ├── MethodMapper.java           # Маппер для Method
│       └── EndpointMapper.java         # Маппер для Endpoint
└── repository/
    ├── ClassNodeRepository.java        # Репозиторий для классов
    ├── MethodNodeRepository.java       # Репозиторий для методов
    └── EndpointNodeRepository.java     # Репозиторий для endpoints
```

---

## Доменные модели

### ClassNode - Java класс

```java
/**
 * Java класс в анализируемом проекте.
 *
 * <p>Узел Neo4j с динамическими метками на основе Spring аннотаций.</p>
 */
@Node
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassNode {
    
    @Id
    @GeneratedValue
    private String id;
    
    /** Простое имя класса */
    private String name;
    
    /** Полное имя класса с пакетом */
    private String fullName;
    
    /** Имя пакета */
    private String packageName;
    
    /** Модификаторы доступа */
    private List<String> modifiers;
    
    /** Метки Neo4j (Controller, Service, Repository и т.д.) */
    private List<String> labels;
    
    /** Связи с другими классами */
    @Relationship(type = "DEPENDS_ON")
    private List<DependsOn> dependencies;
    
    /** Методы класса */
    @Relationship(type = "HAS_METHOD", direction = Relationship.Direction.OUTGOING)
    private List<MethodNode> methods;
}
```

### MethodNode - Метод класса

```java
/**
 * Метод Java класса.
 */
@Node
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodNode {
    
    @Id
    @GeneratedValue
    private String id;
    
    /** Имя метода */
    private String name;
    
    /** Сигнатура метода */
    private String signature;
    
    /** Возвращаемый тип */
    private String returnType;
    
    /** Модификаторы */
    private List<String> modifiers;
    
    /** Параметры метода */
    private List<MethodParameter> parameters;
    
    /** Родительский класс */
    @Relationship(type = "HAS_METHOD", direction = Relationship.Direction.INCOMING)
    private ClassNode parentClass;
}
```

### EndpointNode - REST endpoint

```java
/**
 * REST endpoint, экспонируемый методом контроллера.
 */
@Node
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointNode {
    
    @Id
    @GeneratedValue
    private String id;
    
    /** HTTP путь */
    private String path;
    
    /** HTTP метод (GET, POST, PUT, DELETE) */
    private String httpMethod;
    
    /** Content-Type для ответа */
    private String produces;
    
    /** Content-Type для запроса */
    private String consumes;
    
    /** Метод, экспонирующий endpoint */
    @Relationship(type = "EXPOSES_ENDPOINT", direction = Relationship.Direction.INCOMING)
    private MethodNode exposingMethod;
}
```

---

## Связи (Relationships)

### DependsOn - DI зависимость

```java
/**
 * Связь зависимости между классами через DI.
 */
@RelationshipProperties
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DependsOn {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @TargetNode
    private ClassNode targetClass;
    
    /** Имя поля для инъекции */
    private String fieldName;
    
    /** Тип инъекции (constructor, setter, field) */
    private String injectionType;
}
```

### Calls - Вызов метода

```java
/**
 * Связь вызова одного метода другим.
 */
@RelationshipProperties
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Calls {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @TargetNode
    private MethodNode targetMethod;
    
    /** Позиция в исходном коде */
    private Integer lineNumber;
}
```

### ExposesEndpoint - REST endpoint

```java
/**
 * Связь метода с экспонируемым REST endpoint.
 */
@RelationshipProperties
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExposesEndpoint {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @TargetNode
    private EndpointNode endpoint;
}
```

---

## Метки (Labels)

### Формирование меток

Метки формируются на основе Spring аннотаций:

| Аннотация | Метка Neo4j |
|-----------|-------------|
| @Controller | Controller |
| @RestController | RestController |
| @Service | Service |
| @Repository | Repository |
| @Component | Component |
| @Configuration | Configuration |
| @Bean | Bean |

### Использование меток

```java
/**
 * Сервис для работы с метками классов.
 */
@Service
public class ClassLabelService {
    
    private static final Map<String, String> ANNOTATION_TO_LABEL = Map.of(
        "Controller", "Controller",
        "RestController", "RestController",
        "Service", "Service",
        "Repository", "Repository",
        "Component", "Component",
        "Configuration", "Configuration"
    );
    
    public List<String> resolveLabels(List<String> annotations) {
        return annotations.stream()
            .map(ANNOTATION_TO_LABEL::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
```

---

## Репозитории

### ClassNodeRepository

```java
/**
 * Репозиторий для работы с классами в Neo4j.
 */
@Repository
public interface ClassNodeRepository extends ReactiveNeo4jRepository<ClassNode, String> {
    
    /**
     * Найти класс по полному имени.
     */
    Mono<ClassNode> findByFullName(String fullName);
    
    /**
     * Найти все классы в пакете.
     */
    Flux<ClassNode> findByPackageNameStartingWith(String packageName);
    
    /**
     * Найти классы по метке.
     */
    Flux<ClassNode> findByLabelsContaining(String label);
    
    /**
     * Найти все контроллеры.
     */
    @Query("MATCH (c:ClassNode:Controller) RETURN c")
    Flux<ClassNode> findAllControllers();
    
    /**
     * Найти все сервисы.
     */
    @Query("MATCH (c:ClassNode:Service) RETURN c")
    Flux<ClassNode> findAllServices();
    
    /**
     * Найти зависимости класса.
     */
    @Query("MATCH (c:ClassNode {id: $id})-[:DEPENDS_ON]->(dep:ClassNode) RETURN dep")
    Flux<ClassNode> findDependencies(String id);
    
    /**
     * Найти классы, зависящие от данного.
     */
    @Query("MATCH (c:ClassNode)-[:DEPENDS_ON]->(target:ClassNode {id: $id}) RETURN c")
    Flux<ClassNode> findDependents(String id);
}
```

### MethodNodeRepository

```java
/**
 * Репозиторий для работы с методами.
 */
@Repository
public interface MethodNodeRepository extends ReactiveNeo4jRepository<MethodNode, String> {
    
    /**
     * Найти методы класса.
     */
    Flux<MethodNode> findByParentClassId(String classId);
    
    /**
     * Найти метод по имени и классу.
     */
    @Query("MATCH (c:ClassNode {id: $classId})-[:HAS_METHOD]->(m:MethodNode {name: $name}) RETURN m")
    Mono<MethodNode> findByClassIdAndName(String classId, String name);
    
    /**
     * Найти вызовы метода.
     */
    @Query("MATCH (m:MethodNode {id: $id})-[:CALLS]->(called:MethodNode) RETURN called")
    Flux<MethodNode> findCalledMethods(String id);
    
    /**
     * Найти вызывающие методы.
     */
    @Query("MATCH (caller:MethodNode)-[:CALLS]->(m:MethodNode {id: $id}) RETURN caller")
    Flux<MethodNode> findCallingMethods(String id);
}
```

### EndpointNodeRepository

```java
/**
 * Репозиторий для работы с endpoints.
 */
@Repository
public interface EndpointNodeRepository extends ReactiveNeo4jRepository<EndpointNode, String> {
    
    /**
     * Найти endpoint по пути и методу.
     */
    Mono<EndpointNode> findByPathAndHttpMethod(String path, String httpMethod);
    
    /**
     * Найти все endpoints с HTTP методом.
     */
    Flux<EndpointNode> findByHttpMethod(String httpMethod);
    
    /**
     * Найти endpoint по экспонирующему методу.
     */
    @Query("MATCH (m:MethodNode {id: $methodId})-[:EXPOSES_ENDPOINT]->(e:EndpointNode) RETURN e")
    Mono<EndpointNode> findByExposingMethodId(String methodId);
}
```

---

## API

### REST Endpoints

| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/api/v1/classes` | Получить все классы |
| GET | `/api/v1/classes/{id}` | Получить класс по ID |
| GET | `/api/v1/classes/{id}/dependencies` | Получить зависимости класса |
| GET | `/api/v1/classes/{id}/methods` | Получить методы класса |
| GET | `/api/v1/methods/{id}` | Получить метод по ID |
| GET | `/api/v1/methods/{id}/calls` | Получить вызовы метода |
| GET | `/api/v1/endpoints` | Получить все endpoints |
| GET | `/api/v1/endpoints/{id}` | Получить endpoint по ID |

---

## Зависимости

```mermaid
graph TD
    architecture --> app
    
    subgraph architecture
        api --> service
        service --> domain
        service --> repository
    end
    
    project -.-> architecture
    analysis -.-> architecture
    report -.-> architecture
    mcp -.-> architecture
```

### Используется

- **project** - для связей проекта с классами
- **analysis** - для сохранения результатов анализа
- **report** - для формирования explain-отчетов
- **mcp** - для предоставления контекста через MCP
- **migration** - для создания индексов и ограничений

---

## Тестирование

### Тестовые профили

```java
public class ArchitectureTestProfile {
    
    public static ClassNode createDefaultClassNode() {
        return ClassNode.builder()
            .id("test-class-id")
            .name("TestService")
            .fullName("com.example.TestService")
            .packageName("com.example")
            .labels(List.of("Service"))
            .modifiers(List.of("public"))
            .build();
    }
    
    public static MethodNode createDefaultMethodNode() {
        return MethodNode.builder()
            .id("test-method-id")
            .name("doSomething")
            .signature("public void doSomething()")
            .returnType("void")
            .modifiers(List.of("public"))
            .build();
    }
    
    public static EndpointNode createDefaultEndpointNode() {
        return EndpointNode.builder()
            .id("test-endpoint-id")
            .path("/api/test")
            .httpMethod("GET")
            .produces("application/json")
            .build();
    }
}