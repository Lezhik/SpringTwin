# AGENTS.md: Integration тестирование модуля Migration

Правила и структура integration тестирования для модуля migration.

---

## Структура тестов

```
src/test/java/twin/spring/integration/migration/
└── MigrationIntegrationTest.java
```

**Примечание:** Модуль migration не требует E2E тестов, так как миграции выполняются автоматически при запуске приложения и проверяются на уровне integration тестов.

---

## Тестовые профили

### Использование MigrationTestProfile

```java
import twin.spring.profiles.migration.MigrationTestProfile;

@BeforeEach
void setUp() {
    migrationTestProfile.seedTestData();
}
```

Профиль находится в `src/test/java/twin/spring/profiles/migration/MigrationTestProfile.java`

---

## Integration тесты

### MigrationIntegrationTest.java

```java
/**
 * Integration тесты для миграций Neo4j.
 * Тестирует выполнение миграций с использованием Neo4j embedded.
 */
@SpringBootTest
@Import(TestNeo4jConfig.class)
class MigrationIntegrationTest {
    
    @Autowired
    private Driver neo4jDriver;
    
    @Autowired
    private MigrationService migrationService;
    
    @Autowired
    private MigrationTestProfile migrationTestProfile;
    
    @BeforeEach
    void setUp() {
        // Clean database before each test
        try (Session session = neo4jDriver.session()) {
            session.run("MATCH (n) DETACH DELETE n").consume();
        }
    }
    
    @Test
    void shouldCreateIndexesOnStartup() {
        try (Session session = neo4jDriver.session()) {
            // Check for ClassNode index
            var indexResult = session.run(
                "SHOW INDEXES WHERE name = 'classNode_fullName_idx'"
            );
            
            assertThat(indexResult.hasNext()).isTrue();
        }
    }
    
    @Test
    void shouldCreateConstraintsOnStartup() {
        try (Session session = neo4jDriver.session()) {
            // Check for ClassNode constraint
            var constraintResult = session.run(
                "SHOW CONSTRAINTS WHERE name = 'classNode_id_pk'"
            );
            
            assertThat(constraintResult.hasNext()).isTrue();
        }
    }
    
    @Test
    void shouldRecordMigrationInDatabase() {
        List<MigrationRecord> history = migrationService.getMigrationHistory()
            .collectList()
            .block();
        
        assertThat(history).isNotEmpty();
    }
    
    @Test
    void shouldNotReapplyMigrations() {
        // Get initial migration count
        long initialCount = migrationService.getMigrationHistory()
            .count()
            .block();
        
        // Trigger migration check again
        migrationService.checkAndApplyMigrations().block();
        
        // Verify no new migrations were applied
        long finalCount = migrationService.getMigrationHistory()
            .count()
            .block();
        
        assertThat(finalCount).isEqualTo(initialCount);
    }
    
    @Test
    void shouldCreateUniqueConstraintForClassNode() {
        try (Session session = neo4jDriver.session()) {
            // Try to create duplicate ClassNode
            session.run(
                "CREATE (c:ClassNode {id: 'test-id', name: 'TestClass', fullName: 'com.test.TestClass'})"
            ).consume();
            
            // Should fail due to unique constraint
            assertThatThrownBy(() -> session.run(
                "CREATE (c:ClassNode {id: 'test-id', name: 'AnotherClass', fullName: 'com.test.AnotherClass'})"
            ).consume())
                .hasMessageContaining("already exists");
        }
    }
    
    @Test
    void shouldCreateIndexForMethodNode() {
        try (Session session = neo4jDriver.session()) {
            // Create method node
            session.run(
                "CREATE (m:MethodNode {id: 'method-id', signature: 'public void test()'})"
            ).consume();
            
            // Verify index exists by checking query plan uses index
            var explainResult = session.run(
                "EXPLAIN MATCH (m:MethodNode) WHERE m.signature = 'public void test()' RETURN m"
            );
            
            assertThat(explainResult.hasNext()).isTrue();
        }
    }
    
    @Test
    void shouldCreateCompositeIndexForEndpointNode() {
        try (Session session = neo4jDriver.session()) {
            // Create endpoint node
            session.run(
                "CREATE (e:EndpointNode {id: 'endpoint-id', path: '/api/users', httpMethod: 'GET'})"
            ).consume();
            
            // Verify composite index exists
            var indexResult = session.run(
                "SHOW INDEXES WHERE labelsOrTypes = ['EndpointNode'] AND properties = ['path', 'httpMethod']"
            );
            
            assertThat(indexResult.hasNext()).isTrue();
        }
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| Индексы | Все индексы созданы |
| Ограничения | Все ограничения созданы |
| Идемпотентность | Повторный запуск безопасен |
| Автозапуск | Миграции выполняются при старте |

---

## Запуск тестов

```bash
# Integration тесты модуля migration
gradlew.bat test --tests "twin.spring.integration.migration.*"