# 011-penalty-edges — API

## Статус: pending

## Описание

Обнаружение штрафных рёбер — связей между классами из разных кластеров. Формат penaltyEdges: Map<String, Set<String>>, где ключ — полное имя класса, значение — множество классов из других кластеров, на которые он ссылается.

## Классы

### 1. `spring.twin.cluster.PenaltyEdgeDetector`

**Тип:** @Component

**Конструктор:**
- `PenaltyEdgeDetector()` — конструктор по умолчанию

**Методы:**
- `Map<String, Set<String>> detect(Partition partition, Map<String, Map<String, Set<LinkDetails>>> dependencyGraph)` — обнаруживает штрафные рёбра. Для каждого класса проверяет все его зависимости: если зависимый класс в другом кластере — это штрафное ребро. Возвращает Map<String, Set<String>> в формате SPEC. **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

- Фича 004: partition-model (использует `Partition`)

## Результат

Создан класс `PenaltyEdgeDetector` с заглушками методов (методы возвращают null или бросают `UnsupportedOperationException`).