# 030-bytecode-class-analyzer-details — Тесты

## Статус: pending

## Описание

Unit тесты для нового метода `extractDependenciesDetails()` в `BytecodeClassAnalyzer`. Тесты используют скомпилированные классы-образцы из `src/test/java/spring/twin/testee/`.

## Тестируемый класс

`spring.twin.scan.BytecodeClassAnalyzer`

## Тестируемый метод

`Map<String, Map<String, Set<LinkDetails>>> extractDependenciesDetails(byte[] classBytes)`

## Классы-образцы (testee)

Используются существующие:
- `ComplexService.java` — класс с различными типами зависимостей
- `InheritanceChild.java` — класс с наследованием
- `FieldHolder.java` — класс с полями
- `AnnotatedClass.java` — класс с аннотациями

## Класс тестов

### `spring.twin.scan.BytecodeClassAnalyzerDetailsTest`

**Методы тестов:**

1. `testExtractDependenciesDetails_complexClass_containsAllLinkTypes()` — `ComplexService` → результат содержит записи с различными LinkType: FIELD, METHOD, CLASS_ANNOTATION и т.д.
2. `testExtractDependenciesDetails_inheritanceClass_containsSuperclassLink()` — `InheritanceChild` → результат содержит `InheritanceBase` с `LinkDetails.of(LinkType.SUPERCLASS)`
3. `testExtractDependenciesDetails_fieldClass_containsFieldLinks()` — `FieldHolder` → результат содержит типы полей с `LinkDetails.of(LinkType.FIELD, fieldName)`
4. `testExtractDependenciesDetails_annotatedClass_containsAnnotationLinks()` — `AnnotatedClass` → результат содержит типы аннотаций с `LinkType.CLASS_ANNOTATION`
5. `testExtractDependenciesDetails_resultStructure_correctOuterKey()` — внешний ключ карты — FQCN анализируемого класса
6. `testExtractDependenciesDetails_resultStructure_correctInnerKeys()` — внутренние ключи — FQCN зависимых классов
7. `testExtractDependenciesDetails_multipleLinksToSameTarget_aggregatedInSet()` — несколько связей с одним FQCN → все в одном Set
8. `testExtractDependenciesDetails_nullBytes_throwsException()` — null → бросает IllegalArgumentException

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Комплексный класс | Все типы связей присутствуют |
| 2 | Наследование | SUPERCLASS связь |
| 3 | Поля | FIELD связи с именами полей |
| 4 | Аннотации | CLASS_ANNOTATION связь |
| 5 | Структура: внешний ключ | FQCN анализируемого класса |
| 6 | Структура: внутренние ключи | FQCN зависимых классов |
| 7 | Несколько связей с одним классом | Агрегированы в Set |