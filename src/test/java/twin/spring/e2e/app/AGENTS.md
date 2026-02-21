# AGENTS.md: E2E тестирование модуля App

Правила и структура E2E тестирования для корневого модуля app.

---

## Структура тестов

```
src/test/java/twin/spring/e2e/app/
└── NavigationE2ETest.java
```

---

## E2E тесты

### NavigationE2ETest.java

```java
/**
 * E2E тесты для навигации приложения.
 * Тестирует пользовательские сценарии с использованием Playwright.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestNeo4jConfig.class)
class NavigationE2ETest {
    
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
    void shouldDisplayHomePage_whenApplicationLoads() {
        page.navigate("http://localhost:" + port + "/");
        
        assertThat(page.locator("[data-test='home-page']").isVisible()).isTrue();
        assertThat(page.locator("[data-test='app-title']").textContent())
            .contains("SpringTwin");
    }
    
    @Test
    void shouldDisplayNavigation_whenPageLoads() {
        page.navigate("http://localhost:" + port + "/");
        
        assertThat(page.locator("[role='navigation']").isVisible()).isTrue();
        assertThat(page.locator("[data-test='nav-item']").count()).isGreaterThanOrEqualTo(4);
    }
    
    @Test
    void shouldNavigateToProjects_whenNavLinkClicked() {
        page.navigate("http://localhost:" + port + "/");
        
        page.click("[data-test='nav-projects']");
        
        assertThat(page.url()).contains("/projects");
        assertThat(page.locator("[data-test='projects-page']").isVisible()).isTrue();
    }
    
    @Test
    void shouldNavigateToArchitecture_whenNavLinkClicked() {
        page.navigate("http://localhost:" + port + "/");
        
        page.click("[data-test='nav-architecture']");
        
        assertThat(page.url()).contains("/architecture");
        assertThat(page.locator("[data-test='architecture-page']").isVisible()).isTrue();
    }
    
    @Test
    void shouldNavigateToReports_whenNavLinkClicked() {
        page.navigate("http://localhost:" + port + "/");
        
        page.click("[data-test='nav-reports']");
        
        assertThat(page.url()).contains("/report");
        assertThat(page.locator("[data-test='report-page']").isVisible()).isTrue();
    }
    
    @Test
    void shouldNavigateToMcp_whenNavLinkClicked() {
        page.navigate("http://localhost:" + port + "/");
        
        page.click("[data-test='nav-mcp']");
        
        assertThat(page.url()).contains("/mcp");
        assertThat(page.locator("[data-test='mcp-page']").isVisible()).isTrue();
    }
    
    @Test
    void shouldDisplaySidebar_whenDesktopView() {
        page.navigate("http://localhost:" + port + "/");
        page.setViewportSize(1280, 720);
        
        assertThat(page.locator("[data-test='sidebar']").isVisible()).isTrue();
    }
    
    @Test
    void shouldHideSidebar_whenMobileView() {
        page.navigate("http://localhost:" + port + "/");
        page.setViewportSize(375, 667);
        
        assertThat(page.locator("[data-test='sidebar']").isVisible()).isFalse();
    }
    
    @Test
    void shouldToggleSidebar_whenMenuButtonClicked() {
        page.navigate("http://localhost:" + port + "/");
        page.setViewportSize(375, 667);
        
        page.click("[data-test='menu-toggle']");
        
        assertThat(page.locator("[data-test='sidebar']").isVisible()).isTrue();
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| Главная | Страница загружается |
| Навигация | Все пункты меню работают |
| Адаптивность | Sidebar скрывается на мобильных |
| Переключение | Sidebar открывается/закрывается |

---

## Запуск тестов

```bash
# E2E тесты модуля app
gradlew.bat test --tests "twin.spring.e2e.app.*"