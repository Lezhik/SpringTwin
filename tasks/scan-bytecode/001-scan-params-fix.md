# 001-scan-params — Исправление

## Статус: pending

## Описание

Запуск тестов и исправление ошибок для фичи scan-params.

## Инструкции

1. Запустить тесты: `gradlew.bat clean test --tests "spring.twin.scan.ScanBytecodeParamsTest"` и `gradlew.bat clean test --tests "spring.twin.scan.MaskParserTest"`
2. Если тесты не проходят — проанализировать сообщения об ошибках
3. Исправить реализацию `ScanBytecodeParams` и `MaskParser` в соответствии с обнаруженными ошибками
4. Повторно запустить тесты
5. Убедиться, что все тесты проходят (зелёные)
6. Запустить полную сборку: `gradlew.bat clean build` — убедиться, что нет ошибок компиляции и все тесты проходят

## Типичные проблемы

- `NullPointerException` при передаче null в `parseMasks()` — не обработан null
- `UnsupportedOperationException` — заглушка не заменена на реализацию
- Несоответствие ожидаемого и фактического размера списков — проблема с фильтрацией пустых элементов
- Проблемы с иммутабельностью — списки не обёрнуты в `List.of()`