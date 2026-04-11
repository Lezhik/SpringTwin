# 014-scan-bytecode-service — Реализация

## Статус: complete

## Описание

Реализация `ScanBytecodeService` — главного сервиса пайплайна scan-bytecode.

## Классы и методы

### `spring.twin.scan.ScanBytecodeService`

**Тип:** Spring bean (`@Service`)

**Конструктор:**
- `ScanBytecodeService(DependencyGraphBuilder dependencyGraphBuilder, DependencyJsonWriter dependencyJsonWriter)` — внедрение зависимостей

**Реализация:**

- `void execute(ScanBytecodeParams params)` — вызывает `dependencyGraphBuilder.build(params.classesDir(), params.includeMasks(), params.excludeMasks())`, затем `dependencyJsonWriter.write(graph, params.outputFile())`. **Метод должен быть аннотирован Javadoc на английском языке.**

- `Map<String, Set<String>> analyze(ScanBytecodeParams params)` — вызывает `dependencyGraphBuilder.build(params.classesDir(), params.includeMasks(), params.excludeMasks())` и возвращает результат без записи в файл. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `execute` — полный пайплайн: анализ + запись
2. `analyze` — только анализ, без побочных эффектов