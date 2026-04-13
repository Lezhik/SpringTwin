# 033-inner-class-merger-details — Тесты

## Статус: complete

## Описание

Unit тесты для нового метода `mergeInnerClassesDetails()` в `InnerClassMerger`.

## Тестируемый класс

`spring.twin.scan.InnerClassMerger`

## Тестируемый метод

`static Map<String, Map<String, Set<LinkDetails>>> mergeInnerClassesDetails(Map<String, Map<String, Set<LinkDetails>>> graph, boolean merge)`

## Класс тестов

### `spring.twin.scan.InnerClassMergerDetailsTest`

**Методы тестов:**

1. `testMergeDetails_mergeFalse_returnsUnmodifiedGraph()` — merge=false → граф без изменений
2. `testMergeDetails_innerClassMergedToOuter_innerKeyRemoved()` — вложенный класс как ключ → удалён, зависимости добавлены к родительскому
3. `testMergeDetails_innerClassDepsAddedToOuter_linkDetailsPreserved()` — LinkDetails вложенного класса сохраняются при объединении
4. `testMergeDetails_innerClassInValues_replacedWithOuter()` — ссылки на вложенный класс в значениях заменены на родительский
5. `testMergeDetails_selfReferenceRemoved_afterMerge()` — самоссылка родительского класса на себя удалена
6. `testMergeDetails_noInnerClasses_graphUnchanged()` — нет вложенных классов → граф без изменений
7. `testMergeDetails_deepInnerClass_mergedToFirstOuter()` — `Outer$Inner$Deep` → объединяется с `Outer`
8. `testMergeDetails_outerNotInGraph_innerDepsBecomeOuterDeps()` — родительского класса нет в графе → создаётся запись с зависимостями вложенного

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | merge=false | Без изменений |
| 2 | Вложенный класс как ключ | Удалён, зависимости к родителю |
| 3 | LinkDetails сохраняются | При объединении |
| 4 | Вложенный класс в значениях | Заменён на родительский |
| 5 | Самоссылка | Удалена |
| 6 | Нет вложенных | Без изменений |
| 7 | Глубокий вложенный | К первому родителю |
| 8 | Родитель отсутствует | Создана новая запись |