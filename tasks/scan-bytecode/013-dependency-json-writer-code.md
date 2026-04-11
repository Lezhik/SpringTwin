# 013-dependency-json-writer — Реализация

## Статус: complete

## Описание

Реализация `DependencyJsonWriter` для записи графа зависимостей в JSON файл.

## Классы и методы

### `spring.twin.scan.DependencyJsonWriter`

**Тип:** Spring bean (`@Component`)

**Реализация:**

- `void write(Map<String, Set<String>> graph, Path outputFile)` — записывает граф в JSON:
  1. Создать `ObjectMapper` с `SerFeature.SORT_PROPERTIES_ALPHABETICALLY` и `SerializationFeature.INDENT_OUTPUT`
  2. Преобразовать `Map<String, Set<String>>` в `Map<String, List<String>>` — каждый Set в отсортированный List
  3. Использовать `TreeMap` для сортировки ключей
  4. Создать родительские директории через `Files.createDirectories(outputFile.getParent())`
  5. Записать JSON через `objectMapper.writeValue(outputFile.toFile(), sortedMap)`
  **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Конвертация Set → List для JSON сериализации (JSON не имеет Set)
2. Сортировка ключей и значений для детерминированности
3. Pretty print для читаемости
4. Создание директорий при необходимости
5. Запись через Jackson ObjectMapper