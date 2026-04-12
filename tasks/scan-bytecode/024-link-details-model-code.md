# 024-link-details-model — Реализация

## Статус: pending

## Описание

Реализация record `LinkDetails` для хранения детальной информации о связи между классами.

## Классы и методы

### `spring.twin.scan.LinkDetails`

**Тип:** record

**Поля:**

- `LinkType type` — тип связи
- `String details` — дополнительная информация о связи

**Реализация:**

- `static LinkDetails of(LinkType type, String details)` — создаёт новый экземпляр LinkDetails с указанным типом и деталями. Если details равен null, заменяется на пустую строку. **Метод должен быть аннотирован Javadoc на английском языке.**
- `static LinkDetails of(LinkType type)` — создаёт новый экземпляр LinkDetails с указанным типом и пустой строкой в details. **Метод должен быть аннотирован Javadoc на английском языке.**

**Jackson аннотации:**

- `@JsonProperty("type")` на поле type — для сериализации type как строки
- `@JsonProperty("details")` на поле details
- Пользовательский сериализатор для `LinkType`: `type.getJsonName()` для получения строкового значения
- Пользовательский десериализатор для `LinkType`: `LinkType.valueOf(string)` для восстановления из строки

## Логика работы

1. Record с двумя полями: type и details
2. Фабричные методы для удобного создания экземпляров
3. equals/hashCode на основе обоих полей (автоматически через record)
4. Jackson сериализация: type → строка (имя константы), details → строка
5. Jackson десериализация: строка → LinkType.valueOf(), строка → details