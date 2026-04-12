# 027-method-type-extractor-details — Тесты

## Статус: complete

## Описание

Unit тесты для нового метода `extractDetails()` в `MethodTypeExtractor`. Тесты используют скомпилированные классы-образцы из `src/test/java/spring/twin/testee/`.

## Тестируемый класс

`spring.twin.scan.MethodTypeExtractor`

## Тестируемый метод

`Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes)`

## Классы-образцы (testee)

Используются существующие:
- `MethodHolder.java` — класс с методами различных сигнатур

## Класс тестов

### `spring.twin.scan.MethodTypeExtractorDetailsTest`

**Методы тестов:**

1. `testExtractDetails_methodWithReturnType_returnsMethodLink()` — метод с возвращаемым типом `OrderModel` → карта содержит FQCN типа со значением `{LinkDetails.of(LinkType.METHOD, signature)}`
2. `testExtractDetails_methodWithParameterType_returnsMethodLink()` — метод с параметром `OrderModel model` → карта содержит FQCN типа со значением `{LinkDetails.of(LinkType.METHOD, signature)}`
3. `testExtractDetails_methodWithGenericReturnType_returnsMethodAndGenericLinks()` — метод с generic-возвращаемым типом `List<String>` → содержит FQCN List и String, оба с `LinkType.METHOD` и сигнатурой метода
4. `testExtractDetails_constructorWithParameter_returnsMethodLink()` — конструктор с параметром → содержит FQCN типа параметра с `LinkType.METHOD` и сигнатурой конструктора
5. `testExtractDetails_voidMethod_noReturnTypeLink()` — void метод без ссылочных параметров → не содержит лишних записей
6. `testExtractDetails_primitiveParams_notIncluded()` — примитивные параметры не включаются
7. `testExtractDetails_nullBytes_throwsException()` — null → бросает IllegalArgumentException

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Возвращаемый тип метода | FQCN типа → {METHOD, сигнатура} |
| 2 | Параметр метода | FQCN типа → {METHOD, сигнатура} |
| 3 | Generic-возвращаемый тип | Базовый тип + generic-параметры, все с METHOD |
| 4 | Конструктор с параметром | FQCN типа → {METHOD, сигнатура конструктора} |
| 5 | void метод | Нет лишних записей для void |
| 6 | Примитивные параметры | Не включаются |