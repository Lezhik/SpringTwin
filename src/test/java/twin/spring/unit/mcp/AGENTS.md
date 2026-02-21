# AGENTS.md: Unit тестирование модуля MCP (Backend)

Правила и структура unit тестирования для модуля mcp.

---

## Структура тестов

```
src/test/java/twin/spring/unit/mcp/
├── service/
│   ├── McpToolServiceTest.java
│   └── McpExecutorServiceTest.java
└── mapper/
    └── McpContextMapperTest.java
```

---

## Тестовые профили

### Использование McpTestProfile

```java
import twin.spring.profiles.mcp.McpTestProfile;

@Test
void executeTool_validRequest_returnsResponse() {
    // Arrange - используем профиль для создания тестовых данных
    McpRequest request = McpTestProfile.createMcpRequest();
    McpTool tool = McpTestProfile.createMcpTool();
    McpContext context = McpTestProfile.createMcpContext();
    
    // ...
}
```

Профиль находится в `src/test/java/twin/spring/profiles/mcp/McpTestProfile.java`

---

## Unit тесты

### McpToolServiceTest.java

```java
/**
 * Unit тесты для McpToolService.
 * Тестирует бизнес-логику в изоляции с использованием Mockito.
 */
@ExtendWith(MockitoExtension.class)
class McpToolServiceTest {
    
    @InjectMocks
    private McpToolService mcpToolService;
    
    @Test
    void getAvailableTools_shouldReturnAllTools() {
        // Act
        List<McpTool> tools = mcpToolService.getAvailableTools();
        
        // Assert
        assertThat(tools).isNotEmpty();
        assertThat(tools).extracting("name")
            .contains("get_class_context", "get_method_context", "get_endpoint_context");
    }
    
    @Test
    void getToolByName_existingName_returnsTool() {
        // Act
        McpTool tool = mcpToolService.getToolByName("get_class_context");
        
        // Assert
        assertThat(tool).isNotNull();
        assertThat(tool.getName()).isEqualTo("get_class_context");
        assertThat(tool.getParameters()).hasSize(2);
    }
    
    @Test
    void getToolByName_unknownName_returnsNull() {
        // Act
        McpTool tool = mcpToolService.getToolByName("unknown_tool");
        
        // Assert
        assertThat(tool).isNull();
    }
    
    @Test
    void validateParameters_validParameters_passes() {
        // Arrange
        McpTool tool = McpTestProfile.createMcpTool();
        Map<String, Object> params = Map.of(
            "fullName", "com.example.UserService",
            "includeMethods", true
        );
        
        // Act & Assert
        assertThatCode(() -> mcpToolService.validateParameters(tool, params))
            .doesNotThrowAnyException();
    }
    
    @Test
    void validateParameters_missingRequiredParameter_throwsException() {
        // Arrange
        McpTool tool = McpTestProfile.createMcpTool();
        Map<String, Object> params = Map.of("includeMethods", true);
        
        // Act & Assert
        assertThatThrownBy(() -> mcpToolService.validateParameters(tool, params))
            .isInstanceOf(McpValidationException.class)
            .hasMessageContaining("fullName");
    }
}
```

### McpExecutorServiceTest.java

