# 004-partition-model — Тесты

## Статус: pending

## Описание

Unit тесты для `Partition`.

## Тестируемые классы

- `spring.twin.cluster.Partition`

## Класс тестов

### `spring.twin.cluster.PartitionTest`

**Методы тестов:**

1. `testConstructor_eachNodeInOwnCommunity()` — при создании каждый узел в своём сообществе
2. `testConstructor_emptyNodes_createsEmptyPartition()` — пустое множество узлов → пустое разбиение
3. `testCommunityOf_returnsCorrectCommunity()` — communityOf возвращает корректный номер
4. `testMoveNode_changesCommunity()` — moveNode изменяет принадлежность узла
5. `testMoveNode_updatesCommunityCount()` — при перемещении в новое сообщество communityCount увеличивается
6. `testNodesInCommunity_returnsCorrectNodes()` — nodesInCommunity возвращает все узлы сообщества
7. `testNodesInCommunity_emptyCommunity_returnsEmptySet()` — несуществующее сообщество → пустое множество
8. `testCommunities_returnsAllCommunityIds()` — communities возвращает все номера сообществ
9. `testNodes_returnsAllNodes()` — nodes возвращает все узлы
10. `testCommunityCount_returnsCorrectCount()` — communityCount корректен
11. `testIsEmpty_emptyPartition_returnsTrue()` — isEmpty для пустого разбиения
12. `testIsEmpty_nonEmptyPartition_returnsFalse()` — isEmpty для непустого разбиения
13. `testCopy_createsDeepCopy()` — копия независима от оригинала
14. `testToCommunityMap_returnsCorrectMapping()` — toCommunityMap корректно группирует узлы

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Создание разбиения | Каждый узел в своём сообществе |
| 2 | Перемещение узла | Узел в новом сообществе, счётчик обновлён |
| 3 | Копирование | Независимая копия |
| 4 | Группировка по сообществам | Корректный маппинг |