# 012-cluster-result — Реализация

## Статус: complete

## Описание

Реализация `ClusterRecord`, `ClusterResult` и `ClusterResultBuilder`.

## Классы и методы

### `spring.twin.cluster.ClusterRecord`

**Тип:** record

**Реализация:**
- `ClusterRecord(String id, List<String> classes, ClusterMetricsRecord metrics)` — record-конструктор

### `spring.twin.cluster.ClusterResult`

**Тип:** record

**Реализация:**
- `ClusterResult(List<ClusterRecord> clusters, Map<String, Set<String>> penaltyEdges)` — record-конструктор

### `spring.twin.cluster.ClusterResultBuilder`

**Тип:** @Component

**Реализация:**
- `ClusterResultBuilder()` — конструктор по умолчанию
- `ClusterResult build(Partition partition, Map<String, Map<String, Set<LinkDetails>>> dependencyGraph, ClusterMetricsCalculator metricsCalculator, PenaltyEdgeDetector penaltyDetector)` — (1) получает communityMap через partition.toCommunityMap(), (2) вычисляет метрики через metricsCalculator.calculateAll(), (3) обнаруживает штрафные рёбра через penaltyDetector.detect(), (4) для каждого сообщества создаёт ClusterRecord с id="cluster-{номер}", отсортированным списком классов и метриками, (5) сортирует ClusterRecord по id, (6) собирает ClusterResult. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `build()` — оркестрация: communityMap → метрики → штрафные рёбра → ClusterRecord → ClusterResult