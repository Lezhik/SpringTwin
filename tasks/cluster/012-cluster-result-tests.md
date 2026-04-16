# 012-cluster-result — Тесты

## Статус: pending

## Описание

Unit тесты для `ClusterRecord`, `ClusterResult` и `ClusterResultBuilder`.

## Тестируемые классы

- `spring.twin.cluster.ClusterRecord`
- `spring.twin.cluster.ClusterResult`
- `spring.twin.cluster.ClusterResultBuilder`

## Класс тестов

### `spring.twin.cluster.ClusterResultBuilderTest`

**Методы тестов:**

1. `testBuild_createsClusterResultWithClusters()` — build создаёт ClusterResult с кластерами
2. `testBuild_createsClusterResultWithPenaltyEdges()` — build создаёт ClusterResult с штрафными рёбрами
3. `testBuild_clustersSortedById()` — кластеры отсортированы по id
4. `testBuild_classesSortedAlphabetically()` — классы в каждом кластере отсортированы
5. `testBuild_emptyPartition_returnsEmptyClusters()` — пустое разбиение → пустой список кластеров
6. `testBuild_singleCommunity_singleCluster()` — одно сообщество → один кластер
7. `testBuild_clusterIdsFormattedCorrectly()` — id кластеров в формате "cluster-{номер}"

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Корректное построение результата | ClusterResult с кластерами и штрафными рёбрами |
| 2 | Сортировка | Кластеры по id, классы по алфавиту |
| 3 | Пустое разбиение | Пустой список кластеров |