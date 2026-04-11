# 021-cli-merge — Реализация

## Статус: complete

## Описание

Добавление опции `--merge-inner-classes` в CLI команду `ScanBytecodeCommand`.

## Классы и методы

### `spring.twin.command.ScanBytecodeCommand` (модификация)

**Реализация:**

- Обновить метод `scanBytecode()` — добавить 5-й параметр:
  ```java
  @ShellOption(value = "--merge-inner-classes", help = "Merge inner classes with outer classes", defaultValue = "true") String mergeInnerClasses
  ```
  **Метод должен быть аннотирован Javadoc на английском языке.**

- Обновить вызов `ScanBytecodeParams.of()`:
  ```java
  ScanBytecodeParams params = ScanBytecodeParams.of(
      Path.of(classes),
      Path.of(output),
      include,
      exclude,
      Boolean.parseBoolean(mergeInnerClasses)
  );
  ```

- Обновить Javadoc метода: добавить описание параметра `--merge-inner-classes`

## Логика работы

1. Параметр `--merge-inner-classes` принимается как `String` со значением по умолчанию `"true"`
2. Конвертация в `boolean` через `Boolean.parseBoolean()` — стандартный Java-метод, `"true"` (case-insensitive) → `true`, всё остальное → `false`
3. Значение передаётся в `ScanBytecodeParams.of()` и далее через весь пайплайн