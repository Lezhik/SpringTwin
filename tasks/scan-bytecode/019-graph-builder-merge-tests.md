# 019-graph-builder-merge — Тесты

## Статус: pending

## Описание

Unit тесты для обновлённого `DependencyGraphBuilder` с поддержкой `mergeInnerClasses`.

## Тестируемые классы

- `spring.twin.scan.DependencyGraphBuilder`

## Класс тестов

### `spring.twin.scan.DependencyGraphBuilderTest` (модификация существующего)

**Новые методы тестов:**

1. `testBuild_withMergeInnerClassesTrue_innerClassesMerged()` — вызов `build(dir, includeMasks, excludeMasks, true)` с testee-классами, содержащими `Outer$Inner` → в результате ключ `Outer$Inner` отсутствует, его зависимости объединены с `Outer`
2. `testBuild_withMergeInnerClassesFalse_innerClassesSeparate()` — вызов `build(dir, includeMasks, excludeMasks, false)` → внутренние классы присутствуют как отдельные ключи в графе
3. `testBuild_withMergeInnerClassesTrue_noInnerClasses_unchanged()` — вызов с маской, не включающей внутренние классы → граф без изменений
4. `testBuild_withMergeInnerClassesTrue_outerNotInGraph_outerCreated()` — если внутренний класс включён маской, а внешний — нет, то после мержа внешний класс появляется в графе
5. `testBuild_oldMethodWithoutMerge_backwardCompatible()` — вызов старого метода `build(dir, includeMasks, excludeMasks)` → внутренние классы присутствуют как отдельные ключи (обратная совместимость)

**Существующие тесты** — не удаляются и не изменяются, должны продолжать проходить.

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | mergeInnerClasses=true | Внутренние классы объединены с внешними |
| 2 | mergeInnerClasses=false | Внутренние классы — отдельные ключи |
| 3 | Нет внутренних классов + merge=true | Граф без изменений |
| 4 | Старый метод build() | Обратная совместимость, внутренние классы отдельные |
| 5 | Все существующие тесты | Продолжают проходить |