# 028-annotation-type-extractor-details — Реализация

## Статус: pending

## Описание

Реализация метода `extractDetails()` в `AnnotationTypeExtractor` для детализированного извлечения аннотаций.

## Классы и методы

### `spring.twin.scan.AnnotationTypeExtractor` (модификация)

**Новый метод:**

- `Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes)` — анализирует байткод класса и извлекает все типы аннотаций с деталями. **Метод должен быть аннотирован Javadoc на английском языке.**

**Реализация:**

1. Создать `ClassNode` через ASM `ClassReader`
2. Извлечь имя класса из `ClassNode.name` для формирования сигнатур
3. Аннотации класса (`visibleAnnotations`, `invisibleAnnotations`):
   - Для каждой аннотации: `FqcnNormalizer.fromDescriptor(annotation.desc)` → добавить `fqcn → {LinkDetails.of(LinkType.CLASS_ANNOTATION)}`
4. Аннотации полей:
   - Для каждого поля и каждой его аннотации: `FqcnNormalizer.fromDescriptor(annotation.desc)` → добавить `fqcn → {LinkDetails.of(LinkType.FIELD_ANNOTATION, field.name)}`
5. Аннотации методов:
   - Для каждого метода и каждой его аннотации: `FqcnNormalizer.fromDescriptor(annotation.desc)` → добавить `fqcn → {LinkDetails.of(LinkType.METHOD_ANNOTATION, methodSignature)}`
   - methodSignature = `className + "." + method.name + method.desc`
6. Аннотации параметров методов:
   - Для каждого метода и каждого параметра с аннотациями: `FqcnNormalizer.fromDescriptor(annotation.desc)` → добавить `fqcn → {LinkDetails.of(LinkType.METHOD_ARG_ANNOTATION, methodSignature)}`
7. Вернуть результирующую карту

**Вспомогательные приватные методы:**

- `void addDetail(Map<String, Set<LinkDetails>> map, String fqcn, LinkDetails detail)` — добавляет LinkDetails в Set для указанного FQCN. **Метод должен быть аннотирован Javadoc на английском языке.**
- `void extractAnnotationDetails(List<AnnotationNode> annotations, LinkType linkType, String details, Map<String, Set<LinkDetails>> result)` — извлекает FQCN из списка аннотаций и добавляет с указанным типом и деталями. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Прочитать байткод через `ClassReader` → `ClassNode`
2. Класс-аннотации → CLASS_ANNOTATION, details=""
3. Поле-аннотации → FIELD_ANNOTATION, details=имя поля
4. Метод-аннотации → METHOD_ANNOTATION, details=сигнатура метода
5. Параметр-аннотации → METHOD_ARG_ANNOTATION, details=сигнатура метода
6. Собрать всё в Map<String, Set<LinkDetails>>