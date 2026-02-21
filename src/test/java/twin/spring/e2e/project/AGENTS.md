# AGENTS.md: E2E тестирование модуля Project

Правила и структура E2E тестирования для модуля project.

---

## Структура тестов

```
src/test/java/twin/spring/e2e/project/
├── ProjectManagementE2ETest.java
└── ProjectConfigurationE2ETest.java
```

---

## Тестовые профили

### Использование ProjectTestProfile

```java
import twin.spring.profiles.project.ProjectTestProfile;

@BeforeEach
void setUp() {
    projectTestProfile.seedTestData();
}
```

Профиль находится в `src/test/java/twin/spring/profiles/project/ProjectTestProfile.java`

---

## E2E тесты

### ProjectManagementE2ETest.java

```java
/**
 * E2E тесты для управления проектами.
 * Тестирует пользовательские сценарии с использованием Playwright.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestNeo4jConfig.class)
class ProjectManagementE2ETest {
    
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
    void shouldDisplayProjectList_whenPageLoads() {
        page.navigate("http://localhost:" + port + "/projects");
        
        assertThat(page.locator("[data-test='project-list']").isVisible()).isTrue();
        assertThat(page.locator("[data-test='project-card']").count()).isGreaterThanOrEqualTo(1);
    }
    
    @Test
    void shouldCreateNewProject_whenFormSubmitted() {
        page.navigate("http://localhost:" + port + "/projects");
        
        page.click("[data-test='btn-new-project']");
        page.fill("[data-test='input-project-name']", "My New Project");
        page.fill("[data-test='input-project-path']", "/path/to/project");
        page.click("[data-test='btn-save-project']");
        
        assertThat(page.locator("[data-test='notification']").textContent())
            .contains("Project created");
    }
    
    @Test
    void shouldDisplayProjectDetails_whenProjectClicked() {
        page.navigate("http://localhost:" + port + "/projects");
        
        page.click("[data-test='project-card']:first-child");
        
        assertThat(page.locator("[data-test='project-detail']").isVisible()).isTrue();
        assertThat(page.locator("[data-test='project-name']").textContent()).isNotEmpty();
    }
    
    @Test
    void shouldEditProject_whenEditButtonClicked() {
        page.navigate("http://localhost:" + port + "/projects");
        
        page.click("[data-test='project-card']:first-child");
        page.click("[data-test='btn-edit-project']");
        page.fill("[data-test='input-project-name']", "Updated Name");
        page.click("[data-test='btn-save-project']");
        
        assertThat(page.locator("[data-test='project-name']").textContent())
            .contains("Updated Name");
    }
    
    @Test
    void shouldDeleteProject_whenConfirmed() {
        page.navigate("http://localhost:" + port + "/projects");
        
        page.click("[data-test='project-card']:first-child");
        page.click("[data-test='btn-delete-project']");
        page.click("[data-test='btn-confirm-delete']");
        
        assertThat(page.locator("[data-test='notification']").textContent())
            .contains("Project deleted");
    }
    
    @Test
    void shouldShowValidationError_whenRequiredFieldsEmpty() {
        page.navigate("http://localhost:" + port + "/projects");
        
        page.click("[data-test='btn-new-project']");
        page.click("[data-test='btn-save-project']");
        
        assertThat(page.locator("[data-test='validation-error']").isVisible()).isTrue();
    }
}
```

### ProjectConfigurationE2ETest.java

```java
/**
 * E2E тесты для конфигурации проектов.
 * Тестирует сценарии настройки include/exclude пакетов.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestNeo4jConfig.class)
class ProjectConfigurationE2ETest {
    
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
    void shouldDisplayPackageConfiguration_whenProjectSelected() {
        page.navigate("http://localhost:" + port + "/projects");
        
        page.click("[data-test='project-card']:first-child");
        page.click("[data-test='btn-configure']");
        
        assertThat(page.locator("[data-test='package-config']").isVisible()).isTrue();
    }
    
    @Test
    void shouldAddIncludePackage_whenFormSubmitted() {
        page.navigate("http://localhost:" + port + "/projects");
        
        page.click("[data-test='project-card']:first-child");
        page.click("[data-test='btn-configure']");
        page.fill("[data-test='input-include-package']", "com.example.service");
        page.click("[data-test='btn-add-include']");
        
        assertThat(page.locator("[data-test='include-list']").textContent())
            .contains("com.example.service");
    }
    
    @Test
    void shouldAddExcludePackage_whenFormSubmitted() {
        page.navigate("http://localhost:" + port + "/projects");
        
        page.click("[data-test='project-card']:first-child");
        page.click("[data-test='btn-configure']");
        page.fill("[data-test='input-exclude-package']", "com.example.config");
        page.click("[data-test='btn-add-exclude']");
        
        assertThat(page.locator("[data-test='exclude-list']").textContent())
            .contains("com.example.config");
    }
    
    @Test
    void shouldRemovePackage_whenDeleteClicked() {
        page.navigate("http://localhost:" + port + "/projects");
        
        page.click("[data-test='project-card']:first-child");
        page.click("[data-test='btn-configure']");
        page.click("[data-test='include-item']:first-child [data-test='btn-remove']");
        
        assertThat(page.locator("[data-test='notification']").textContent())
            .contains("Package removed");
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| CRUD | Все операции проверены |
| Навигация | Переходы между страницами |
| Валидация | Сообщения об ошибках |
| Конфигурация | Include/exclude пакеты |

---

## Запуск тестов

```bash
# E2E тесты модуля project
gradlew.bat test --tests "twin.spring.e2e.project.*"