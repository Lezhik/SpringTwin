# 029-code-usage-extractor-details — API

## Статус: complete

## Описание

Добавление детализированного метода извлечения использования типов в коде в `CodeUsageExtractor`. Новый метод возвращает `Map<String, Set<LinkDetails>>` вместо `Set<String>`, где ключ — FQCN зависимого класса, а значение — множество LinkDetails с типом связи STATIC_BLOCK или METHOD и сигнатурой метода в details.

## Классы

### `spring.twin.scan.CodeUsageExtractor` (модификация)

**Новые методы:**

- `Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes)` — анализирует байткод класса и извлекает все типы, используемые в коде методов и статических блоках, с деталями связей. Возвращает карту: FQCN зависимого класса → множество LinkDetails. **Метод должен быть аннотирован Javadoc на английском языке.**

**Правила формирования LinkDetails:**

- Использование в статическом блоке инициализации (`<clinit>`) → `LinkDetails.of(LinkType.STATIC_BLOCK)` (details пустое)
- Использование в методе (вызов метода, объявление переменной, создание объекта и т.д.) → `LinkDetails.of(LinkType.METHOD, methodSignature)` где methodSignature — сигнатура метода

**Формат сигнатуры метода:**

Используется формат ASM: `ownerClassName.methodName(descriptor)`

**Существующие методы:**

- `Set<String> extract(byte[] classBytes)` — остаётся без изменений для обратной совместимости

## Зависимости

- Фича 024: `LinkDetails`, `LinkType`
- Фича 002: `FqcnNormalizer`

## Результат

Добавлен метод `extractDetails()` в класс `CodeUsageExtractor` с заглушкой реализации.