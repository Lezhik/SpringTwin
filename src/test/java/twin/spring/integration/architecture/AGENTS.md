# AGENTS.md: Integration тестирование модуля Architecture

Правила и структура integration тестирования для модуля architecture.

---

## Структура тестов

```
src/test/java/twin/spring/integration/architecture/
├── api/
│   ├── ClassNodeControllerIntegrationTest.java
│   ├── MethodNodeControllerIntegrationTest.java
│   └── EndpointNodeControllerIntegrationTest.java
└── repository/
    ├── ClassNodeRepositoryIntegrationTest.java
    ├── MethodNodeRepositoryIntegrationTest.java
    └── EndpointNodeRepositoryIntegrationTest.java
```

---

## Тестовые профили

### Использование ArchitectureTestProfile

```java
import twin.spring.profiles.architecture.ArchitectureTestProfile;

@BeforeEach
void setUp() {
    architectureTestProfile.seedTestData();
}
```

Профиль находится в `src/test/java/twin/spring/profiles/architecture/ArchitectureTestProfile.java`

---

## Integration тесты

### ClassNodeControllerIntegrationTest.java

```java
/**
 * Integration тесты для ClassNodeController.
 * Тестирует REST API для работы с узлами классов.
 */
@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestNeo4jConfig.class)
class ClassNodeControllerIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private ArchitectureTestProfile architectureTestProfile;
    
    @BeforeEach
    void setUp() {
        architectureTestProfile.seedTestData();
    }
    
    @Test
    void getClassNode_existingId_returnsClassNode() {
        ClassNode classNode = ArchitectureTestProfile.createClassNode();
        
        webTestClient.get()
            .uri("/api/v1/architecture/classes/{id}", classNode.getId())
            .exchange()
            .expectStatus().isOk()
            .expectBody(ClassNodeResponse.class)
            .value(response -> {
                assertThat(response.getName()).isEqualTo(classNode.getName());
                assertThat(response.getFullName()).isEqualTo(classNode.getFullName());
            });
    }
    
    @Test
    void getClassNode_nonExistingId_returnsNotFound() {
        webTestClient.get()
            .uri("/api/v1/architecture/classes/{id}", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isNotFound();
    }
    
    @Test
    void getClassNodeByFullName_existingName_returnsClassNode() {
        ClassNode classNode = ArchitectureTestProfile.createClassNode();
        
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v1/architecture/classes/by-name")
                .queryParam("fullName", classNode.getFullName())
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(ClassNodeResponse.class)
            .value(response -> {
                assertThat(response.getFullName()).isEqualTo(classNode.getFullName());
            });
    }
    
    @Test
    void getAllClassNodes_returnsClassNodeList() {
        webTestClient.get()
            .uri("/api/v1/architecture/classes")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(ClassNodeResponse.class);
    }
    
    @Test
    void getClassNodeDependencies_returnsDependencies() {
        ClassNode classNode = ArchitectureTestProfile.createClassNode();
        
        webTestClient.get()
            .uri("/api/v1/architecture/classes/{id}/dependencies", classNode.getId())
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(DependencyResponse.class);
    }
}
```

### ClassNodeRepositoryIntegrationTest.java

```java
/**
 * Integration тесты для ClassNodeRepository.
 * Тестирует запросы к Neo4j для узлов классов.
 */
@SpringBootTest
@Import(TestNeo4jConfig.class)
class ClassNodeRepositoryIntegrationTest {
    
    @Autowired
    private ClassNodeRepository classNodeRepository;
    
    @BeforeEach
    void setUp() {
        classNodeRepository.deleteAll().block();
    }
    
    @Test
    void save_andFindById_returnsClassNode() {
        // Arrange
        ClassNode classNode = ArchitectureTestProfile.createClassNode();
        
        // Act
        ClassNode saved = classNodeRepository.save(classNode).block();
        ClassNode found = classNodeRepository.findById(saved.getId()).block();
        
        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getFullName()).isEqualTo(classNode.getFullName());
    }
    
    @Test
    void findByFullName_returnsClassNode() {
        // Arrange
        ClassNode classNode = ArchitectureTestProfile.createClassNode();
        classNodeRepository.save(classNode).block();
        
        // Act & Assert
        StepVerifier.create(classNodeRepository.findByFullName(classNode.getFullName()))
            .expectNextMatches(found -> found.getName().equals(classNode.getName()))
            .verifyComplete();
    }
    
    @Test
    void findByLabel_returnsClassNodesWithLabel() {
        // Arrange
        ClassNode serviceClass = ArchitectureTestProfile.createClassNodeWithLabel("Service");
        ClassNode controllerClass = ArchitectureTestProfile.createClassNodeWithLabel("Controller");
        classNodeRepository.saveAll(List.of(serviceClass, controllerClass)).collectList().block();
        
        // Act & Assert
        StepVerifier.create(classNodeRepository.findByLabel("Service"))
            .expectNextCount(1)
            .verifyComplete();
    }
    
    @Test
    void findDependencies_returnsDependentClasses() {
        // Arrange
        ClassNode classA = ArchitectureTestProfile.createClassNode("ClassA");
        ClassNode classB = ArchitectureTestProfile.createClassNode("ClassB");
        classNodeRepository.saveAll(List.of(classA, classB)).collectList().block();
        
        // Create dependency relationship
        // ... create DEPENDS_ON relationship
        
        // Act & Assert
        StepVerifier.create(classNodeRepository.findDependencies(classA.getId()))
            .expectNextCount(1)
            .verifyComplete();
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| REST API | Все методы контроллеров покрыты |
| Репозитории | Запросы к графу проверены |
| Связи | Зависимости между узлами проверены |
| Профили | Тестовые данные вынесены |

---

## Запуск тестов

```bash
# Integration тесты модуля architecture
gradlew.bat test --tests "twin.spring.integration.architecture.*"