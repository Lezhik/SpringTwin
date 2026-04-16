# 011-penalty-edges — Тесты

## Статус: pending

## Описание

Unit тесты для `PenaltyEdgeDetector`.

## Тестируемые классы

- `spring.twin.cluster.PenaltyEdgeDetector`

## Класс тестов

### `spring.twin.cluster.PenaltyEdgeDetectorTest`

**Методы тестов:**

1. `testDetect_noCrossClusterEdges_returnsEmptyMap()` — все зависимости внутри кластеров → пустая Map
2. `testDetect_crossClusterEdges_detected()` — связь между разными кластерами → штрафное ребро
3. `testDetect_multipleCrossClusterEdges_allDetected()` — несколько межкластерных связей → все обнаружены
4. `testDetect_emptyGraph_returnsEmptyMap()` — пустой граф → пустая Map
5. `testDetect_singleCluster_noPenaltyEdges()` — один кластер → нет штрафных рёбер
6. `testDetect_bidirectionalCrossCluster_bothDirections()` — A→B и B→A из разных кластеров → оба направления в penaltyEdges

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Нет межкластерных связей | Пустая Map |
| 2 | Межкластерная связь | Обнаружена |
| 3 | Несколько связей | Все обнаружены |
| 4 | Двунаправленные связи | Оба направления |