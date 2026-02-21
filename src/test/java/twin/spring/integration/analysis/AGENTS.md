# AGENTS.md: Integration тестирование модуля Analysis

Правила и структура integration тестирования для модуля analysis.

---

## Структура тестов

```
src/test/java/twin/spring/integration/analysis/
├── api/
│   └── AnalysisControllerIntegrationTest.java
└── repository/
    └── AnalysisRepositoryIntegrationTest.java
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

## Integration тесты

### AnalysisControllerIntegrationTest.java

```java
/**
 * Integration тесты для AnalysisController.
 * Тестирует REST API для запуска анализа проекта.
 */
@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestNeo4jConfig.class)
class AnalysisControllerIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private AnalysisTestProfile analysisTestProfile;
    
    @BeforeEach
    void setUp() {
        analysisTestProfile.seedTestData();
    }
    
    @Test
    void startAnalysis_validProjectId_returnsAnalysisResult() {
        String projectId = AnalysisTestProfile.DEFAULT_PROJECT_ID;
        
        webTestClient.post()
            .uri("/api/v1/analysis/start/{projectId}", projectId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(AnalysisResultResponse.class)
            .value(response -> {
                assertThat(response.getProjectId()).isEqualTo(projectId);
                assertThat(response.getStatus()).isEqualTo("COMPLETED");
            });
    }
    
    @Test
    void getAnalysisStatus_runningAnalysis_returnsStatus() {
        String analysisId = AnalysisTestProfile.DEFAULT_ANALYSIS_ID;
        
        webTestClient.get()
            .uri("/api/v1/analysis/{analysisId}/status", analysisId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(AnalysisStatusResponse.class)
            .value(response -> {
                assertThat(response.getAnalysisId()).isEqualTo(analysisId);
            });
    }
    
    @Test
    void getAnalysisResult_completedAnalysis_returnsResult() {
        String analysisId = AnalysisTestProfile.DEFAULT_ANALYSIS_ID;
        
        webTestClient.get()
            .uri("/api/v1/analysis/{analysisId}/result", analysisId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(AnalysisResultResponse.class)
            .value(response -> {
                assertThat(response.getClassesCount()).isGreaterThanOrEqualTo(0);
                assertThat(response.getMethodsCount()).isGreaterThanOrEqualTo(0);
            });
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| REST API | Все методы контроллера покрыты |
| Анализ | Процесс анализа проверен |
| Статусы | Все статусы анализа проверены |
| Профили | Тестовые данные вынесены |

---

## Запуск тестов

```bash
# Integration тесты модуля analysis
gradlew.bat test --tests "twin.spring.integration.analysis.*"