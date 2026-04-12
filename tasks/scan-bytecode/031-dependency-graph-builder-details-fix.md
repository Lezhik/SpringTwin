# 031-dependency-graph-builder-details — Исправление

## Статус: complete

## Описание

Запуск тестов и исправление ошибок для фичи dependency-graph-builder-details.

## Инструкции

1. Запустить тесты: `gradlew.bat clean test --tests "spring.twin.scan.DependencyGraphBuilderDetailsTest"`
2. Если тесты не проходят — проанализировать сообщения об ошибках
3. Исправить реализацию `DependencyGraphBuilder.buildDetails()` в соответствии с обнаруженными ошибками
4. Повторно запустить тесты
5. Убедиться, что все тесты проходят (зелёные)
6. Запустить полную сборку: `gradlew.bat clean build` — убедиться, что нет ошибок компиляции и все тесты проходят

## Типичные проблемы

- Неверная структура результата: внешний ключ не FQCN класса
- Примитивные типы не фильтруются из внутренних ключей
- Маски include/exclude не применяются к зависимостям
- InnerClassMerger не поддерживает новый формат Map<String, Map<String, Set<LinkDetails>>>
- NullPointerException при пустых результатах экстракторов
- TreeMap/TreeMap не используется для детерминированной сортировки