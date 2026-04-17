# 013-cluster-json-writer — API

## Статус: complete

## Описание

Запись ClusterResult в JSON файл clusters.json в формате, определённом SPEC.

## Классы

### 1. `spring.twin.cluster.ClusterJsonWriter`

**Тип:** @Component

**Конструктор:**
- `ClusterJsonWriter()` — конструктор по умолчанию

**Методы:**
- `void write(ClusterResult clusterResult, Path outputFile)` — записывает ClusterResult в JSON файл. Формат: { "clusters": [...], "penaltyEdges": {...} }. Ключи в penaltyEdges отсортированы, значения отсортированы. Pretty-printed с 2-space indentation. UTF-8. Родительские директории создаются автоматически. **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

- Фича 012: cluster-result (использует `ClusterResult`, `ClusterRecord`, `ClusterMetricsRecord`)

## Результат

Создан класс `ClusterJsonWriter` с заглушками методов (методы бросают `UnsupportedOperationException`).