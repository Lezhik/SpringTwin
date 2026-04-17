# 014-cluster-service — API

## Статус: complete

## Описание

Главный сервис пайплайна cluster. Оркестрирует весь процесс: чтение dependencies.json → конвертация графа → кластеризация → построение результата → запись clusters.json.

## Классы

### 1. `spring.twin.cluster.ClusterService`

**Тип:** @Service

**Конструктор:**
- `ClusterService(DependencyReader dependencyReader, GraphConverter graphConverter, LeidenAlgorithm leidenAlgorithm, ClusterMetricsCalculator metricsCalculator, PenaltyEdgeDetector penaltyDetector, ClusterResultBuilder resultBuilder, ClusterJsonWriter jsonWriter)` — внедрение всех зависимостей

**Методы:**
- `void execute(ClusterParams params)` — выполняет полный пайплайн: (1) читает dependencies.json через DependencyReader, (2) конвертирует в ненаправленный граф через GraphConverter, (3) кластеризует через LeidenAlgorithm, (4) строит ClusterResult через ClusterResultBuilder, (5) записывает через ClusterJsonWriter. **Метод должен быть аннотирован Javadoc на английском языке.**
- `ClusterResult analyze(ClusterParams params)` — выполняет пайплайн без записи в файл, возвращает ClusterResult. **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

- Фича 001: cluster-params
- Фича 002: dependency-reader
- Фича 003: graph-converter
- Фича 009: leiden-algorithm
- Фича 010: cluster-metrics
- Фича 011: penalty-edges
- Фича 012: cluster-result
- Фича 013: cluster-json-writer

## Результат

Создан класс `ClusterService` с заглушками методов (методы бросают `UnsupportedOperationException`).