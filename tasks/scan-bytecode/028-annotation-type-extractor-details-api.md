# 028-annotation-type-extractor-details — API

## Статус: complete

## Описание

Добавление детализированного метода извлечения аннотаций в `AnnotationTypeExtractor`. Новый метод возвращает `Map<String, Set<LinkDetails>>` вместо `Set<String>`, где ключ — FQCN зависимого класса, а значение — множество LinkDetails с типом связи и контекстом аннотации.

## Классы

### `spring.twin.scan.AnnotationTypeExtractor` (модификация)

**Новые методы:**

- `Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes)` — анализирует байткод класса и извлекает все типы аннотаций с деталями связей. Возвращает карту: FQCN зависимого класса → множество LinkDetails. **Метод должен быть аннотирован Javadoc на английском языке.**

**Правила формирования LinkDetails:**

- Аннотация к классу → `LinkDetails.of(LinkType.CLASS_ANNOTATION)` (details пустое)
- Аннотация к полю → `LinkDetails.of(LinkType.FIELD_ANNOTATION, fieldName)` где fieldName — имя поля
- Аннотация к методу → `LinkDetails.of(LinkType.METHOD_ANNOTATION, methodSignature)` где methodSignature — сигнатура метода
- Аннотация к аргументу метода → `LinkDetails.of(LinkType.METHOD_ARG_ANNOTATION, methodSignature)` где methodSignature — сигнатура метода

**Формат сигнатуры метода:**

Используется формат ASM: `ownerClassName.methodName(descriptor)`

**Существующие методы:**

- `Set<String> extract(byte[] classBytes)` — остаётся без изменений для обратной совместимости

## Зависимости

- Фича 024: `LinkDetails`, `LinkType`
- Фича 002: `FqcnNormalizer`

## Результат

Добавлен метод `extractDetails()` в класс `AnnotationTypeExtractor` с заглушкой реализации.