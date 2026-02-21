# AGENTS.md: Integration тестирование модуля App

Правила и структура integration тестирования для корневого модуля app.

---

## Структура тестов

```
src/test/java/twin/spring/integration/app/
└── api/
    └── AppControllerIntegrationTest.java
```

---

## Тестовые профили

### Использование AppTestProfile

```java
import twin.spring.profiles.app.AppTestProfile;

@BeforeEach
void setUp() {
    appTestProfile.seedTestData();
}
```

Профиль находится в `src/test/java/twin/spring/profiles/app/AppTestProfile.java`

---

## Integration тесты

### AppControllerIntegrationTest.java

```java
/**
 * Integration тесты для AppController.
 * Тестирует REST API корневого приложения.
 */
@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestNeo4jConfig.class)
class AppControllerIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Test
    void healthCheck_shouldReturnOk() {
        webTestClient.get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("UP");
    }
    
    @Test
    void getNavigation_shouldReturnMenuItems() {
        webTestClient.get()
            .uri("/api/v1/app/navigation")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(NavigationItemResponse.class)
            .value(items -> {
                assertThat(items).isNotEmpty();
                assertThat(items.get(0).getName()).isNotNull();
                assertThat(items.get(0).getPath()).isNotNull();
            });
    }
    
    @Test
    void getAppInfo_shouldReturnApplicationInfo() {
        webTestClient.get()
            .uri("/api/v1/app/info")
            .exchange()
            .expectStatus().isOk()
            .expectBody(AppInfoResponse.class)
            .value(info -> {
                assertThat(info.getName()).isEqualTo("SpringTwin");
                assertThat(info.getVersion()).isNotNull();
            });
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| Health | Health check работает |
| Navigation | Навигация возвращается |
| Info | Информация о приложении возвращается |
| Профили | Тестовые данные вынесены |

---

## Запуск тестов

```bash
# Integration тесты модуля app
gradlew.bat test --tests "twin.spring.integration.app.*"