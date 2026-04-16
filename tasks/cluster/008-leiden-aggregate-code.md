# 008-leiden-aggregate — Реализация

## Статус: pending

## Описание

Реализация `LeidenAggregate`.

## Классы и методы

### `spring.twin.cluster.LeidenAggregate`

**Тип:** @Component

**Реализация:**
- `LeidenAggregate()` — конструктор по умолчанию
- `String toSuperNodeName(int communityId)` — возвращает `"community-" + communityId`. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Map<String, Map<String, Double>> aggregate(Map<String, Map<String, Double>> graph, Partition partition)` — (1) получает все сообщества из partition, (2) для каждого сообщества создаёт суперузел, (3) итерирует по всем рёбрам исходного графа: если оба конца в одном сообществе — добавляет вес к петле суперузла, если в разных — добавляет вес к ребру между соответствующими суперузлами, (4) возвращает агрегированный граф. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Partition createAggregatePartition(Map<String, Map<String, Double>> aggregatedGraph)` — создаёт Partition из ключей агрегированного графа, каждый суперузел в своём сообществе. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `toSuperNodeName()` — форматирование имени суперузла
2. `aggregate()` — проход по всем рёбрам, группировка по сообществам концов, суммирование весов
3. `createAggregatePartition()` — инициализация разбиения для следующей итерации алгоритма