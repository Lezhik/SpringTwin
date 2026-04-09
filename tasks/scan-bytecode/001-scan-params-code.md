# 001-scan-params — Реализация

## Статус: pending

## Описание

Реализация `ScanBytecodeParams` и `MaskParser`.

## Классы и методы

### `spring.twin.scan.ScanBytecodeParams`

**Тип:** record

**Реализация:**
- `ScanBytecodeParams(Path classesDir, Path outputFile, List<String> includeMasks, List<String> excludeMasks)` — record-конструктор, списки оборачиваются в `List.of()` для иммутабельности
- `static ScanBytecodeParams of(Path classesDir, Path outputFile, String includeRaw, String excludeRaw)` — вызывает `MaskParser.parseMasks()` для разбора сырых строк масок, затем создаёт record через конструктор. **Метод должен быть аннотирован Javadoc на английском языке.**

### `spring.twin.scan.MaskParser`

**Тип:** final утилитный класс

**Реализация:**
- Приватный конструктор — предотвращает инстанцирование
- `static List<String> parseMasks(String raw)` — если `raw` == null или `isBlank()`, возвращает `Collections.emptyList()`. Иначе разбивает по `;`, тримит каждый элемент, фильтрует пустые, собирает в `List.of()`. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `MaskParser.parseMasks()` — безопасный разбор строки масок с обработкой null, пустых строк, пробелов и лишних разделителей
2. `ScanBytecodeParams.of()` — фабричный метод, делегирующий парсинг масок `MaskParser`, обеспечивает иммутабельность через `List.of()`