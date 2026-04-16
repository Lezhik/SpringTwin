# 009-leiden-algorithm — API

## Статус: pending

## Описание

Полный алгоритм Лейдена — оркестрация трёх фаз (local move → refine → aggregate) с итерациями до сходимости. Также отвечает за маппинг результатов агрегированного графа обратно на исходные узлы.

## Классы

### 1. `spring.twin.cluster.LeidenAlgorithm`

**Тип:** @Component

**Конструктор:**
- `LeidenAlgorithm(LeidenLocalMove localMove, LeidenRefine refine, LeidenAggregate aggregate)` — внедрение зависимостей фаз алгоритма

**Методы:**
- `Partition cluster(Map<String, Map<String, Double>> graph, double resolution)` — выполняет полный алгоритм Лейдена. Создаёт начальное разбиение (каждый узел в своём сообществе), затем итеративно: (1) local move, (2) refine, (3) aggregate. Если после local move разбиение не изменилось — сходимость, алгоритм останавливается. Иначе продолжает на агрегированном графе. После сходимости маппит результаты обратно на исходные узлы. Seed задаётся случайно через `new Random()`. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Partition cluster(Map<String, Map<String, Double>> graph, double resolution, long seed)` — вариант с фиксированным seed для воспроизводимости. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Partition flattenPartition(Partition currentPartition, Partition aggregatePartition, Map<String, Set<String>> superNodeToNodes)` — маппит разбиение агрегированного графа обратно на исходные узлы. Для каждого суперузла берёт его сообщество из aggregatePartition и назначает это сообщество всем исходным узлам. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Map<String, Set<String>> buildSuperNodeMapping(Partition partition)` — строит маппинг: имя суперузла → множество исходных узлов. **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

- Фича 006: leiden-local-move
- Фича 007: leiden-refine
- Фича 008: leiden-aggregate

## Результат

Создан класс `LeidenAlgorithm` с заглушками методов (методы возвращают null или бросают `UnsupportedOperationException`).