# 010-code-usage-extractor — API

## Статус: complete

## Описание

Извлечение зависимостей по использованию типов в коде методов: вызовы методов, объявления переменных, статическая инициализация, создание объектов и другие использования типов в теле метода.

## Классы

### `spring.twin.scan.CodeUsageExtractor`

**Тип:** класс с внедрением через конструктор (Spring bean)

**Методы:**

- `Set<String> extract(byte[] classBytes)` — анализирует байткод класса и извлекает все типы, используемые в телах методов и статических инициализаторах. Использует ASM `MethodVisitor` для обхода инструкций метода. Извлекает типы из: `NEW`, `NEWARRAY`, `ANEWARRAY`, `CHECKCAST`, `INSTANCEOF`, `GETSTATIC`, `PUTSTATIC`, `GETFIELD`, `PUTFIELD`, `INVOKEVIRTUAL`, `INVOKESTATIC`, `INVOKESPECIAL`, `INVOKEINTERFACE`, `LDC` (с Class-значением). Возвращает множество FQCN. **Метод должен быть аннотирован Javadoc на английском языке.**

## Извлекаемые связи

- Создание объекта (`NEW`)
- Создание массива (`ANEWARRAY`)
- Приведение типа (`CHECKCAST`)
- Проверка типа (`INSTANCEOF`)
- Доступ к полю (чтение/запись, статическое/экземплярное)
- Вызов метода (virtual, static, special, interface)
- LDC с Class-значением

## Зависимости

- Фича 002: `FqcnNormalizer` — для конвертации внутренних имён
- Внешняя: `org.ow2.asm:asm-tree` — для парсинга байткода

## Результат

Создан класс `CodeUsageExtractor` с заглушками методов.