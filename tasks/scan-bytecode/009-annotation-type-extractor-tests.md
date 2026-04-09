# 009-annotation-type-extractor — Тесты

## Статус: pending

## Описание

Unit тесты для `AnnotationTypeExtractor`. Тесты используют скомпилированные классы-образцы из `src/test/java/spring/twin/testee/`.

## Тестируемый класс

`spring.twin.scan.AnnotationTypeExtractor`

## Классы-образцы (testee)

Необходимо создать в `src/test/java/spring/twin/testee/`:

- `AnnotatedClass.java` — класс с аннотациями:
  - `@Deprecated` на классе
  - `@SuppressWarnings("unchecked")` на поле
  - `@Override` на методе
- Пользовательская аннотация `CustomAnnotation.java`:
  - `@Retention(RetentionPolicy.RUNTIME)` — для видимости в байткоде
  - `@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})`

## Класс тестов

### `spring.twin.scan.AnnotationTypeExtractorTest`

**Методы тестов:**

1. `testExtract_classAnnotation_returnsAnnotationType()` — `@Deprecated` на классе → содержит `java.lang.Deprecated`
2. `testExtract_fieldAnnotation_returnsAnnotationType()` — `@SuppressWarnings` на поле → содержит `java.lang.SuppressWarnings`
3. `testExtract_methodAnnotation_returnsAnnotationType()` — `@Override` на методе → содержит `java.lang.Override`
4. `testExtract_customAnnotation_returnsFqcn()` — `@CustomAnnotation` → содержит `spring.twin.testee.CustomAnnotation`
5. `testExtract_noAnnotations_returnsEmptySet()` — класс без аннотаций → пустое множество
6. `testExtract_nullBytes_throwsException()` — null → бросает исключение

## Сценарии

| # | Сценарий | Аннотация | Ожидаемый тип |
|---|----------|-----------|---------------|
| 1 | Аннотация класса | `@Deprecated` | `java.lang.Deprecated` |
| 2 | Аннотация поля | `@SuppressWarnings` | `java.lang.SuppressWarnings` |
| 3 | Аннотация метода | `@Override` | `java.lang.Override` |
| 4 | Пользовательская аннотация | `@CustomAnnotation` | `spring.twin.testee.CustomAnnotation` |