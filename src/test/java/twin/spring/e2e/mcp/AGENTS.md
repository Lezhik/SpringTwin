# AGENTS.md: E2E тестирование модуля MCP

Правила и структура E2E тестирования для модуля mcp.

---

## Структура тестов

```
src/test/java/twin/spring/e2e/mcp/
└── McpExecutionE2ETest.java
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

## E2E тесты

### McpExecutionE2ETest.java

```java
/**
 * E2E тесты для выполнения MCP tools.
 * Тестирует пользовательские сценарии с использованием Playwright.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestNeo4jConfig.class)
class McpExecutionE2ETest {
    
    @LocalServerPort
    private int port;
    
    private Playwright playwright;
    private Browser browser;
    private Page page;
    
    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        page = browser.newPage();
    }
    
    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
    
    @Test
    void shouldDisplayToolList_whenPageLoads() {
        page.navigate("http://localhost:" + port + "/mcp");
        
        assertThat(page.locator("[data-test='tool-card']").count()).isGreaterThanOrEqualTo(3);
    }
    
    @Test
    void shouldDisplayToolDetails_whenToolSelected() {
        page.navigate("http://localhost:" + port + "/mcp");
        
        page.click("[data-test='tool-card']:first-child");
        
        assertThat(page.locator("[data-test='tool-detail']").isVisible()).isTrue();
        assertThat(page.locator("[data-test='tool-name']").textContent())
            .contains("get_class_context");
    }
    
    @Test
    void shouldExecuteClassContextTool_whenParametersProvided() {
        page.navigate("http://localhost:" + port + "/mcp");
        
        page.click("[data-test='tool-card']:first-child");
        page.fill("input[name='fullName']", "com.example.service.UserService");
        page.click("[data-test='execute-btn']");
        
        assertThat(page.locator("[data-test='mcp-response']").isVisible()).isTrue();
        assertThat(page.locator("[data-test='context-type']").textContent())
            .contains("CLASS");
    }
    
    @Test
    void shouldShowValidationError_whenParametersMissing() {
        page.navigate("http://localhost:" + port + "/mcp");
        
        page.click("[data-test='tool-card']:first-child");
        page.click("[data-test='execute-btn']");
        
        assertThat(page.locator("[data-test='validation-error']").isVisible()).isTrue();
    }
    
    @Test
    void shouldHandleExecutionError_whenClassNotFound() {
        page.navigate("http://localhost:" + port + "/mcp");
        
        page.click("[data-test='tool-card']:first-child");
        page.fill("input[name='fullName']", "com.nonexistent.Class");
        page.click("[data-test='execute-btn']");
        
        assertThat(page.locator("[data-test='error-message']").isVisible()).isTrue();
    }
    
    @Test
    void shouldCopyResponseToClipboard_whenCopyClicked() {
        page.navigate("http://localhost:" + port + "/mcp");
        
        page.click("[data-test='tool-card']:first-child");
        page.fill("input[name='fullName']", "com.example.service.UserService");
        page.click("[data-test='execute-btn']");
        page.click("[data-test='copy-response-btn']");
        
        assertThat(page.locator("[data-test='notification']").textContent())
            .contains("Copied");
    }
    
    @Test
    void shouldExecuteEndpointContextTool_whenEndpointProvided() {
        page.navigate("http://localhost:" + port + "/mcp");
        
        page.click("[data-test='tool-card']:contains('endpoint')");
        page.fill("input[name='path']", "/api/users");
        page.click("[data-test='execute-btn']");
        
        assertThat(page.locator("[data-test='mcp-response']").isVisible()).isTrue();
        assertThat(page.locator("[data-test='context-type']").textContent())
            .contains("ENDPOINT");
    }
    
    @Test
    void shouldShowExecutionHistory_whenToolsExecuted() {
        page.navigate("http://localhost:" + port + "/mcp");
        
        // Execute a tool first
        page.click("[data-test='tool-card']:first-child");
        page.fill("input[name='fullName']", "com.example.service.UserService");
        page.click("[data-test='execute-btn']");
        
        // Check history
        assertThat(page.locator("[data-test='history-item']").count()).isGreaterThanOrEqualTo(1);
    }
    
    @Test
    void shouldReExecuteFromHistory_whenHistoryItemClicked() {
        page.navigate("http://localhost:" + port + "/mcp");
        
        // Execute a tool first
        page.click("[data-test='tool-card']:first-child");
        page.fill("input[name='fullName']", "com.example.service.UserService");
        page.click("[data-test='execute-btn']");
        
        // Clear and re-execute from history
        page.click("[data-test='clear-btn']");
        page.click("[data-test='history-item']:first-child");
        
        assertThat(page.locator("input[name='fullName']").inputValue())
            .isEqualTo("com.example.service.UserService");
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| Список tools | Все tools отображаются |
| Выполнение | Tools выполняются корректно |
| Валидация | Ошибки валидации проверены |
| История | История выполнения сохраняется |
| Копирование | Ответ копируется в буфер |

---

## Запуск тестов

```bash
# E2E тесты модуля mcp
gradlew.bat test --tests "twin.spring.e2e.mcp.*"