# 006-inheritance-extractor — API

## Статус: pending

## Описание

Извлечение зависимостей наследования и имплементации интерфейсов из `.class` файла. Использует ASM для анализа байткода.

## Классы

### `spring.twin.scan.InheritanceExtractor`

**Тип:** класс с внедрением через конструктор (Spring bean)

**Методы:**

- `Set<String> extract(byte[] classBytes)` — анализирует байткод класса и извлекает все типы, от которых класс наследуется или которые имплементирует. Включает: суперкласс, прямые интерфейсы, а также типы из generic-сигнатур наследования. Возвращает множество FQCN. **Метод должен быть аннотирован Javadoc на английском языке.**

## Извлекаемые связи

- Суперкласс (extends) — из `ClassNode.superName`
- Интерфейсы (implements) — из `ClassNode.interfaces`
- Generic-параметры наследования — из `ClassNode.signature` через `GenericTypeExtractor`

## Зависимости

- Фича 002: `FqcnNormalizer` — для конвертации внутренних имён
- Фича 005: `GenericTypeExtractor` — для извлечения типов из generic-сигнатур
- Внешняя: `org.ow2.asm:asm-tree` — для парсинга байткода

## Результат

Создан класс `InheritanceExtractor` с заглушками методов.