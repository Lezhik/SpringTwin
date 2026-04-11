# 011-bytecode-class-analyzer — Реализация

## Статус: complete

## Описание

Реализация `BytecodeClassAnalyzer` — оркестратора всех экстракторов.

## Классы и методы

### `spring.twin.scan.BytecodeClassAnalyzer`

**Тип:** Spring bean (`@Component`)

**Конструктор:**
- `BytecodeClassAnalyzer(InheritanceExtractor inheritanceExtractor, FieldTypeExtractor fieldTypeExtractor, MethodTypeExtractor methodTypeExtractor, AnnotationTypeExtractor annotationTypeExtractor, CodeUsageExtractor codeUsageExtractor)` — внедрение всех экстракторов

**Реализация:**

- `Set<String> extractDependencies(byte[] classBytes)` — вызывает все 5 экстракторов: `inheritanceExtractor.extract()`, `fieldTypeExtractor.extract()`, `methodTypeExtractor.extract()`, `annotationTypeExtractor.extract()`, `codeUsageExtractor.extract()`. Объединяет все результаты в `HashSet`. Возвращает `Set.of()` для иммутабельности. **Метод должен быть аннотирован Javadoc на английском языке.**

- `String extractClassName(byte[] classBytes)` — создаёт `ClassReader`, вызывает `getClassName()`, конвертирует через `FqcnNormalizer.fromInternalName()`. Бросает исключение если результат empty. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `extractDependencies` — делегирует анализ каждому экстрактору, объединяет результаты
2. `extractClassName` — извлекает имя класса из байткода через ASM ClassReader