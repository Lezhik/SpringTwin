# 013-dependency-json-writer — Тесты

## Статус: complete

## Описание

Unit тесты для `DependencyJsonWriter`.

## Тестируемый класс

`spring.twin.scan.DependencyJsonWriter`

## Класс тестов

### `spring.twin.scan.DependencyJsonWriterTest`

**Методы тестов:**

1. `testWrite_validGraph_createsJsonFile()` — запись графа → файл существует
2. `testWrite_validGraph_correctJsonContent()` — запись графа → содержимое соответствует ожидаемому JSON
3. `testWrite_sortedKeys()` — ключи в JSON отсортированы по алфавиту
4. `testWrite_sortedValues()` — массивы зависимостей отсортированы по алфавиту
5. `testWrite_emptyGraph_createsEmptyJsonObject()` — пустой граф → `{}`
6. `testWrite_createsParentDirectories()` — несуществующие родительские директории создаются
7. `testWrite_overwritesExistingFile()` — существующий файл перезаписывается
8. `testWrite_emptyDependencySet_createsEmptyArray()` — класс без зависимостей → `"com.example.Service": []`
9. `testWrite_utf8Encoding()` — FQCN с не-ASCII символами корректно сериализуются

## Сценарии

| # | Сценарий | Вход | Ожидаемый результат |
|---|----------|------|-------------------|
| 1 | Валидный граф | Map с 2 записями | JSON с 2 ключами, отсортированными |
| 2 | Пустой граф | Пустой Map | `{}` |
| 3 | Класс без зависимостей | Map с пустым Set | `"class": []` |
| 4 | Несуществующая директория | Путь с поддиректориями | Файл создан, директории созданы |