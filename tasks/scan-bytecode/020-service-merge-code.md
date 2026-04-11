# 020-service-merge — Реализация

## Статус: pending

## Описание

Обновление `ScanBytecodeService` для передачи `mergeInnerClasses` из `ScanBytecodeParams` в `DependencyGraphBuilder`.

## Классы и методы

### `spring.twin.scan.ScanBytecodeService` (модификация)

**Реализация:**

- `void execute(ScanBytecodeParams params)` — заменить вызов `dependencyGraphBuilder.build(params.classesDir(), params.includeMasks(), params.excludeMasks())` на `dependencyGraphBuilder.build(params.classesDir(), params.includeMasks(), params.excludeMasks(), params.mergeInnerClasses())`. **Метод должен быть аннотирован Javadoc на английском языке.**

- `Map<String, Set<String>> analyze(ScanBytecodeParams params)` — заменить вызов `dependencyGraphBuilder.build(params.classesDir(), params.includeMasks(), params.excludeMasks())` на `dependencyGraphBuilder.build(params.classesDir(), params.includeMasks(), params.excludeMasks(), params.mergeInnerClasses())`. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Оба метода `execute()` и `analyze()` теперь передают 4-й параметр `mergeInnerClasses` в `DependencyGraphBuilder.build()`
2. Значение берётся из `ScanBytecodeParams.mergeInnerClasses()`, которое по умолчанию равно `true`
3. Конструктор `ScanBytecodeService` не меняется — зависимости те же