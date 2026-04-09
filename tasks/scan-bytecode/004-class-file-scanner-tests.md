# 004-class-file-scanner — Тесты

## Статус: pending

## Описание

Unit тесты для `ClassFileScanner`. Тесты создают временную директорию с `.class` файлами.

## Тестируемый класс

`spring.twin.scan.ClassFileScanner`

## Класс тестов

### `spring.twin.scan.ClassFileScannerTest`

**Методы тестов:**

1. `testScan_emptyDir_returnsEmptyList()` — пустая директория → пустой список
2. `testScan_singleClassFile_returnsSingletonList()` — один `.class` файл → список из одного пути
3. `testScan_multipleClassFiles_returnsAll()` — несколько `.class` файлов в одной директории → все найдены
4. `testScan_nestedDirectories_returnsAll()` — `.class` файлы в поддиректориях → все найдены рекурсивно
5. `testScan_ignoresNonClassFiles()` — файлы `.java`, `.txt` в директории → не включаются в результат
6. `testScan_nonExistentDir_throwsUncheckedIOException()` — несуществующая директория → `UncheckedIOException`
7. `testToClassName_simpleClass_returnsFqcn()` — `com/example/Service.class` → `com.example.Service`
8. `testToClassName_nestedPackage_returnsFqcn()` — `com/example/service/OrderService.class` → `com.example.service.OrderService`
9. `testToClassName_defaultPackage_returnsSimpleClassName()` — `Service.class` → `Service`
10. `testToClassName_innerClass_returnsFqcnWithDollar()` — `com/example/Outer$Inner.class` → `com.example.Outer$Inner`
11. `testToClassName_nonClassFile_returnsEmpty()` — файл без расширения `.class` → `Optional.empty()`

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Пустая директория | Пустой список файлов |
| 2 | Один .class файл | Один элемент в списке |
| 3 | Вложенные директории | Все .class файлы найдены рекурсивно |
| 4 | Файлы других расширений | Игнорируются |
| 5 | Несуществующая директория | UncheckedIOException |
| 6 | Вычисление FQCN из пути | Корректная замена `/` на `.` и удаление `.class` |