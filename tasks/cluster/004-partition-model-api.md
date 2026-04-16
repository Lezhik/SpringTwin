# 004-partition-model — API

## Статус: pending

## Описание

Модель данных разбиения графа на сообщества (кластеры). Partition хранит принадлежность узлов к сообществам и предоставляет методы для манипуляции.

## Классы

### 1. `spring.twin.cluster.Partition`

**Тип:** класс (mutable в рамках алгоритма)

**Поля:**
- `Map<String, Integer> nodeCommunity` — маппинг: имя узла → номер сообщества
- `int communityCount` — текущее количество сообществ

**Конструктор:**
- `Partition(Set<String> nodes)` — создаёт разбиение, где каждый узел в своём собственном сообществе (communityCount = nodes.size())
- `Partition(Map<String, Integer> nodeCommunity, int communityCount)` — создаёт разбиение из готового маппинга

**Методы:**
- `int communityOf(String node)` — возвращает номер сообщества для узла
- `void moveNode(String node, int newCommunity)` — перемещает узел в другое сообщество
- `Set<String> nodesInCommunity(int community)` — возвращает все узлы данного сообщества
- `Set<Integer> communities()` — возвращает множество всех номеров сообществ
- `Set<String> nodes()` — возвращает множество всех узлов
- `int communityCount()` — возвращает количество сообществ
- `boolean isEmpty()` — возвращает true если разбиение пустое
- `Partition copy()` — создаёт глубокую копию разбиения
- `Map<Integer, Set<String>> toCommunityMap()` — возвращает маппинг: номер сообщества → множество узлов

## Зависимости

Нет зависимостей от других фич cluster.

## Результат

Создан класс `Partition` с заглушками методов (методы возвращают null, 0 или бросают `UnsupportedOperationException`).