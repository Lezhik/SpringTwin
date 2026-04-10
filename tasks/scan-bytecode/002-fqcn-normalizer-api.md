# 002-fqcn-normalizer — API

## Статус: complete

## Описание

Нормализация имён классов из JVM внутренних дескрипторов и сигнатур в полные имена классов (FQCN). Обрабатывает массивы (ссылка на базовый тип) и примитивы (отсекаются).

## Классы

### `spring.twin.scan.FqcnNormalizer`

**Тип:** утилитный класс (final, приватный конструктор)

**Методы:**

- `static Optional<String> fromInternalName(String internalName)` — преобразует JVM внутреннее имя (например `com/example/OrderService`) в FQCN (`com.example.OrderService`). Возвращает `Optional.empty()` для примитивов и null.
- `static Optional<String> fromDescriptor(String descriptor)` — извлекает FQCN из JVM дескриптора поля/типа (например `Lcom/example/OrderService;` → `com.example.OrderService`). Для массивов (например `[Lcom/example/OrderService;`) извлекает базовый тип. Возвращает `Optional.empty()` для примитивов и массивов примитивов.
- `static String internalToFqcn(String internalName)` — прямая конвертация внутреннего имени в FQCN заменой `/` на `.`. Не фильтрует примитивы.

## Зависимости

Нет зависимостей от других фич.

## Результат

Создан класс `FqcnNormalizer` с заглушками методов.