# Spring Twin CLI --- CLI и форматы данных

Документ описывает параметры CLI и структуры JSON-файлов, используемых в
pipeline Spring Twin.

------------------------------------------------------------------------

# Общая схема pipeline

    scan-classes  → di.json
    scan-bytecode → bytecode.json
    cluster       → clusters.json
    generate-refactoring → tasks.json

Каждый этап читает входные JSON-файлы и формирует новый JSON-артефакт.

------------------------------------------------------------------------

# 1. scan-classes

Анализирует скомпилированные .class файлы и извлекает Spring DI зависимости,
включая сгенерированные конструкторы (например, из Lombok).

## Команда

    spring-twin scan-classes
      --classes <path>
      --output <file>
      --include <mask>
      --exclude <mask>

## Параметры

  параметр      описание
  ------------- ------------------------------------------------------------------
  `--classes`   путь к директории с `.class` файлами
  `--output`    путь к JSON результату
  `--include`   маски полных имён классов (FQCN), несколько через `;`
  `--exclude`   маски полных имён классов (FQCN), несколько через `;`

**Формат масок:**
- маски применяются к полным именам классов (FQCN), например: `com.example.*`, `*.service.*`
- упрощённый синтаксис: `*` (любые символы), `?` (один символ)
- проверка по вхождению: достаточно указать часть пакета, например: `order`
- несколько масок перечисляются через `;`, например: `com.example.*;com.demo.*`

## Выходной файл

`di.json`

### Структура

``` json
{
  "nodes": [
    {
      "type": "Class",
      "name": "OrderService",
      "package": "com.example.service",
      "labels": ["Service"]
    }
  ],
  "edges": [
    {
      "type": "DEPENDS_ON",
      "from": "OrderController",
      "to": "OrderService",
      "details": {
        "injectionType": "constructor",
        "parameterIndex": 0
      }
    }
  ]
}
```

------------------------------------------------------------------------

# 2. scan-bytecode

Анализирует `.class` файлы и извлекает структурные зависимости.

## Команда

    spring-twin scan-bytecode
      --classes <path>
      --output <file>
      --include <mask>
      --exclude <mask>

## Параметры

  параметр      описание
  ------------- ------------------------------------------------------------------
  `--classes`   путь к директории с `.class` файлами
  `--output`    путь к JSON результату
  `--include`   маски полных имён классов (FQCN), несколько через `;`
  `--exclude`   маски полных имён классов (FQCN), несколько через `;`

**Формат масок:**
- маски применяются к полным именам классов (FQCN), например: `com.example.*`, `*.service.*`
- упрощённый синтаксис: `*` (любые символы), `?` (один символ)
- проверка по вхождению: достаточно указать часть пакета, например: `order`
- несколько масок перечисляются через `;`, например: `com.example.*;com.demo.*`

## Выходной файл

`bytecode.json`

### Структура

``` json
{
  "edges": [
    {
      "type": "CALLS",
      "fromClass": "OrderService",
      "toClass": "PaymentClient",
      "method": "processPayment"
    },
    {
      "type": "ACCESSES_FIELD",
      "fromClass": "OrderService",
      "toClass": "OrderRepository",
      "field": "repository"
    },
    {
      "type": "INSTANTIATES",
      "fromClass": "OrderService",
      "toClass": "Order"
    }
  ]
}
```

------------------------------------------------------------------------

# 3. cluster

Объединяет графы зависимостей и выполняет кластеризацию.

## Команда

    spring-twin cluster
      --di <file>
      --bytecode <file>
      --output <file>
      --algorithm <name>

## Параметры

  параметр        описание
  --------------- ------------------------
  `--di`          файл DI графа
  `--bytecode`    файл графа байткода
  `--output`      файл кластеров
  `--algorithm`   алгоритм кластеризации

## Выходной файл

`clusters.json`

### Структура

``` json
{
  "clusters": [
    {
      "id": "cluster-1",
      "classes": [
        "OrderController",
        "OrderService",
        "OrderRepository"
      ],
      "metrics": {
        "cohesion": 0.81,
        "coupling": 0.12
      }
    }
  ],
  "penaltyEdges": [
    {
      "from": "OrderService",
      "to": "PaymentClient",
      "type": "CALLS"
    }
  ]
}
```

------------------------------------------------------------------------

# 4. generate-refactoring

Формирует задачи для AI-агентов по архитектурному рефакторингу.

## Команда

    spring-twin generate-refactoring
      --clusters <file>
      --output <file>

## Параметры

  параметр       описание
  -------------- ----------------
  `--clusters`   файл кластеров
  `--output`     файл задач

## Выходной файл

`tasks.json`

### Структура

``` json
{
  "tasks": [
    {
      "taskType": "split-class",
      "class": "OrderService",
      "reason": "Uses classes from multiple clusters",
      "suggestedModules": [
        "order-domain",
        "payment-integration"
      ]
    }
  ]
}
```

------------------------------------------------------------------------

# Типы связей графа

Поддерживаемые типы связей:

    DEPENDS_ON
    CALLS
    INSTANTIATES
    ACCESSES_FIELD

Каждая связь может содержать дополнительные детали в объекте `details`.

------------------------------------------------------------------------

# Назначение файлов

  файл              назначение
  ----------------- -------------------------------------
  `di.json`         зависимости Spring DI
  `bytecode.json`   структурные зависимости из байткода
  `clusters.json`   результат кластеризации
  `tasks.json`      задачи для AI-рефакторинга
