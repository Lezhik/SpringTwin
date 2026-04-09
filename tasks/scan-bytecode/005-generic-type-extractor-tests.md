# 005-generic-type-extractor — Тесты

## Статус: pending

## Описание

Unit тесты для `GenericTypeExtractor`. Тесты используют реальные сигнатуры из JVM байткода.

## Тестируемый класс

`spring.twin.scan.GenericTypeExtractor`

## Класс тестов

### `spring.twin.scan.GenericTypeExtractorTest`

**Методы тестов:**

1. `testExtractTypes_nullSignature_returnsEmptySet()` — null → пустое множество
2. `testExtractTypes_emptyString_returnsEmptySet()` — пустая строка → пустое множество
3. `testExtractTypes_simpleObjectType_returnsFqcn()` — `Ljava/lang/String;` → `[java.lang.String]`
4. `testExtractTypes_genericList_returnsListAndParameter()` — `Ljava/util/List<Ljava/lang/String;>;` → `[java.util.List, java.lang.String]`
5. `testExtractTypes_genericMap_returnsMapAndBothParameters()` — `Ljava/util/Map<Ljava/lang/String;Ljava/lang/Integer;>;` → `[java.util.Map, java.lang.String, java.lang.Integer]`
6. `testExtractTypes_nestedGenerics_returnsAllTypes()` — `Ljava/util/Map<Ljava/lang/String;Ljava/util/List<Ljava/lang/Integer;>;>;` → `[java.util.Map, java.lang.String, java.util.List, java.lang.Integer]`
7. `testExtractTypes_primitiveOnly_returnsEmptySet()` — `I` (int) → пустое множество
8. `testExtractTypes_arrayOfObject_returnsBaseType()` — `[Ljava/lang/String;` → `[java.lang.String]`
9. `testExtractTypes_classSignatureWithFormalTypeParameter()` — `<T:Ljava/lang/Object;>Ljava/lang/Object;` → `[java.lang.Object]` (T — формальный параметр, не извлекается)
10. `testExtractTypes_wildcardExtends_returnsBoundType()` — `Ljava/util/List<+Ljava/lang/Number;>;` → `[java.util.List, java.lang.Number]`
11. `testExtractTypes_wildcardSuper_returnsBoundType()` — `Ljava/util/List<-Ljava/lang/Number;>;` → `[java.util.List, java.lang.Number]`
12. `testExtractTypeNames_simpleObjectType_returnsFqcn()` — `Ljava/lang/String;` → `[java.lang.String]`
13. `testExtractTypeNames_genericWithNested_returnsAllTypes()` — `Ljava/util/Map<Ljava/lang/String;Ljava/util/List<Ljava/lang/Integer;>;>;` → все 4 типа
14. `testExtractTypeNames_primitiveDescriptor_returnsEmptySet()` — `I` → пустое множество

## Сценарии

| # | Сценарий | Сигнатура | Ожидаемые типы |
|---|----------|-----------|---------------|
| 1 | Простой тип | `Ljava/lang/String;` | `java.lang.String` |
| 2 | Generic с одним параметром | `List<String>` | `List`, `String` |
| 3 | Generic с двумя параметрами | `Map<String, Integer>` | `Map`, `String`, `Integer` |
| 4 | Вложенные generic | `Map<String, List<Integer>>` | `Map`, `String`, `List`, `Integer` |
| 5 | Примитив | `I` | пусто |
| 6 | Массив объектов | `[Ljava/lang/String;` | `java.lang.String` |
| 7 | Wildcard extends | `List<? extends Number>` | `List`, `Number` |
| 8 | Формальный параметр | `<T:Ljava/lang/Object;>` | `java.lang.Object` (T не извлекается) |