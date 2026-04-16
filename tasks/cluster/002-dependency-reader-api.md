# 002-dependency-reader — API

## Статус: pending

## Описание

Чтение файла `dependencies.json` и десериализация в структуру `Map<String, Map<String, Set<LinkDetails>>>`.

## Классы

### 1. `spring.twin.cluster.DependencyReader`

**Тип:** @Component

**Конструктор:**
- `DependencyReader()` — конструктор по умолчанию

**Методы:**
- `Map<String, Map<String, Set<LinkDetails>>> read(Path depsFile)` — читает JSON файл зависимостей и возвращает структуру графа. Бросает `UncheckedIOException` если файл не найден или невалиден. **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

Нет зависимостей от других фич cluster. Использует `spring.twin.scan.LinkDetails` из scan-bytecode.

## Результат

Создан класс `DependencyReader` с заглушками методов (методы возвращают null или бросают `UnsupportedOperationException`).