# 010-code-usage-extractor — Тесты

## Статус: complete

## Описание

Unit тесты для `CodeUsageExtractor`. Тесты используют скомпилированные классы-образцы из `src/test/java/spring/twin/testee/`.

## Тестируемый класс

`spring.twin.scan.CodeUsageExtractor`

## Классы-образцы (testee)

Необходимо создать в `src/test/java/spring/twin/testee/`:

- `CodeUsageExample.java` — класс с различными видами использования типов в коде:
  - `public void createObject()` — содержит `new ArrayList<>()`
  - `public void callMethod()` — содержит `String.valueOf(42)`
  - `public void castType(Object obj)` — содержит `(String) obj`
  - `public void instanceCheck(Object obj)` — содержит `obj instanceof String`
  - `public void accessField()` — содержит `System.out`
  - `public void staticInit()` — статический блок с инициализацией

## Класс тестов

### `spring.twin.scan.CodeUsageExtractorTest`

**Методы тестов:**

1. `testExtract_newObject_returnsObjectType()` — `new ArrayList<>()` → содержит `java.util.ArrayList`
2. `testExtract_methodCall_returnsOwnerType()` — `String.valueOf(42)` → содержит `java.lang.String`
3. `testExtract_castType_returnsTargetType()` — `(String) obj` → содержит `java.lang.String`
4. `testExtract_instanceofCheck_returnsCheckedType()` — `obj instanceof String` → содержит `java.lang.String`
5. `testExtract_fieldAccess_returnsOwnerType()` — `System.out` → содержит `java.lang.System`
6. `testExtract_staticInit_returnsUsedTypes()` — статический блок → содержит типы из инициализации
7. `testExtract_noCode_returnsEmptySet()` — интерфейс без реализации → пустое множество
8. `testExtract_nullBytes_throwsException()` — null → бросает исключение

## Сценарии

| # | Сценарий | Код | Ожидаемый тип |
|---|----------|-----|---------------|
| 1 | new объект | `new ArrayList<>()` | `java.util.ArrayList` |
| 2 | Вызов метода | `String.valueOf(42)` | `java.lang.String` |
| 3 | Приведение типа | `(String) obj` | `java.lang.String` |
| 4 | instanceof | `obj instanceof String` | `java.lang.String` |
| 5 | Доступ к полю | `System.out` | `java.lang.System` |