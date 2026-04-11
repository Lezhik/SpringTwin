# 021-cli-merge — API

## Статус: pending

## Описание

Добавление опции `--merge-inner-classes` в CLI команду `ScanBytecodeCommand`.

## Изменения в классах

### `spring.twin.command.ScanBytecodeCommand` (модификация)

**Обновлённый метод:**

- `String scanBytecode(@ShellOption("--classes") String classes, @ShellOption("--output") String output, @ShellOption("--include") String include, @ShellOption("--exclude") String exclude, @ShellOption(value = "--merge-inner-classes", help = "Merge inner classes with outer classes", defaultValue = "true") String mergeInnerClasses)` — добавляется 5-й параметр `mergeInnerClasses` типа `String`. Значение парсится в `boolean` и передаётся в `ScanBytecodeParams.of()`.

**Логика парсинга:**
- Параметр `mergeInnerClasses` принимается как `String` и конвертируется в `boolean` через `Boolean.parseBoolean()` — значения `"true"` (без учёта регистра) → `true`, всё остальное → `false`
- Значение по умолчанию: `"true"` (объединение включено по умолчанию, как указано в SPEC)

**Обновлённый вызов:**
- `ScanBytecodeParams.of(Path.of(classes), Path.of(output), include, exclude, Boolean.parseBoolean(mergeInnerClasses))`

## Зависимости

- Фича 018: `ScanBytecodeParams.of()` с 5 параметрами
- Фича 020: `ScanBytecodeService` передаёт `mergeInnerClasses`

## Результат

CLI команда `scan-bytecode` поддерживает опцию `--merge-inner-classes <true|false>` со значением по умолчанию `true`.