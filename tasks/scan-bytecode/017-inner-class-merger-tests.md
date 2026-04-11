# 017-inner-class-merger — Тесты

## Статус: pending

## Описание

Unit тесты для `InnerClassMerger`.

## Тестируемые классы

- `spring.twin.scan.InnerClassMerger`

## Класс тестов

### `spring.twin.scan.InnerClassMergerTest`

**Методы тестов:**

#### isInnerClass

1. `testIsInnerClass_withDollarSign_returnsTrue()` — `com.example.Outer$Inner` → `true`
2. `testIsInnerClass_withoutDollarSign_returnsFalse()` — `com.example.Outer` → `false`
3. `testIsInnerClass_multipleDollarSigns_returnsTrue()` — `com.example.Outer$Inner$Deep` → `true`
4. `testIsInnerClass_dollarAtStart_returnsFalse()` — `$Proxy` → `false` (символ `$` в начале — не внутренний класс)
5. `testIsInnerClass_nullInput_returnsFalse()` — `null` → `false`
6. `testIsInnerClass_emptyString_returnsFalse()` — `""` → `false`

#### getOuterClassName

7. `testGetOuterClassName_innerClass_returnsOuterName()` — `com.example.Outer$Inner` → `com.example.Outer`
8. `testGetOuterClassName_nestedInnerClass_returnsTopLevelOuter()` — `com.example.Outer$Inner$Deep` → `com.example.Outer`
9. `testGetOuterClassName_notInnerClass_returnsSameName()` — `com.example.Outer` → `com.example.Outer`
10. `testGetOuterClassName_nullInput_returnsNull()` — `null` → `null`
11. `testGetOuterClassName_dollarAtStart_returnsSameName()` — `$Proxy` → `$Proxy`

#### mergeInnerClasses

12. `testMergeInnerClasses_mergesInnerIntoOuter()` — граф с `Outer → [dep1]` и `Outer$Inner → [dep2]` → `Outer → [dep1, dep2]`, ключ `Outer$Inner` удалён
13. `testMergeInnerClasses_outerNotInGraph_createsOuterEntry()` — граф с `Outer$Inner → [dep1]` (без Outer) → `Outer → [dep1]`
14. `testMergeInnerClasses_noInnerClasses_returnsSameGraph()` — граф без внутренних классов → без изменений
15. `testMergeInnerClasses_nestedInnerClass_mergesToTopLevel()` — граф с `Outer$Inner$Deep → [dep1]` → `Outer → [dep1]`
16. `testMergeInnerClasses_multipleInnerClasses_mergesAllToOuter()` — граф с `Outer → [dep1]`, `Outer$Inner1 → [dep2]`, `Outer$Inner2 → [dep3]` → `Outer → [dep1, dep2, dep3]`
17. `testMergeInnerClasses_innerClassDependsOnOuter_outerNotInDeps()` — если `Outer$Inner` зависит от `Outer`, то после мержа `Outer` не должен содержать самоссылку
18. `testMergeInnerClasses_emptyGraph_returnsEmptyMap()` — пустой граф → пустой `Map`
19. `testMergeInnerClasses_resultIsSorted()` — ключи и значения результата отсортированы
20. `testMergeInnerClasses_withMergeTrue_performsMerge()` — `mergeInnerClasses(graph, true)` → выполняет объединение
21. `testMergeInnerClasses_withMergeFalse_returnsOriginalGraph()` — `mergeInnerClasses(graph, false)` → возвращает исходный граф без изменений
22. `testMergeInnerClasses_dollarInDependencyName_remappedToOuter()` — если в зависимостях есть `Outer$Inner`, она заменяется на `Outer`

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Внутренний класс объединяется с внешним | Зависимости внутреннего добавлены к внешнему, внутренний удалён из ключей |
| 2 | Внешний класс отсутствует в графе | Создана новая запись для внешнего класса |
| 3 | Нет внутренних классов | Граф без изменений |
| 4 | Глубоко вложенный класс | Объединяется с top-level внешним классом |
| 5 | merge=false | Граф возвращён без изменений |
| 6 | Самоссылка после мержа | Внешний класс не ссылается сам на себя |