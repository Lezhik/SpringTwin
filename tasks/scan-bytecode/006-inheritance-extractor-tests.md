# 006-inheritance-extractor — Тесты

## Статус: pending

## Описание

Unit тесты для `InheritanceExtractor`. Тесты используют скомпилированные классы-образцы из `src/test/java/spring/twin/testee/`.

## Тестируемый класс

`spring.twin.scan.InheritanceExtractor`

## Классы-образцы (testee)

Необходимо создать в `src/test/java/spring/twin/testee/`:

- `InheritanceBase.java` — базовый класс без наследования
- `InheritanceChild.java` — класс, наследующий `InheritanceBase`
- `InheritanceImpl.java` — класс, имплементирующий `java.io.Serializable`
- `InheritanceGenericChild.java` — класс с generic-наследованием: `extends ArrayList<String>`

## Класс тестов

### `spring.twin.scan.InheritanceExtractorTest`

**Методы тестов:**

1. `testExtract_simpleClass_noInheritance_returnsEmpty()` — класс `Object` (без явного extends) → `java.lang.Object` не включается (базовый тип)
2. `testExtract_classExtendsAnother_returnsSuperClass()` — `InheritanceChild extends InheritanceBase` → содержит `spring.twin.testee.InheritanceBase`
3. `testExtract_classImplementsInterface_returnsInterface()` — `InheritanceImpl implements Serializable` → содержит `java.io.Serializable`
4. `testExtract_classExtendsGeneric_returnsGenericTypes()` — `InheritanceGenericChild extends ArrayList<String>` → содержит `java.util.ArrayList`, `java.lang.String`
5. `testExtract_interfaceExtendsInterface_returnsParentInterface()` — интерфейс, расширяющий другой интерфейс → содержит родительский интерфейс
6. `testExtract_nullBytes_throwsException()` — null → бросает исключение

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Класс без наследования | Пустое множество или только `java.lang.Object` (фильтруется) |
| 2 | extends другого класса | Суперкласс в множестве |
| 3 | implements интерфейс | Интерфейс в множестве |
| 4 | Generic-наследование | Суперкласс + generic-параметры |
| 5 | Интерфейс extends интерфейс | Родительский интерфейс в множестве |