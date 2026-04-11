# 018-scan-params-merge — Реализация

## Статус: complete

## Описание

Добавление поля `mergeInnerClasses` в `ScanBytecodeParams` с сохранением обратной совместимости.

## Классы и методы

### `spring.twin.scan.ScanBytecodeParams` (модификация)

**Тип:** record

**Реализация:**

- Добавить поле `boolean mergeInnerClasses` в record — после `excludeMasks`

- Обновлённый compact-конструктор:
  ```java
  public ScanBytecodeParams {
      includeMasks = List.copyOf(includeMasks);
      excludeMasks = List.copyOf(excludeMasks);
  }
  ```
  Поле `mergeInnerClasses` — примитив, не требует обработки в compact-конструкторе.

- `static ScanBytecodeParams of(Path classesDir, Path outputFile, String includeRaw, String excludeRaw, boolean mergeInnerClasses)` — вызывает `MaskParser.parseMasks()` для разбора масок, создаёт record через новый 5-параметровый конструктор. **Метод должен быть аннотирован Javadoc на английском языке.**

- Сохранить существующий `static ScanBytecodeParams of(Path classesDir, Path outputFile, String includeRaw, String excludeRaw)` — делегирует в новый метод `of()` со значением `mergeInnerClasses = true`. **Метод должен быть аннотирован Javadoc на английском языке.**

- Сохранить существующий конструктор `ScanBytecodeParams(Path classesDir, Path outputFile, List<String> includeMasks, List<String> excludeMasks)` — делегирует в новый 5-параметровый конструктор со значением `mergeInnerClasses = true`. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Новое поле `mergeInnerClasses` хранит флаг объединения внутренних классов, по умолчанию `true`
2. Обратная совместимость обеспечивается делегированием старых конструкторов/методов в новые с дефолтным значением `true`
3. Все существующие вызовы продолжают работать без изменений