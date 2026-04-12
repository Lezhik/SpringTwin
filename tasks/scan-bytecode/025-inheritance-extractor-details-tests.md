# 025-inheritance-extractor-details — Тесты

## Статус: pending

## Описание

Unit тесты для нового метода `extractDetails()` в `InheritanceExtractor`. Тесты используют скомпилированные классы-образцы из `src/test/java/spring/twin/testee/`.

## Тестируемый класс

`spring.twin.scan.InheritanceExtractor`

## Тестируемый метод

`Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes)`

## Классы-образцы (testee)

Используются существующие:
- `InheritanceBase.java`
- `InheritanceChild.java`
- `InheritanceImpl.java`
- `InheritanceGenericChild.java`
- `InheritanceChildInterface.java`

## Класс тестов

### `spring.twin.scan.InheritanceExtractorDetailsTest`

**Методы тестов:**

1. `testExtractDetails_simpleClass_noInheritance_returnsEmpty()` — класс без extends (кроме Object) → пустая карта
2. `testExtractDetails_classExtendsAnother_returnsSuperclassLink()` — `InheritanceChild extends InheritanceBase` → карта содержит ключ `spring.twin.testee.InheritanceBase` со значением `{LinkDetails.of(LinkType.SUPERCLASS)}`
3. `testExtractDetails_classImplementsInterface_returnsInterfaceLink()` — `InheritanceImpl implements Serializable` → карта содержит ключ `java.io.Serializable` со значением `{LinkDetails.of(LinkType.INTERFACE)}`
4. `testExtractDetails_classExtendsGeneric_returnsSuperclassAndGenericTypes()` — `InheritanceGenericChild extends ArrayList<String>` → карта содержит `java.util.ArrayList` с `LinkDetails.of(LinkType.SUPERCLASS)` и `java.lang.String` с `LinkDetails.of(LinkType.SUPERCLASS)` (generic наследует тип SUPERCLASS)
5. `testExtractDetails_interfaceExtendsInterface_returnsInterfaceLink()` — интерфейс, расширяющий другой интерфейс → содержит родительский интерфейс с `LinkType.INTERFACE`
6. `testExtractDetails_nullBytes_throwsException()` — null → бросает IllegalArgumentException

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Класс без наследования | Пустая карта |
| 2 | extends другого класса | Ключ — FQCN суперкласса, значение — SUPERCLASS |
| 3 | implements интерфейс | Ключ — FQCN интерфейса, значение — INTERFACE |
| 4 | Generic-наследование | Суперкласс + generic-параметры, все с типом SUPERCLASS |
| 5 | Интерфейс extends интерфейс | Ключ — FQCN родительского интерфейса, значение — INTERFACE |