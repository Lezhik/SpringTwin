# 025-inheritance-extractor-details — Реализация

## Статус: pending

## Описание

Реализация метода `extractDetails()` в `InheritanceExtractor` для детализированного извлечения наследования.

## Классы и методы

### `spring.twin.scan.InheritanceExtractor` (модификация)

**Новый метод:**

- `Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes)` — анализирует байткод класса и извлекает все типы наследования с деталями. **Метод должен быть аннотирован Javadoc на английском языке.**

**Реализация:**

1. Создать `ClassNode` через ASM `ClassReader`
2. Извлечь суперкласс: если `superName` не null и не `java/lang/Object`:
   - Конвертировать через `FqcnNormalizer.fromInternalName()`
   - Добавить в карту: `fqcn → {LinkDetails.of(LinkType.SUPERCLASS)}`
3. Извлечь интерфейсы: для каждого интерфейса из `ClassNode.interfaces`:
   - Конвертировать через `FqcnNormalizer.fromInternalName()`
   - Добавить в карту: `fqcn → {LinkDetails.of(LinkType.INTERFACE)}`
4. Извлечь generic-типы из `ClassNode.signature`:
   - Извлечь типы через `GenericTypeExtractor.extractTypes()`
   - Каждый generic-тип наследует тип связи контекста: если из superclass — SUPERCLASS, если из interface — INTERFACE
   - Для упрощения: generic-типы из signature наследования получают тип SUPERCLASS (т.к. signature относится к extends)
5. Объединить все записи в результирующую карту `Map<String, Set<LinkDetails>>`

**Вспомогательный приватный метод:**

- `void addDetail(Map<String, Set<LinkDetails>> map, String fqcn, LinkDetails detail)` — добавляет LinkDetails в Set для указанного FQCN, создавая Set если нужно. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Прочитать байткод через `new ClassReader(classBytes)` → `ClassNode`
2. Извлечь суперкласс → LinkType.SUPERCLASS, details=""
3. Извлечь интерфейсы → LinkType.INTERFACE, details=""
4. Извлечь generic-типы из signature → наследуют тип SUPERCLASS
5. Собрать всё в Map<String, Set<LinkDetails>>