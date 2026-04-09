# 014-scan-bytecode-service — API

## Статус: pending

## Описание

Главный сервис пайплайна scan-bytecode. Оркестрирует весь процесс: принимает параметры, строит граф зависимостей, записывает результат в JSON.

## Классы

### `spring.twin.scan.ScanBytecodeService`

**Тип:** Spring bean с внедрением зависимостей через конструктор

**Конструктор:**
- `ScanBytecodeService(DependencyGraphBuilder dependencyGraphBuilder, DependencyJsonWriter dependencyJsonWriter)`

**Методы:**

- `void execute(ScanBytecodeParams params)` — главный метод пайплайна:
  1. Вызывает `dependencyGraphBuilder.build(params.classesDir(), params.includeMasks(), params.excludeMasks())`
  2. Вызывает `dependencyJsonWriter.write(graph, params.outputFile())`
  **Метод должен быть аннотирован Javadoc на английском языке.**

- `Map<String, Set<String>> analyze(ScanBytecodeParams params)` — анализирует байткод и возвращает граф без записи в файл. Удобен для программного использования. **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

- Фича 001: `ScanBytecodeParams`
- Фича 012: `DependencyGraphBuilder`
- Фича 013: `DependencyJsonWriter`

## Результат

Создан класс `ScanBytecodeService` с заглушками методов.