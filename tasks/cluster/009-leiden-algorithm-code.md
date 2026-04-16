# 009-leiden-algorithm — Реализация

## Статус: pending

## Описание

Реализация `LeidenAlgorithm`.

## Классы и методы

### `spring.twin.cluster.LeidenAlgorithm`

**Тип:** @Component

**Реализация:**
- `LeidenAlgorithm(LeidenLocalMove localMove, LeidenRefine refine, LeidenAggregate aggregate)` — сохраняет зависимости в поля
- `Map<String, Set<String>> buildSuperNodeMapping(Partition partition)` — использует `partition.toCommunityMap()` и конвертирует в Map<String, Set<String>>, где ключ = "community-{id}", значение = множество узлов. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Partition flattenPartition(Partition currentPartition, Partition aggregatePartition, Map<String, Set<String>> superNodeToNodes)` — для каждого суперузла определяет его сообщество из aggregatePartition, затем назначает это сообщество всем исходным узлам из superNodeToNodes. Возвращает новое разбиение на исходных узлах. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Partition cluster(Map<String, Map<String, Double>> graph, double resolution)` — делегирует в `cluster(graph, resolution, new Random().nextLong())`. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Partition cluster(Map<String, Map<String, Double>> graph, double resolution, long seed)` — основной алгоритм: (1) создаёт начальное Partition из узлов графа, (2) сохраняет маппинг superNodeToNodes, (3) цикл: localMove → если partition не изменился, break → refine → aggregate → обновить superNodeToNodes → создать новый partition для агрегированного графа → продолжить, (4) после выхода из цикла — flattenPartition для маппинга обратно на исходные узлы. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `cluster()` — итеративный процесс: local move → проверка сходимости → refine → aggregate → повтор
2. `buildSuperNodeMapping()` — строит соответствие суперузлов исходным узлам
3. `flattenPartition()` — переносит разбиение с агрегированного графа на исходные узлы