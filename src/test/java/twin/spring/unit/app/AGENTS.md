# AGENTS.md: Unit тестирование модуля App (Backend)

Правила и структура unit тестирования для корневого модуля app.

---

## Структура тестов

```
src/test/java/twin/spring/unit/app/
├── service/
│   └── AppServiceTest.java
└── mapper/
    └── AppMapperTest.java
```

---

## Тестовые профили

### Использование AppTestProfile

```java
import twin.spring.profiles.app.AppTestProfile;

@Test
void getNavItems_shouldReturnNavItems() {
    // Arrange - используем профиль для создания тестовых данных
    List<NavItem> navItems = AppTestProfile.createNavItems();
    
    // ...
}
```

Профиль находится в `src/test/java/twin/spring/profiles/app/AppTestProfile.java`

---

## Unit тесты

### AppServiceTest.java

```java
/**
 * Unit тесты для AppService.
 * Тестирует бизнес-логику в изоляции с использованием Mockito.
 */
@ExtendWith(MockitoExtension.class)
class AppServiceTest {
    
    @Mock
    private AppRepository appRepository;
    
    @Mock
    private AppMapper appMapper;
    
    @InjectMocks
    private AppService appService;
    
    @Test
    void getNavItems_shouldReturnNavItems() {
        // Arrange
        List<NavItem> expected = AppTestProfile.createNavItems();
        when(appRepository.findNavItems()).thenReturn(Flux.fromIterable(expected));
        
        // Act
        StepVerifier.create(appService.getNavItems())
            .expectNextCount(3)
            .verifyComplete();
    }
    
    @Test
    void getAppConfig_shouldReturnConfig() {
        // Arrange
        AppConfig expected = AppTestProfile.createAppConfig();
        when(appRepository.findConfig()).thenReturn(Mono.just(expected));
        
        // Act & Assert
        StepVerifier.create(appService.getAppConfig())
            .expectNext(expected)
            .verifyComplete();
    }
    
    @Test
    void getHomeMetadata_shouldReturnMetadata() {
        // Arrange
        HomeMetadata expected = AppTestProfile.createHomeMetadata();
        when(appRepository.findHomeMetadata()).thenReturn(Mono.just(expected));
        
        // Act & Assert
        StepVerifier.create(appService.getHomeMetadata())
            .expectNext(expected)
            .verifyComplete();
    }
    
    @Test
    void getStats_shouldReturnAggregatedStats() {
        // Arrange
        AppStats expected = AppStats.builder()
            .projectsCount(10)
            .classesCount(500)
            .endpointsCount(50)
            .build();
        when(appRepository.findStats()).thenReturn(Mono.just(expected));
        
        // Act & Assert
        StepVerifier.create(appService.getStats())
            .expectNext(expected)
            .verifyComplete();
    }
}
```

### AppMapperTest.java

```java
/**
 * Unit тесты для AppMapper.
 */
class AppMapperTest {
    
    private AppMapper mapper = new AppMapper();
    
    @Test
    void toNavItemResponse_validEntity_returnsResponse() {
        // Arrange
        NavItem navItem = NavItem.builder()
            .id("nav-home")
            .label("Home")
            .path("/")
            .icon("home")
            .order(1)
            .build();
        
        // Act
        NavItemResponse response = mapper.toNavItemResponse(navItem);
        
        // Assert
        assertThat(response.getId()).isEqualTo(navItem.getId());
        assertThat(response.getLabel()).isEqualTo(navItem.getLabel());
        assertThat(response.getPath()).isEqualTo(navItem.getPath());
    }
    
    @Test
    void toAppConfigResponse_validEntity_returnsResponse() {
        // Arrange
        AppConfig config = AppTestProfile.createAppConfig();
        
        // Act
        AppConfigResponse response = mapper.toAppConfigResponse(config);
        
        // Assert
        assertThat(response.getAppName()).isEqualTo(config.getAppName());
        assertThat(response.getVersion()).isEqualTo(config.getVersion());
    }
    
    @Test
    void toHomeMetadataResponse_validEntity_returnsResponse() {
        // Arrange
        HomeMetadata metadata = AppTestProfile.createHomeMetadata();
        
        // Act
        HomeMetadataResponse response = mapper.toHomeMetadataResponse(metadata);
        
        // Assert
        assertThat(response.getTitle()).isEqualTo(metadata.getTitle());
        assertThat(response.getDescription()).isEqualTo(metadata.getDescription());
    }
}
```

---

## Критерии приемки

| Критерий | Описание |
|----------|----------|
| Сервисы | Все публичные методы покрыты |
| Мапперы | Все методы преобразования покрыты |
| Изоляция | Зависимости замоканы |
| Профили | Тестовые данные вынесены |

---

## Запуск тестов

```bash
# Unit тесты модуля app
gradlew.bat test --tests "twin.spring.unit.app.*"