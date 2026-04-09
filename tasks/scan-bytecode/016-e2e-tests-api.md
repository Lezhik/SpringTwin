# 016-e2e-tests — API

## Статус: pending

## Описание

End-to-End тесты полного пайплайна `scan-bytecode`. Тесты запускают приложение через `CliApplication.main()` с параметрами команды и проверяют выходной JSON файл.

## Классы

### `spring.twin.scan.e2e.ScanBytecodeE2eTest`

**Тип:** Integration тест с Spring Boot Test

**Аннотации:** `@SpringBootTest`, `@TempDir`

**Поля:**
- `@TempDir Path tempDir` — временная директория для выходных файлов
- `Path classesDir` — путь к скомпилированным testee-классам (вычисляется относительно проекта)

**Методы:**

- `void e2e_scanBytecode_noMasks_producesDependenciesJson()` — запуск с `--classes` и `--output` без масок → файл `dependencies.json` создан и содержит корректные записи
- `void e2e_scanBytecode_withIncludeMask_filtersClasses()` — запуск с `--include com.example.*` → только классы, соответствующие маске
- `void e2e_scanBytecode_withExcludeMask_excludesClasses()` — запуск с `--exclude *Test` → исключённые классы отсутствуют
- `void e2e_scanBytecode_emptyDirectory_producesEmptyJson()` — запуск с пустой директорией → `{}`
- `void e2e_scanBytecode_outputFile_hasSortedKeys()` — ключи в выходном JSON отсортированы
- `void e2e_scanBytecode_outputFile_hasSortedValues()` — массивы зависимостей отсортированы
- `void e2e_scanBytecode_inheritanceDependency_detected()` — зависимость наследования обнаружена
- `void e2e_scanBytecode_fieldDependency_detected()` — зависимость по полю обнаружена
- `void e2e_scanBytecode_methodDependency_detected()` — зависимость по методу обнаружена
- `void e2e_scanBytecode_annotationDependency_detected()` — зависимость по аннотации обнаружена
- `void e2e_scanBytecode_genericDependency_detected()` — generic-типы извлечены
- `void e2e_scanBytecode_arrayDependency_baseTypeExtracted()` — массивы ссылаются на базовый тип

## Зависимости

- Все предыдущие фичи (015 — полная интеграция)

## Результат

Создан класс `ScanBytecodeE2eTest` с заглушками тестовых методов.