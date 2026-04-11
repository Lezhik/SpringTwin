# 008-method-type-extractor — Реализация

## Статус: complete

## Описание

Реализация `MethodTypeExtractor` для извлечения зависимостей по типам параметров и возвращаемых значений методов.

## Классы и методы

### `spring.twin.scan.MethodTypeExtractor`

**Тип:** Spring bean (`@Component`)

**Реализация:**

- `Set<String> extract(byte[] classBytes)` — создаёт `ClassNode` через ASM `ClassReader`. Для каждого метода из `ClassNode.methods`:
  1. Парсит дескриптор метода (`MethodNode.desc`) через `Type.getArgumentTypes()` и `Type.getReturnType()` — извлекает типы параметров и возвращаемый тип через `FqcnNormalizer.fromDescriptor()`
  2. Если `MethodNode.signature` не null — извлекает типы через `GenericTypeExtractor.extractTypes()`
  Результат — объединённое множество всех найденных FQCN. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Прочитать байткод через `ClassReader` → `ClassNode`
2. Обойти все методы (`ClassNode.methods`)
3. Для каждого метода:
   - Из дескриптора извлечь типы параметров через `Type.getArgumentTypes(desc)` и каждый конвертировать через `FqcnNormalizer.fromDescriptor()`
   - Из дескриптора извлечь возвращаемый тип через `Type.getReturnType(desc)` и конвертировать через `FqcnNormalizer.fromDescriptor()`
   - Из generic-сигнатуры (`signature`) извлечь типы через `GenericTypeExtractor.extractTypes()`
4. Объединить все типы в одно множество FQCN