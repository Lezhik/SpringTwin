# 011-penalty-edges — Реализация

## Статус: complete

## Описание

Реализация `PenaltyEdgeDetector`.

## Классы и методы

### `spring.twin.cluster.PenaltyEdgeDetector`

**Тип:** @Component

**Реализация:**
- `PenaltyEdgeDetector()` — конструктор по умолчанию
- `Map<String, Set<String>> detect(Partition partition, Map<String, Map<String, Set<LinkDetails>>> dependencyGraph)` — итерирует по всем узлам dependencyGraph, для каждого узла проверяет все его зависимости. Если `partition.communityOf(source)` ≠ `partition.communityOf(target)`, добавляет target в Set штрафных рёбер для source. Пропускает узлы, отсутствующие в partition. Возвращает TreeMap для сортировки ключей. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `detect()` — проход по всем направленным рёбрам, фильтрация по принадлежности к разным сообществам