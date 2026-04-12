# 030-bytecode-class-analyzer-details — API

## Статус: complete

## Описание

Добавление детализированного метода анализа в `BytecodeClassAnalyzer`. Новый метод объединяет результаты всех экстракторов и возвращает `Map<String, Map<String, Set<LinkDetails>>>` вместо `Set<String>`.

## Классы

### `spring.twin.scan.BytecodeClassAnalyzer` (модификация)

**Новые методы:**

- `Map<String, Map<String, Set<LinkDetails>>> extractDependenciesDetails(byte[] classBytes)` — анализирует байткод класса и извлекает все зависимости с деталями связей. Возвращает двухуровневую карту: внешний ключ — FQCN зависимого класса, внутренний ключ — FQCN класса, на который ссылается, значение — множество LinkDetails. **Метод должен быть аннотирован Javadoc на английском языке.**

**Правила формирования результата:**

Результат имеет структуру `Map<targetFqcn, Map<sourceFqcn, Set<LinkDetails>>>`, где:
- `targetFqcn` — FQCN анализируемого класса (извлекается из байткода)
- `sourceFqcn` — FQCN класса, на который ссылается анализируемый класс
- `Set<LinkDetails>` — множество деталей связей

Метод объединяет результаты из:
- `InheritanceExtractor.extractDetails()` → SUPERCLASS, INTERFACE
- `FieldTypeExtractor.extractDetails()` → FIELD
- `MethodTypeExtractor.extractDetails()` → METHOD
- `AnnotationTypeExtractor.extractDetails()` → CLASS_ANNOTATION, FIELD_ANNOTATION, METHOD_ANNOTATION, METHOD_ARG_ANNOTATION
- `CodeUsageExtractor.extractDetails()` → STATIC_BLOCK, METHOD

Для каждого экстрактора его результат `Map<String, Set<LinkDetails>>` (где ключ — FQCN зависимости) объединяется в общую карту: `targetFqcn → dependencyFqcn → Set<LinkDetails>`.

**Существующие методы:**

- `Set<String> extractDependencies(byte[] classBytes)` — остаётся без изменений
- `String extractClassName(byte[] classBytes)` — остаётся без изменений

## Зависимости

- Фича 025: `InheritanceExtractor.extractDetails()`
- Фича 026: `FieldTypeExtractor.extractDetails()`
- Фича 027: `MethodTypeExtractor.extractDetails()`
- Фича 028: `AnnotationTypeExtractor.extractDetails()`
- Фича 029: `CodeUsageExtractor.extractDetails()`
- Фича 024: `LinkDetails`, `LinkType`

## Результат

Добавлен метод `extractDependenciesDetails()` в класс `BytecodeClassAnalyzer` с заглушкой реализации.