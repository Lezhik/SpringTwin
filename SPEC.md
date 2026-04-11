# Spring Twin CLI --- CLI и форматы данных

Sprin Twin - это прототип цифрового двойника для Java/Spring Boot проектов для переработки legacy проектов с помошью AI агентов и Cloud LLM.

Проект:
1. Извлекает из java байткода взаимосвязь между объектами в виде графа
2. Кластеризует граф
3. На основе шаблона формирует задачи на рефакторинг проекта (разбиение связей из разных кластеров)

В MVP разрабатывается CLI утилита, которая хранит результаты работы в JSON (минимально возможный функционал).

Документ описывает параметры CLI и структуры JSON-файлов, используемых в Spring Twin.

------------------------------------------------------------------------

# Общая схема pipeline

scan-bytecode 		 → dependencies.json
cluster      		 → clusters.json
generate-refactoring → tasks.json

Каждый этап читает входные JSON-файлы и формирует новый JSON-артефакт.

------------------------------------------------------------------------

# 1. scan-bytecode

Анализирует `.class` файлы и извлекает структурные зависимости.

## Команда

    spring-twin scan-bytecode
      --classes <path>
      --output <file>
      --include <mask>
      --exclude <mask>
      --merge-inner-classes <true|false>

## Параметры

  параметр      описание
  ------------- ------------------------------------------------------------------
  `--classes`   путь к директории с `.class` файлами. для windows символы '\\' должны быть экранированы.
                например: c:\\\\myproject\\\\build\\\\classes
  `--output`    путь к JSON результату
  `--include`   маски полных имён классов (FQCN), несколько через `;`
  `--exclude`   маски полных имён классов (FQCN), несколько через `;`
  `--merge-inner-classes` объединять вложенные классы с родительскими, по умолчанию true.

**Формат масок:**
- маски применяются к полным именам классов (FQCN), например: `com.example.*`, `*.service.*`
- упрощённый синтаксис: `*` (любые символы), `?` (один символ)
- проверка по вхождению: достаточно указать часть пакета, например: `order`
- несколько масок перечисляются через `;`, например: `com.example.*;com.demo.*`
- пустая маска include - означает любой класс, пустая маска exclude - не исключает никакие классы

## Выходной файл
Виды связей:
- Наследование класса или интерфейса
- Имплементация интерфейса
- Поле класса
- Аргумент метода или конструктора
- Возвращаемый тип метода
- Использование в коде (статическая инициализация или тело метода): вызов метода, объявление переменной итд
- Аннотации

Нужно учитывать, что типы могут быть:
- Массивами - тогда нужно ссылаться на базовый тип массива, а не на массив
- Generic-ми, тогда нужно ссылаться на все типы: сам тип, его generic и на вложенные generic-и, если они есть

## Выходной файл

`dependencies.json`

### Структура

Структура должна соответствовать структуре Map<String, Map<String, Set<LinkDetails>>> 
где ключом выступает полное имя класса, 
ключ второго уровня - полное имея класса, на который он ссылается
а детали запись с информацией о ссылке

LinkDetails: 
``` json
{
  type: "FIELD",
  details: "orderRepository"
}
```

type определяет тип связи (базовый класс, поле итп),
поле details описывает данные о связи и зависит от типа:
- type: SUPERCLASS - базовый класс, поле details пустое
- type: INTERFACE - имплментируемый интерфейс, поле details пустое
- type: FIELD - поле класса, в поле details имя поля
- type: STATIC_BLOCK - используется в статическом блоке инициализации, поле details пустое
- type: METHOD - используется в методе, как возвращаемый тип, аргумент или в коде метода, в поле details сигнатура метода
- type: CLASS_ANNOTATION - аннотация к классу, поле details пустое
- type: FIELD_ANNOTATION - аннотация к полю, в details имя поля
- type: METHOD_ANNOTATION - аннотация к методу, в details сигнатура метода
- type: METHOD_ARG_ANNOTATION - аннотация к аргументу метода, в details сигнатура метода

ВАЖНО! извлеченные generic типы имеют такой же тип ссылки, как тип, для которого они извлекались.
для LinkType перегружаются equals/hashCode, чтобы избежать в Set дублирования одинаковых ссылок 

``` json
{
  "com.example.OrderService": [
	"com.example.PaymentClient": [
	  {"type": "FIELD", "details": "paymentClient"}.
	], 
	"com.example.repository.OrderRepository": [
	  {"type": "FIELD", "details": "orderRepository"}.
	]
  ],
  "com.example.repository.OrderRepository": [
	"com.example.model.OrderModel": [
	  {"type": "METHOD", "details": "Lcom/example/model/OrderModel;add(Lcom/example/model/OrderModel;)"}
	]
  ]
}
```

------------------------------------------------------------------------

# 2. cluster

Выполняет кластеризацию графа зависимостей

## Команда

    spring-twin cluster
      --deps <file>
      --output <file>

## Параметры

  параметр        описание
  --------------- ------------------------
  `--deps`    файл графа байткода
  `--output`  файл кластеров

## Выходной файл

`clusters.json`

### Структура

Структура penaltyEdges должна соответствовать структуре Map<String, Set<String>  где ключом выступает полное имя класса,а в множестве содержаться все классы, на которые он ссылается.

``` json
{
  "clusters": [
    {
      "id": "cluster-1",
      "classes": [
        "com.example.controller.OrderController",
        "com.example.service.OrderService",
        "com.example.repository.OrderRepository"
      ],
      "metrics": {
        "cohesion": 0.81,
        "coupling": 0.12
      }
    },
	{
      "id": "cluster-2",
      "classes": [
        "com.example.PaymentClient",
      ],
      "metrics": {
        "cohesion": 0.81,
        "coupling": 0.12
      }
	}
  ],
  "penaltyEdges": {
      "com.example.OrderService": ["com.example.PaymentClient"]
  }
}
```

------------------------------------------------------------------------

# 3. generate-refactoring

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
      "class": "com.example.service.OrderService",
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

# Назначение файлов

  файл              назначение
  ----------------- -------------------------------------
  `dependencies.json`   структурные зависимости из байткода
  `clusters.json`   	результат кластеризации
  `tasks.json`      	задачи для AI-рефакторинга
