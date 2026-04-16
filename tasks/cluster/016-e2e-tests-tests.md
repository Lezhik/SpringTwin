# 016-e2e-tests — Unit тесты (сценарии E2E)

## Статус: pending

## Описание

Сценарии End-to-End тестов для команды `cluster`. Тесты принимают на вход параметры для метода main и проверяют выходной JSON файл.

## Тестируемый класс

`spring.twin.cluster.e2e.ClusterE2eTest`

## Класс тестов

`spring.twin.cluster.e2e.ClusterE2eTest`

## Методы тестов и сценарии

### 1. `shouldClusterSimpleGraph()`
**Сценарий:** Кластеризация простого графа с 3 классами в одном кластере. Входной dependencies.json содержит 3 класса с сильными связями. Проверка: выходной JSON содержит 1 кластер, все классы в нём, cohesion > 0.5, penaltyEdges пустой.

### 2. `shouldClusterTwoCommunities()`
**Сценарий:** Кластеризация графа с двумя чёткими сообществами. Входной dependencies.json содержит 2 группы по 3 класса с внутренними связями и 1 связью между группами. Проверка: выходной JSON содержит 2 кластера, penaltyEdges содержит межкластерную связь.

### 3. `shouldProduceValidClustersJsonStructure()`
**Сценарий:** Проверка структуры выходного JSON: наличие полей `clusters` и `penaltyEdges`, каждый кластер содержит `id`, `classes`, `metrics` с `cohesion` и `coupling`.

### 4. `shouldRespectResolutionParameter()`
**Сценарий:** Запуск с разным resolution (0.5 и 5.0). При низком resolution должно быть меньше кластеров, при высоком — больше. Проверка: количество кластеров при resolution=5.0 >= количества при resolution=0.5.

### 5. `shouldHandleSingleClassGraph()`
**Сценарий:** Граф из одного класса без связей. Проверка: выходной JSON содержит 1 кластер с 1 классом, cohesion=1.0, coupling=0.0, penaltyEdges пустой.

### 6. `shouldHandleEmptyGraph()`
**Сценарий:** Пустой dependencies.json (нет классов). Проверка: выходной JSON содержит пустой массив clusters и пустой penaltyEdges.

### 7. `shouldAssignSequentialClusterIds()`
**Сценарий:** Проверка что идентификаторы кластеров имеют формат `cluster-1`, `cluster-2`, ... и идут последовательно.

### 8. `shouldCalculateMetricsCorrectly()`
**Сценарий:** Проверка что cohesion и coupling в выходном JSON имеют корректные значения (0.0-1.0), cohesion + coupling <= 1.0 для каждого кластера.

### 9. `shouldDetectPenaltyEdgesBetweenClusters()`
**Сценарий:** Граф с двумя кластерами и явной связью между ними. Проверка: penaltyEdges содержит класс из первого кластера со ссылкой на класс из второго кластера.

### 10. `shouldProduceDeterministicResultsForSameSeed()`
**Сценарий:** Два запуска с одинаковыми входными данными должны давать одинаковое количество кластеров (алгоритм детерминирован при фиксированном seed).

## Подход к тестированию

- Тесты создают временный dependencies.json во временной директории
- Вызывают ClusterCommand.cluster() с параметрами как из CLI
- Читают выходной JSON файл и проверяют его структуру и содержимое
- Используют реальный Spring контекст (@SpringBootTest)

## Зависимости

- Фича 016: e2e-tests (API)
- Все фичи 001-015 должны быть реализованы