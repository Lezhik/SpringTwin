# 006-leiden-local-move — Реализация

## Статус: pending

## Описание

Реализация `LeidenLocalMove`.

## Классы и методы

### `spring.twin.cluster.LeidenLocalMove`

**Тип:** @Component

**Реализация:**
- `LeidenLocalMove()` — конструктор по умолчанию
- `Set<Integer> neighborCommunities(String node, Map<String, Map<String, Double>> graph, Partition partition)` — получает соседей узла из графа, для каждого соседа определяет его сообщество через `partition.communityOf()`, собирает в Set. Включает текущее сообщество узла. **Метод должен быть аннотирован Javadoc на английском языке.**
- `Partition move(Map<String, Map<String, Double>> graph, Partition partition, double resolution, Random random)` — основной цикл: (1) создаёт копию partition, (2) получает список узлов и перемешивает через `Collections.shuffle(nodes, random)`, (3) для каждого узла вычисляет соседние сообщества, (4) для каждого соседнего сообщества вычисляет ΔQ через `ModularityCalculator.deltaModularity()`, (5) если максимальный ΔQ > 0, перемещает узел, (6) повторяет пока хотя бы один узел перемещён за проход. Возвращает финальное разбиение. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `neighborCommunities()` — собирает уникальные номера сообществ соседей узла
2. `move()` — итеративный процесс: перемешивание узлов → вычисление лучшего хода → перемещение → повтор до сходимости