# AGENTS.md: E2E тестирование модуля Report

Правила и структура E2E тестирования для модуля report.

---

## Структура тестов

```
src/test/java/twin/spring/e2e/report/
├── ReportGenerationE2ETest.java
└── LlmExportE2ETest.java
```

---

## Тестовые профили

### Использование ReportTestProfile

```java
import twin.spring.profiles.report.ReportTestProfile;

@BeforeEach
void setUp() {
    reportTestProfile.seedTestData();
}
```

Профиль находится в `src/test/java/twin/spring/profiles/report/ReportTestProfile.java`

---

## E2E тесты

### ReportGenerationE2ETest.java

```java
/**
 * E2E тесты для генерации отчетов.
 * Тестирует пользовательские сценарии с использованием Playwright.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestNeo4jConfig.class)
class ReportGenerationE2ETest {
    
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
    void shouldGenerateClassReport_whenClassSelected() {
        page.navigate("http://localhost:" + port + "/report");
        
        page.selectOption("[data-test='report-type-selector']", "CLASS");
        page.fill("[data-test='element-search']", "UserService");
        page.click("[data-test='generate-btn']");
        
        assertThat(page.locator("[data-test='class-report']").isVisible()).isTrue();
        assertThat(page.locator("[data-test='class-name']").textContent())
            .contains("UserService");
    }
    
    @Test
    void shouldGenerateEndpointReport_whenEndpointSelected() {
        page.navigate("http://localhost:" + port + "/report");
        
        page.selectOption("[data-test='report-type-selector']", "ENDPOINT");
        page.fill("[data-test='element-search']", "/api/users");
        page.click("[data-test='generate-btn']");
        
        assertThat(page.locator("[data-test='endpoint-report']").isVisible()).isTrue();
        assertThat(page.locator("[data-test='endpoint-path']").textContent())
            .contains("/api/users");
    }
    
    @Test
    void shouldGenerateMethodReport_whenMethodSelected() {
        page.navigate("http://localhost:" + port + "/report");
        
        page.selectOption("[data-test='report-type-selector']", "METHOD");
        page.fill("[data-test='element-search']", "getAllUsers");
        page.click("[data-test='generate-btn']");
        
        assertThat(page.locator("[data-test='method-report']").isVisible()).isTrue();
        assertThat(page.locator("[data-test='method-name']").textContent())
            .contains("getAllUsers");
    }
    
    @Test
    void shouldNavigateToDependency_whenLinkClicked() {
        page.navigate("http://localhost:" + port + "/report");
        
        page.selectOption("[data-test='report-type-selector']", "CLASS");
        page.fill("[data-test='element-search']", "UserController");
        page.click("[data-test='generate-btn']");
        
        // Click on dependency link
        page.click("[data-test='dependency-link']:first-child");
        
        assertThat(page.locator("[data-test='class-report']").isVisible()).isTrue();
    }
    
    @Test
    void shouldShowCallChain_whenEndpointReportGenerated() {
        page.navigate("http://localhost:" + port + "/report");
        
        page.selectOption("[data-test='report-type-selector']", "ENDPOINT");
        page.fill("[data-test='element-search']", "/api/users");
        page.click("[data-test='generate-btn']");
        
        assertThat(page.locator("[data-test='call-chain']").isVisible()).isTrue();
        assertThat(page.locator("[data-test='call-chain-item']").count()).isGreaterThanOrEqualTo(1);
    }
    
    @Test
    void shouldShowNoResults_whenElementNotFound() {
        page.navigate("http://localhost:" + port + "/report");
        
        page.selectOption("[data-test='report-type-selector']", "CLASS");
        page.fill("[data-test='element-search']", "NonExistentClass");
        page.click("[data-test='generate-btn']");
        
        assertThat(page.locator("[data-test='no-results']").isVisible()).isTrue();
    }
}
```

### LlmExportE2ETest.java

```java
/**
 * E2E тесты для экспорта контекста для LLM.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestNeo4jConfig.class)
class LlmExportE2ETest {
    
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
    void shouldExportToJsonFormat_whenExportClicked() {
        page.navigate("http://localhost:" + port + "/report");
        
        page.selectOption("[data-test='report-type-selector']", "CLASS");
        page.fill("[data-test='element-search']", "UserService");
        page.click("[data-test='generate-btn']");
        page.click("[data-test='export-btn']");
        
        assertThat(page.locator("[data-test='llm-export']").isVisible()).isTrue();
        assertThat(page.locator("[data-test='json-view']").isVisible()).isTrue();
    }
    
    @Test
    void shouldExportToMarkdownFormat_whenFormatSelected() {
        page.navigate("http://localhost:" + port + "/report");
        
        page.selectOption("[data-test='report-type-selector']", "CLASS");
        page.fill("[data-test='element-search']", "UserService");
        page.click("[data-test='generate-btn']");
        page.click("[data-test='export-btn']");
        page.selectOption("[data-test='format-selector']", "markdown");
        
        assertThat(page.locator("[data-test='markdown-view']").isVisible()).isTrue();
    }
    
    @Test
    void shouldCopyToClipboard_whenCopyClicked() {
        page.navigate("http://localhost:" + port + "/report");
        
        page.selectOption("[data-test='report-type-selector']", "CLASS");
        page.fill("[data-test='element-search']", "UserService");
        page.click("[data-test='generate-btn']");
        page.click("[data-test='export-btn']");
        page.click("[data-test='copy-btn']");
        
        assertThat(page.locator("[data-test='notification']").textContent())
            .contains("Copied");
    }
    
    @Test
    void shouldDownloadExport_whenDownloadClicked() {
        page.navigate("http://localhost:" + port + "/report");
        
        page.selectOption("[data-test='report-type-selector']", "CLASS");
        page.fill("[data-test='element-search']", "UserService");
        page.click("[data-test='generate-btn']");
        page.click("[data-test='export-btn']");
        
        // Setup download handler
        Download download = page.waitForDownload(() -> {
            page.click("[data-test='download-btn']");
        });
        
        assertThat(download.suggestedFilename()).endsWith(".json");
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| Генерация | Все типы отчетов проверены |
| Навигация | Переходы по зависимостям |
| Экспорт | JSON и Markdown форматы |
| Скачивание | Файлы скачиваются корректно |

---

## Запуск тестов

```bash
# E2E тесты модуля report
gradlew.bat test --tests "twin.spring.e2e.report.*"