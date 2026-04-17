# 012-cluster-result — API

## Статус: complete

## Описание

Модели данных результата кластеризации для формирования clusters.json. Включает ClusterRecord (один кластер), ClusterMetricsRecord (метрики) и ClusterResult (полный результат).

## Классы

### 1. `spring.twin.cluster.ClusterRecord`

**Тип:** record (immutable DTO)

**Поля:**
- `String id` — идентификатор кластера (например, "cluster-1")
- `List<String> classes` — список FQCN классов в кластере (отсортированный)
- `ClusterMetricsRecord metrics` — метрики кластера

### 2. `spring.twin.cluster.ClusterResult`

**Тип:** record (immutable DTO)

**Поля:**
- `List<ClusterRecord> clusters` — список кластеров (отсортированный по id)
- `Map<String, Set<String>> penaltyEdges` — штрафные рёбра

### 3. `spring.twin.cluster.ClusterResultBuilder`

**Тип:** @Component

**Конструктор:**
- `ClusterResultBuilder()` — конструктор по умолчанию

**Методы:**
- `ClusterResult build(Partition partition, Map<String, Map<String, Set<LinkDetails>>> dependencyGraph, ClusterMetricsCalculator metricsCalculator, PenaltyEdgeDetector penaltyDetector)` — строит ClusterResult из разбиения. (1) получает communityMap из partition, (2) вычисляет метрики через metricsCalculator, (3) обнаруживает штрафные рёбра через penaltyDetector, (4) создаёт ClusterRecord для каждого сообщества, (5) собирает ClusterResult. **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

- Фича 004: partition-model (использует `Partition`)
- Фича 010: cluster-metrics (использует `ClusterMetricsRecord`, `ClusterMetricsCalculator`)
- Фича 011: penalty-edges (использует `PenaltyEdgeDetector`)

## Результат

Созданы классы `ClusterRecord`, `ClusterResult` и `ClusterResultBuilder` с заглушками методов.