# 003-graph-converter — Реализация

## Статус: complete

## Описание

Реализация `GraphConverter`.

## Классы и методы

### `spring.twin.cluster.GraphConverter`

**Тип:** @Component

**Реализация:**
- `GraphConverter()` — конструктор по умолчанию
- `Map<String, Map<String, Double>> toUndirectedWeightedGraph(Map<String, Map<String, Set<LinkDetails>>> dependencyGraph)` — проходит по всем парам (source, target) направленного графа. Для каждой пары увеличивает вес ребра source↔target на размер Set<LinkDetails> (количество типов связей). Пропускает петли (source == target). Результат — симметричная Map: если есть ребро A↔B с весом w, то graph[A][B] == w и graph[B][A] == w. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Set<String> collectNodes(Map<String, Map<String, Set<LinkDetails>>> dependencyGraph)` — собирает все ключи внешней Map и все ключи внутренних Map в единый Set. **Метод должен быть аннотирован Javadoc на английском языке.**
- `double totalEdgeWeight(Map<String, Map<String, Double>> graph)` — суммирует веса всех рёбер, учитывая каждое ребро один раз (для узла A учитываются только рёбра к узлам B, где B > A в лексикографическом порядке, чтобы избежать двойного учёта). **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `toUndirectedWeightedGraph()` — итерирует по всем направленным рёбрам, агрегирует вес по ненаправленным парам, пропускает петли
2. `collectNodes()` — объединяет множество ключей и множество значений в один Set
3. `totalEdgeWeight()` — суммирует уникальные рёбра (каждое ребро один раз)