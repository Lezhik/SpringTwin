# 034-scan-bytecode-service-details — API

## Статус: pending

## Описание

Добавление детализированного метода в `ScanBytecodeService`. Новый метод `executeDetails()` использует `buildDetails()` и `writeDetails()` для записи графа в новом формате SPEC.

## Классы

### `spring.twin.scan.ScanBytecodeService` (модификация)

**Новые методы:**

- `void executeDetails(ScanBytecodeParams params)` — выполняет пайплайн scan-bytecode с детализированным выводом. Вызывает `dependencyGraphBuilder.buildDetails()` и `dependencyJsonWriter.writeDetails()`. **Метод должен быть аннотирован Javadoc на английском языке.**

- `Map<String, Map<String, Set<LinkDetails>>> analyzeDetails(ScanBytecodeParams params)` — анализирует байткод и возвращает детализированный граф без записи в файл. **Метод должен быть аннотирован Javadoc на английском языке.**

**Существующие методы:**

- `void execute(ScanBytecodeParams params)` — остаётся без изменений (использует старый формат `Map<String, Set<String>>`)
- `Map<String, Set<String>> analyze(ScanBytecodeParams params)` — остаётся без изменений

## Зависимости

- Фича 031: `DependencyGraphBuilder.buildDetails()`
- Фича 032: `DependencyJsonWriter.writeDetails()`
- Фича 024: `LinkDetails`

## Результат

Добавлены методы `executeDetails()` и `analyzeDetails()` в класс `ScanBytecodeService` с заглушками реализации.