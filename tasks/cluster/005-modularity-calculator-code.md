# 005-modularity-calculator — Реализация

## Статус: pending

## Описание

Реализация `ModularityCalculator`.

## Классы и методы

### `spring.twin.cluster.ModularityCalculator`

**Тип:** final утилитный класс

**Реализация:**
- Приватный конструктор — предотвращает инстанцирование
- `static double nodeDegree(String node, Map<String, Map<String, Double>> graph)` — сумма весов всех рёбер узла. Если узел отсутствует в графе, возвращает 0. **Метод должен быть аннотирован Javadoc на английском языке.**
- `static double communityDegree(int community, Map<String, Map<String, Double>> graph, Partition partition)` — сумма `nodeDegree` для всех узлов в сообществе. **Метод должен быть аннотирован Javadoc на английском языке.**
- `static double edgesInsideCommunity(int community, Map<String, Map<String, Double>> graph, Partition partition)` — для каждой пары узлов в сообществе проверяет наличие ребра, суммирует вес. Каждое ребро учитывается один раз. **Метод должен быть аннотирован Javadoc на английском языке.**
- `static double edgesToCommunity(String node, int community, Map<String, Map<String, Double>> graph, Partition partition)` — для каждого соседа узла, принадлежащего сообществу, суммирует вес ребра. **Метод должен быть аннотирован Javadoc на английском языке.**
- `static double calculateModularity(Map<String, Map<String, Double>> graph, Partition partition, double resolution)` — вычисляет CPM модулярность: Q = (1/2m) * Σ_ij [A_ij - γ * k_i * k_j / (2m)] * δ(c_i, c_j). Итерирует по всем парам узлов в одном сообществе. **Метод должен быть аннотирован Javadoc на английском языке.**
- `static double deltaModularity(String node, int targetCommunity, Map<String, Map<String, Double>> graph, Partition partition, double resolution)` — прирост модулярности при перемещении: ΔQ = [k_i,in / m] - [γ * Σ_tot * k_i / (2m^2)], где k_i,in = edgesToCommunity, Σ_tot = communityDegree, k_i = nodeDegree, m = totalEdgeWeight. Упрощённая формула CPM. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `nodeDegree()` — сумма значений из Map соседей узла
2. `communityDegree()` — агрегация nodeDegree по узлам сообщества
3. `edgesInsideCommunity()` — суммирование весов рёбер между узлами одного сообщества
4. `edgesToCommunity()` — суммирование весов рёбер от узла к сообществу
5. `calculateModularity()` — полная формула CPM модулярности
6. `deltaModularity()` — инкрементальная формула для оптимизации перемещений