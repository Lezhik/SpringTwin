# AGENTS.md: Unit тестирование модуля Architecture (Backend)

Правила и структура unit тестирования для модуля architecture.

---

## Структура тестов

```
src/test/java/twin/spring/unit/architecture/
├── service/
│   ├── ClassNodeServiceTest.java
│   ├── MethodNodeServiceTest.java
│   └── GraphServiceTest.java
└── mapper/
    ├── ClassNodeMapperTest.java
    ├── MethodNodeMapperTest.java
    └── EndpointNodeMapperTest.java
```

---

## Тестовые профили

### Использование ArchitectureTestProfile

```java
import twin.spring.profiles.architecture.ArchitectureTestProfile;

@Test
void findById_existingId_returnsClassNode() {
    // Arrange - используем профиль для создания тестовых данных
    ClassNode classNode = ArchitectureTestProfile.createClassNode();
    ClassNodeResponse response = ArchitectureTestProfile.createClassNodeResponse();
    
    // ...
}
```

Профиль находится в `src/test/java/twin/spring/profiles/architecture/ArchitectureTestProfile.java`

---

## Unit тесты

### ClassNodeServiceTest.java

```java
/**
 * Unit тесты для ClassNodeService.
 * Тестирует бизнес-логику в изоляции с использованием Mockito.
 */
@ExtendWith(MockitoExtension.class)
class ClassNodeServiceTest {
    
    @Mock
    private ClassNodeRepository classNodeRepository;
    
    @Mock
    private ClassNodeMapper classNodeMapper;
    
    @InjectMocks
    private ClassNodeService classNodeService;
    
    @Test
    void findById_existingId_returnsClassNode() {
        // Arrange
        ClassNode classNode = ArchitectureTestProfile.createClassNode();
        ClassNodeResponse expected = ClassNodeResponse.builder()
            .id(classNode.getId())
            .name(classNode.getName())
            .fullName(classNode.getFullName())
            .build();
            
        when(classNodeRepository.findById(classNode.getId()))
            .thenReturn(Mono.just(classNode));
        when(classNodeMapper.toResponse(classNode))
            .thenReturn(expected);
        
        // Act & Assert
        StepVerifier.create(classNodeService.findById(classNode.getId()))
            .expectNext(expected)
            .verifyComplete();
    }
    
    @Test
    void findById_nonExistingId_returnsEmpty() {
        // Arrange
        String id = UUID.randomUUID().toString();
        when(classNodeRepository.findById(id)).thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(classNodeService.findById(id))
            .verifyComplete();
    }
    
    @Test
    void findByLabel_existingLabel_returnsFilteredClasses() {
        // Arrange
        List<ClassNode> classes = ArchitectureTestProfile.createClassNodeList();
        when(classNodeRepository.findByLabel("Service"))
            .thenReturn(Flux.fromIterable(classes));
        
        // Act & Assert
        StepVerifier.create(classNodeService.findByLabel("Service"))
            .expectNextCount(3)
            .verifyComplete();
    }
    
    @Test
    void searchByName_matchingQuery_returnsClasses() {
        // Arrange
        ClassNode classNode = ArchitectureTestProfile.createClassNode();
        when(classNodeRepository.findByNameContainingIgnoreCase("User"))
            .thenReturn(Flux.just(classNode));
        
        // Act & Assert
        StepVerifier.create(classNodeService.searchByName("User"))
            .expectNextCount(1)
            .verifyComplete();
    }
}
```

### ClassNodeMapperTest.java

```java
/**
 * Unit тесты для ClassNodeMapper.
 * Тестирует преобразование между DTO и Entity.
 */
class ClassNodeMapperTest {
    
    private ClassNodeMapper mapper = new ClassNodeMapper();
    
    @Test
    void toResponse_validEntity_returnsResponse() {
        // Arrange
        ClassNode classNode = ArchitectureTestProfile.createClassNode();
        
        // Act
        ClassNodeResponse response = mapper.toResponse(classNode);
        
        // Assert
        assertThat(response.getId()).isEqualTo(classNode.getId());
        assertThat(response.getName()).isEqualTo(classNode.getName());
        assertThat(response.getFullName()).isEqualTo(classNode.getFullName());
        assertThat(response.getPackageName()).isEqualTo(classNode.getPackageName());
        assertThat(response.getLabels()).isEqualTo(classNode.getLabels());
    }
    
    @Test
    void toEntity_validRequest_returnsEntity() {
        // Arrange
        ClassNodeRequest request = ClassNodeRequest.builder()
            .name("TestService")
            .fullName("com.example.TestService")
            .packageName("com.example")
            .labels(List.of("Service"))
            .build();
        
        // Act
        ClassNode entity = mapper.toEntity(request);
        
        // Assert
        assertThat(entity.getName()).isEqualTo(request.getName());
        assertThat(entity.getFullName()).isEqualTo(request.getFullName());
        assertThat(entity.getPackageName()).isEqualTo(request.getPackageName());
        assertThat(entity.getLabels()).isEqualTo(request.getLabels());
        assertThat(entity.getId()).isNotNull();
    }
}
```

### MethodNodeMapperTest.java

```java
/**
 * Unit тесты для MethodNodeMapper.
 */
class MethodNodeMapperTest {
    
    private MethodNodeMapper mapper = new MethodNodeMapper();
    
    @Test
    void toResponse_validEntity_returnsResponse() {
        // Arrange
        MethodNode methodNode = ArchitectureTestProfile.createMethodNode();
        
        // Act
        MethodNodeResponse response = mapper.toResponse(methodNode);
        
        // Assert
        assertThat(response.getId()).isEqualTo(methodNode.getId());
        assertThat(response.getName()).isEqualTo(methodNode.getName());
        assertThat(response.getSignature()).isEqualTo(methodNode.getSignature());
        assertThat(response.getReturnType()).isEqualTo(methodNode.getReturnType());
    }
}
```

### EndpointNodeMapperTest.java

```java
/**
 * Unit тесты для EndpointNodeMapper.
 */
class EndpointNodeMapperTest {
    
    private EndpointNodeMapper mapper = new EndpointNodeMapper();
    
    @Test
    void toResponse_validEntity_returnsResponse() {
        // Arrange
        EndpointNode endpointNode = ArchitectureTestProfile.createEndpointNode();
        
        // Act
        EndpointNodeResponse response = mapper.toResponse(endpointNode);
        
        // Assert
        assertThat(response.getId()).isEqualTo(endpointNode.getId());
        assertThat(response.getPath()).isEqualTo(endpointNode.getPath());
        assertThat(response.getHttpMethod()).isEqualTo(endpointNode.getHttpMethod());
        assertThat(response.getProduces()).isEqualTo(endpointNode.getProduces());
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
# Unit тесты модуля architecture
gradlew.bat test --tests "twin.spring.unit.architecture.*"