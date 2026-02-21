# AGENTS.md: Integration тестирование модуля MCP

Правила и структура integration тестирования для модуля mcp.

---

## Структура тестов

```
src/test/java/twin/spring/integration/mcp/
├── api/
│   └── McpControllerIntegrationTest.java
└── repository/
    └── McpRepositoryIntegrationTest.java
```

---

## Тестовые профили

### Использование McpTestProfile

```java
import twin.spring.profiles.mcp.McpTestProfile;

@BeforeEach
void setUp() {
    mcpTestProfile.seedTestData();
}
```

Профиль находится в `src/test/java/twin/spring/profiles/mcp/McpTestProfile.java`

---

## Integration тесты

### McpControllerIntegrationTest.java

```java
/**
 * Integration тесты для McpController.
 * Тестирует REST API с использованием WebTestClient и Neo4j embedded.
 */
@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestNeo4jConfig.class)
class McpControllerIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private McpTestProfile mcpTestProfile;
    
    @BeforeEach
    void setUp() {
        mcpTestProfile.seedTestData();
    }
    
    @Test
    void getTools_shouldReturnAllTools() {
        webTestClient.get()
            .uri("/api/v1/mcp/tools")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(McpToolResponse.class)
            .hasSize(3)
            .value(tools -> {
                assertThat(tools.get(0).getName()).isEqualTo("get_class_context");
            });
    }
    
    @Test
    void getToolByName_shouldReturnTool() {
        webTestClient.get()
            .uri("/api/v1/mcp/tools/{name}", "get_class_context")
            .exchange()
            .expectStatus().isOk()
            .expectBody(McpToolResponse.class)
            .value(tool -> {
                assertThat(tool.getName()).isEqualTo("get_class_context");
                assertThat(tool.getParameters()).hasSize(2);
            });
    }
    
    @Test
    void getToolByName_shouldReturn404ForUnknownTool() {
        webTestClient.get()
            .uri("/api/v1/mcp/tools/{name}", "unknown_tool")
            .exchange()
            .expectStatus().isNotFound();
    }
    
    @Test
    void executeTool_shouldReturnContext() {
        McpRequest request = McpTestProfile.createMcpRequest();
        
        webTestClient.post()
            .uri("/api/v1/mcp/execute")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectBody(McpResponse.class)
            .value(response -> {
                assertThat(response.isSuccess()).isTrue();
                assertThat(response.getContext()).isNotNull();
                assertThat(response.getContext().getElementName()).isEqualTo("UserService");
            });
    }
    
    @Test
    void executeTool_shouldReturnErrorForInvalidParameters() {
        McpRequest request = McpRequest.builder()
            .toolName("get_class_context")
            .parameters(Map.of())
            .build();
        
        webTestClient.post()
            .uri("/api/v1/mcp/execute")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(McpResponse.class)
            .value(response -> {
                assertThat(response.isSuccess()).isFalse();
                assertThat(response.getErrors()).isNotEmpty();
            });
    }
    
    @Test
    void executeTool_shouldReturnErrorForUnknownTool() {
        McpRequest request = McpRequest.builder()
            .toolName("unknown_tool")
            .parameters(Map.of("param", "value"))
            .build();
        
        webTestClient.post()
            .uri("/api/v1/mcp/execute")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound();
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| REST API | Все методы контроллера покрыты |
| Tools | Список и детали tools проверены |
| Execute | Выполнение tools проверено |
| Валидация | Ошибки валидации проверены |
| Профили | Тестовые данные вынесены |

---

## Запуск тестов

```bash
# Integration тесты модуля mcp
gradlew.bat test --tests "twin.spring.integration.mcp.*"