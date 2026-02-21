# AGENTS.md: Unit тестирование модуля Analysis (Backend)

Правила и структура unit тестирования для модуля analysis.

---

## Структура тестов

```
src/test/java/twin/spring/unit/analysis/
├── service/
│   ├── AnalysisServiceTest.java
│   ├── AstIndexerServiceTest.java
│   └── BytecodeAnalyzerServiceTest.java
└── mapper/
    └── AnalysisTaskMapperTest.java
```

---

## Тестовые профили

### Использование AnalysisTestProfile

```java
import twin.spring.profiles.analysis.AnalysisTestProfile;

@Test
void startAnalysis_validProjectId_returnsTask() {
    // Arrange - используем профиль для создания тестовых данных
    AnalysisTask task = AnalysisTestProfile.createAnalysisTask();
    AnalysisTaskResponse response = AnalysisTestProfile.createTaskResponse();
    
    // ...
}
```

Профиль находится в `src/test/java/twin/spring/profiles/analysis/AnalysisTestProfile.java`

---

## Unit тесты

### AnalysisServiceTest.java

```java
/**
 * Unit тесты для AnalysisService.
 * Тестирует бизнес-логику в изоляции с использованием Mockito.
 */
@ExtendWith(MockitoExtension.class)
class AnalysisServiceTest {
    
    @Mock
    private AnalysisTaskRepository taskRepository;
    
    @Mock
    private AstIndexerService astIndexerService;
    
    @Mock
    private BytecodeAnalyzerService bytecodeAnalyzerService;
    
    @Mock
    private AnalysisTaskMapper taskMapper;
    
    @InjectMocks
    private AnalysisService analysisService;
    
    @Test
    void startAnalysis_validProjectId_returnsTask() {
        // Arrange
        String projectId = UUID.randomUUID().toString();
        AnalysisTask task = AnalysisTestProfile.createAnalysisTask();
        AnalysisTaskResponse expected = AnalysisTaskResponse.builder()
            .id(task.getId())
            .projectId(projectId)
            .status("PENDING")
            .build();
            
        when(taskRepository.save(any())).thenReturn(Mono.just(task));
        when(taskMapper.toResponse(task)).thenReturn(expected);
        
        // Act & Assert
        StepVerifier.create(analysisService.startAnalysis(projectId))
            .expectNext(expected)
            .verifyComplete();
    }
    
    @Test
    void getTaskStatus_existingTask_returnsStatus() {
        // Arrange
        AnalysisTask task = AnalysisTestProfile.createRunningTask();
        AnalysisTaskResponse expected = AnalysisTaskResponse.builder()
            .id(task.getId())
            .status("RUNNING")
            .classesFound(5)
            .build();
            
        when(taskRepository.findById(task.getId())).thenReturn(Mono.just(task));
        when(taskMapper.toResponse(task)).thenReturn(expected);
        
        // Act & Assert
        StepVerifier.create(analysisService.getTaskStatus(task.getId()))
            .expectNext(expected)
            .verifyComplete();
    }
    
    @Test
    void cancelAnalysis_runningTask_cancelsSuccessfully() {
        // Arrange
        AnalysisTask task = AnalysisTestProfile.createRunningTask();
        AnalysisTask cancelledTask = AnalysisTask.builder()
            .id(task.getId())
            .status(AnalysisStatus.CANCELLED)
            .build();
            
        when(taskRepository.findById(task.getId())).thenReturn(Mono.just(task));
        when(taskRepository.save(any())).thenReturn(Mono.just(cancelledTask));
        
        // Act & Assert
        StepVerifier.create(analysisService.cancelAnalysis(task.getId()))
            .verifyComplete();
    }
    
    @Test
    void getResult_completedTask_returnsResult() {
        // Arrange
        AnalysisResult result = AnalysisTestProfile.createAnalysisResult();
        
        when(taskRepository.findResultByTaskId(result.getTaskId()))
            .thenReturn(Mono.just(result));
        
        // Act & Assert
        StepVerifier.create(analysisService.getResult(result.getTaskId()))
            .expectNext(result)
            .verifyComplete();
    }
    
    @Test
    void getHistory_existingProject_returnsHistory() {
        // Arrange
        String projectId = UUID.randomUUID().toString();
        List<AnalysisTask> history = AnalysisTestProfile.createAnalysisHistory();
        
        when(taskRepository.findByProjectIdOrderByStartedAtDesc(projectId))
            .thenReturn(Flux.fromIterable(history));
        
        // Act & Assert
        StepVerifier.create(analysisService.getHistory(projectId))
            .expectNextCount(2)
            .verifyComplete();
    }
}
```

