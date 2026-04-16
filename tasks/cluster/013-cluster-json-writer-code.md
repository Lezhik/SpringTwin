# 013-cluster-json-writer — Реализация

## Статус: pending

## Описание

Реализация `ClusterJsonWriter`.

## Классы и методы

### `spring.twin.cluster.ClusterJsonWriter`

**Тип:** @Component

**Реализация:**
- `ClusterJsonWriter()` — конструктор по умолчанию
- `void write(ClusterResult clusterResult, Path outputFile)` — (1) создаёт ObjectMapper с INDENT_OUTPUT, (2) конвертирует ClusterResult в промежуточную структуру для сериализации: Map с ключами "clusters" (List of Map) и "penaltyEdges" (Map<String, List<String>>), (3) для каждого ClusterRecord создаёт Map с "id", "classes" (отсортированный List), "metrics" (Map с "cohesion" и "coupling"), (4) penaltyEdges конвертирует Set в отсортированный List, использует TreeMap для сортировки ключей, (5) создаёт родительские директории, (6) записывает JSON. Бросает `UncheckedIOException` при ошибке. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `write()` — конвертация ClusterResult в сериализуемую структуру, сортировка, запись JSON