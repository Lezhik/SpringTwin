# AGENTS.md: Unit тестирование модуля Report (Backend)

Правила и структура unit тестирования для модуля report.

---

## Структура тестов

```
src/test/java/twin/spring/unit/report/
├── service/
│   ├── ReportServiceTest.java
│   └── LlmExportServiceTest.java
└── mapper/
    └── ReportMapperTest.java
```

---

## Тестовые профили

### Использование ReportTestProfile

```java
import twin.spring.profiles.report.ReportTestProfile;

@Test
void generateClassReport_existingClass_returnsReport() {
    // Arrange - используем профиль для создания тестовых данных
    ClassReport report = ReportTestProfile.createClassReport();
    
    // ...
}
```

Профиль находится в `src/test/java/twin/spring/profiles/report/ReportTestProfile.java`

---

## Unit тесты

### ReportServiceTest.java

```java
/**
 * Unit тесты для ReportService.
 * Тестирует бизнес-логику в изоляции с использованием Mockito.
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    
    @Mock
    private ClassNodeRepository classNodeRepository;
    
    @Mock
    private MethodNodeRepository methodNodeRepository;
    
    @Mock
    private EndpointNodeRepository endpointNodeRepository;
    
    @Mock
    private ReportMapper reportMapper;
    
    @InjectMocks
    private ReportService reportService;
    
    @Test
    void generateEndpointReport_existingEndpoint_returnsReport() {
        // Arrange
        String endpointId = UUID.randomUUID().toString();
        EndpointReport expected = ReportTestProfile.createEndpointReport();
        
        when(endpointNodeRepository.findById(endpointId))
            .thenReturn(Mono.just(createEndpointNode()));
        when(reportMapper.toEndpointReport(any()))
            .thenReturn(expected);
        
        // Act & Assert
        StepVerifier.create(reportService.generateEndpointReport(endpointId))
            .expectNext(expected)
            .verifyComplete();
    }
    
    @Test
    void generateClassReport_existingClass_returnsReport() {
        // Arrange
        String classId = UUID.randomUUID().toString();
        ClassReport expected = ReportTestProfile.createClassReport();
        
        when(classNodeRepository.findById(classId))
            .thenReturn(Mono.just(createClassNode()));
        when(classNodeRepository.findDependencies(classId))
            .thenReturn(Flux.empty());
        when(classNodeRepository.findDependents(classId))
            .thenReturn(Flux.empty());
        when(reportMapper.toClassReport(any(), any(), any()))
            .thenReturn(expected);
        
        // Act & Assert
        StepVerifier.create(reportService.generateClassReport(classId))
            .expectNext(expected)
            .verifyComplete();
    }
    
    @Test
    void generateClassReportByName_existingClass_returnsReport() {
        // Arrange
        String fullName = "com.example.service.UserService";
        ClassReport expected = ReportTestProfile.createClassReport();
        
        when(classNodeRepository.findByFullName(fullName))
            .thenReturn(Mono.just(createClassNode()));
        when(reportMapper.toClassReport(any(), any(), any()))
            .thenReturn(expected);
        
        // Act & Assert
        StepVerifier.create(reportService.generateClassReportByName(fullName))
            .expectNext(expected)
            .verifyComplete();
    }
    
    @Test
    void generateMethodReport_existingMethod_returnsReport() {
        // Arrange
        String methodId = UUID.randomUUID().toString();
        MethodReport expected = ReportTestProfile.createMethodReport();
        
        when(methodNodeRepository.findById(methodId))
            .thenReturn(Mono.just(createMethodNode()));
        when(reportMapper.toMethodReport(any(), any(), any(), any()))
            .thenReturn(expected);
        
        // Act & Assert
        StepVerifier.create(reportService.generateMethodReport(methodId))
            .expectNext(expected)
            .verifyComplete();
    }
    
    @Test
    void generateClassReport_nonExistingClass_returnsEmpty() {
        // Arrange
        String classId = UUID.randomUUID().toString();
        
        when(classNodeRepository.findById(classId))
            .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(reportService.generateClassReport(classId))
            .verifyComplete();
    }
}
```

### LlmExportServiceTest.java

