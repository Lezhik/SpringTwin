# 014-cluster-service — Реализация

## Статус: pending

## Описание

Реализация `ClusterService`.

## Классы и методы

### `spring.twin.cluster.ClusterService`

**Тип:** @Service

**Реализация:**
- `ClusterService(DependencyReader dependencyReader, GraphConverter graphConverter, LeidenAlgorithm leidenAlgorithm, ClusterMetricsCalculator metricsCalculator, PenaltyEdgeDetector penaltyDetector, ClusterResultBuilder resultBuilder, ClusterJsonWriter jsonWriter)` — сохраняет все зависимости в поля
- `void execute(ClusterParams params)` — (1) `dependencyReader.read(params.depsFile())`, (2) `graphConverter.toUndirectedWeightedGraph(depGraph)`, (3) `leidenAlgorithm.cluster(undirectedGraph, params.resolution())`, (4) `resultBuilder.build(partition, depGraph, metricsCalculator, penaltyDetector)`, (5) `jsonWriter.write(clusterResult, params.outputFile())`. **Метод должен быть аннотирован Javadoc на английском языке.**
- `ClusterResult analyze(ClusterParams params)` — выполняет шаги (1)–(4) без записи в файл, возвращает ClusterResult. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `execute()` — полный пайплайн: read → convert → cluster → build → write
2. `analyze()` — пайплайн без записи: read → convert → cluster → build → return