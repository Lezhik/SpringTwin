# 022-e2e-merge-tests — Тесты

## Статус: pending

## Описание

Unit тесты (заглушки) для E2E тестов опции `--merge-inner-classes`.

## Тестируемые классы

- `spring.twin.scan.e2e.ScanBytecodeE2eMergeTest`

## Класс тестов

### `spring.twin.scan.e2e.ScanBytecodeE2eMergeTest`

**Методы тестов:**

1. `e2e_mergeInnerClassesTrue_innerClassesMergedIntoOuter()` — создаёт `ScanBytecodeParams` с `mergeInnerClasses=true`, вызывает `scanBytecodeService.execute()`, читает выходной JSON, проверяет что ключ `spring.twin.testee.Outer$Inner` отсутствует, а ключ `spring.twin.testee.Outer` присутствует и содержит зависимости внутреннего класса

2. `e2e_mergeInnerClassesFalse_innerClassesSeparate()` — создаёт `ScanBytecodeParams` с `mergeInnerClasses=false`, вызывает `scanBytecodeService.execute()`, читает выходной JSON, проверяет что ключ `spring.twin.testee.Outer$Inner` присутствует как отдельная запись

3. `e2e_mergeInnerClassesDefault_isTrue()` — создаёт `ScanBytecodeParams` через 4-параметровый конструктор (без `mergeInnerClasses`), проверяет что внутренние классы объединены (по умолчанию `true`)

4. `e2e_mergeInnerClassesTrue_noSelfReference()` — запуск с `mergeInnerClasses=true`, проверяет что ни один класс не ссылается сам на себя после мержа

5. `e2e_mergeInnerClassesTrue_dependencyOnInnerClass_remappedToOuter()` — если класс A зависит от `Outer$Inner`, после мержа зависимость A должна ссылаться на `Outer`, а не на `Outer$Inner`

6. `e2e_mergeInnerClassesTrue_sortedOutput()` — проверяет что ключи и значения в выходном JSON отсортированы после выполнения мержа

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | mergeInnerClasses=true | Outer$Inner объединён с Outer |
| 2 | mergeInnerClasses=false | Outer$Inner — отдельный ключ |
| 3 | Значение по умолчанию | mergeInnerClasses=true |
| 4 | Нет самоссылок | Класс не ссылается сам на себя |
| 5 | Зависимость на внутренний класс | Заменена на внешний класс |
| 6 | Сортировка | Ключи и значения отсортированы |