```java
/**
 * Unit тесты для LlmExportService.
 */
@ExtendWith(MockitoExtension.class)
class LlmExportServiceTest {
    
    @Mock
    private ReportService reportService;
    
    @InjectMocks
    private LlmExportService llmExportService;
    
    @Test
    void exportForLlm_classContext_returnsExport() {
        // Arrange
        String classId = UUID.randomUUID().toString();
        ClassReport report = ReportTestProfile.createClassReport();
        LlmContextExport expected = ReportTestProfile.createLlmExport();
        
        when(reportService.generateClassReport(classId))
            .thenReturn(Mono.just(report));
        
        // Act & Assert
        StepVerifier.create(llmExportService.exportForLlm(ReportType.CLASS, classId))
            .expectNextMatches(export -> {
                assertThat(export.getContextType()).isEqualTo(ReportType.CLASS);
                assertThat(export.getSummary()).isNotNull();
                return true;
            })
            .verifyComplete();
    }
    
    @Test
    void exportForLlm_endpointContext_returnsExport() {
        // Arrange
        String endpointId = UUID.randomUUID().toString();
        EndpointReport report = ReportTestProfile.createEndpointReport();
        
        when(reportService.generateEndpointReport(endpointId))
            .thenReturn(Mono.just(report));
        
        // Act & Assert
        StepVerifier.create(llmExportService.exportForLlm(ReportType.ENDPOINT, endpointId))
            .expectNextMatches(export -> {
                assertThat(export.getContextType()).isEqualTo(ReportType.ENDPOINT);
                return true;
            })
            .verifyComplete();
    }
    
    @Test
    void exportForLlm_jsonFormat_returnsStructuredData() {
        // Arrange
        String classId = UUID.randomUUID().toString();
        ClassReport report = ReportTestProfile.createClassReport();
        
        when(reportService.generateClassReport(classId))
            .thenReturn(Mono.just(report));
        
        // Act & Assert
        StepVerifier.create(llmExportService.exportForLlm(ReportType.CLASS, classId, "json"))
            .expectNextMatches(export -> export.getStructuredData() != null)
            .verifyComplete();
    }
    
    @Test
    void exportForLlm_markdownFormat_returnsPromptTemplate() {
        // Arrange
        String classId = UUID.randomUUID().toString();
        ClassReport report = ReportTestProfile.createClassReport();
        
        when(reportService.generateClassReport(classId))
            .thenReturn(Mono.just(report));
        
        // Act & Assert
        StepVerifier.create(llmExportService.exportForLlm(ReportType.CLASS, classId, "markdown"))
            .expectNextMatches(export -> export.getPromptTemplate() != null)
            .verifyComplete();
    }
}
```

### ReportMapperTest.java

```java
/**
 * Unit тесты для ReportMapper.
 */
class ReportMapperTest {
    
    private ReportMapper mapper = new ReportMapper();
    
    @Test
    void toClassReport_validData_returnsReport() {
        // Arrange
        ClassNode classNode = ArchitectureTestProfile.createClassNode();
        List<DependencyRelation> dependencies = List.of();
        List<DependencyRelation> dependents = List.of();
        
        // Act
        ClassReport report = mapper.toClassReport(classNode, dependencies, dependents);
        
        // Assert
        assertThat(report.getElementId()).isEqualTo(classNode.getId());
        assertThat(report.getReportType()).isEqualTo(ReportType.CLASS);
        assertThat(report.getClassInfo().getName()).isEqualTo(classNode.getName());
    }
    
    @Test
    void toMethodReport_validData_returnsReport() {
        // Arrange
        MethodNode methodNode = ArchitectureTestProfile.createMethodNode();
        ClassNode parentClass = ArchitectureTestProfile.createClassNode();
        List<MethodReference> calledMethods = List.of();
        List<MethodReference> callingMethods = List.of();
        
        // Act
        MethodReport report = mapper.toMethodReport(methodNode, parentClass, calledMethods, callingMethods);
        
        // Assert
        assertThat(report.getElementId()).isEqualTo(methodNode.getId());
        assertThat(report.getReportType()).isEqualTo(ReportType.METHOD);
        assertThat(report.getMethodInfo().getName()).isEqualTo(methodNode.getName());
    }
    
    @Test
    void toEndpointReport_validData_returnsReport() {
        // Arrange
        EndpointNode endpointNode = ArchitectureTestProfile.createEndpointNode();
        MethodNode exposingMethod = ArchitectureTestProfile.createMethodNode();
        ClassNode controllerClass = ArchitectureTestProfile.createControllerClassNode();
        List<CallChainItem> callChain = List.of();
        List<DependencyInfo> dependencies = List.of();
        
        // Act
        EndpointReport report = mapper.toEndpointReport(endpointNode, exposingMethod, controllerClass, callChain, dependencies);
        
        // Assert
        assertThat(report.getElementId()).isEqualTo(endpointNode.getId());
        assertThat(report.getReportType()).isEqualTo(ReportType.ENDPOINT);
        assertThat(report.getEndpoint().getPath()).isEqualTo(endpointNode.getPath());
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
# Unit тесты модуля report
gradlew.bat test --tests "twin.spring.unit.report.*"