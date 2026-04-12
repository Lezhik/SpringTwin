# 026-field-type-extractor-details — API

## Статус: complete

## Описание

Добавление детализированного метода извлечения типов полей в `FieldTypeExtractor`. Новый метод возвращает `Map<String, Set<LinkDetails>>` вместо `Set<String>`, где ключ — FQCN зависимого класса, а значение — множество LinkDetails с типом связи FIELD и именем поля в details.

## Классы

### `spring.twin.scan.FieldTypeExtractor` (модификация)

**Новые методы:**

- `Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes)` — анализирует байткод класса и извлекает все типы полей с деталями связей. Возвращает карту: FQCN зависимого класса → множество LinkDetails. **Метод должен быть аннотирован Javadoc на английском языке.**

**Правила формирования LinkDetails:**

- Тип поля (из дескриптора) → `LinkDetails.of(LinkType.FIELD, fieldName)` где fieldName — имя поля
- Generic-параметры поля → `LinkDetails.of(LinkType.FIELD, fieldName)` — наследуют тип FIELD и имя поля, из которого извлечены

**Существующие методы:**

- `Set<String> extract(byte[] classBytes)` — остаётся без изменений для обратной совместимости

## Зависимости

- Фича 024: `LinkDetails`, `LinkType`
- Фича 002: `FqcnNormalizer`
- Фича 005: `GenericTypeExtractor`

## Результат

Добавлен метод `extractDetails()` в класс `FieldTypeExtractor` с заглушкой реализации.