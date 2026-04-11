# 014-scan-bytecode-service — Тесты

## Статус: complete

## Описание

Unit тесты для `ScanBytecodeService`.

## Тестируемый класс

`spring.twin.scan.ScanBytecodeService`

## Класс тестов

### `spring.twin.scan.ScanBytecodeServiceTest`

**Методы тестов:**

1. `testExecute_buildsGraphAndWritesFile()` — `execute()` вызывает builder и writer, файл создан
2. `testExecute_correctGraphWritten()` — содержимое файла соответствует ожидаемому графу
3. `testAnalyze_returnsGraphWithoutWriting()` — `analyze()` возвращает граф без создания файла
4. `testExecute_withMasks_filtersCorrectly()` — маски применяются корректно
5. `testExecute_emptyDirectory_createsEmptyJson()` — пустая директория → `{}`
6. `testAnalyze_emptyDirectory_returnsEmptyMap()` — пустая директория → пустой Map

## Сценарии

| # | Сценарий | Метод | Ожидаемый результат |
|---|----------|-------|-------------------|
| 1 | Полный пайплайн | `execute()` | Файл создан с корректным JSON |
| 2 | Только анализ | `analyze()` | Граф возвращён, файл не создан |
| 3 | Пустая директория | `execute()` | Файл с `{}` |