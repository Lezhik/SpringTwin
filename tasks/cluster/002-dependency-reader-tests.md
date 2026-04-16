# 002-dependency-reader — Тесты

## Статус: pending

## Описание

Unit тесты для `DependencyReader`.

## Тестируемые классы

- `spring.twin.cluster.DependencyReader`

## Класс тестов

### `spring.twin.cluster.DependencyReaderTest`

**Методы тестов:**

1. `testRead_validFile_returnsDependencyMap()` — читает валидный dependencies.json, проверяет структуру возвращаемой Map
2. `testRead_emptyFile_returnsEmptyMap()` — читает пустой JSON `{}`, возвращает пустую Map
3. `testRead_nonExistentFile_throwsUncheckedIOException()` — несуществующий файл бросает `UncheckedIOException`
4. `testRead_invalidJson_throwsUncheckedIOException()` — невалидный JSON бросает `UncheckedIOException`
5. `testRead_fileWithMultipleClasses_returnsAllClasses()` — файл с несколькими классами возвращает все ключи
6. `testRead_fileWithLinkDetails_returnsCorrectLinkDetails()` — проверяет что LinkDetails десериализуются корректно с type и details

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Чтение валидного dependencies.json | Map с корректной структурой и LinkDetails |
| 2 | Чтение пустого JSON | Пустая Map |
| 3 | Несуществующий файл | UncheckedIOException |
| 4 | Невалидный JSON | UncheckedIOException |