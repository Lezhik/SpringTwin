# 008-method-type-extractor — Тесты

## Статус: complete

## Описание

Unit тесты для `MethodTypeExtractor`. Тесты используют скомпилированные классы-образцы из `src/test/java/spring/twin/testee/`.

## Тестируемый класс

`spring.twin.scan.MethodTypeExtractor`

## Классы-образцы (testee)

Необходимо создать в `src/test/java/spring/twin/testee/`:

- `MethodHolder.java` — класс с различными методами:
  - `public String getName()` — простой возвращаемый тип
  - `public void setName(String name)` — простой параметр
  - `public List<String> getItems()` — generic возвращаемый тип
  - `public void process(Map<String, Integer> data)` — generic параметр
  - `public int calculate()` — примитивный возвращаемый тип
  - `public void setValues(String[] values)` — массив в параметре
  - `public MethodHolder(String name)` — конструктор с параметром

## Класс тестов

### `spring.twin.scan.MethodTypeExtractorTest`

**Методы тестов:**

1. `testExtract_simpleReturnType_returnsType()` — метод `String getName()` → содержит `java.lang.String`
2. `testExtract_simpleParameter_returnsParamType()` — метод `setName(String name)` → содержит `java.lang.String`
3. `testExtract_genericReturnType_returnsTypeAndParams()` — метод `List<String> getItems()` → содержит `java.util.List`, `java.lang.String`
4. `testExtract_genericParameter_returnsTypeAndParams()` — метод `process(Map<String, Integer> data)` → содержит `java.util.Map`, `java.lang.String`, `java.lang.Integer`
5. `testExtract_primitiveReturnType_notIncluded()` — метод `int calculate()` → не содержит примитив
6. `testExtract_arrayParameter_returnsBaseType()` — метод `setValues(String[] values)` → содержит `java.lang.String`
7. `testExtract_constructorParameter_returnsParamType()` — конструктор `MethodHolder(String name)` → содержит `java.lang.String`
8. `testExtract_noMethods_returnsEmptySet()` — класс без методов (кроме дефолтного конструктора) → минимальный набор
9. `testExtract_nullBytes_throwsException()` — null → бросает исключение

## Сценарии

| # | Сценарий | Метод | Ожидаемые типы |
|---|----------|-------|---------------|
| 1 | Простой возврат | `String getName()` | `java.lang.String` |
| 2 | Простой параметр | `setName(String)` | `java.lang.String` |
| 3 | Generic возврат | `List<String> getItems()` | `List`, `String` |
| 4 | Generic параметр | `process(Map<String, Integer>)` | `Map`, `String`, `Integer` |
| 5 | Примитивный возврат | `int calculate()` | не включается |
| 6 | Массив в параметре | `setValues(String[])` | `java.lang.String` |