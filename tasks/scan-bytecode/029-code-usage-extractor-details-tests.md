# 029-code-usage-extractor-details — Тесты

## Статус: pending

## Описание

Unit тесты для нового метода `extractDetails()` в `CodeUsageExtractor`. Тесты используют скомпилированные классы-образцы из `src/test/java/spring/twin/testee/`.

## Тестируемый класс

`spring.twin.scan.CodeUsageExtractor`

## Тестируемый метод

`Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes)`

## Классы-образцы (testee)

Используются существующие:
- `CodeUsageExample.java` — класс с использованием типов в коде методов

## Класс тестов

### `spring.twin.scan.CodeUsageExtractorDetailsTest`

**Методы тестов:**

1. `testExtractDetails_newObjectInMethod_returnsMethodLink()` — создание объекта в методе `new OrderService()` → карта содержит FQCN типа со значением `{LinkDetails.of(LinkType.METHOD, methodSignature)}`
2. `testExtractDetails_methodInvocation_returnsMethodLink()` — вызов метода `service.process()` → карта содержит FQCN владельца метода со значением `{LinkDetails.of(LinkType.METHOD, methodSignature)}`
3. `testExtractDetails_staticBlockUsage_returnsStaticBlockLink()` — использование типа в статическом блоке → карта содержит FQCN типа со значением `{LinkDetails.of(LinkType.STATIC_BLOCK)}`
4. `testExtractDetails_typeCast_returnsMethodLink()` — приведение типа `(OrderModel) obj` → карта содержит FQCN типа со значением `{LinkDetails.of(LinkType.METHOD, methodSignature)}`
5. `testExtractDetails_fieldAccess_returnsMethodLink()` — доступ к полю `obj.field` → карта содержит FQCN владельца поля со значением `{LinkDetails.of(LinkType.METHOD, methodSignature)}`
6. `testExtractDetails_multipleUsagesInSameMethod_sameSignature()` — несколько использований одного типа в одном методе → одна запись с сигнатурой метода
7. `testExtractDetails_nullBytes_throwsException()` — null → бросает IllegalArgumentException

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | new в методе | FQCN → {METHOD, сигнатура} |
| 2 | Вызов метода | FQCN владельца → {METHOD, сигнатура} |
| 3 | Статический блок | FQCN → {STATIC_BLOCK, ""} |
| 4 | Приведение типа | FQCN → {METHOD, сигнатура} |
| 5 | Доступ к полю | FQCN владельца → {METHOD, сигнатура} |
| 6 | Несколько использований в одном методе | Без дубликатов |