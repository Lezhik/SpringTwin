# 026-field-type-extractor-details — Реализация

## Статус: complete

## Описание

Реализация метода `extractDetails()` в `FieldTypeExtractor` для детализированного извлечения типов полей.

## Классы и методы

### `spring.twin.scan.FieldTypeExtractor` (модификация)

**Новый метод:**

- `Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes)` — анализирует байткод класса и извлекает все типы полей с деталями. **Метод должен быть аннотирован Javadoc на английском языке.**

**Реализация:**

1. Создать `ClassNode` через ASM `ClassReader`
2. Для каждого поля из `ClassNode.fields`:
   - Извлечь тип из дескриптора через `FqcnNormalizer.fromDescriptor(field.desc)`
   - Если тип найден (не примитив): добавить в карту `fqcn → {LinkDetails.of(LinkType.FIELD, field.name)}`
   - Если есть generic-сигнатура `field.signature`:
     - Извлечь типы через `GenericTypeExtractor.extractTypeNames(field.signature)`
     - Каждый generic-тип: добавить в карту `genericFqcn → {LinkDetails.of(LinkType.FIELD, field.name)}`
3. Вернуть результирующую карту

**Вспомогательный приватный метод:**

- `void addDetail(Map<String, Set<LinkDetails>> map, String fqcn, LinkDetails detail)` — добавляет LinkDetails в Set для указанного FQCN. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Прочитать байткод через `new ClassReader(classBytes)` → `ClassNode`
2. Для каждого поля: извлечь тип → LinkType.FIELD, details=имя поля
3. Для каждого поля с generic: извлечь generic-типы → LinkType.FIELD, details=имя поля
4. Собрать всё в Map<String, Set<LinkDetails>>