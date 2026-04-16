# 005-modularity-calculator — Тесты

## Статус: pending

## Описание

Unit тесты для `ModularityCalculator`.

## Тестируемые классы

- `spring.twin.cluster.ModularityCalculator`

## Класс тестов

### `spring.twin.cluster.ModularityCalculatorTest`

**Методы тестов:**

1. `testNodeDegree_nodeWithNoEdges_returnsZero()` — узел без рёбер → степень 0
2. `testNodeDegree_nodeWithEdges_returnsSumOfWeights()` — узел с рёбрами → сумма весов
3. `testCommunityDegree_singleNodeCommunity_returnsNodeDegree()` — сообщество из одного узла → степень узла
4. `testCommunityDegree_multiNodeCommunity_returnsSumOfDegrees()` — сообщество из нескольких узлов → сумма степеней
5. `testEdgesInsideCommunity_noInternalEdges_returnsZero()` — сообщество без внутренних рёбер → 0
6. `testEdgesInsideCommunity_withInternalEdges_returnsSum()` — сообщество с внутренними рёбрами → сумма весов
7. `testEdgesToCommunity_nodeNotConnectedToCommunity_returnsZero()` — нет связей с сообществом → 0
8. `testEdgesToCommunity_nodeConnectedToCommunity_returnsSum()` — есть связи → сумма весов рёбер
9. `testCalculateModularity_allNodesInOneCommunity_returnsValue()` — все узлы в одном сообществе → вычислимое значение
10. `testCalculateModularity_eachNodeInOwnCommunity_returnsNegativeValue()` — каждый узел отдельно → отрицательная модулярность (для resolution > 0)
11. `testCalculateModularity_optimalPartition_higherThanRandom()` — оптимальное разбиение имеет более высокую модулярность
12. `testDeltaModularity_movingToBetterCommunity_positiveDelta()` — перемещение в лучшее сообщество → положительный ΔQ
13. `testDeltaModularity_movingToWorseCommunity_negativeDelta()` — перемещение в худшее сообщество → отрицательный ΔQ
14. `testDeltaModularity_stayingInSameCommunity_zeroDelta()` — перемещение в текущее сообщество → ΔQ ≈ 0
15. `testCalculateModularity_emptyGraph_returnsZero()` — пустой граф → модулярность 0

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Степень узла | Корректная сумма весов |
| 2 | Внутренние рёбра сообщества | Сумма весов без двойного учёта |
| 3 | Модулярность полного графа | Вычислимое значение |
| 4 | Прирост модулярности | Положительный для лучшего хода |
| 5 | Пустой граф | Нулевая модулярность |