# 004-partition-model — Реализация

## Статус: complete

## Описание

Реализация `Partition`.

## Классы и методы

### `spring.twin.cluster.Partition`

**Тип:** класс (mutable)

**Реализация:**
- `Partition(Set<String> nodes)` — инициализирует `nodeCommunity` так, что каждый узел получает уникальный номер сообщества (0, 1, 2, ...). `communityCount` = nodes.size(). **Метод должен быть аннотирован Javadoc на английском языке.**
- `Partition(Map<String, Integer> nodeCommunity, int communityCount)` — инициализирует из готового маппинга. Копирует Map через `new HashMap<>()`. **Метод должен быть аннотирован Javadoc на английском языке.**
- `int communityOf(String node)` — возвращает `nodeCommunity.get(node)`. **Метод должен быть аннотирован Javadoc на английском языке.**
- `void moveNode(String node, int newCommunity)` — обновляет `nodeCommunity.put(node, newCommunity)`. Если newCommunity >= communityCount, обновляет communityCount = newCommunity + 1. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Set<String> nodesInCommunity(int community)` — фильтрует nodeCommunity по значению community, возвращает множество узлов. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Set<Integer> communities()` — возвращает `new HashSet<>(nodeCommunity.values())`. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Set<String> nodes()` — возвращает `nodeCommunity.keySet()`. **Метод должен быть аннотирован Javadoc на английском языке.**
- `int communityCount()` — возвращает `communityCount`. **Метод должен быть аннотирован Javadoc на английском языке.**
- `boolean isEmpty()` — возвращает `nodeCommunity.isEmpty()`. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Partition copy()` — создаёт новый Partition с копией nodeCommunity и тем же communityCount. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Map<Integer, Set<String>> toCommunityMap()` — группирует узлы по номеру сообщества в `Map<Integer, Set<String>>`. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Конструктор из Set — каждому узлу присваивается уникальный ID сообщества
2. moveNode — перемещает узел, при необходимости расширяет communityCount
3. copy — глубокая копия для безопасного использования в итерациях алгоритма
4. toCommunityMap — удобная группировка для построения результата кластеризации