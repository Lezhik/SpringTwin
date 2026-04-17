# 006-leiden-local-move — Тесты

## Статус: complete

## Описание

Unit тесты для `LeidenLocalMove`.

## Тестируемые классы

- `spring.twin.cluster.LeidenLocalMove`

## Класс тестов

### `spring.twin.cluster.LeidenLocalMoveTest`

**Методы тестов:**

1. `testNeighborCommunities_nodeWithNeighbors_returnsAllNeighborCommunities()` — узел с соседями в разных сообществах
2. `testNeighborCommunities_isolatedNode_returnsOwnCommunity()` — изолированный узел → только своё сообщество
3. `testMove_singleIteration_nodesMoveToBetterCommunity()` — за одну итерацию узлы перемещаются в лучшие сообщества
4. `testMove_converges_stopsWhenNoImprovement()` — алгоритм останавливается когда нет улучшений
5. `testMove_emptyGraph_returnsOriginalPartition()` — пустой граф → исходное разбиение
6. `testMove_singleNode_returnsOriginalPartition()` — один узел → исходное разбиение
7. `testMove_twoConnectedNodes_sameCommunity()` — два связанных узла → одно сообщество
8. `testMove_threeNodes_twoClusters()` — три узла с сильными внутренними связями → два кластера
9. `testMove_preservesAllNodes()` — после перемещения все узлы на месте
10. `testMove_deterministicWithSameSeed()` — одинаковый seed → одинаковый результат

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Соседние сообщества | Все сообщества соседей |
| 2 | Сходимость | Остановка при отсутствии улучшений |
| 3 | Пустой граф | Исходное разбиение |
| 4 | Связанные узлы | Объединение в одно сообщество |
| 5 | Детерминизм | Одинаковый seed → одинаковый результат |