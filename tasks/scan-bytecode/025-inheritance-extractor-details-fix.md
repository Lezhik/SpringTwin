# 025-inheritance-extractor-details — Исправление

## Статус: pending

## Описание

Запуск тестов и исправление ошибок для фичи inheritance-extractor-details.

## Инструкции

1. Запустить тесты: `gradlew.bat clean test --tests "spring.twin.scan.InheritanceExtractorDetailsTest"`
2. Если тесты не проходят — проанализировать сообщения об ошибках
3. Исправить реализацию `InheritanceExtractor.extractDetails()` в соответствии с обнаруженными ошибками
4. Повторно запустить тесты
5. Убедиться, что все тесты проходят (зелёные)
6. Запустить полную сборку: `gradlew.bat clean build` — убедиться, что нет ошибок компиляции и все тесты проходят

## Типичные проблемы

- Generic-типы получают неверный LinkType (например, INTERFACE вместо SUPERCLASS)
- Вспомогательный метод `addDetail` не создаёт новый Set для нового ключа
- Дублирование LinkDetails в Set — проверить equals/hashCode
- java.lang.Object ошибочно включён как SUPERCLASS
- NullPointerException при null байткоде