# 027-method-type-extractor-details — Реализация

## Статус: pending

## Описание

Реализация метода `extractDetails()` в `MethodTypeExtractor` для детализированного извлечения типов методов.

## Классы и методы

### `spring.twin.scan.MethodTypeExtractor` (модификация)

**Новый метод:**

- `Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes)` — анализирует байткод класса и извлекает все типы методов с деталями. **Метод должен быть аннотирован Javadoc на английском языке.**

**Реализация:**

1. Создать `ClassReader` и `ClassNode` из байткода
2. Извлечь имя класса из `ClassNode.name` для формирования сигнатуры
3. Для каждого метода из `ClassNode.methods`:
   - Сформировать сигнатуру метода: `className + "." + method.name + method.desc`
   - Извлечь типы аргументов из `Type.getArgumentTypes(method.desc)`:
     - Каждый тип: `FqcnNormalizer.fromDescriptor(argType.getDescriptor())` → добавить `fqcn → {LinkDetails.of(LinkType.METHOD, signature)}`
   - Извлечь возвращаемый тип из `Type.getReturnType(method.desc)`:
     - `FqcnNormalizer.fromDescriptor(returnType.getDescriptor())` → добавить `fqcn → {LinkDetails.of(LinkType.METHOD, signature)}`
   - Если есть generic-сигнатура `method.signature`:
     - Извлечь типы через `GenericTypeExtractor.extractTypes(method.signature)`
     - Каждый generic-тип: добавить `genericFqcn → {LinkDetails.of(LinkType.METHOD, signature)}`
4. Вернуть результирующую карту

**Вспомогательный приватный метод:**

- `void addDetail(Map<String, Set<LinkDetails>> map, String fqcn, LinkDetails detail)` — добавляет LinkDetails в Set для указанного FQCN. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Прочитать байткод через `ClassReader` → `ClassNode`
2. Для каждого метода: сформировать сигнатуру в формате ASM
3. Извлечь типы параметров и возвращаемого значения → LinkType.METHOD, details=сигнатура
4. Извлечь generic-типы → LinkType.METHOD, details=сигнатура
5. Собрать всё в Map<String, Set<LinkDetails>>