### AstIndexerServiceTest.java

```java
/**
 * Unit тесты для AstIndexerService.
 */
@ExtendWith(MockitoExtension.class)
class AstIndexerServiceTest {
    
    @Mock
    private ClassNodeRepository classNodeRepository;
    
    @Mock
    private MethodNodeRepository methodNodeRepository;
    
    @InjectMocks
    private AstIndexerService astIndexerService;
    
    @Test
    void indexProject_validPath_indexesClasses() {
        // Arrange
        String projectId = UUID.randomUUID().toString();
        Path projectPath = Paths.get("/path/to/project");
        
        when(classNodeRepository.saveAll(any())).thenReturn(Flux.empty());
        when(methodNodeRepository.saveAll(any())).thenReturn(Flux.empty());
        
        // Act & Assert
        StepVerifier.create(astIndexerService.indexProject(projectId, projectPath))
            .expectNextCount(1)
            .verifyComplete();
    }
    
    @Test
    void detectSpringAnnotations_controllerClass_returnsLabels() {
        // Arrange
        ClassNode classNode = ClassNode.builder()
            .name("UserController")
            .labels(List.of("RestController"))
            .build();
        
        // Act
        List<String> labels = astIndexerService.detectSpringAnnotations(classNode);
        
        // Assert
        assertThat(labels).contains("RestController", "Controller");
    }
    
    @Test
    void detectSpringAnnotations_serviceClass_returnsLabels() {
        // Arrange
        ClassNode classNode = ClassNode.builder()
            .name("UserService")
            .labels(List.of("Service"))
            .build();
        
        // Act
        List<String> labels = astIndexerService.detectSpringAnnotations(classNode);
        
        // Assert
        assertThat(labels).contains("Service");
    }
}
```

### BytecodeAnalyzerServiceTest.java

```java
/**
 * Unit тесты для BytecodeAnalyzerService.
 */
@ExtendWith(MockitoExtension.class)
class BytecodeAnalyzerServiceTest {
    
    @Mock
    private ClassNodeRepository classNodeRepository;
    
    @Mock
    private MethodNodeRepository methodNodeRepository;
    
    @InjectMocks
    private BytecodeAnalyzerService bytecodeAnalyzerService;
    
    @Test
    void analyzeClass_validClassFile_extractsMethods() {
        // Arrange
        Path classFile = Paths.get("/path/to/TestClass.class");
        
        // Act
        List<MethodNode> methods = bytecodeAnalyzerService.analyzeClass(classFile);
        
        // Assert
        assertThat(methods).isNotEmpty();
    }
    
    @Test
    void extractMethodCalls_validMethod_returnsCalls() {
        // Arrange
        MethodNode method = AnalysisTestProfile.createMethodNode();
        
        // Act
        List<MethodCall> calls = bytecodeAnalyzerService.extractMethodCalls(method);
        
        // Assert
        assertThat(calls).isNotNull();
    }
}
```

### AnalysisTaskMapperTest.java

```java
/**
 * Unit тесты для AnalysisTaskMapper.
 */
class AnalysisTaskMapperTest {
    
    private AnalysisTaskMapper mapper = new AnalysisTaskMapper();
    
    @Test
    void toResponse_validTask_returnsResponse() {
        // Arrange
        AnalysisTask task = AnalysisTestProfile.createCompletedTask();
        
        // Act
        AnalysisTaskResponse response = mapper.toResponse(task);
        
        // Assert
        assertThat(response.getId()).isEqualTo(task.getId());
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getClassesFound()).isEqualTo(task.getClassesFound());
    }
    
    @Test
    void toEntity_validRequest_returnsEntity() {
        // Arrange
        StartAnalysisRequest request = StartAnalysisRequest.builder()
            .projectId(UUID.randomUUID().toString())
            .build();
        
        // Act
        AnalysisTask entity = mapper.toEntity(request);
        
        // Assert
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getProjectId()).isEqualTo(request.getProjectId());
        assertThat(entity.getStatus()).isEqualTo(AnalysisStatus.PENDING);
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
# Unit тесты модуля analysis
gradlew.bat test --tests "twin.spring.unit.analysis.*"