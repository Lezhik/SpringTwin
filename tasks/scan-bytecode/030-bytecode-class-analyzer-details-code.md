# 030-bytecode-class-analyzer-details — Реализация

## Статус: complete

## Описание

Реализация метода `extractDependenciesDetails()` в `BytecodeClassAnalyzer` для детализированного анализа класса.

## Классы и методы

### `spring.twin.scan.BytecodeClassAnalyzer` (модификация)

**Новый метод:**

- `Map<String, Map<String, Set<LinkDetails>>> extractDependenciesDetails(byte[] classBytes)` — анализирует байткод класса и извлекает все зависимости с деталями. **Метод должен быть аннотирован Javadoc на английском языке.**

**Реализация:**

1. Извлечь FQCN анализируемого класса через `extractClassName(classBytes)`
2. Вызвать `extractDetails()` каждого экстрактора:
   - `inheritanceExtractor.extractDetails(classBytes)` → `Map<String, Set<LinkDetails>>`
   - `fieldTypeExtractor.extractDetails(classBytes)` → `Map<String, Set<LinkDetails>>`
   - `methodTypeExtractor.extractDetails(classBytes)` → `Map<String, Set<LinkDetails>>`
   - `annotationTypeExtractor.extractDetails(classBytes)` → `Map<String, Set<LinkDetails>>`
   - `codeUsageExtractor.extractDetails(classBytes)` → `Map<String, Set<LinkDetails>>`
3. Объединить результаты: для каждого экстрактора его карта `Map<String, Set<LinkDetails>>` (ключ — FQCN зависимости) преобразуется в структуру `targetFqcn → dependencyFqcn → Set<LinkDetails>`
4. Результат: `Map.of(targetFqcn, mergedInnerMap)` где `mergedInnerMap` — объединение всех зависимостей

**Вспомогательный приватный метод:**

- `void mergeDetails(Map<String, Set<LinkDetails>> target, Map<String, Set<LinkDetails>> source)` — объединяет исходную карту деталей в целевую, добавляя LinkDetails в существующие Set или создавая новые. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Получить FQCN анализируемого класса
2. Вызвать extractDetails() всех 5 экстракторов
3. Каждый экстрактор возвращает Map<dependencyFqcn, Set<LinkDetails>>
4. Объединить все карты в одну: Map<dependencyFqcn, Set<LinkDetails>>
5. Обернуть в структуру: Map.of(fqcn, mergedMap)