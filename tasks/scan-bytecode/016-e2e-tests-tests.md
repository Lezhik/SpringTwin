# 016-e2e-tests — Тесты

## Статус: pending

## Описание

Написание End-to-End тестов для полного пайплайна `scan-bytecode`. Тесты вызывают `ScanBytecodeService.execute()` напрямую (не через CLI, так как Spring Shell интерактивный) и проверяют выходной JSON файл.

## Тестируемый сценарий

Полный пайплайн: параметры → сканирование → анализ → запись JSON

## Класс тестов

### `spring.twin.scan.e2e.ScanBytecodeE2eTest`

**Аннотации:** `@SpringBootTest`, `@TempDir`

**Вспомогательные методы:**
- `Path getTesteeClassesDir()` — возвращает путь к `build/classes/java/test/spring/twin/testee/`
- `Map<String, List<String>> readOutputJson(Path outputFile)` — читает и парсит выходной JSON

**Методы тестов:**

1. `e2e_scanBytecode_noMasks_producesDependenciesJson()` — вызывает `ScanBytecodeService.execute()` с testee-классами без масок → файл создан, содержит записи для всех testee-классов
2. `e2e_scanBytecode_withIncludeMask_filtersClasses()` — include=`*.testee.*` → только testee-классы в ключах
3. `e2e_scanBytecode_withExcludeMask_excludesClasses()` — exclude=`*FieldHolder*` → FieldHolder отсутствует в ключах
4. `e2e_scanBytecode_emptyDirectory_producesEmptyJson()` — пустая директория → JSON `{}`
5. `e2e_scanBytecode_outputFile_hasSortedKeys()` — ключи JSON отсортированы по алфавиту
6. `e2e_scanBytecode_outputFile_hasSortedValues()` — массивы зависимостей отсортированы по алфавиту
7. `e2e_scanBytecode_inheritanceDependency_detected()` — `ChildClass` зависит от `ParentClass` (наследование)
8. `e2e_scanBytecode_fieldDependency_detected()` — класс с полем типа `OrderModel` зависит от `OrderModel`
9. `e2e_scanBytecode_methodDependency_detected()` — класс с параметром метода типа `OrderModel` зависит от `OrderModel`
10. `e2e_scanBytecode_annotationDependency_detected()` — класс с аннотацией `@Service` зависит от `org.springframework.stereotype.Service`
11. `e2e_scanBytecode_genericDependency_detected()` — класс с `List<OrderModel>` зависит от `OrderModel` и `java.util.List`
12. `e2e_scanBytecode_arrayDependency_baseTypeExtracted()` — класс с `OrderModel[]` зависит от `OrderModel` (не от массива)

## Сценарии

| # | Сценарий | Параметры | Проверка |
|---|----------|-----------|----------|
| 1 | Без масок | `--classes testee --output file` | Все классы в JSON |
| 2 | Include маска | `--include *.testee.*` | Только testee классы |
| 3 | Exclude маска | `--exclude *FieldHolder*` | Без FieldHolder |
| 4 | Пустая директория | `--classes empty --output file` | `{}` |
| 5 | Наследование | без масок | ChildClass → ParentClass |
| 6 | Поле | без масок | Class → FieldType |
| 7 | Метод | без масок | Class → ParamType |
| 8 | Аннотация | без масок | Class → AnnotationType |
| 9 | Generic | без масок | Class → GenericTypeParam |
| 10 | Массив | без масок | Class → BaseArrayType |