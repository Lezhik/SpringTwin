# 010-code-usage-extractor — Реализация

## Статус: complete

## Описание

Реализация `CodeUsageExtractor` для извлечения зависимостей по использованию типов в коде методов.

## Классы и методы

### `spring.twin.scan.CodeUsageExtractor`

**Тип:** Spring bean (`@Component`)

**Реализация:**

- `Set<String> extract(byte[] classBytes)` — использует ASM `ClassReader` с кастомным `ClassVisitor` и `MethodVisitor`. Для каждого метода посещает инструкции и извлекает типы из:
  - `NEW` → `FqcnNormalizer.fromInternalName()`
  - `ANEWARRAY` → `FqcnNormalizer.fromInternalName()`
  - `CHECKCAST` → `FqcnNormalizer.fromInternalName()`
  - `INSTANCEOF` → `FqcnNormalizer.fromInternalName()`
  - `GETSTATIC`, `PUTSTATIC`, `GETFIELD`, `PUTFIELD` → владелец поля через `FqcnNormalizer.fromInternalName()`
  - `INVOKEVIRTUAL`, `INVOKESTATIC`, `INVOKESPECIAL`, `INVOKEINTERFACE` → владелец метода через `FqcnNormalizer.fromInternalName()`
  - `LDC` с `Type` значением → извлечение типа
  Результат — множество FQCN. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Создать `ClassReader` из байткода
2. Создать кастомный `ClassVisitor`, который для каждого метода возвращает `MethodVisitor`
3. В `MethodVisitor` переопределить `visitTypeInsn`, `visitFieldInsn`, `visitMethodInsn`, `visitLdcInsn`
4. Собрать все типы в множество
5. Вернуть множество FQCN