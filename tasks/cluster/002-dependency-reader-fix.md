# 002-dependency-reader — Исправление

## Статус: pending

## Описание

Запуск тестов и исправление ошибок для фичи dependency-reader.

## Инструкции

1. Запустить тесты: `gradlew.bat clean test --tests "spring.twin.cluster.DependencyReaderTest"`
2. Если тесты не проходят — проанализировать сообщения об ошибках
3. Исправить реализацию `DependencyReader` в соответствии с обнаруженными ошибками
4. Повторно запустить тесты
5. Убедиться, что все тесты проходят (зелёные)
6. Запустить полную сборку: `gradlew.bat clean build` — убедиться, что нет ошибок компиляции и все тесты проходят

## Типичные проблемы

- `UnsupportedOperationException` — заглушка не заменена на реализацию
- `UncheckedIOException` не бросается — ошибки ввода-вывода не обёрнуты
- Ошибки десериализации LinkDetails — некорректный TypeReference или отсутствие регистрации кастомных сериализаторов LinkDetails
- `JsonProcessingException` не обработан — нужно оборачивать в `UncheckedIOException`