# 027-method-type-extractor-details — API

## Статус: pending

## Описание

Добавление детализированного метода извлечения типов методов в `MethodTypeExtractor`. Новый метод возвращает `Map<String, Set<LinkDetails>>` вместо `Set<String>`, где ключ — FQCN зависимого класса, а значение — множество LinkDetails с типом связи METHOD и сигнатурой метода в details.

## Классы

### `spring.twin.scan.MethodTypeExtractor` (модификация)

**Новые методы:**

- `Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes)` — анализирует байткод класса и извлекает все типы методов с деталями связей. Возвращает карту: FQCN зависимого класса → множество LinkDetails. **Метод должен быть аннотирован Javadoc на английском языке.**

**Правила формирования LinkDetails:**

- Тип аргумента метода → `LinkDetails.of(LinkType.METHOD, methodSignature)` где methodSignature — сигнатура метода в формате ASM
- Возвращаемый тип метода → `LinkDetails.of(LinkType.METHOD, methodSignature)`
- Generic-параметры метода → `LinkDetails.of(LinkType.METHOD, methodSignature)` — наследуют тип METHOD и сигнатуру метода

**Формат сигнатуры метода:**

Используется формат ASM: `ownerClassName.methodName(descriptor)`, например:
- `com/example/OrderService.add(Lcom/example/model/OrderModel;)`
- Для конструкторов: `com/example/OrderService.<init>(Lcom/example/model/OrderModel;)`

**Существующие методы:**

- `Set<String> extract(byte[] classBytes)` — остаётся без изменений для обратной совместимости

## Зависимости

- Фича 024: `LinkDetails`, `LinkType`
- Фича 002: `FqcnNormalizer`
- Фича 005: `GenericTypeExtractor`

## Результат

Добавлен метод `extractDetails()` в класс `MethodTypeExtractor` с заглушкой реализации.