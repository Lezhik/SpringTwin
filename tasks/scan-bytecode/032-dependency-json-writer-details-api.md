# 032-dependency-json-writer-details — API

## Описание

Добавление метода записи детализированного графа в JSON в `DependencyJsonWriter`. Новый метод записывает `Map<String, Map<String, Set<LinkDetails>>>` в формат, соответствующий новой спецификации SPEC.

## Классы

### `spring.twin.scan.DependencyJsonWriter` (модификация)

**Новые методы:**

- `void writeDetails(Map<String, Map<String, Set<LinkDetails>>> graph, Path outputFile)` — записывает детализированный граф зависимостей в JSON файл в новом формате. **Метод должен быть аннотирован Javadoc на английском языке.**

**Формат выходного файла (согласно SPEC):**

```json
{
  "com.example.OrderService": {
    "com.example.PaymentClient": [
      {"type": "FIELD", "details": "paymentClient"}
    ],
    "com.example.repository.OrderRepository": [
      {"type": "FIELD", "details": "orderRepository"}
    ]
  },
  "com.example.repository.OrderRepository": {
    "com.example.model.OrderModel": [
      {"type": "METHOD", "details": "Lcom/example/repository/OrderRepository;add(Lcom/example/model/OrderModel;)"}
    ]
  }
}
```

**Требования к формату:**

- Ключи первого уровня отсортированы по алфавиту
- Ключи второго уровня отсортированы по алфавиту
- Массивы LinkDetails отсортированы (по type, затем по details)
- Pretty print с 2-мя пробелами отступа
- UTF-8 кодировка
- Пустой граф записывается как `{}`
- LinkDetails сериализуется как объект с полями `type` и `details`
- LinkType сериализуется как строковое значение (имя enum)

**Существующие методы:**

- `void write(Map<String, Set<String>> graph, Path outputFile)` — остаётся без изменений для обратной совместимости

## Зависимости

- Фича 024: `LinkDetails`, `LinkType`
- Внешняя: `com.fasterxml.jackson.core:jackson-databind`

## Результат

Добавлен метод `writeDetails()` в класс `DependencyJsonWriter` с заглушкой реализации.