# 001-cluster-params — Тесты

## Статус: pending

## Описание

Unit тесты для `ClusterParams`.

## Тестируемые классы

- `spring.twin.cluster.ClusterParams`

## Класс тестов

### `spring.twin.cluster.ClusterParamsTest`

**Методы тестов:**

1. `testOf_createsParamsWithDefaultResolution()` — проверяет что `ClusterParams.of()` с пустой строкой resolution создаёт объект с resolution = 1.5
2. `testOf_createsParamsWithCustomResolution()` — проверяет что `ClusterParams.of()` с валидной строкой "2.0" создаёт объект с resolution = 2.0
3. `testOf_nullResolutionRaw_usesDefault()` — при null для resolutionRaw используется значение 1.5
4. `testOf_emptyResolutionRaw_usesDefault()` — при пустой строке для resolutionRaw используется значение 1.5
5. `testOf_resolutionBelowMin_throwsException()` — значение 0.4 бросает `IllegalArgumentException`
6. `testOf_resolutionAboveMax_throwsException()` — значение 5.1 бросает `IllegalArgumentException`
7. `testOf_resolutionMinBoundary_accepted()` — значение 0.5 принимается
8. `testOf_resolutionMaxBoundary_accepted()` — значение 5.0 принимается
9. `testConstructor_storesAllFields()` — прямой конструктор корректно сохраняет все поля
10. `testOf_invalidNumberFormat_throwsException()` — нечисловая строка бросает `NumberFormatException`

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Создание params с путями и resolution по умолчанию | resolution = 1.5 |
| 2 | Создание params с путями и кастомным resolution | resolution = переданное значение |
| 3 | Resolution вне диапазона | IllegalArgumentException |
| 4 | Невалидный формат числа | NumberFormatException |