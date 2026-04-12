# 028-annotation-type-extractor-details — Тесты

## Статус: pending

## Описание

Unit тесты для нового метода `extractDetails()` в `AnnotationTypeExtractor`. Тесты используют скомпилированные классы-образцы из `src/test/java/spring/twin/testee/`.

## Тестируемый класс

`spring.twin.scan.AnnotationTypeExtractor`

## Тестируемый метод

`Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes)`

## Классы-образцы (testee)

Используются существующие:
- `AnnotatedClass.java` — класс с класс-аннотацией
- `FieldAnnotatedClass.java` — класс с аннотацией на поле
- `MethodAnnotatedClass.java` — класс с аннотацией на метод
- `ParameterAnnotatedClass.java` — класс с аннотацией на параметр метода
- `CustomAnnotatedClass.java` — класс с кастомной аннотацией
- `CustomAnnotation.java` — кастомная аннотация

## Класс тестов

### `spring.twin.scan.AnnotationTypeExtractorDetailsTest`

**Методы тестов:**

1. `testExtractDetails_classAnnotation_returnsClassAnnotationLink()` — класс с аннотацией `@Deprecated` → карта содержит FQCN аннотации со значением `{LinkDetails.of(LinkType.CLASS_ANNOTATION)}`
2. `testExtractDetails_fieldAnnotation_returnsFieldAnnotationLink()` — поле с аннотацией `@Deprecated` → карта содержит FQCN аннотации со значением `{LinkDetails.of(LinkType.FIELD_ANNOTATION, fieldName)}`
3. `testExtractDetails_methodAnnotation_returnsMethodAnnotationLink()` — метод с аннотацией `@Deprecated` → карта содержит FQCN аннотации со значением `{LinkDetails.of(LinkType.METHOD_ANNOTATION, methodSignature)}`
4. `testExtractDetails_parameterAnnotation_returnsMethodArgAnnotationLink()` — параметр метода с аннотацией → карта содержит FQCN аннотации со значением `{LinkDetails.of(LinkType.METHOD_ARG_ANNOTATION, methodSignature)}`
5. `testExtractDetails_customAnnotation_returnsCorrectLink()` — кастомная аннотация → корректный FQCN и тип CLASS_ANNOTATION
6. `testExtractDetails_multipleAnnotationsOnSameTarget_returnsAllLinks()` — несколько аннотаций на одном элементе → все включены
7. `testExtractDetails_nullBytes_throwsException()` — null → бросает IllegalArgumentException

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Аннотация класса | FQCN аннотации → {CLASS_ANNOTATION, ""} |
| 2 | Аннотация поля | FQCN аннотации → {FIELD_ANNOTATION, имя поля} |
| 3 | Аннотация метода | FQCN аннотации → {METHOD_ANNOTATION, сигнатура} |
| 4 | Аннотация параметра | FQCN аннотации → {METHOD_ARG_ANNOTATION, сигнатура} |
| 5 | Кастомная аннотация | Корректный FQCN |
| 6 | Несколько аннотаций | Все включены |