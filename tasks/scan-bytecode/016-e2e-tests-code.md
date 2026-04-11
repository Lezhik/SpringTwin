# 016-e2e-tests — Реализация

## Статус: complete

## Описание

Реализация End-to-End тестов для полного пайплайна `scan-bytecode`.

## Классы и методы

### `spring.twin.scan.e2e.ScanBytecodeE2eTest`

**Аннотации:** `@SpringBootTest`, `@TempDir`

**Внедряемые зависимости:**
- `@Autowired ScanBytecodeService scanBytecodeService`

**Поля:**
- `@TempDir Path tempDir`
- `Path classesDir` — инициализируется в `@BeforeEach` как путь к `build/classes/java/test/spring/twin/testee/`

**Вспомогательные методы:**

- `Path getTesteeClassesDir()` — возвращает `Path.of("build/classes/java/test/spring/twin/testee/")`. **Метод должен быть аннотирован Javadoc на английском языке.**

- `Map<String, List<String>> readOutputJson(Path outputFile)` — читает JSON файл через `ObjectMapper` и возвращает `Map<String, List<String>>`. **Метод должен быть аннотирован Javadoc на английском языке.**

**Тестовые методы:**

1. `e2e_scanBytecode_noMasks_producesDependenciesJson()` — создаёт `ScanBytecodeParams` с testee-классами и путём к tempDir/output.json, вызывает `scanBytecodeService.execute(params)`, проверяет что файл существует и содержит записи
2. `e2e_scanBytecode_withIncludeMask_filtersClasses()` — include=`*.testee.*`, проверяет что только testee-классы в ключах
3. `e2e_scanBytecode_withExcludeMask_excludesClasses()` — exclude=`*FieldHolder*`, проверяет отсутствие FieldHolder
4. `e2e_scanBytecode_emptyDirectory_producesEmptyJson()` — пустая директория → `{}`
5. `e2e_scanBytecode_outputFile_hasSortedKeys()` — проверяет порядок ключей
6. `e2e_scanBytecode_outputFile_hasSortedValues()` — проверяет порядок значений в массивах
7. `e2e_scanBytecode_inheritanceDependency_detected()` — проверяет что ChildClass зависит от ParentClass
8. `e2e_scanBytecode_fieldDependency_detected()` — проверяет зависимость по полю
9. `e2e_scanBytecode_methodDependency_detected()` — проверяет зависимость по параметру/возвращаемому типу метода
10. `e2e_scanBytecode_annotationDependency_detected()` — проверяет зависимость по аннотации
11. `e2e_scanBytecode_genericDependency_detected()` — проверяет извлечение generic-типов
12. `e2e_scanBytecode_arrayDependency_baseTypeExtracted()` — проверяет что массив ссылается на базовый тип

## Логика работы

Каждый тест:
1. Формирует `ScanBytecodeParams` с нужными параметрами
2. Вызывает `scanBytecodeService.execute(params)`
3. Читает выходной JSON файл
4. Проверяет содержимое JSON на соответствие ожиданиям