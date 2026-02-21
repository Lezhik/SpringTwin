# AGENTS.md: Integration тестирование модуля Report

Правила и структура integration тестирования для модуля report.

---

## Структура тестов

```
src/test/java/twin/spring/integration/report/
├── api/
│   └── ReportControllerIntegrationTest.java
└── repository/
    └── ReportRepositoryIntegrationTest.java
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

## Integration тесты

### ReportControllerIntegrationTest.java

```java
/**
 * Integration тесты для ReportController.
 * Тестирует REST API с использованием WebTestClient и Neo4j embedded.
 */
@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestNeo4jConfig.class)
class ReportControllerIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private ReportTestProfile reportTestProfile;
    
    @BeforeEach
    void setUp() {
        reportTestProfile.seedTestData();
    }
    
    @Test
    void explainEndpoint_shouldReturnReport() {
        String endpointId = UUID.randomUUID().toString();
        
        webTestClient.get()
            .uri("/api/v1/explain/endpoint/{id}", endpointId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(EndpointReportResponse.class)
            .value(response -> {
                assertThat(response.getEndpoint().getPath()).isEqualTo("/api/users");
                assertThat(response.getCallChain()).isNotEmpty();
            });
    }
    
    @Test
    void explainClass_shouldReturnReport() {
        String classId = UUID.randomUUID().toString();
        
        webTestClient.get()
            .uri("/api/v1/explain/class/{id}", classId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(ClassReportResponse.class)
            .value(response -> {
                assertThat(response.getClassInfo().getName()).isNotEmpty();
                assertThat(response.getMethods()).isNotEmpty();
            });
    }
    
    @Test
    void explainClassByName_shouldReturnReport() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v1/explain/class")
                .queryParam("fullName", "com.example.service.UserService")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(ClassReportResponse.class);
    }
    
    @Test
    void explainMethod_shouldReturnReport() {
        String methodId = UUID.randomUUID().toString();
        
        webTestClient.get()
            .uri("/api/v1/explain/method/{id}", methodId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(MethodReportResponse.class)
            .value(response -> {
                assertThat(response.getMethodInfo().getName()).isNotEmpty();
                assertThat(response.getParentClass()).isNotNull();
            });
    }
    
    @Test
    void exportForLlm_shouldReturnExport() {
        String classId = UUID.randomUUID().toString();
        
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v1/export/class/{id}/llm", classId)
                .queryParam("format", "json")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(LlmContextExportResponse.class)
            .value(response -> {
                assertThat(response.getContextType()).isEqualTo("CLASS");
                assertThat(response.getStructuredData()).isNotNull();
            });
    }
    
    @Test
    void exportForLlm_shouldSupportMarkdown() {
        String classId = UUID.randomUUID().toString();
        
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v1/export/class/{id}/llm", classId)
                .queryParam("format", "markdown")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .value(content -> {
                assertThat(content).contains("# UserService");
            });
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| REST API | Все методы контроллера покрыты |
| Экспорт | JSON и Markdown форматы проверены |
| Отчеты | Все типы отчетов проверены |
| Профили | Тестовые данные вынесены |

---

## Запуск тестов

```bash
# Integration тесты модуля report
gradlew.bat test --tests "twin.spring.integration.report.*"