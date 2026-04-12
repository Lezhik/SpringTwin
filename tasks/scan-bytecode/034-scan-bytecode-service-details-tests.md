# 034-scan-bytecode-service-details — Тесты

## Статус: pending

## Описание

Unit тесты для новых методов `executeDetails()` и `analyzeDetails()` в `ScanBytecodeService`.

## Тестируемый класс

`spring.twin.scan.ScanBytecodeService`

## Тестируемые методы

- `void executeDetails(ScanBytecodeParams params)`
- `Map<String, Map<String, Set<LinkDetails>>> analyzeDetails(ScanBytecodeParams params)`

## Класс тестов

### `spring.twin.scan.ScanBytecodeServiceDetailsTest`

**Методы тестов:**

1. `testExecuteDetails_callsBuildDetailsAndWriteDetails()` — проверяет что `executeDetails()` вызывает `buildDetails()` и `writeDetails()`
2. `testExecuteDetails_writesOutputFile()` — после выполнения существует выходной файл с корректным JSON
3. `testAnalyzeDetails_returnsDetailedGraph()` — `analyzeDetails()` возвращает `Map<String, Map<String, Set<LinkDetails>>>`
4. `testAnalyzeDetails_doesNotWriteFile()` — `analyzeDetails()` не создаёт выходной файл
5. `testExecuteDetails_withMergeInnerClasses_passesFlagToBuilder()` — флаг mergeInnerClasses передаётся в builder
6. `testAnalyzeDetails_withMergeInnerClasses_passesFlagToBuilder()` — флаг mergeInnerClasses передаётся в builder

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | executeDetails | Вызывает buildDetails и writeDetails |
| 2 | Выходной файл | Существует с корректным JSON |
| 3 | analyzeDetails | Возвращает детализированный граф |
| 4 | analyzeDetails без файла | Файл не создаётся |
| 5 | mergeInnerClasses в execute | Флаг передан |
| 6 | mergeInnerClasses в analyze | Флаг передан |