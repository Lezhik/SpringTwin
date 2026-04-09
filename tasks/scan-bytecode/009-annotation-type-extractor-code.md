# 009-annotation-type-extractor — Реализация

## Статус: pending

## Описание

Реализация `AnnotationTypeExtractor` для извлечения зависимостей по аннотациям.

## Классы и методы

### `spring.twin.scan.AnnotationTypeExtractor`

**Тип:** Spring bean (`@Component`)

**Реализация:**

- `Set<String> extract(byte[] classBytes)` — создаёт `ClassNode` через ASM `ClassReader`. Извлекает:
  1. Аннотации класса из `ClassNode.visibleAnnotations` и `ClassNode.invisibleAnnotations` — дескриптор аннотации (`AnnotationNode.desc`) конвертируется через `FqcnNormalizer.fromDescriptor()`
  2. Аннотации полей — для каждого поля из `ClassNode.fields` обходит `visibleAnnotations` и `invisibleAnnotations`
  3. Аннотации методов — для каждого метода из `ClassNode.methods` обходит `visibleAnnotations` и `invisibleAnnotations`
  4. Аннотации параметров методов — из `MethodNode.visibleParameterAnnotations` и `MethodNode.invisibleParameterAnnotations`
  Результат — объединённое множество всех найденных FQCN аннотаций. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Прочитать байткод через `ClassReader` → `ClassNode`
2. Собрать аннотации класса, полей, методов и параметров
3. Каждый дескриптор аннотации конвертировать через `FqcnNormalizer.fromDescriptor()`
4. Объединить все типы в одно множество FQCN