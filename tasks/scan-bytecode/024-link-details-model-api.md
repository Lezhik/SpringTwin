# 024-link-details-model — API

## Статус: pending

## Описание

Модель данных `LinkDetails` для хранения детальной информации о связи между классами. Содержит тип связи (`LinkType`) и дополнительное описание (`details`), зависящее от типа.

## Классы

### `spring.twin.scan.LinkDetails`

**Тип:** record

**Поля:**

- `LinkType type` — тип связи (SUPERCLASS, INTERFACE, FIELD, METHOD и т.д.)
- `String details` — дополнительная информация о связи. Зависит от типа:
  - SUPERCLASS — пустая строка
  - INTERFACE — пустая строка
  - FIELD — имя поля
  - STATIC_BLOCK — пустая строка
  - METHOD — сигнатура метода
  - CLASS_ANNOTATION — пустая строка
  - FIELD_ANNOTATION — имя поля
  - METHOD_ANNOTATION — сигнатура метода
  - METHOD_ARG_ANNOTATION — сигнатура метода

**Методы:**

- `static LinkDetails of(LinkType type, String details)` — фабричный метод для создания экземпляра. **Метод должен быть аннотирован Javadoc на английском языке.**
- `static LinkDetails of(LinkType type)` — фабричный метод для создания экземпляра с пустым details. **Метод должен быть аннотирован Javadoc на английском языке.**

**Требования к equals/hashCode:**

- Перегрузить `equals()` и `hashCode()` чтобы избежать дублирования одинаковых ссылок в `Set<LinkDetails>`. Два `LinkDetails` считаются равными, если равны их `type` и `details`.

**Требования к Jackson сериализации:**

- Сериализуется в JSON как `{"type": "FIELD", "details": "orderRepository"}`
- Поле `type` сериализуется как строка (имя константы LinkType)
- Поле `details` сериализуется как строка

## Зависимости

- Фича 023: `LinkType`

## Результат

Создан record `LinkDetails` с заглушками методов.