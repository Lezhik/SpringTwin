# 007-leiden-refine — Тесты

## Статус: pending

## Описание

Unit тесты для `LeidenRefine`.

## Тестируемые классы

- `spring.twin.cluster.LeidenRefine`

## Класс тестов

### `spring.twin.cluster.LeidenRefineTest`

**Методы тестов:**

1. `testIsCommunityStable_stableCommunity_returnsTrue()` — стабильное сообщество (удаление любого узла ухудшает модулярность)
2. `testIsCommunityStable_unstableCommunity_returnsFalse()` — нестабильное сообщество (есть узел, удаление которого улучшает модулярность)
3. `testRefine_stableCommunity_noSplit()` — стабильное сообщество не разделяется
4. `testRefine_unstableCommunity_splitsIntoSubcommunities()` — нестабильное сообщество разделяется
5. `testRefine_emptyGraph_returnsOriginalPartition()` — пустой граф → исходное разбиение
6. `testRefine_singleNodeCommunity_noSplit()` — сообщество из одного узла не разделяется
7. `testRefine_preservesAllNodes()` — все узлы сохраняются после уточнения
8. `testRefine_twoStableCommunities_noChange()` — два стабильных сообщества не изменяются

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Стабильное сообщество | Не разделяется |
| 2 | Нестабильное сообщество | Разделяется на подкластеры |
| 3 | Пустой граф | Исходное разбиение |
| 4 | Все узлы сохраняются | Тот же набор узлов |