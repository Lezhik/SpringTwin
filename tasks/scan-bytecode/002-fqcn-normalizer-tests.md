# 002-fqcn-normalizer — Тесты

## Статус: complete

## Описание

Unit тесты для `FqcnNormalizer`.

## Тестируемый класс

`spring.twin.scan.FqcnNormalizer`

## Класс тестов

### `spring.twin.scan.FqcnNormalizerTest`

**Методы тестов:**

1. `testFromInternalName_simpleClass_returnsFqcn()` — `com/example/OrderService` → `Optional.of("com.example.OrderService")`
2. `testFromInternalName_nestedClass_returnsFqcn()` — `com/example/Outer$Inner` → `Optional.of("com.example.Outer$Inner")`
3. `testFromInternalName_primitiveInt_returnsEmpty()` — `int` → `Optional.empty()`
4. `testFromInternalName_primitiveBoolean_returnsEmpty()` — `boolean` → `Optional.empty()`
5. `testFromInternalName_primitiveVoid_returnsEmpty()` — `void` → `Optional.empty()`
6. `testFromInternalName_null_returnsEmpty()` — null → `Optional.empty()`
7. `testFromInternalName_allPrimitives_returnEmpty()` — проверка всех 8 примитивов + void
8. `testFromDescriptor_objectType_returnsFqcn()` — `Lcom/example/OrderService;` → `Optional.of("com.example.OrderService")`
9. `testFromDescriptor_arrayOfObject_returnsBaseType()` — `[Lcom/example/OrderService;` → `Optional.of("com.example.OrderService")`
10. `testFromDescriptor_multiDimensionalArrayOfObject_returnsBaseType()` — `[[Lcom/example/OrderService;` → `Optional.of("com.example.OrderService")`
11. `testFromDescriptor_primitiveInt_returnsEmpty()` — `I` → `Optional.empty()`
12. `testFromDescriptor_arrayOfPrimitiveInt_returnsEmpty()` — `[I` → `Optional.empty()`
13. `testFromDescriptor_null_returnsEmpty()` — null → `Optional.empty()`
14. `testInternalToFqcn_convertsSlashesToDots()` — `com/example/Service` → `com.example.Service`
15. `testInternalToFqcn_simpleName_noSlashes()` — `Service` → `Service`
16. `testInternalToFqcn_dollarSign_preserved()` — `com/example/Outer$Inner` → `com.example.Outer$Inner`

## Сценарии

| # | Сценарий | Вход | Ожидаемый результат |
|---|----------|------|-------------------|
| 1 | Внутреннее имя класса | `com/example/Service` | `com.example.Service` |
| 2 | Примитивный тип | `int` | empty |
| 3 | Дескриптор объекта | `Lcom/example/Service;` | `com.example.Service` |
| 4 | Дескриптор массива объектов | `[Lcom/example/Service;` | `com.example.Service` |
| 5 | Дескриптор массива примитивов | `[I` | empty |
| 6 | Многомерный массив объектов | `[[Lcom/example/Service;` | `com.example.Service` |
| 7 | Вложенный класс | `com/example/Outer$Inner` | `com.example.Outer$Inner` |
| 8 | null вход | null | empty |