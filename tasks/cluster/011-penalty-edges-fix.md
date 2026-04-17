# 011-penalty-edges — Исправление

## Статус: complete

## Описание

Запуск тестов и исправление ошибок для фичи penalty-edges.

## Инструкции

1. Запустить тесты: `gradlew.bat clean test --tests "spring.twin.cluster.PenaltyEdgeDetectorTest"`
2. Если тесты не проходят — проанализировать сообщения об ошибках
3. Исправить реализацию `PenaltyEdgeDetector` в соответствии с обнаруженными ошибками
4. Повторно запустить тесты
5. Убедиться, что все тесты проходят (зелёные)
6. Запустить полную сборку: `gradlew.bat clean build` — убедиться, что нет ошибок компиляции и все тесты проходят

## Типичные проблемы

- `UnsupportedOperationException` — заглушка не заменена на реализацию
- Внутрикластерные связи попали в penaltyEdges — ошибка в проверке communityOf
- NullPointerException — узел из dependencyGraph отсутствует в partition
- Штрафные рёбра не отсортированы — не используется TreeMap