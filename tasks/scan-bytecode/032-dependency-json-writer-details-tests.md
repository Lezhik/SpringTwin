# 032-dependency-json-writer-details — Тесты

## Статус: complete

## Описание

Unit тесты для нового метода `writeDetails()` в `DependencyJsonWriter`. Тесты проверяют корректность сериализации детализированного графа в JSON.

## Тестируемый класс

`spring.twin.scan.DependencyJsonWriter`

## Тестируемый метод

`void writeDetails(Map<String, Map<String, Set<LinkDetails>>> graph, Path outputFile)`

## Класс тестов

### `spring.twin.scan.DependencyJsonWriterDetailsTest`

**Методы тестов:**

1. `testWriteDetails_emptyGraph_writesEmptyJson()` — пустой граф → файл содержит `{}`
2. `testWriteDetails_singleClassSingleDependency_writesCorrectJson()` — один класс с одной зависимостью → корректный JSON с LinkDetails
3. `testWriteDetails_singleClassMultipleDependencies_writesSortedKeys()` — один класс с несколькими зависимостями → ключи второго уровня отсортированы
4. `testWriteDetails_multipleClasses_writesSortedKeys()` — несколько классов → ключи первого уровня отсортированы
5. `testWriteDetails_linkDetailsSerialized_typeAndDetailsFields()` — LinkDetails сериализуется как `{"type": "FIELD", "details": "fieldName"}`
6. `testWriteDetails_linkTypeSerialized_asStringName()` — LinkType.FIELD сериализуется как строка `"FIELD"`, не как число
7. `testWriteDetails_multipleLinkDetailsPerTarget_sortedArray()` — несколько LinkDetails для одного FQCN → массив отсортирован по type, затем по details
8. `testWriteDetails_createsParentDirectories()` — несуществующие родительские директории создаются автоматически
9. `testWriteDetails_prettyPrinted_twoSpaceIndent()` — JSON отформатирован с 2-мя пробелами отступа
10. `testWriteDetails_superclassLink_emptyDetails()` — LinkType.SUPERCLASS → `{"type": "SUPERCLASS", "details": ""}`

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Пустой граф | `{}` |
| 2 | Одна зависимость | Корректный JSON |
| 3 | Несколько зависимостей | Отсортированные ключи |
| 4 | Несколько классов | Отсортированные ключи |
| 5 | LinkDetails сериализация | Поля type и details |
| 6 | LinkType сериализация | Строковое имя |
| 7 | Несколько LinkDetails | Отсортированный массив |
| 8 | Родительские директории | Создаются |
| 9 | Форматирование | 2 пробела |
| 10 | SUPERCLASS | details="" |