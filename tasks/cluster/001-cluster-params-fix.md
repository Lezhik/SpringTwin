# 001-cluster-params — Исправление

## Статус: complete

## Описание

Запуск тестов и исправление ошибок для фичи cluster-params.

## Инструкции

1. Запустить тесты: `gradlew.bat clean test --tests "spring.twin.cluster.ClusterParamsTest"`
2. Если тесты не проходят — проанализировать сообщения об ошибках
3. Исправить реализацию `ClusterParams` в соответствии с обнаруженными ошибками
4. Повторно запустить тесты
5. Убедиться, что все тесты проходят (зелёные)
6. Запустить полную сборку: `gradlew.bat clean build` — убедиться, что нет ошибок компиляции и все тесты проходят

## Типичные проблемы

- `UnsupportedOperationException` — заглушка не заменена на реализацию
- `NumberFormatException` — не обработан null/пустая строка в `of()`
- `IllegalArgumentException` не бросается — не реализована валидация диапазона resolution
- Проблемы с точностью double — использовать `assertEquals(expected, actual, delta)` с погрешностью 0.001