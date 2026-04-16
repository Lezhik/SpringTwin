# 009-leiden-algorithm — Тесты

## Статус: pending

## Описание

Unit тесты для `LeidenAlgorithm`.

## Тестируемые классы

- `spring.twin.cluster.LeidenAlgorithm`

## Класс тестов

### `spring.twin.cluster.LeidenAlgorithmTest`

**Методы тестов:**

1. `testCluster_emptyGraph_returnsEmptyPartition()` — пустой граф → пустое разбиение
2. `testCluster_singleNode_returnsSingleNodeInOneCommunity()` — один узел → одно сообщество
3. `testCluster_twoConnectedNodes_sameCommunity()` — два связанных узла → одно сообщество
4. `testCluster_disconnectedNodes_differentCommunities()` — несвязанные узлы → разные сообщества
5. `testCluster_threeNodesTwoClusters_correctGrouping()` — три узла с двумя кластерами
6. `testCluster_preservesAllNodes()` — все узлы присутствуют в результате
7. `testCluster_withSeed_deterministicResult()` — одинаковый seed → одинаковый результат
8. `testCluster_differentSeeds_mayDiffer()` — разные seed → возможно разный результат
9. `testBuildSuperNodeMapping_correctMapping()` — корректный маппинг суперузлов на исходные узлы
10. `testFlattenPartition_correctMapping()` — корректный обратный маппинг разбиения
11. `testCluster_highResolution_moreClusters()` — высокий resolution → больше кластеров
12. `testCluster_lowResolution_fewerClusters()` — низкий resolution → меньше кластеров

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Пустой граф | Пустое разбиение |
| 2 | Связанные узлы | Одно сообщество |
| 3 | Несвязанные узлы | Разные сообщества |
| 4 | Детерминизм | Одинаковый seed → одинаковый результат |
| 5 | Параметр resolution | Влияет на количество кластеров |