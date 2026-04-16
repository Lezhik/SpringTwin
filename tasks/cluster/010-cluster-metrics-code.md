# 010-cluster-metrics — Реализация

## Статус: pending

## Описание

Реализация `ClusterMetricsRecord` и `ClusterMetricsCalculator`.

## Классы и методы

### `spring.twin.cluster.ClusterMetricsRecord`

**Тип:** record

**Реализация:**
- `ClusterMetricsRecord(double cohesion, double coupling)` — record-конструктор

### `spring.twin.cluster.ClusterMetricsCalculator`

**Тип:** @Component

**Реализация:**
- `ClusterMetricsCalculator()` — конструктор по умолчанию
- `ClusterMetricsRecord calculate(Set<String> clusterClasses, Map<String, Map<String, Set<LinkDetails>>> dependencyGraph)` — подсчитывает внутренние связи (оба конца в clusterClasses) и внешние связи (один конец в clusterClasses, другой вне). Cohesion = internal / (internal + external). Coupling = external / (internal + external). Если internal + external == 0, возвращает ClusterMetricsRecord(1.0, 0.0). **Метод должен быть аннотирован Javadoc на английском языке.**
- `Map<Integer, ClusterMetricsRecord> calculateAll(Map<Integer, Set<String>> communityMap, Map<String, Map<String, Set<LinkDetails>>> dependencyGraph)` — для каждого сообщества вызывает calculate() и собирает результаты в Map. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `calculate()` — итерирует по узлам кластера в dependencyGraph, разделяет связи на внутренние и внешние, вычисляет доли
2. `calculateAll()` — применяет calculate() для каждого сообщества