# 010-cluster-metrics — Тесты

## Статус: pending

## Описание

Unit тесты для `ClusterMetricsRecord` и `ClusterMetricsCalculator`.

## Тестируемые классы

- `spring.twin.cluster.ClusterMetricsRecord`
- `spring.twin.cluster.ClusterMetricsCalculator`

## Класс тестов

### `spring.twin.cluster.ClusterMetricsCalculatorTest`

**Методы тестов:**

1. `testCalculate_isolatedCluster_returnsHighCohesionZeroCoupling()` — кластер без внешних связей → cohesion = 1.0, coupling = 0.0
2. `testCalculate_clusterWithOnlyExternalLinks_returnsZeroCohesionHighCoupling()` — кластер только с внешними связями → cohesion = 0.0, coupling = 1.0
3. `testCalculate_mixedCluster_returnsCorrectRatio()` — смешанный кластер → корректные доли
4. `testCalculate_emptyCluster_returnsDefaultMetrics()` — пустой кластер → cohesion = 1.0, coupling = 0.0
5. `testCalculate_clusterWithNoLinks_returnsDefaultMetrics()` — кластер без связей → cohesion = 1.0, coupling = 0.0
6. `testCalculateAll_multipleClusters_returnsMetricsForAll()` — несколько кластеров → метрики для каждого
7. `testCohesionAndCoupling_sumToOne()` — cohesion + coupling = 1.0 для кластера со связями

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Изолированный кластер | cohesion=1.0, coupling=0.0 |
| 2 | Только внешние связи | cohesion=0.0, coupling=1.0 |
| 3 | Смешанные связи | Корректные доли, сумма=1.0 |
| 4 | Пустой кластер | Значения по умолчанию |