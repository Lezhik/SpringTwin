# 012-dependency-graph-builder — Тесты

## Статус: complete

## Описание

Unit тесты для `DependencyGraphBuilder`. Тесты используют скомпилированные классы-образцы из `src/test/java/spring/twin/testee/`.

## Тестируемый класс

`spring.twin.scan.DependencyGraphBuilder`

## Классы-образцы (testee)

Используются все ранее созданные классы из `src/test/java/spring/twin/testee/`.

## Класс тестов

### `spring.twin.scan.DependencyGraphBuilderTest`

**Методы тестов:**

1. `testBuild_scansDirectoryAndBuildsGraph()` — сканирование директории с testee-классами → граф содержит записи для каждого класса
2. `testBuild_emptyMasks_includesAllClasses()` — пустые маски → все классы включены
3. `testBuild_includeMask_filtersClasses()` — include маска `*.testee.*` → только классы из пакета testee
4. `testBuild_excludeMask_excludesClasses()` — exclude маска `*FieldHolder*` → класс FieldHolder исключён
5. `testBuild_includeAndExclude_combined()` — include + exclude → корректная комбинация
6. `testBuild_dependenciesFilteredByMasks()` — зависимости также фильтруются масками
7. `testBuild_emptyDirectory_returnsEmptyMap()` — пустая директория → пустой граф
8. `testBuild_sortedKeys()` — ключи графа отсортированы по алфавиту
9. `testBuild_nonExistingDirectory_throwsException()` — несуществующая директория → бросает исключение

## Сценарии

| # | Сценарий | Маски | Ожидаемый результат |
|---|----------|-------|-------------------|
| 1 | Без масок | пустые | Все классы и все зависимости |
| 2 | Include | `*.testee.*` | Только классы из testee |
| 3 | Exclude | `*FieldHolder*` | Без FieldHolder |
| 4 | Пустая директория | любые | Пустой граф |