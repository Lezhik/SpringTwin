# 018-scan-params-merge — API

## Статус: complete

## Описание

Добавление поля `mergeInnerClasses` в существующий record `ScanBytecodeParams` и обновление фабричного метода `of()`.

## Изменения в классах

### `spring.twin.scan.ScanBytecodeParams` (модификация)

**Тип:** record (immutable DTO)

**Новое поле:**
- `boolean mergeInnerClasses` — флаг объединения внутренних классов с родительскими (по умолчанию `true`)

**Обновлённый конструктор:**
- `ScanBytecodeParams(Path classesDir, Path outputFile, List<String> includeMasks, List<String> excludeMasks, boolean mergeInnerClasses)`

**Обновлённые методы:**
- `static ScanBytecodeParams of(Path classesDir, Path outputFile, String includeRaw, String excludeRaw, boolean mergeInnerClasses)` — фабричный метод с новым параметром

**Обратная совместимость:**
- Сохранить существующий конструктор `ScanBytecodeParams(Path classesDir, Path outputFile, List<String> includeMasks, List<String> excludeMasks)` — делегирует в новый конструктор со значением `mergeInnerClasses = true`
- Сохранить существующий метод `of(Path, Path, String, String)` — делегирует в новый метод со значением `mergeInnerClasses = true`

## Зависимости

Нет зависимостей от других фич (изменение существующего класса).

## Результат

Класс `ScanBytecodeParams` обновлён: добавлено поле `mergeInnerClasses`, обновлены конструктор и фабричный метод, сохранена обратная совместимость.