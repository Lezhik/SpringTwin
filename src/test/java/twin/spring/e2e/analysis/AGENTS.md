# AGENTS.md: E2E тестирование модуля Analysis

Правила и структура E2E тестирования для модуля analysis.

---

## Структура тестов

```
src/test/java/twin/spring/e2e/analysis/
└── AnalysisWorkflowE2ETest.java
```

---

## Тестовые профили

### Использование AnalysisTestProfile

```java
import twin.spring.profiles.analysis.AnalysisTestProfile;

@BeforeEach
void setUp() {
    analysisTestProfile.seedTestData();
}
```

Профиль находится в `src/test/java/twin/spring/profiles/analysis/AnalysisTestProfile.java`

---

## E2E тесты

### AnalysisWorkflowE2ETest.java

```java
/**
 * E2E тесты для процесса анализа проекта.
 * Тестирует пользовательские сценарии с использованием Playwright.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestNeo4jConfig.class)
class AnalysisWorkflowE2ETest {
    
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
    void shouldDisplayAnalysisPage_whenPageLoads() {
        page.navigate("http://localhost:" + port + "/analysis");
        
        assertThat(page.locator("[data-test='analysis-page']").isVisible()).isTrue();
    }
    
    @Test
    void shouldDisplayProjectSelector_whenPageLoads() {
        page.navigate("http://localhost:" + port + "/analysis");
        
        assertThat(page.locator("[data-test='project-selector']").isVisible()).isTrue();
    }
    
    @Test
    void shouldStartAnalysis_whenProjectSelected() {
        page.navigate("http://localhost:" + port + "/analysis");
        
        page.selectOption("[data-test='project-selector']", "test-project-id");
        page.click("[data-test='start-analysis-btn']");
        
        assertThat(page.locator("[data-test='analysis-status']").isVisible()).isTrue();
    }
    
    @Test
    void shouldDisplayProgress_whenAnalysisRunning() {
        page.navigate("http://localhost:" + port + "/analysis");
        
        page.selectOption("[data-test='project-selector']", "test-project-id");
        page.click("[data-test='start-analysis-btn']");
        
        assertThat(page.locator("[data-test='progress-bar']").isVisible()).isTrue();
    }
    
    @Test
    void shouldDisplayResults_whenAnalysisCompleted() {
        page.navigate("http://localhost:" + port + "/analysis");
        
        page.selectOption("[data-test='project-selector']", "test-project-id");
        page.click("[data-test='start-analysis-btn']");
        
        // Wait for analysis to complete
        page.waitForSelector("[data-test='analysis-result']", new Page.WaitForSelectorOptions()
            .setTimeout(30000));
        
        assertThat(page.locator("[data-test='analysis-result']").isVisible()).isTrue();
    }
    
    @Test
    void shouldDisplayStatistics_whenAnalysisCompleted() {
        page.navigate("http://localhost:" + port + "/analysis");
        
        page.selectOption("[data-test='project-selector']", "test-project-id");
        page.click("[data-test='start-analysis-btn']");
        
        page.waitForSelector("[data-test='analysis-result']", new Page.WaitForSelectorOptions()
            .setTimeout(30000));
        
        assertThat(page.locator("[data-test='classes-count']").textContent()).isNotEmpty();
        assertThat(page.locator("[data-test='methods-count']").textContent()).isNotEmpty();
        assertThat(page.locator("[data-test='endpoints-count']").textContent()).isNotEmpty();
    }
    
    @Test
    void shouldCancelAnalysis_whenCancelButtonClicked() {
        page.navigate("http://localhost:" + port + "/analysis");
        
        page.selectOption("[data-test='project-selector']", "test-project-id");
        page.click("[data-test='start-analysis-btn']");
        page.click("[data-test='cancel-analysis-btn']");
        
        assertThat(page.locator("[data-test='analysis-status']").textContent())
            .contains("Cancelled");
    }
    
    @Test
    void shouldShowError_whenAnalysisFails() {
        page.navigate("http://localhost:" + port + "/analysis");
        
        page.selectOption("[data-test='project-selector']", "invalid-project-id");
        page.click("[data-test='start-analysis-btn']");
        
        assertThat(page.locator("[data-test='error-message']").isVisible()).isTrue();
    }
    
    @Test
    void shouldNavigateToResults_whenViewResultsClicked() {
        page.navigate("http://localhost:" + port + "/analysis");
        
        page.selectOption("[data-test='project-selector']", "test-project-id");
        page.click("[data-test='start-analysis-btn']");
        
        page.waitForSelector("[data-test='analysis-result']", new Page.WaitForSelectorOptions()
            .setTimeout(30000));
        
        page.click("[data-test='view-results-btn']");
        
        assertThat(page.url()).contains("/architecture");
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| Запуск анализа | Анализ запускается |
| Прогресс | Прогресс отображается |
| Результаты | Результаты отображаются |
| Отмена | Анализ можно отменить |
| Ошибки | Ошибки обрабатываются |

---

## Запуск тестов

```bash
# E2E тесты модуля analysis
gradlew.bat test --tests "twin.spring.e2e.analysis.*"