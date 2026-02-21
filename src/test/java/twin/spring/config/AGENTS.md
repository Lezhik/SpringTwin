# AGENTS.md: Конфигурация тестов

Данный документ содержит правила и стандарты для конфигурации тестов в проекте SpringTwin.

---

## Назначение

Конфигурационные классы тестов обеспечивают:

- **Neo4j Embedded** - настройку in-memory базы данных для тестов
- **Playwright** - конфигурацию браузера для E2E тестов
- **Тестовые бины** - специальные бины для тестового контекста
- **Профили** - настройки для различных тестовых сценариев

---

## Структура

### Организация директории

```
src/test/java/twin/spring/config/
├── AGENTS.md                    # Этот файл
├── TestNeo4jConfig.java         # Конфигурация Neo4j Embedded
└── PlaywrightConfiguration.java # Конфигурация Playwright
```

---

## TestNeo4jConfig

### Назначение

Конфигурация Neo4j Embedded для использования в Integration и E2E тестах.

### Шаблон

```java
package twin.spring.config;

import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.neo4j.config.ReactiveNeo4jConfiguration;

/**
 * Конфигурация Neo4j Embedded для тестов.
 * 
 * <p>Обеспечивает in-memory Neo4j базу данных для Integration и E2E тестов.
 * База данных создается в памяти и уничтожается после завершения тестов.</p>
 * 
 * <p>Использование:</p>
 * <pre>
 * {@literal @}SpringBootTest
 * {@literal @}Import(TestNeo4jConfig.class)
 * class MyIntegrationTest {
 *     // ...
 * }
 * </pre>
 */
@TestConfiguration
public class TestNeo4jConfig extends ReactiveNeo4jConfiguration {
    
    private static final String NEO4J_URI = "bolt://localhost:7687";
    private static final String NEO4J_USER = "neo4j";
    private static final String NEO4J_PASSWORD = "password";
    
    /**
     * Создает драйвер Neo4j для подключения к embedded базе.
     * 
     * @return Neo4j driver
     */
    @Bean
    @Primary
    @Override
    public Driver neo4jDriver() {
        // Для embedded in-memory режима
        return GraphDatabase.driver(NEO4J_URI);
    }
}
```

### Использование

```java
// В Integration тестах
@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestNeo4jConfig.class)
class ProjectControllerIntegrationTest {
    // ...
}

// В E2E тестах
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestNeo4jConfig.class, PlaywrightConfiguration.class})
class ProjectManagementE2ETest {
    // ...
}
```

---

## PlaywrightConfiguration

### Назначение

Конфигурация Playwright для E2E тестирования.

### Шаблон

```java
package twin.spring.config;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * Конфигурация Playwright для E2E тестов.
 * 
 * <p>Обеспечивает настройку браузера для E2E тестирования.
 * По умолчанию используется Chromium в headless режиме.</p>
 * 
 * <p>Использование:</p>
 * <pre>
 * {@literal @}SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
 * {@literal @}Import({TestNeo4jConfig.class, PlaywrightConfiguration.class})
 * class MyE2ETest {
 *     {@literal @}Autowired
 *     private Playwright playwright;
 *     
 *     {@literal @}Autowired
 *     private Browser browser;
 *     // ...
 * }
 * </pre>
 */
@TestConfiguration
public class PlaywrightConfiguration {
    
    /**
     * Создает экземпляр Playwright.
     * Scope prototype для создания нового экземпляра на каждый тест.
     * 
     * @return Playwright instance
     */
    @Bean
    @Scope("prototype")
    public Playwright playwright() {
        return Playwright.create();
    }
    
    /**
     * Создает браузер для E2E тестов.
     * По умолчанию Chromium в headless режиме.
     * 
     * @param playwright Playwright instance
     * @return Browser instance
     */
    @Bean
    @Scope("prototype")
    public Browser browser(Playwright playwright) {
        return playwright.chromium().launch(
            new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setSlowMo(100) // Небольшая задержка для стабильности
        );
    }
}
```

