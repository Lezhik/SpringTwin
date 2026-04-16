# 001-cluster-params — Реализация

## Статус: pending

## Описание

Реализация `ClusterParams`.

## Классы и методы

### `spring.twin.cluster.ClusterParams`

**Тип:** record

**Реализация:**
- `ClusterParams(Path depsFile, Path outputFile, double resolution)` — record-конструктор
- `static ClusterParams of(Path depsFile, Path outputFile, String resolutionRaw)` — если `resolutionRaw` == null или `isBlank()`, создаёт record с resolution = 1.5. Иначе парсит строку через `Double.parseDouble()`, проверяет что значение в диапазоне [0.5, 5.0], при нарушении бросает `IllegalArgumentException`. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `ClusterParams.of()` — фабричный метод, обрабатывает null/пустую строку resolutionRaw (значение по умолчанию 1.5), парсит число, валидирует диапазон [0.5, 5.0]