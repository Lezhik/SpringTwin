# 002-dependency-reader — Реализация

## Статус: pending

## Описание

Реализация `DependencyReader`.

## Классы и методы

### `spring.twin.cluster.DependencyReader`

**Тип:** @Component

**Реализация:**
- `DependencyReader()` — конструктор по умолчанию
- `Map<String, Map<String, Set<LinkDetails>>> read(Path depsFile)` — использует `ObjectMapper` для десериализации JSON файла в `Map<String, Map<String, Set<LinkDetails>>>`. При ошибке чтения (файл не найден, невалидный JSON) оборачивает IOException в `UncheckedIOException`. Использует `TypeReference<Map<String, Map<String, Set<LinkDetails>>>>` для типобезопасной десериализации. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `read()` — создаёт ObjectMapper, читает файл по пути, десериализует в типизированную Map. Ошибки ввода-вывода и парсинга оборачиваются в UncheckedIOException