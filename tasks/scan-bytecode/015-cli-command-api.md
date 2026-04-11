# 015-cli-command — API

## Статус: complete

## Описание

CLI команда `scan-bytecode` для Spring Shell. Принимает параметры командной строки, создаёт `ScanBytecodeParams` и вызывает `ScanBytecodeService.execute()`.

## Классы

### `spring.twin.command.ScanBytecodeCommand`

**Тип:** Spring bean (`@Component`), Spring Shell команда (`@ShellComponent`)

**Конструктор:**
- `ScanBytecodeCommand(ScanBytecodeService scanBytecodeService)`

**Методы:**

- `String scanBytecode(String classes, String output, String include, String exclude)` — аннотирован `@ShellMethod(key = "scan-bytecode", value = "Scan bytecode and extract dependencies")`. Параметры аннотированы `@ShellOption`:
  - `@ShellOption(value = "--classes", help = "Path to directory with .class files")` — `String classes`
  - `@ShellOption(value = "--output", help = "Path to output JSON file")` — `String output`
  - `@ShellOption(value = "--include", help = "FQCN include masks separated by ;", defaultValue = "")` — `String include`
  - `@ShellOption(value = "--exclude", help = "FQCN exclude masks separated by ;", defaultValue = "")` — `String exclude`
  
  Логика: создаёт `ScanBytecodeParams.of(Path.of(classes), Path.of(output), include, exclude)`, вызывает `scanBytecodeService.execute(params)`, возвращает строку с результатом (путь к файлу или сообщение об ошибке). **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

- Фича 001: `ScanBytecodeParams`
- Фича 014: `ScanBytecodeService`
- Внешняя: `org.springframework.shell:spring-shell-starter`

## Примечание

Требуется добавить зависимость `spring-shell-starter` в `build.gradle.kts`.

## Результат

Создан класс `ScanBytecodeCommand` с заглушками методов.