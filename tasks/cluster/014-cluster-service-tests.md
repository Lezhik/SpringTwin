# 014-cluster-service — Тесты

## Статус: complete

## Описание

Unit тесты для `ClusterService`.

## Тестируемые классы

- `spring.twin.cluster.ClusterService`

## Класс тестов

### `spring.twin.cluster.ClusterServiceTest`

**Методы тестов:**

1. `testExecute_callsDependencyReader()` — проверяет что DependencyReader.read() вызывается с правильным путём
2. `testExecute_callsGraphConverter()` — проверяет что GraphConverter.toUndirectedWeightedGraph() вызывается
3. `testExecute_callsLeidenAlgorithm()` — проверяет что LeidenAlgorithm.cluster() вызывается с правильным resolution
4. `testExecute_callsResultBuilder()` — проверяет что ClusterResultBuilder.build() вызывается
5. `testExecute_callsJsonWriter()` — проверяет что ClusterJsonWriter.write() вызывается с правильным путём
6. `testAnalyze_returnsClusterResult()` — analyze возвращает ClusterResult без записи файла
7. `testExecute_withMockedDependencies_fullPipeline()` — полный пайплайн с моками

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Вызов execute | Все зависимости вызываются в правильном порядке |
| 2 | Вызов analyze | Возвращает ClusterResult без записи |