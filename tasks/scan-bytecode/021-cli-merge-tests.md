# 021-cli-merge — Тесты

## Статус: complete

## Описание

Unit тесты для обновлённой CLI команды `ScanBytecodeCommand` с опцией `--merge-inner-classes`.

## Тестируемые классы

- `spring.twin.command.ScanBytecodeCommand`

## Класс тестов

### `spring.twin.command.ScanBytecodeCommandTest` (модификация существующего)

**Новые методы тестов:**

1. `testScanBytecode_withMergeInnerClassesTrue_passesTrueToParams()` — вызов `scanBytecode(classes, output, include, exclude, "true")`, проверка через `ArgumentCaptor`, что `ScanBytecodeParams.mergeInnerClasses() == true`

2. `testScanBytecode_withMergeInnerClassesFalse_passesFalseToParams()` — вызов `scanBytecode(classes, output, include, exclude, "false")`, проверка что `ScanBytecodeParams.mergeInnerClasses() == false`

3. `testScanBytecode_defaultMergeInnerClasses_isTrue()` — вызов `scanBytecode(classes, output, include, exclude, "true")` (defaultValue в ShellOption), проверка что `mergeInnerClasses() == true`

4. `testScanBytecode_invalidMergeValue_treatedAsFalse()` — вызов `scanBytecode(classes, output, include, exclude, "invalid")`, проверка что `Boolean.parseBoolean("invalid") == false` и `mergeInnerClasses() == false`

**Существующие тесты** — не удаляются и не изменяются, должны продолжать проходить. При необходимости обновить вызовы `scanBytecode()` для совместимости с новой сигнатурой (добавить 5-й параметр).

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | --merge-inner-classes true | params.mergeInnerClasses() == true |
| 2 | --merge-inner-classes false | params.mergeInnerClasses() == false |
| 3 | Значение по умолчанию | mergeInnerClasses == true |
| 4 | Невалидное значение | Boolean.parseBoolean возвращает false |
| 5 | Все существующие тесты | Продолжают проходить |