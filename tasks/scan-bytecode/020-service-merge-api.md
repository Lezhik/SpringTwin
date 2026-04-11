# 020-service-merge — API

## Статус: complete

## Описание

Обновление `ScanBytecodeService` для передачи параметра `mergeInnerClasses` из `ScanBytecodeParams` в `DependencyGraphBuilder`.

## Изменения в классах

### `spring.twin.scan.ScanBytecodeService` (модификация)

**Обновлённые методы:**

- `void execute(ScanBytecodeParams params)` — вместо вызова `dependencyGraphBuilder.build(params.classesDir(), params.includeMasks(), params.excludeMasks())` вызывает `dependencyGraphBuilder.build(params.classesDir(), params.includeMasks(), params.excludeMasks(), params.mergeInnerClasses())`

- `Map<String, Set<String>> analyze(ScanBytecodeParams params)` — аналогично, вместо вызова `build()` с 3 параметрами вызывает `build()` с 4 параметрами, передавая `params.mergeInnerClasses()`

## Зависимости

- Фича 018: `ScanBytecodeParams.mergeInnerClasses()`
- Фича 019: `DependencyGraphBuilder.build()` с 4 параметрами

## Результат

Класс `ScanBytecodeService` обновлён: методы `execute()` и `analyze()` передают `mergeInnerClasses` в `DependencyGraphBuilder`.