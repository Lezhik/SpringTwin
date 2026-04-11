# 013-dependency-json-writer — API

## Статус: complete

## Описание

Запись графа зависимостей в JSON файл в формате `dependencies.json` согласно SPEC.

## Классы

### `spring.twin.scan.DependencyJsonWriter`

**Тип:** Spring bean

**Методы:**

- `void write(Map<String, Set<String>> graph, Path outputFile)` — записывает граф зависимостей в JSON файл. Формат: объект, где ключи — FQCN классов, значения — отсортированные массивы FQCN зависимостей. Использует Jackson `ObjectMapper`. Создаёт родительские директории если не существуют. **Метод должен быть аннотирован Javadoc на английском языке.**

## Формат выходного файла

```json
{
  "com.example.OrderService": [
    "com.example.PaymentClient",
    "com.example.repository.OrderRepository"
  ],
  "com.example.repository.OrderRepository": [
    "com.example.model.OrderModel"
  ]
}
```

## Требования к формату

- Ключи отсортированы по алфавиту
- Значения (массивы) отсортированы по алфавиту
- Pretty print с отступами (2 пробела)
- UTF-8 кодировка

## Зависимости

- Внешняя: `com.fasterxml.jackson.core:jackson-databind` (через spring-boot-starter)

## Результат

Создан класс `DependencyJsonWriter` с заглушками методов.