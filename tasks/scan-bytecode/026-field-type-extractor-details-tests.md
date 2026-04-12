# 026-field-type-extractor-details — Тесты

## Статус: complete

## Описание

Unit тесты для нового метода `extractDetails()` в `FieldTypeExtractor`. Тесты используют скомпилированные классы-образцы из `src/test/java/spring/twin/testee/`.

## Тестируемый класс

`spring.twin.scan.FieldTypeExtractor`

## Тестируемый метод

`Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes)`

## Классы-образцы (testee)

Используются существующие:
- `FieldHolder.java` — класс с полями различных типов

## Класс тестов

### `spring.twin.scan.FieldTypeExtractorDetailsTest`

**Методы тестов:**

1. `testExtractDetails_fieldWithType_returnsFieldLink()` — класс с полем `OrderService orderService` → карта содержит ключ FQCN типа поля со значением `{LinkDetails.of(LinkType.FIELD, "orderService")}`
2. `testExtractDetails_multipleFields_returnsAllFieldLinks()` — класс с несколькими полями → каждое поле даёт отдельную запись с правильным именем
3. `testExtractDetails_genericField_returnsFieldAndGenericTypes()` — поле с generic-типом `List<String> names` → содержит FQCN List с `LinkDetails.of(LinkType.FIELD, "names")` и FQCN String с `LinkDetails.of(LinkType.FIELD, "names")`
4. `testExtractDetails_arrayField_returnsBaseType()` — поле-массив `OrderModel[] models` → содержит FQCN базового типа с `LinkDetails.of(LinkType.FIELD, "models")`
5. `testExtractDetails_primitiveField_notIncluded()` — примитивные поля не включаются в результат
6. `testExtractDetails_nullBytes_throwsException()` — null → бросает IllegalArgumentException

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Поле ссылочного типа | FQCN типа → {FIELD, имя поля} |
| 2 | Несколько полей | Каждое поле даёт запись с правильным именем |
| 3 | Generic-поле | Базовый тип + generic-параметры, все с FIELD и именем поля |
| 4 | Массив | Базовый тип массива с FIELD и именем поля |
| 5 | Примитивное поле | Не включается |