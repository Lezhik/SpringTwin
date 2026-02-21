# AGENTS.md: E2E тестирование модуля Architecture

Правила и структура E2E тестирования для модуля architecture.

---

## Структура тестов

```
src/test/java/twin/spring/e2e/architecture/
├── ClassNodeE2ETest.java
└── GraphVisualizationE2ETest.java
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

## E2E тесты

### ClassNodeE2ETest.java

```java
/**
 * E2E тесты для работы с узлами классов.
 * Тестирует пользовательские сценарии с использованием Playwright.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestNeo4jConfig.class)
class ClassNodeE2ETest {
    
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
    void shouldDisplayClassList_whenPageLoads() {
        page.navigate("http://localhost:" + port + "/architecture");
        
        assertThat(page.locator("[data-test='class-list']").isVisible()).isTrue();
        assertThat(page.locator("[data-test='class-item']").count()).isGreaterThanOrEqualTo(1);
    }
    
    @Test
    void shouldDisplayClassDetails_whenClassSelected() {
        page.navigate("http://localhost:" + port + "/architecture");
        
        page.click("[data-test='class-item']:first-child");
        
        assertThat(page.locator("[data-test='class-detail']").isVisible()).isTrue();
        assertThat(page.locator("[data-test='class-name']").textContent()).isNotEmpty();
    }
    
    @Test
    void shouldFilterByLabel_whenLabelSelected() {
        page.navigate("http://localhost:" + port + "/architecture");
        
        page.selectOption("[data-test='label-filter']", "Service");
        
        assertThat(page.locator("[data-test='class-item']").count()).isGreaterThanOrEqualTo(1);
    }
    
    @Test
    void shouldSearchClasses_whenSearchQueryEntered() {
        page.navigate("http://localhost:" + port + "/architecture");
        
        page.fill("[data-test='class-search']", "UserService");
        
        assertThat(page.locator("[data-test='class-item']").count()).isGreaterThanOrEqualTo(1);
    }
    
    @Test
    void shouldDisplayDependencies_whenClassSelected() {
        page.navigate("http://localhost:" + port + "/architecture");
        
        page.click("[data-test='class-item']:first-child");
        
        assertThat(page.locator("[data-test='dependency-list']").isVisible()).isTrue();
    }
    
    @Test
    void shouldNavigateToDependency_whenDependencyClicked() {
        page.navigate("http://localhost:" + port + "/architecture");
        
        page.click("[data-test='class-item']:first-child");
        page.click("[data-test='dependency-item']:first-child");
        
        assertThat(page.locator("[data-test='class-detail']").isVisible()).isTrue();
    }
}
```

### GraphVisualizationE2ETest.java

```java
/**
 * E2E тесты для визуализации графа.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestNeo4jConfig.class)
class GraphVisualizationE2ETest {
    
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
    void shouldDisplayGraph_whenPageLoads() {
        page.navigate("http://localhost:" + port + "/architecture/graph");
        
        assertThat(page.locator("[data-test='graph-container']").isVisible()).isTrue();
    }
    
    @Test
    void shouldDisplayNodes_whenGraphRendered() {
        page.navigate("http://localhost:" + port + "/architecture/graph");
        
        page.waitForSelector("[data-test='graph-node']");
        
        assertThat(page.locator("[data-test='graph-node']").count()).isGreaterThanOrEqualTo(1);
    }
    
    @Test
    void shouldHighlightNode_whenNodeClicked() {
        page.navigate("http://localhost:" + port + "/architecture/graph");
        
        page.waitForSelector("[data-test='graph-node']");
        page.click("[data-test='graph-node']:first-child");
        
        assertThat(page.locator("[data-test='graph-node'].selected").count()).isEqualTo(1);
    }
    
    @Test
    void shouldZoomIn_whenZoomButtonClicked() {
        page.navigate("http://localhost:" + port + "/architecture/graph");
        
        page.click("[data-test='zoom-in-btn']");
        
        // Verify zoom changed (could check transform attribute)
    }
    
    @Test
    void shouldZoomOut_whenZoomButtonClicked() {
        page.navigate("http://localhost:" + port + "/architecture/graph");
        
        page.click("[data-test='zoom-out-btn']");
        
        // Verify zoom changed
    }
    
    @Test
    void shouldResetView_whenResetButtonClicked() {
        page.navigate("http://localhost:" + port + "/architecture/graph");
        
        page.click("[data-test='zoom-in-btn']");
        page.click("[data-test='reset-view-btn']");
        
        // Verify view reset to default
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| Список классов | Классы отображаются |
| Детали класса | Информация отображается |
| Фильтрация | Фильтр по меткам работает |
| Поиск | Поиск работает |
| Граф | Визуализация работает |

---

## Запуск тестов

```bash
# E2E тесты модуля architecture
gradlew.bat test --tests "twin.spring.e2e.architecture.*"