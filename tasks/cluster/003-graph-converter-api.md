# 003-graph-converter — API

## Статус: complete

## Описание

Конвертация направленного графа зависимостей (из dependencies.json) в ненаправленный взвешенный граф для алгоритма Лейдена. Ненаправленный граф представляется как Map<String, Map<String, Double>>, где ключ — узел, значение — Map соседей с весами рёбер.

## Классы

### 1. `spring.twin.cluster.GraphConverter`

**Тип:** @Component

**Конструктор:**
- `GraphConverter()` — конструктор по умолчанию

**Методы:**
- `Map<String, Map<String, Double>> toUndirectedWeightedGraph(Map<String, Map<String, Set<LinkDetails>>> dependencyGraph)` — конвертирует направленный граф зависимостей в ненаправленный взвешенный граф. Для каждой пары A→B и/или B→A создаётся ненаправленное ребро с весом = количество уникальных типов связей между классами. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Set<String> collectNodes(Map<String, Map<String, Set<LinkDetails>>> dependencyGraph)` — собирает все уникальные узлы графа (ключи + все значения внутренних Map). **Метод должен быть аннотирован Javadoc на английском языке.**
- `double totalEdgeWeight(Map<String, Map<String, Double>> graph)` — вычисляет суммарный вес всех рёбер (каждое ребро учитывается один раз). **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

Нет зависимостей от других фич cluster. Использует `spring.twin.scan.LinkDetails` из scan-bytecode.

## Результат

Создан класс `GraphConverter` с заглушками методов (методы возвращают null или бросают `UnsupportedOperationException`).