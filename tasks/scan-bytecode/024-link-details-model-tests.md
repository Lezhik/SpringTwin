# 024-link-details-model — Тесты

## Статус: complete

## Описание

Unit тесты для record `LinkDetails`.

## Тестируемый класс

`spring.twin.scan.LinkDetails`

## Класс тестов

### `spring.twin.scan.LinkDetailsTest`

**Методы тестов:**

1. `testOfTypeAndDetails_createsLinkDetails()` — `LinkDetails.of(LinkType.FIELD, "orderRepository")` создаёт объект с type=FIELD и details="orderRepository"
2. `testOfTypeOnly_createsLinkDetailsWithEmptyDetails()` — `LinkDetails.of(LinkType.SUPERCLASS)` создаёт объект с type=SUPERCLASS и details=""
3. `testEquals_sameTypeAndDetails_returnsTrue()` — два LinkDetails с одинаковыми type и details равны
4. `testEquals_differentType_returnsFalse()` — два LinkDetails с разным type не равны
5. `testEquals_differentDetails_returnsFalse()` — два LinkDetails с разным details не равны
6. `testHashCode_sameTypeAndDetails_sameHashCode()` — одинаковые LinkDetails имеют одинаковый hashCode
7. `testSetDuplicate_sameTypeAndDetails_notAddedTwice()` — добавление одинаковых LinkDetails в Set не создаёт дубликатов
8. `testSerialization_toJson_correctFormat()` — Jackson сериализация `LinkDetails.of(LinkType.FIELD, "orderRepository")` даёт JSON `{"type":"FIELD","details":"orderRepository"}`
9. `testSerialization_emptyDetails_correctFormat()` — Jackson сериализация `LinkDetails.of(LinkType.SUPERCLASS)` даёт JSON `{"type":"SUPERCLASS","details":""}`
10. `testDeserialization_fromJson_correctObject()` — Jackson десериализация `{"type":"METHOD","details":"add(OrderModel)"}` даёт LinkDetails с type=METHOD и details="add(OrderModel)"

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Создание с type и details | Корректный объект |
| 2 | Создание только с type | details = пустая строка |
| 3 | Равенство одинаковых объектов | true |
| 4 | Равенство разных объектов | false |
| 5 | hashCode одинаковых объектов | Совпадает |
| 6 | Дубликаты в Set | Не добавляются |
| 7 | Сериализация в JSON | Корректный формат |
| 8 | Десериализация из JSON | Корректный объект |