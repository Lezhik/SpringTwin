# 020-service-merge — Тесты

## Статус: pending

## Описание

Unit тесты для обновлённого `ScanBytecodeService` с передачей `mergeInnerClasses`.

## Тестируемые классы

- `spring.twin.scan.ScanBytecodeService`

## Класс тестов

### `spring.twin.scan.ScanBytecodeServiceTest` (модификация существующего)

**Новые методы тестов:**

1. `testExecute_withMergeInnerClassesTrue_passesFlagToBuilder()` — создаёт `ScanBytecodeParams` с `mergeInnerClasses=true`, вызывает `execute()`, проверяет через `ArgumentCaptor`, что `dependencyGraphBuilder.build()` вызван с 4 параметрами и `mergeInnerClasses=true`

2. `testExecute_withMergeInnerClassesFalse_passesFlagToBuilder()` — создаёт `ScanBytecodeParams` с `mergeInnerClasses=false`, вызывает `execute()`, проверяет что `build()` вызван с `mergeInnerClasses=false`

3. `testAnalyze_withMergeInnerClassesTrue_passesFlagToBuilder()` — аналогично для `analyze()`, проверяет что `build()` вызван с `mergeInnerClasses=true`

4. `testAnalyze_withMergeInnerClassesFalse_passesFlagToBuilder()` — аналогично для `analyze()`, проверяет что `build()` вызван с `mergeInnerClasses=false`

**Существующие тесты** — не удаляются и не изменяются, должны продолжать проходить. При необходимости обновить моки `dependencyGraphBuilder.build()` для совместимости с новой сигнатурой.

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | execute() с mergeInnerClasses=true | build() вызван с 4 параметрами, 4-й = true |
| 2 | execute() с mergeInnerClasses=false | build() вызван с 4 параметрами, 4-й = false |
| 3 | analyze() с mergeInnerClasses=true | build() вызван с 4 параметрами, 4-й = true |
| 4 | analyze() с mergeInnerClasses=false | build() вызван с 4 параметрами, 4-й = false |
| 5 | Все существующие тесты | Продолжают проходить |