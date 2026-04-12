# 025-inheritance-extractor-details — API

## Статус: pending

## Описание

Добавление детализированного метода извлечения наследования и имплементации в `InheritanceExtractor`. Новый метод возвращает `Map<String, Set<LinkDetails>>` вместо `Set<String>`, где ключ — FQCN зависимого класса, а значение — множество LinkDetails с типом связи.

## Классы

### `spring.twin.scan.InheritanceExtractor` (модификация)

**Новые методы:**

- `Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes)` — анализирует байткод класса и извлекает все типы наследования с деталями связей. Возвращает карту: FQCN зависимого класса → множество LinkDetails. **Метод должен быть аннотирован Javadoc на английском языке.**

**Правила формирования LinkDetails:**

- Суперкласс → `LinkDetails.of(LinkType.SUPERCLASS)` (details пустое)
- Интерфейс → `LinkDetails.of(LinkType.INTERFACE)` (details пустое)
- Generic-параметры наследования → наследуют тип связи контекста: если из superclass — `SUPERCLASS`, если из interface — `INTERFACE`

**Существующие методы:**

- `Set<String> extract(byte[] classBytes)` — остаётся без изменений для обратной совместимости

## Зависимости

- Фича 024: `LinkDetails`, `LinkType`
- Фича 002: `FqcnNormalizer`
- Фича 005: `GenericTypeExtractor`

## Результат

Добавлен метод `extractDetails()` в класс `InheritanceExtractor` с заглушкой реализации.