### Использование

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestNeo4jConfig.class, PlaywrightConfiguration.class})
class ProjectManagementE2ETest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private Playwright playwright;
    
    @Autowired
    private Browser browser;
    
    private Page page;
    
    @BeforeEach
    void setUp() {
        page = browser.newPage();
    }
    
    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
    
    @Test
    void shouldDisplayProjectList() {
        page.navigate("http://localhost:" + port + "/projects");
        assertThat(page.locator("[data-test='project-list']").isVisible()).isTrue();
    }
}
```

---

## Дополнительные конфигурации

### TestProfileConfig

Конфигурация для активации тестового профиля:

```java
package twin.spring.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import twin.spring.profiles.project.ProjectTestProfile;
import twin.spring.profiles.architecture.ClassNodeTestProfile;

/**
 * Конфигурация для импорта тестовых профилей.
 */
@TestConfiguration
@Import({
    ProjectTestProfile.class,
    ClassNodeTestProfile.class
    // Другие профили...
})
public class TestProfileConfig {
}
```

### TestWebConfig

Конфигурация для WebTestClient:

```java
package twin.spring.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Конфигурация WebTestClient для тестирования REST API.
 */
@TestConfiguration
public class TestWebConfig {
    
    @Bean
    public WebTestClient webTestClient() {
        return WebTestClient.bindToServer().build();
    }
}
```

---

## Аннотации для тестов

### Базовые аннотации

| Аннотация | Описание |
|-----------|----------|
| `@SpringBootTest` | Полный Spring контекст |
| `@AutoConfigureWebTestClient` | Автонастройка WebTestClient |
| `@Import` | Импорт конфигураций |
| `@TestConfiguration` | Класс конфигурации тестов |

### Для Integration тестов

```java
@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestNeo4jConfig.class)
class MyIntegrationTest { }
```

### Для E2E тестов

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestNeo4jConfig.class, PlaywrightConfiguration.class})
class MyE2ETest { }
```

### Для Unit тестов

Unit тесты не требуют Spring контекста и конфигураций:

```java
@ExtendWith(MockitoExtension.class)
class MyUnitTest { }
```

---

## Профили Spring

### Тестовый профиль

Используйте `application-test.yaml` для тестовых настроек:

```yaml
# src/test/resources/application-test.yaml
spring:
  profiles:
    active: test
  
  neo4j:
    uri: bolt://localhost:7687
    authentication:
      username: neo4j
      password: password
  
  data:
    neo4j:
      database: neo4j

logging:
  level:
    root: WARN
    twin.spring: DEBUG
```

### Активация профиля

```java
@SpringBootTest
@ActiveProfiles("test")
@Import(TestNeo4jConfig.class)
class MyIntegrationTest { }
```

---

## Best Practices

### 1. Использовать @Import для конфигураций

```java
// Хорошо - явный импорт
@SpringBootTest
@Import(TestNeo4jConfig.class)
class MyIntegrationTest { }

// Плохо - неявная конфигурация
@SpringBootTest
class MyIntegrationTest { }
```

### 2. Prototype scope для ресурсов

```java
// Хорошо - новый экземпляр на каждый тест
@Bean
@Scope("prototype")
public Browser browser(Playwright playwright) {
    return playwright.chromium().launch();
}
```

### 3. Очистка ресурсов

```java
@AfterEach
void tearDown() {
    // Всегда закрываем ресурсы
    if (page != null) page.close();
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
}
```

### 4. Использовать RANDOM_PORT для E2E

```java
// Хорошо - случайный порт избегает конфликтов
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyE2ETest {
    @LocalServerPort
    private int port;
}

// Плохо - фиксированный порт может конфликтовать
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MyE2ETest { }
```

---

## Связанные документы

- [`src/test/java/AGENTS.md`](../../AGENTS.md) - Общие правила тестирования
- [`src/test/java/twin/spring/profiles/AGENTS.md`](../profiles/AGENTS.md) - Тестовые профили
- [`src/test/java/twin/spring/integration/AGENTS.md`](../integration/AGENTS.md) - Integration тесты
- [`src/test/java/twin/spring/e2e/AGENTS.md`](../e2e/AGENTS.md) - E2E тесты