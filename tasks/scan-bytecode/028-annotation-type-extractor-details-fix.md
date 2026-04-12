# 028-annotation-type-extractor-details — Исправление

## Статус: pending

## Описание

Запуск тестов и исправление ошибок для фичи annotation-type-extractor-details.

## Инструкции

1. Запустить тесты: `gradlew.bat clean test --tests "spring.twin.scan.AnnotationTypeExtractorDetailsTest"`
2. Если тесты не проходят — проанализировать сообщения об ошибках
3. Исправить реализацию `AnnotationTypeExtractor.extractDetails()` в соответствии с обнаруженными ошибками
4. Повторно запустить тесты
5. Убедиться, что все тесты проходят (зелёные)
6. Запустить полную сборку: `gradlew.bat clean build` — убедиться, что нет ошибок компиляции и все тесты проходят

## Типичные проблемы

- Перепутаны LinkType: FIELD_ANNOTATION вместо METHOD_ANNOTATION и наоборот
- Имя поля не передаётся в details для FIELD_ANNOTATION
- Сигнатура метода формируется в неверном формате
- Visible и invisible аннотации обрабатываются не одинаково
- Параметры методов не обрабатываются