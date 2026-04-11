# 018-scan-params-merge — Тесты

## Статус: pending

## Описание

Unit тесты для обновлённого `ScanBytecodeParams` с полем `mergeInnerClasses`.

## Тестируемые классы

- `spring.twin.scan.ScanBytecodeParams`

## Класс тестов

### `spring.twin.scan.ScanBytecodeParamsTest` (модификация существующего)

**Новые методы тестов:**

1. `testOf_withMergeInnerClassesTrue_storesTrue()` — `ScanBytecodeParams.of(classesDir, outputFile, include, exclude, true)` → `mergeInnerClasses()` возвращает `true`
2. `testOf_withMergeInnerClassesFalse_storesFalse()` — `ScanBytecodeParams.of(classesDir, outputFile, include, exclude, false)` → `mergeInnerClasses()` возвращает `false`
3. `testConstructor_withMergeInnerClasses_storesValue()` — конструктор с 5 параметрами → `mergeInnerClasses()` возвращает переданное значение
4. `testBackwardCompatibleConstructor_defaultMergeIsTrue()` — конструктор с 4 параметрами → `mergeInnerClasses()` возвращает `true`
5. `testBackwardCompatibleOf_defaultMergeIsTrue()` — `ScanBytecodeParams.of(classesDir, outputFile, include, exclude)` → `mergeInnerClasses()` возвращает `true`

**Существующие тесты** — не удаляются и не изменяются, должны продолжать проходить.

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Новый конструктор с mergeInnerClasses=true | Поле содержит true |
| 2 | Новый конструктор с mergeInnerClasses=false | Поле содержит false |
| 3 | Старый конструктор (4 параметра) | mergeInnerClasses=true по умолчанию |
| 4 | Старый фабричный метод (4 параметра) | mergeInnerClasses=true по умолчанию |
| 5 | Все существующие тесты | Продолжают проходить |