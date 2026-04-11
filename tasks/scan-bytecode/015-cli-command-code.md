# 015-cli-command — Реализация

## Статус: complete

## Описание

Реализация `ScanBytecodeCommand` — CLI команды для Spring Shell.

## Классы и методы

### `spring.twin.command.ScanBytecodeCommand`

**Тип:** `@ShellComponent`

**Конструктор:**
- `ScanBytecodeCommand(ScanBytecodeService scanBytecodeService)` — внедрение сервиса

**Реализация:**

- `String scanBytecode(String classes, String output, String include, String exclude)` — аннотирован `@ShellMethod(key = "scan-bytecode")`. Параметры аннотированы `@ShellOption`:
  - `classes`: `@ShellOption(value = "--classes", help = "Path to directory with .class files")`
  - `output`: `@ShellOption(value = "--output", help = "Path to output JSON file")`
  - `include`: `@ShellOption(value = "--include", help = "FQCN include masks separated by ;", defaultValue = "")`
  - `exclude`: `@ShellOption(value = "--exclude", help = "FQCN exclude masks separated by ;", defaultValue = "")`
  
  Логика:
  1. Создать `ScanBytecodeParams.of(Path.of(classes), Path.of(output), include, exclude)`
  2. Вызвать `scanBytecodeService.execute(params)`
  3. Вернуть `"Dependencies written to: " + output`
  4. При исключении — вернуть `"Error: " + e.getMessage()`
  **Метод должен быть аннотирован Javadoc на английском языке.**

## Дополнительные изменения

Добавить в `build.gradle.kts`:
```
implementation("org.springframework.shell:spring-shell-starter")
```

## Логика работы

1. Парсинг строковых аргументов в `ScanBytecodeParams`
2. Делегирование сервису
3. Возврат результата пользователю