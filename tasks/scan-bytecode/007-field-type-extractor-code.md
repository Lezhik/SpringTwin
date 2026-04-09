# 007-field-type-extractor — Реализация

## Статус: pending

## Описание

Реализация `FieldTypeExtractor` для извлечения зависимостей по типам полей.

## Классы и методы

### `spring.twin.scan.FieldTypeExtractor`

**Тип:** Spring bean (`@Component`)

**Реализация:**

- `Set<String> extract(byte[] classBytes)` — создаёт `ClassNode` через ASM `ClassReader`. Для каждого поля из `ClassNode.fields`:
  1. Извлекает тип из `FieldNode.desc` через `FqcnNormalizer.fromDescriptor()`
  2. Если `FieldNode.signature` не null — извлекает типы через `GenericTypeExtractor.extractTypeNames()`
  Результат — объединённое множество всех найденных FQCN. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Прочитать байткод через `ClassReader` → `ClassNode`
2. Обойти все поля (`ClassNode.fields`)
3. Для каждого поля:
   - Из дескриптора (`desc`) извлечь тип через `FqcnNormalizer.fromDescriptor()`
   - Из generic-сигнатуры (`signature`) извлечь типы через `GenericTypeExtractor.extractTypeNames()`
4. Объединить все типы в одно множество FQCN