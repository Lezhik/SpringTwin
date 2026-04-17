# 008-leiden-aggregate — API

## Статус: complete

## Описание

Фаза агрегации алгоритма Лейдена. Узлы одного сообщества объединяются в суперузлы. Рёбра между суперузлами получают вес = сумма весов исходных рёбер. Петли (рёбра внутри сообщества) добавляются как вес петли суперузла.

## Классы

### 1. `spring.twin.cluster.LeidenAggregate`

**Тип:** @Component

**Конструктор:**
- `LeidenAggregate()` — конструктор по умолчанию

**Методы:**
- `Map<String, Map<String, Double>> aggregate(Map<String, Map<String, Double>> graph, Partition partition)` — создаёт агрегированный граф. Для каждого сообщества создаётся суперузел с именем "community-{id}". Рёбра между суперузлами получают вес = сумма весов рёбер между узлами соответствующих сообществ. Рёбра внутри сообщества становятся петлями суперузла. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Partition createAggregatePartition(Map<String, Map<String, Double>> aggregatedGraph)` — создаёт разбиение для агрегированного графа, где каждый суперузел в своём сообществе. **Метод должен быть аннотирован Javadoc на английском языке.**
- `String toSuperNodeName(int communityId)` — конвертирует номер сообщества в имя суперузла "community-{id}". **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

- Фича 003: graph-converter (использует тип `Map<String, Map<String, Double>>`)
- Фича 004: partition-model (использует `Partition`)

## Результат

Создан класс `LeidenAggregate` с заглушками методов (методы возвращают null или бросают `UnsupportedOperationException`).