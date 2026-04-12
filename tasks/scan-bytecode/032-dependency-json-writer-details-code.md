# 032-dependency-json-writer-details — Реализация

## Статус: complete

## Описание

Реализация метода `writeDetails()` в `DependencyJsonWriter` для записи детализированного графа в JSON нового формата.

## Классы и методы

### `spring.twin.scan.DependencyJsonWriter` (модификация)

**Новый метод:**

- `void writeDetails(Map<String, Map<String, Set<LinkDetails>>> graph, Path outputFile)` — записывает детализированный граф зависимостей в JSON файл. **Метод должен быть аннотирован Javadoc на английском языке.**

**Реализация:**

1. Создать `ObjectMapper` с `INDENT_OUTPUT`
2. Настроить сериализацию `LinkType` — использовать `@JsonFormat(shape = Shape.STRING)` или зарегистрировать кастомный сериализатор, чтобы enum записывался как строка (имя), а не как число
3. Преобразовать `Map<String, Map<String, Set<LinkDetails>>>` в отсортированную структуру:
   - Внешний TreeMap для сортировки ключей первого уровня
   - Внутренний TreeMap для сортировки ключей второго уровня
   - Set<LinkDetails> → отсортированный List (по type, затем по details)
4. Создать родительские директории если не существуют
5. Записать JSON:
   - Пустой граф → `{}`
   - Непустой → `objectMapper.writeValue(outputFile.toFile(), sortedMap)`

**Настройка Jackson для LinkDetails:**

- Убедиться, что `LinkType` сериализуется как строка через `@JsonFormat(shape = Shape.STRING)` на enum
- Или зарегистрировать `SimpleModule` с кастомным сериализатором для `LinkType`
- `LinkDetails` должен сериализоваться как `{"type": "FIELD", "details": "fieldName"}` — проверить что поля record сериализуются корректно

## Логика работы

1. Отсортировать ключи первого и второго уровня
2. Преобразовать Set в отсортированный List
3. Настроить Jackson для корректной сериализации LinkType и LinkDetails
4. Записать JSON с pretty print