```java
/**
 * Unit тесты для McpExecutorService.
 */
@ExtendWith(MockitoExtension.class)
class McpExecutorServiceTest {
    
    @Mock
    private McpToolService toolService;
    
    @Mock
    private ReportService reportService;
    
    @Mock
    private McpContextMapper contextMapper;
    
    @InjectMocks
    private McpExecutorService mcpExecutorService;
    
    @Test
    void executeTool_validClassContextRequest_returnsResponse() {
        // Arrange
        McpRequest request = McpTestProfile.createMcpRequest();
        McpTool tool = McpTestProfile.createMcpTool();
        ClassReport report = ReportTestProfile.createClassReport();
        McpContext context = McpTestProfile.createMcpContext();
        McpResponse expected = McpTestProfile.createMcpResponse();
        
        when(toolService.getToolByName(request.getToolName())).thenReturn(tool);
        when(reportService.generateClassReportByName("com.example.service.UserService"))
            .thenReturn(Mono.just(report));
        when(contextMapper.toMcpContext(report)).thenReturn(context);
        
        // Act & Assert
        StepVerifier.create(mcpExecutorService.executeTool(request))
            .expectNextMatches(response -> {
                assertThat(response.isSuccess()).isTrue();
                assertThat(response.getContext()).isNotNull();
                return true;
            })
            .verifyComplete();
    }
    
    @Test
    void executeTool_invalidTool_returnsErrorResponse() {
        // Arrange
        McpRequest request = McpTestProfile.createMcpRequest();
        
        when(toolService.getToolByName(request.getToolName())).thenReturn(null);
        
        // Act & Assert
        StepVerifier.create(mcpExecutorService.executeTool(request))
            .expectNextMatches(response -> {
                assertThat(response.isSuccess()).isFalse();
                assertThat(response.getErrors()).isNotEmpty();
                return true;
            })
            .verifyComplete();
    }
    
    @Test
    void executeTool_missingParameters_returnsErrorResponse() {
        // Arrange
        McpRequest request = McpRequest.builder()
            .toolName("get_class_context")
            .parameters(Map.of()) // Missing required parameter
            .build();
        McpTool tool = McpTestProfile.createMcpTool();
        
        when(toolService.getToolByName(request.getToolName())).thenReturn(tool);
        doThrow(new McpValidationException("fullName is required"))
            .when(toolService).validateParameters(tool, request.getParameters());
        
        // Act & Assert
        StepVerifier.create(mcpExecutorService.executeTool(request))
            .expectNextMatches(response -> {
                assertThat(response.isSuccess()).isFalse();
                return true;
            })
            .verifyComplete();
    }
    
    @Test
    void executeTool_classNotFound_returnsErrorResponse() {
        // Arrange
        McpRequest request = McpTestProfile.createMcpRequest();
        McpTool tool = McpTestProfile.createMcpTool();
        
        when(toolService.getToolByName(request.getToolName())).thenReturn(tool);
        when(reportService.generateClassReportByName(any()))
            .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(mcpExecutorService.executeTool(request))
            .expectNextMatches(response -> {
                assertThat(response.isSuccess()).isFalse();
                assertThat(response.getErrors().get(0).getMessage())
                    .contains("not found");
                return true;
            })
            .verifyComplete();
    }
}
```

### McpContextMapperTest.java

```java
/**
 * Unit тесты для McpContextMapper.
 */
class McpContextMapperTest {
    
    private McpContextMapper mapper = new McpContextMapper();
    
    @Test
    void toMcpContext_classReport_returnsContext() {
        // Arrange
        ClassReport report = ReportTestProfile.createClassReport();
        
        // Act
        McpContext context = mapper.toMcpContext(report);
        
        // Assert
        assertThat(context.getContextType()).isEqualTo(ReportType.CLASS);
        assertThat(context.getElementName()).isEqualTo(report.getClassInfo().getName());
        assertThat(context.getSummary()).isNotNull();
    }
    
    @Test
    void toMcpContext_methodReport_returnsContext() {
        // Arrange
        MethodReport report = ReportTestProfile.createMethodReport();
        
        // Act
        McpContext context = mapper.toMcpContext(report);
        
        // Assert
        assertThat(context.getContextType()).isEqualTo(ReportType.METHOD);
        assertThat(context.getElementName()).isEqualTo(report.getMethodInfo().getName());
    }
    
    @Test
    void toMcpContext_endpointReport_returnsContext() {
        // Arrange
        EndpointReport report = ReportTestProfile.createEndpointReport();
        
        // Act
        McpContext context = mapper.toMcpContext(report);
        
        // Assert
        assertThat(context.getContextType()).isEqualTo(ReportType.ENDPOINT);
        assertThat(context.getElementName()).contains(report.getEndpoint().getPath());
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
# Unit тесты модуля mcp
gradlew.bat test --tests "twin.spring.unit.mcp.*"