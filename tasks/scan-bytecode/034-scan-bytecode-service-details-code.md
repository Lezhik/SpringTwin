# 034-scan-bytecode-service-details — Реализация

## Статус: complete

## Описание

Реализация методов `executeDetails()` и `analyzeDetails()` в `ScanBytecodeService`.

## Классы и методы

### `spring.twin.scan.ScanBytecodeService` (модификация)

**Новые методы:**

- `void executeDetails(ScanBytecodeParams params)` — выполняет пайплайн scan-bytecode с детализированным выводом. **Метод должен быть аннотирован Javadoc на английском языке.**

**Реализация:**
1. Вызвать `dependencyGraphBuilder.buildDetails(params.classesDir(), params.includeMasks(), params.excludeMasks(), params.mergeInnerClasses())`
2. Вызвать `dependencyJsonWriter.writeDetails(graph, params.outputFile())`

- `Map<String, Map<String, Set<LinkDetails>>> analyzeDetails(ScanBytecodeParams params)` — анализирует байткод и возвращает детализированный граф. **Метод должен быть аннотирован Javadoc на английском языке.**

**Реализация:**
1. Вызвать `dependencyGraphBuilder.buildDetails(params.classesDir(), params.includeMasks(), params.excludeMasks(), params.mergeInnerClasses())`
2. Вернуть результат

## Логика работы

1. executeDetails: buildDetails → writeDetails
2. analyzeDetails: buildDetails → return