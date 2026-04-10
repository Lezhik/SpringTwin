# 001-scan-params — API

## Статус: complete

## Описание

Определение DTO для параметров команды `scan-bytecode` и парсера масок include/exclude.

## Классы

### 1. `spring.twin.scan.ScanBytecodeParams`

**Тип:** record (immutable DTO)

**Поля:**
- `Path classesDir` — путь к директории с `.class` файлами
- `Path outputFile` — путь к выходному JSON файлу
- `List<String> includeMasks` — список масок включения FQCN (пустой = любой класс)
- `List<String> excludeMasks` — список масок исключения FQCN (пустой = не исключать)

**Конструктор:**
- `ScanBytecodeParams(Path classesDir, Path outputFile, List<String> includeMasks, List<String> excludeMasks)`

**Методы:**
- `static ScanBytecodeParams of(Path classesDir, Path outputFile, String includeRaw, String excludeRaw)` — фабричный метод, разбивает сырые строки масок по `;` в списки

### 2. `spring.twin.scan.MaskParser`

**Тип:** утилитный класс (final, приватный конструктор)

**Методы:**
- `static List<String> parseMasks(String raw)` — разбивает строку масок по `;`, тримит пробелы, фильтрует пустые; возвращает пустой список если `raw` == null или пустая строка

## Зависимости

Нет зависимостей от других фич.

## Результат

Созданы классы `ScanBytecodeParams` и `MaskParser` с заглушками методов (методы возвращают null/пустые значения, бросают `UnsupportedOperationException`).