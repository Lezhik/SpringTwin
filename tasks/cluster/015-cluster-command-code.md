# 015-cluster-command — Реализация

## Статус: complete

## Описание

Реализация CLI команды `cluster` для Spring Shell.

## Классы и методы

### `spring.twin.command.ClusterCommand`

**Аннотации класса:** `@ShellComponent`

**Конструктор:**
- `ClusterCommand(ClusterService clusterService)` — сохраняет ссылку на сервис

**Методы:**

#### `cluster(String deps, String output, String resolution)` → `String`
- Аннотации: `@ShellMethod(key = "cluster", value = "Cluster dependency graph")`
- Параметры:
  - `@ShellOption("--deps") String deps` — путь к файлу зависимостей
  - `@ShellOption("--output") String output` — путь к выходному файлу
  - `@ShellOption(value = "--resolution", defaultValue = "1.5") String resolution` — параметр кластеризации
- Логика:
  1. Парсит `resolution` как `Double.parseDouble(resolution)`
  2. Создаёт `ClusterParams.of(deps, output, parsedResolution)`
  3. Вызывает `clusterService.execute(params)`
  4. Возвращает строку "Clusters written to: " + params.outputFile()
  5. При ошибке парсинга resolution бросает `IllegalArgumentException` с описанием
  6. При исключении от сервиса пробрасывает `RuntimeException` с сообщением

**Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

- Фича 001: ClusterParams
- Фича 014: ClusterService