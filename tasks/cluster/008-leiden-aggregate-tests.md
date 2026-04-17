# 008-leiden-aggregate — Тесты

## Статус: complete

## Описание

Unit тесты для `LeidenAggregate`.

## Тестируемые классы

- `spring.twin.cluster.LeidenAggregate`

## Класс тестов

### `spring.twin.cluster.LeidenAggregateTest`

**Методы тестов:**

1. `testToSuperNodeName_returnsCorrectFormat()` — toSuperNodeName(5) → "community-5"
2. `testAggregate_emptyGraph_returnsEmptyMap()` — пустой граф → пустой агрегированный граф
3. `testAggregate_allNodesInOneCommunity_createsSingleSuperNode()` — все узлы в одном сообществе → один суперузел с петлёй
4. `testAggregate_twoCommunities_createsTwoSuperNodes()` — два сообщества → два суперузла с ребром между ними
5. `testAggregate_edgeWeightAggregated_correctSum()` — вес ребра между суперузлами = сумма весов исходных рёбер
6. `testAggregate_internalEdgesBecomeSelfLoops()` — рёбра внутри сообщества становятся петлями суперузла
7. `testAggregate_noEdgesBetweenCommunities_noInterCommunityEdge()` — нет рёбер между сообществами → нет ребра между суперузлами
8. `testCreateAggregatePartition_eachSuperNodeInOwnCommunity()` — каждый суперузел в своём сообществе
9. `testAggregate_preservesTotalEdgeWeight()` — суммарный вес рёбер сохраняется

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Пустой граф | Пустой агрегированный граф |
| 2 | Одно сообщество | Один суперузел с петлёй |
| 3 | Два сообщества | Два суперузла с межсообщественным ребром |
| 4 | Агрегация весов | Сумма весов исходных рёбер |
| 5 | Сохранение суммарного веса | Тот же totalEdgeWeight |