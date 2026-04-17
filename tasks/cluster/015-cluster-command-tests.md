# 015-cluster-command — Unit тесты

## Статус: complete

## Описание

Unit тесты для CLI команды `cluster`.

## Тестируемый класс

`spring.twin.command.ClusterCommand`

## Класс тестов

`spring.twin.command.ClusterCommandTest`

## Методы тестов и сценарии

### 1. `clusterShouldCallServiceWithValidParams()`
**Сценарий:** Вызов команды с валидными параметрами `--deps`, `--output`, `--resolution` должен создать ClusterParams и вызвать ClusterService.execute()

### 2. `clusterShouldUseDefaultResolution()`
**Сценарий:** Вызов команды без `--resolution` должен использовать значение по умолчанию 1.5

### 3. `clusterShouldReturnSuccessMessage()`
**Сценарий:** При успешном выполнении команда должна возвращать сообщение с путём к выходному файлу

### 4. `clusterShouldThrowOnInvalidResolution()`
**Сценарий:** Передача нечислового значения в `--resolution` должна приводить к ошибке

### 5. `clusterShouldHandleServiceException()`
**Сценарий:** Если ClusterService.execute() бросает исключение, команда должна корректно обработать ошибку

### 6. `clusterShouldParseResolutionAsDouble()`
**Сценарий:** Параметр `--resolution` должен корректно парситься как Double из строки

## Зависимости

- Фича 015: cluster-command (API)
- Мок: ClusterService