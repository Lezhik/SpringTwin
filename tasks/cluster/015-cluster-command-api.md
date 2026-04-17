# 015-cluster-command — API

## Статус: complete

## Описание

CLI команда `cluster` для Spring Shell.

## Классы

### 1. `spring.twin.command.ClusterCommand`

**Тип:** @ShellComponent

**Конструктор:**
- `ClusterCommand(ClusterService clusterService)` — внедрение сервиса

**Методы:**
- `String cluster(@ShellOption("--deps") String deps, @ShellOption("--output") String output, @ShellOption(value="--resolution", defaultValue="1.5") String resolution)` — CLI команда cluster. Создаёт ClusterParams, вызывает ClusterService.execute(), возвращает сообщение об успехе или ошибке. **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

- Фича 001: cluster-params
- Фича 014: cluster-service

## Результат

Создан класс `ClusterCommand` с заглушками методов (методы бросают `UnsupportedOperationException`).