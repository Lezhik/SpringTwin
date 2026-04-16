# 003-graph-converter — Тесты

## Статус: complete

## Описание

Unit тесты для `GraphConverter`.

## Тестируемые классы

- `spring.twin.cluster.GraphConverter`

## Класс тестов

### `spring.twin.cluster.GraphConverterTest`

**Методы тестов:**

1. `testToUndirectedWeightedGraph_emptyGraph_returnsEmptyMap()` — пустой граф → пустая Map
2. `testToUndirectedWeightedGraph_singleNodeNoEdges_returnsNodeWithEmptyNeighbors()` — граф с одним узлом без рёбер
3. `testToUndirectedWeightedGraph_oneDirectionEdge_createsUndirectedEdge()` — направленное ребро A→B создаёт ненаправленное ребро A↔B
4. `testToUndirectedWeightedGraph_bidirectionalEdges_mergesWeights()` — рёбра A→B и B→A сливаются, вес = сумме
5. `testToUndirectedWeightedGraph_multipleLinkTypes_aggregatesWeight()` — несколько типов связей между A и B увеличивают вес ребра
6. `testToUndirectedWeightedGraph_selfReference_notIncluded()` — ссылки класса на себя не создают петель
7. `testCollectNodes_emptyGraph_returnsEmptySet()` — пустой граф → пустое множество
8. `testCollectNodes_includesKeysAndValues()` — узлы включают и ключи, и значения внутренних Map
9. `testCollectNodes_includesOnlyTargetNodes()` — узлы-цели, не являющиеся ключами внешней Map, тоже включаются
10. `testTotalEdgeWeight_emptyGraph_returnsZero()` — пустой граф → вес 0
11. `testTotalEdgeWeight_singleEdge_returnsWeight()` — одно ребро → его вес
12. `testTotalEdgeWeight_multipleEdges_returnsSum()` — несколько рёбер → сумма весов (каждое ребро один раз)

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Пустой граф | Пустая Map / нулевой вес |
| 2 | Однонаправленное ребро | Ненаправленное ребро с весом |
| 3 | Двунаправленные рёбра | Слияние в одно ребро с суммарным весом |
| 4 | Самореференция | Отсутствие петель |
| 5 | Все узлы собираются | Ключи + значения включены |