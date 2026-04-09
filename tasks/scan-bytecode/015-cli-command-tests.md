# 015-cli-command — Тесты

## Статус: pending

## Описание

Unit тесты для `ScanBytecodeCommand`.

## Тестируемый класс

`spring.twin.command.ScanBytecodeCommand`

## Класс тестов

### `spring.twin.command.ScanBytecodeCommandTest`

**Методы тестов:**

1. `testScanBytecode_validParams_returnsSuccessMessage()` — корректные параметры → сообщение об успехе с путём к файлу
2. `testScanBytecode_callsServiceExecute()` — проверяет что `scanBytecodeService.execute()` вызывается с правильными параметрами
3. `testScanBytecode_emptyIncludeAndExclude()` — пустые include/exclude → сервис вызывается с пустыми списками масок
4. `testScanBytecode_nonExistingClassesDir_throwsException()` — несуществующая директория → исключение или сообщение об ошибке
5. `testScanBytecode_createsCorrectParams()` — проверяет корректность создания `ScanBytecodeParams` из строковых аргументов

## Сценарии

| # | Сценарий | Аргументы | Ожидаемый результат |
|---|----------|-----------|-------------------|
| 1 | Полные параметры | `--classes`, `--output`, `--include`, `--exclude` | Сервис вызван, файл создан |
| 2 | Без масок | `--classes`, `--output` | Сервис вызван с пустыми масками |
| 3 | Несуществующая директория | невалидный `--classes` | Исключение |