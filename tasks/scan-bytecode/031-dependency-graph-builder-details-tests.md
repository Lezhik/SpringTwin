# 031-dependency-graph-builder-details — Тесты

## Статус: complete

## Описание

Unit тесты для новых методов `buildDetails()` в `DependencyGraphBuilder`. Тесты используют скомпилированные классы-образцы из `src/test/java/spring/twin/testee/`.

## Тестируемый класс

`spring.twin.scan.DependencyGraphBuilder`

## Тестируемые методы

- `Map<String, Map<String, Set<LinkDetails>>> buildDetails(Path classesDir, List<String> includeMasks, List<String> excludeMasks)`
- `Map<String, Map<String, Set<LinkDetails>>> buildDetails(Path classesDir, List<String> includeMasks, List<String> excludeMasks, boolean mergeInnerClasses)`

## Класс тестов

### `spring.twin.scan.DependencyGraphBuilderDetailsTest`

**Методы тестов:**

1. `testBuildDetails_scansDirectory_returnsDetailedGraph()` — сканирование директории с testee-классами → результат содержит записи с LinkDetails
2. `testBuildDetails_filtersByIncludeMask_returnsOnlyMatchingClasses()` — include-маска фильтрует классы
3. `testBuildDetails_filtersByExcludeMask_excludesMatchingClasses()` — exclude-маска исключает классы
4. `testBuildDetails_filtersPrimitiveTypes_notInResult()` — примитивные типы не включаются в зависимости
5. `testBuildDetails_resultStructure_correctNestedMap()` — структура: внешний ключ FQCN → внутренний ключ FQCN зависимости → Set<LinkDetails>
6. `testBuildDetails_multipleClasses_allAggregated()` — несколько классов → все включены в общий граф
7. `testBuildDetails_mergeInnerClasses_mergesInnerToOuter()` — mergeInnerClasses=true → вложенные классы объединены с родительскими
8. `testBuildDetails_noMergeInnerClasses_separateEntries()` — mergeInnerClasses=false → вложенные классы как отдельные записи

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Сканирование директории | Граф с LinkDetails |
| 2 | Include-маска | Только подходящие классы |
| 3 | Exclude-маска | Исключённые классы отсутствуют |
| 4 | Примитивы | Не включены |
| 5 | Структура карты | Внешний ключ → внутренний ключ → Set |
| 6 | Несколько классов | Все агрегированы |
| 7 | Merge inner | Объединены с родителями |
| 8 | No merge inner | Отдельные записи |