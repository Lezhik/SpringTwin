# 010-cluster-metrics — API

## Статус: complete

## Описание

Вычисление метрик cohesion и coupling для кластеров. Cohesion — доля внутренних связей кластера от общего числа связей. Coupling — доля внешних связей кластера от общего числа связей.

## Классы

### 1. `spring.twin.cluster.ClusterMetricsRecord`

**Тип:** record (immutable DTO)

**Поля:**
- `double cohesion` — доля внутренних связей (0.0–1.0)
- `double coupling` — доля внешних связей (0.0–1.0)

### 2. `spring.twin.cluster.ClusterMetricsCalculator`

**Тип:** @Component

**Конструктор:**
- `ClusterMetricsCalculator()` — конструктор по умолчанию

**Методы:**
- `ClusterMetricsRecord calculate(Set<String> clusterClasses, Map<String, Map<String, Set<LinkDetails>>> dependencyGraph)` — вычисляет метрики для одного кластера. Cohesion = внутренние_связи / (внутренние_связи + внешние_связи). Coupling = внешние_связи / (внутренние_связи + внешние_связи). Если нет связей — cohesion = 1.0, coupling = 0.0. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Map<Integer, ClusterMetricsRecord> calculateAll(Map<Integer, Set<String>> communityMap, Map<String, Map<String, Set<LinkDetails>>> dependencyGraph)` — вычисляет метрики для всех кластеров. **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

Нет зависимостей от других фич cluster. Использует `spring.twin.scan.LinkDetails` из scan-bytecode.

## Результат

Созданы классы `ClusterMetricsRecord` и `ClusterMetricsCalculator` с заглушками методов.