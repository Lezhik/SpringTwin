# 005-generic-type-extractor — API

## Статус: pending

## Описание

Извлечение всех типов из generic-сигнатур байткода. Обрабатывает generic-параметры классов, методов и полей, извлекая сам тип, его generic-параметры и вложенные generic-и.

## Классы

### `spring.twin.scan.GenericTypeExtractor`

**Тип:** утилитный класс (final, приватный конструктор)

**Методы:**

- `static Set<String> extractTypes(String signature)` — парсит generic-сигнатуру (формат JVM Signature) и извлекает все упомянутые типы в виде FQCN. Возвращает пустое множество если `signature` == null или не содержит ссылочных типов. **Метод должен быть аннотирован Javadoc на английском языке.**

- `static Set<String> extractTypeNames(String typeSignature)` — извлекает имена типов из одной type-сигнатуры (например `Ljava/util/List<Ljava/lang/String;>;` → `[java.util.List, java.lang.String]`). **Метод должен быть аннотирован Javadoc на английском языке.**

## Формат generic-сигнатур (JVM)

Примеры сигнатур:
- Класс: `<T:Ljava/lang/Object;>Ljava/lang/Object;`
- Поле: `Ljava/util/List<Ljava/lang/String;>;`
- Метод: `(Ljava/util/List<Ljava/lang/String;>;)Ljava/util/Map<Ljava/lang/String;Ljava/lang/Integer;>;;`

## Зависимости

- Фича 002: `FqcnNormalizer` — для конвертации внутренних имён в FQCN

## Результат

Создан класс `GenericTypeExtractor` с заглушками методов.