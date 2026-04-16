# 005-modularity-calculator — API

## Статус: complete

## Описание

Вычисление модулярности графа и прироста модулярности при перемещении узла. Модулярность — метрика качества разбиения графа на сообщества. Используется CPM (Constant Potts Model) с параметром resolution.

## Классы

### 1. `spring.twin.cluster.ModularityCalculator`

**Тип:** утилитный класс (final, приватный конструктор)

**Методы:**
- `static double calculateModularity(Map<String, Map<String, Double>> graph, Partition partition, double resolution)` — вычисляет модулярность CPM для всего графа и разбиения. Формула: Q = Σ_ij [A_ij - γ * k_i * k_j / (2m)] * δ(c_i, c_j), где A_ij — вес ребра, k_i — степень узла, m — суммарный вес рёбер, γ — resolution, δ — индикатор одного сообщества. **Метод должен быть аннотирован Javadoc на английском языке.**
- `static double deltaModularity(String node, int targetCommunity, Map<String, Map<String, Double>> graph, Partition partition, double resolution)` — вычисляет прирост модулярности при перемещении узла в целевое сообщество. Формула: ΔQ = [Σ_in + 2*k_i,in] / (2m) - [Σ_tot + k_i]^2 / (2m)^2 - Σ_in/(2m) + Σ_tot^2/(2m)^2 + γ*k_i/(2m), где Σ_in — сумма весов рёбер внутри целевого сообщества, Σ_tot — сумма степеней узлов целевого сообщества, k_i — степень узла, k_i,in — сумма весов рёбер от узла к целевому сообществу. **Метод должен быть аннотирован Javadoc на английском языке.**
- `static double nodeDegree(String node, Map<String, Map<String, Double>> graph)` — вычисляет степень узла (сумму весов всех рёбер узла). **Метод должен быть аннотирован Javadoc на английском языке.**
- `static double communityDegree(int community, Map<String, Map<String, Double>> graph, Partition partition)` — вычисляет сумму степеней всех узлов в сообществе. **Метод должен быть аннотирован Javadoc на английском языке.**
- `static double edgesInsideCommunity(int community, Map<String, Map<String, Double>> graph, Partition partition)` — вычисляет сумму весов рёбер внутри сообщества (каждое ребро учитывается один раз). **Метод должен быть аннотирован Javadoc на английском языке.**
- `static double edgesToCommunity(String node, int community, Map<String, Map<String, Double>> graph, Partition partition)` — вычисляет сумму весов рёбер от узла к узлам в указанном сообществе. **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

- Фича 003: graph-converter (использует тип `Map<String, Map<String, Double>>`)
- Фича 004: partition-model (использует `Partition`)

## Результат

Создан класс `ModularityCalculator` с заглушками методов (методы возвращают 0.0 или бросают `UnsupportedOperationException`).