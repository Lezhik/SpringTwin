# 016-e2e-tests — Реализация

## Статус: pending

## Описание

Реализация End-to-End тестов для команды `cluster`. Тесты принимают на вход параметры для метода main и проверяют выходной JSON файл.

## Классы и методы

### `spring.twin.cluster.e2e.ClusterE2eTest`

**Аннотации класса:** `@SpringBootTest`

**Поля:**
- `@TempDir Path tempDir` — временная директория для входных и выходных файлов
- `@Autowired ClusterCommand clusterCommand` — CLI команда для вызова
- `@Autowired ObjectMapper objectMapper` — для чтения JSON

**Методы:**

#### `shouldClusterSimpleGraph()` → `void`
- Создаёт временный dependencies.json с 3 классами: A→B, B→C, C→A (сильные связи)
- Вызывает `clusterCommand.cluster(depsPath, outputPath, "1.5")`
- Читает выходной JSON как Map
- Проверяет: clusters.size() >= 1, все классы присутствуют, cohesion > 0.5, penaltyEdges пустой или содержит только внутренние связи
- **Метод должен быть аннотирован Javadoc на английском языке.**

#### `shouldClusterTwoCommunities()` → `void`
- Создаёт временный dependencies.json с 2 группами: {A1→A2, A2→A3, A3→A1} и {B1→B2, B2→B3, B3→B1} и одной связью A1→B1
- Вызывает `clusterCommand.cluster(depsPath, outputPath, "1.5")`
- Проверяет: clusters.size() >= 2, penaltyEdges содержит связь между кластерами
- **Метод должен быть аннотирован Javadoc на английском языке.**

#### `shouldProduceValidClustersJsonStructure()` → `void`
- Создаёт минимальный dependencies.json
- Вызывает `clusterCommand.cluster(depsPath, outputPath, "1.5")`
- Читает выходной JSON и проверяет структуру: наличие `clusters` (Array), `penaltyEdges` (Object), каждый кластер имеет `id` (String), `classes` (Array), `metrics` (Object с `cohesion` и `coupling`)
- **Метод должен быть аннотирован Javadoc на английском языке.**

#### `shouldRespectResolutionParameter()` → `void`
- Создаёт dependencies.json с 6 классами в 2 группах
- Запускает с resolution=0.5 и resolution=5.0
- Проверяет: clusterCount(5.0) >= clusterCount(0.5)
- **Метод должен быть аннотирован Javadoc на английском языке.**

#### `shouldHandleSingleClassGraph()` → `void`
- Создаёт dependencies.json с 1 классом без связей
- Проверяет: 1 кластер, 1 класс, cohesion=1.0, coupling=0.0, penaltyEdges пустой
- **Метод должен быть аннотирован Javadoc на английском языке.**

#### `shouldHandleEmptyGraph()` → `void`
- Создаёт пустой dependencies.json: `{}`
- Проверяет: clusters пустой, penaltyEdges пустой
- **Метод должен быть аннотирован Javadoc на английском языке.**

#### `shouldAssignSequentialClusterIds()` → `void`
- Создаёт dependencies.json с несколькими кластерами
- Проверяет: все id имеют формат `cluster-N`, N начинается с 1 и идёт последовательно
- **Метод должен быть аннотирован Javadoc на английском языке.**

#### `shouldCalculateMetricsCorrectly()` → `void`
- Создаёт dependencies.json с известной структурой
- Проверяет: для каждого кластера 0.0 <= cohesion <= 1.0, 0.0 <= coupling <= 1.0
- **Метод должен быть аннотирован Javadoc на английском языке.**

#### `shouldDetectPenaltyEdgesBetweenClusters()` → `void`
- Создаёт dependencies.json с двумя группами и явной межгрупповой связью
- Проверяет: penaltyEdges содержит ключ-класс из одного кластера со значением-классом из другого
- **Метод должен быть аннотирован Javadoc на английском языке.**

#### `shouldProduceDeterministicResultsForSameSeed()` → `void`
- Создаёт dependencies.json
- Запускает дважды с одинаковыми параметрами
- Проверяет: одинаковое количество кластеров
- **Метод должен быть аннотирован Javadoc на английском языке.**

## Вспомогательный метод

#### `writeDependenciesJson(Path file, Map<String, Map<String, Set<Map<String, String>>>> deps)` → `void`
- Записывает Map в JSON файл через ObjectMapper
- **Метод должен быть аннотирован Javadoc на английском языке.**

#### `readClustersJson(Path file)` → `Map<String, Object>`
- Читает JSON файл и возвращает как Map
- **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

- Все фичи 001-015 должны быть реализованы