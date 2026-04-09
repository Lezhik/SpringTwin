# 011-bytecode-class-analyzer — API

## Статус: pending

## Описание

Оркестрация всех экстракторов для одного `.class` файла. Объединяет результаты наследования, полей, методов, аннотаций и использования в коде в единое множество зависимостей.

## Классы

### `spring.twin.scan.BytecodeClassAnalyzer`

**Тип:** Spring bean с внедрением зависимостей через конструктор

**Конструктор:**
- `BytecodeClassAnalyzer(InheritanceExtractor inheritanceExtractor, FieldTypeExtractor fieldTypeExtractor, MethodTypeExtractor methodTypeExtractor, AnnotationTypeExtractor annotationTypeExtractor, CodeUsageExtractor codeUsageExtractor)`

**Методы:**

- `Set<String> extractDependencies(byte[] classBytes)` — анализирует байткод класса, вызывая все экстракторы и объединяя их результаты в единое множество FQCN зависимостей. Возвращает объединённое множество всех зависимостей класса. **Метод должен быть аннотирован Javadoc на английском языке.**

- `String extractClassName(byte[] classBytes)` — извлекает FQCN анализируемого класса из байткода. Использует `ClassReader.getClassName()` и `FqcnNormalizer.fromInternalName()`. **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

- Фича 006: `InheritanceExtractor`
- Фича 007: `FieldTypeExtractor`
- Фича 008: `MethodTypeExtractor`
- Фича 009: `AnnotationTypeExtractor`
- Фича 010: `CodeUsageExtractor`
- Фича 002: `FqcnNormalizer`

## Результат

Создан класс `BytecodeClassAnalyzer` с заглушками методов.