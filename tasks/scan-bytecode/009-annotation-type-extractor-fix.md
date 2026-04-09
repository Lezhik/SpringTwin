# 009-annotation-type-extractor — Исправление

## Статус: pending

## Описание

Запуск тестов и исправление ошибок для фичи annotation-type-extractor.

## Инструкции

1. Запустить тесты: `gradlew.bat clean test --tests "spring.twin.scan.AnnotationTypeExtractorTest"`
2. Если тесты не проходят — проанализировать сообщения об ошибках
3. Исправить реализацию `AnnotationTypeExtractor` в соответствии с обнаруженными ошибками
4. Повторно запустить тесты
5. Убедиться, что все тесты проходят (зелёные)
6. Запустить полную сборку: `gradlew.bat clean build` — убедиться, что нет ошибок компиляции и все тесты проходят

## Типичные проблемы

- `visibleAnnotations` или `invisibleAnnotations` == null — нужно проверять на null перед обходом
- `@Override` — это source-level аннотация, может не быть в байткоде (RetentionPolicy.SOURCE)
- Дескриптор аннотации не конвертирован через `FqcnNormalizer`
- Аннотации полей/методов пропущены
- Аннотации параметров методов не обработаны