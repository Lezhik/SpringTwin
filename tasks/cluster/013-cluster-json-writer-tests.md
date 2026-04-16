# 013-cluster-json-writer — Тесты

## Статус: pending

## Описание

Unit тесты для `ClusterJsonWriter`.

## Тестируемые классы

- `spring.twin.cluster.ClusterJsonWriter`

## Класс тестов

### `spring.twin.cluster.ClusterJsonWriterTest`

**Методы тестов:**

1. `testWrite_validClusterResult_createsFile()` — запись создаёт файл
2. `testWrite_validClusterResult_correctJsonStructure()` — JSON содержит "clusters" и "penaltyEdges"
3. `testWrite_clustersHaveIdClassesMetrics()` — каждый кластер содержит id, classes, metrics
4. `testWrite_metricsHaveCohesionAndCoupling()` — metrics содержит cohesion и coupling
5. `testWrite_penaltyEdgesCorrectFormat()` — penaltyEdges в формате Map<String, List<String>>
6. `testWrite_emptyClusterResult_createsEmptyJson()` — пустой результат → {"clusters":[],"penaltyEdges":{}}
7. `testWrite_sortedKeys()` — ключи penaltyEdges отсортированы, значения отсортированы
8. `testWrite_createsParentDirectories()` — родительские директории создаются автоматически

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Валидный ClusterResult | Файл создан с корректной структурой JSON |
| 2 | Пустой результат | Пустые clusters и penaltyEdges |
| 3 | Сортировка | Ключи и значения отсортированы |
| 4 | Родительские директории | Создаются автоматически |