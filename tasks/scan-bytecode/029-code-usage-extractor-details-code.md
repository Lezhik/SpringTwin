# 029-code-usage-extractor-details — Реализация

## Статус: pending

## Описание

Реализация метода `extractDetails()` в `CodeUsageExtractor` для детализированного извлечения использования типов в коде.

## Классы и методы

### `spring.twin.scan.CodeUsageExtractor` (модификация)

**Новый метод:**

- `Map<String, Set<LinkDetails>> extractDetails(byte[] classBytes)` — анализирует байткод класса и извлекает все типы, используемые в коде, с деталями. **Метод должен быть аннотирован Javadoc на английском языке.**

**Реализация:**

1. Создать `ClassReader` из байткода
2. Создать `ClassVisitor` с переопределённым `visitMethod()`, который возвращает `MethodVisitor`
3. В `visitMethod()` определить: является ли метод статическим блоком (`<clinit>`) — тогда linkType = `STATIC_BLOCK`, иначе linkType = `METHOD`
4. Сформировать сигнатуру метода: `className + "." + methodName + descriptor`
5. В `MethodVisitor` переопределить:
   - `visitTypeInsn(opcode, type)` → `FqcnNormalizer.fromInternalName(type)` → добавить с linkType и signature
   - `visitFieldInsn(opcode, owner, name, descriptor)` → `FqcnNormalizer.fromInternalName(owner)` → добавить с linkType и signature
   - `visitMethodInsn(opcode, owner, name, descriptor, isInterface)` → `FqcnNormalizer.fromInternalName(owner)` → добавить с linkType и signature
   - `visitLdcInsn(value)` → если `value instanceof Type` → `FqcnNormalizer.fromDescriptor(type.getDescriptor())` → добавить с linkType и signature
6. Вернуть результирующую карту

**Вспомогательный приватный метод:**

- `void addDetail(Map<String, Set<LinkDetails>> map, String fqcn, LinkDetails detail)` — добавляет LinkDetails в Set для указанного FQCN. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Прочитать байткод через `ClassReader` → `ClassVisitor` → `MethodVisitor`
2. Для каждого метода: определить linkType (STATIC_BLOCK для `<clinit>`, METHOD для остальных)
3. Сформировать сигнатуру метода
4. Извлечь типы из инструкций байткода → linkType + signature
5. Собрать всё в Map<String, Set<LinkDetails>>