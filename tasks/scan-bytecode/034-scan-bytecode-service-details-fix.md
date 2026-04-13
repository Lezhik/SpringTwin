# 034-scan-bytecode-service-details — Исправление

## Статус: complete

## Описание

Запуск тестов и исправление ошибок для фичи scan-bytecode-service-details.

## Инструкции

1. Запустить тесты: `gradlew.bat clean test --tests "spring.twin.scan.ScanBytecodeServiceDetailsTest"`
2. Если тесты не проходят — проанализировать сообщения об ошибках
3. Исправить реализацию `ScanBytecodeService.executeDetails()` и `analyzeDetails()` в соответствии с обнаруженными ошибками
4. Повторно запустить тесты
5. Убедиться, что все тесты проходят (зелёные)
6. Запустить полную сборку: `gradlew.bat clean build` — убедиться, что нет ошибок компиляции и все тесты проходят

## Типичные проблемы

- Методы вызывают старые build()/write() вместо buildDetails()/writeDetails()
- Флаг mergeInnerClasses не передаётся в builder
- Несовместимость типов: Map<String, Set<String>> вместо Map<String, Map<String, Set<LinkDetails>>>