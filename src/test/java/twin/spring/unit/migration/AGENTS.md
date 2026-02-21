# AGENTS.md: Unit тестирование модуля Migration (Backend)

Правила и структура unit тестирования для модуля migration.

---

## Структура тестов

```
src/test/java/twin/spring/unit/migration/
└── service/
    └── MigrationServiceTest.java
```

**Примечание:** Модуль migration не требует E2E тестов, так как миграции выполняются автоматически при запуске приложения и проверяются на уровне integration тестов.

---

## Тестовые профили

### Использование MigrationTestProfile

```java
import twin.spring.profiles.migration.MigrationTestProfile;

@Test
void getMigrationHistory_shouldReturnHistory() {
    // Arrange - используем профиль для создания тестовых данных
    List<MigrationRecord> history = MigrationTestProfile.createMigrationHistory();
    
    // ...
}
```

Профиль находится в `src/test/java/twin/spring/profiles/migration/MigrationTestProfile.java`

---

## Unit тесты

### MigrationServiceTest.java

```java
/**
 * Unit тесты для MigrationService.
 * Тестирует бизнес-логику в изоляции с использованием Mockito.
 */
@ExtendWith(MockitoExtension.class)
class MigrationServiceTest {
    
    @Mock
    private MigrationRepository migrationRepository;
    
    @Mock
    private Neo4jClient neo4jClient;
    
    @InjectMocks
    private MigrationService migrationService;
    
    @Test
    void getMigrationHistory_shouldReturnHistory() {
        // Arrange
        List<MigrationRecord> history = MigrationTestProfile.createMigrationHistory();
        when(migrationRepository.findAllByOrderByExecutedAtDesc())
            .thenReturn(Flux.fromIterable(history));
        
        // Act & Assert
        StepVerifier.create(migrationService.getMigrationHistory())
            .expectNextCount(2)
            .verifyComplete();
    }
    
    @Test
    void isMigrationApplied_appliedMigration_returnsTrue() {
        // Arrange
        when(migrationRepository.findByVersion("v0001"))
            .thenReturn(Mono.just(MigrationTestProfile.createMigrationRecord()));
        
        // Act & Assert
        StepVerifier.create(migrationService.isMigrationApplied("v0001"))
            .expectNext(true)
            .verifyComplete();
    }
    
    @Test
    void isMigrationApplied_notAppliedMigration_returnsFalse() {
        // Arrange
        when(migrationRepository.findByVersion("v0003"))
            .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(migrationService.isMigrationApplied("v0003"))
            .expectNext(false)
            .verifyComplete();
    }
    
    @Test
    void recordMigration_validRecord_savesRecord() {
        // Arrange
        MigrationRecord record = MigrationTestProfile.createMigrationRecord();
        when(migrationRepository.save(any())).thenReturn(Mono.just(record));
        
        // Act & Assert
        StepVerifier.create(migrationService.recordMigration(record))
            .expectNext(record)
            .verifyComplete();
    }
    
    @Test
    void getLatestVersion_hasMigrations_returnsLatestVersion() {
        // Arrange
        List<MigrationRecord> history = MigrationTestProfile.createMigrationHistory();
        when(migrationRepository.findTopByOrderByVersionDesc())
            .thenReturn(Mono.just(history.get(0)));
        
        // Act & Assert
        StepVerifier.create(migrationService.getLatestVersion())
            .expectNext("v0001")
            .verifyComplete();
    }
    
    @Test
    void getLatestVersion_noMigrations_returnsEmpty() {
        // Arrange
        when(migrationRepository.findTopByOrderByVersionDesc())
            .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(migrationService.getLatestVersion())
            .verifyComplete();
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| Сервисы | Все публичные методы покрыты |
| Изоляция | Зависимости замоканы |
| Профили | Тестовые данные вынесены |

---

## Запуск тестов

```bash
# Unit тесты модуля migration
gradlew.bat test --tests "twin.spring.unit.migration.*"