# 001-cluster-params — API

## Статус: complete

## Описание

Определение DTO для параметров команды `cluster`.

## Классы

### 1. `spring.twin.cluster.ClusterParams`

**Тип:** record (immutable DTO)

**Поля:**
- `Path depsFile` — путь к файлу графа зависимостей (dependencies.json)
- `Path outputFile` — путь к выходному JSON файлу (clusters.json)
- `double resolution` — параметр кластеризации, определяет размер кластеров (0.5–5.0), по умолчанию 1.5

**Конструктор:**
- `ClusterParams(Path depsFile, Path outputFile, double resolution)` — основной конструктор

**Методы:**
- `static ClusterParams of(Path depsFile, Path outputFile, String resolutionRaw)` — фабричный метод, парсит строку resolution; если `resolutionRaw` == null или пустая строка, использует значение по умолчанию 1.5; если значение вне диапазона 0.5–5.0, бросает `IllegalArgumentException`

## Зависимости

Нет зависимостей от других фич.

## Результат

Создан класс `ClusterParams` с заглушками методов (методы возвращают null или бросают `UnsupportedOperationException`).