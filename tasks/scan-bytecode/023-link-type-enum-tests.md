# 023-link-type-enum — Тесты

## Статус: complete

## Описание

Unit тесты для enum `LinkType`.

## Тестируемый класс

`spring.twin.scan.LinkType`

## Класс тестов

### `spring.twin.scan.LinkTypeTest`

**Методы тестов:**

1. `testGetJsonName_superclass_returnsSUPERCLASS()` — `LinkType.SUPERCLASS.getJsonName()` возвращает `"SUPERCLASS"`
2. `testGetJsonName_interface_returnsINTERFACE()` — `LinkType.INTERFACE.getJsonName()` возвращает `"INTERFACE"`
3. `testGetJsonName_field_returnsFIELD()` — `LinkType.FIELD.getJsonName()` возвращает `"FIELD"`
4. `testGetJsonName_staticBlock_returnsSTATIC_BLOCK()` — `LinkType.STATIC_BLOCK.getJsonName()` возвращает `"STATIC_BLOCK"`
5. `testGetJsonName_method_returnsMETHOD()` — `LinkType.METHOD.getJsonName()` возвращает `"METHOD"`
6. `testGetJsonName_classAnnotation_returnsCLASS_ANNOTATION()` — `LinkType.CLASS_ANNOTATION.getJsonName()` возвращает `"CLASS_ANNOTATION"`
7. `testGetJsonName_fieldAnnotation_returnsFIELD_ANNOTATION()` — `LinkType.FIELD_ANNOTATION.getJsonName()` возвращает `"FIELD_ANNOTATION"`
8. `testGetJsonName_methodAnnotation_returnsMETHOD_ANNOTATION()` — `LinkType.METHOD_ANNOTATION.getJsonName()` возвращает `"METHOD_ANNOTATION"`
9. `testGetJsonName_methodArgAnnotation_returnsMETHOD_ARG_ANNOTATION()` — `LinkType.METHOD_ARG_ANNOTATION.getJsonName()` возвращает `"METHOD_ARG_ANNOTATION"`
10. `testValues_count_returnsNine()` — `LinkType.values()` содержит 9 констант
11. `testValueOf_validName_returnsConstant()` — `LinkType.valueOf("SUPERCLASS")` возвращает `LinkType.SUPERCLASS`

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Получение jsonName для каждой константы | Имя совпадает с именем константы |
| 2 | Количество констант | 9 штук |
| 3 | valueOf по имени | Соответствующая константа |