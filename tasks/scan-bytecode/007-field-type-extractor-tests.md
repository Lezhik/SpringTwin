# 007-field-type-extractor — Тесты

## Статус: pending

## Описание

Unit тесты для `FieldTypeExtractor`. Тесты используют скомпилированные классы-образцы из `src/test/java/spring/twin/testee/`.

## Тестируемый класс

`spring.twin.scan.FieldTypeExtractor`

## Классы-образцы (testee)

Необходимо создать в `src/test/java/spring/twin/testee/`:

- `FieldHolder.java` — класс с различными типами полей:
  - `private String simpleField;` — простое поле
  - `private List<String> genericField;` — generic-поле
  - `private int primitiveField;` — примитивное поле
  - `private String[] arrayField;` — массив объектов
  - `private Map<String, Integer> mapField;` — generic-поле с двумя параметрами

## Класс тестов

### `spring.twin.scan.FieldTypeExtractorTest`

**Методы тестов:**

1. `testExtract_simpleField_returnsFieldType()` — поле `String simpleField` → содержит `java.lang.String`
2. `testExtract_genericField_returnsTypeAndGenericParams()` — поле `List<String> genericField` → содержит `java.util.List`, `java.lang.String`
3. `testExtract_primitiveField_notIncluded()` — поле `int primitiveField` → не содержит примитив
4. `testExtract_arrayField_returnsBaseType()` — поле `String[] arrayField` → содержит `java.lang.String` (не массив)
5. `testExtract_mapField_returnsTypeAndBothParams()` — поле `Map<String, Integer> mapField` → содержит `java.util.Map`, `java.lang.String`, `java.lang.Integer`
6. `testExtract_noFields_returnsEmptySet()` — класс без полей → пустое множество
7. `testExtract_nullBytes_throwsException()` — null → бросает исключение

## Сценарии

| # | Сценарий | Поле | Ожидаемые типы |
|---|----------|------|---------------|
| 1 | Простое поле | `String` | `java.lang.String` |
| 2 | Generic-поле | `List<String>` | `java.util.List`, `java.lang.String` |
| 3 | Примитив | `int` | не включается |
| 4 | Массив объектов | `String[]` | `java.lang.String` |
| 5 | Map с двумя generic | `Map<String, Integer>` | `Map`, `String`, `Integer` |