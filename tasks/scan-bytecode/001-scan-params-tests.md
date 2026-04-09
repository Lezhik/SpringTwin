# 001-scan-params — Тесты

## Статус: pending

## Описание

Unit тесты для `ScanBytecodeParams` и `MaskParser`.

## Тестируемые классы

- `spring.twin.scan.ScanBytecodeParams`
- `spring.twin.scan.MaskParser`

## Класс тестов

### `spring.twin.scan.ScanBytecodeParamsTest`

**Методы тестов:**

1. `testOf_createsParamsWithParsedMasks()` — проверяет что `ScanBytecodeParams.of()` корректно создаёт объект с разобранными масками
2. `testOf_emptyIncludeRaw_returnsEmptyIncludeList()` — при пустой строке include список масок пустой
3. `testOf_emptyExcludeRaw_returnsEmptyExcludeList()` — при пустой строке exclude список масок пустой
4. `testOf_nullIncludeRaw_returnsEmptyIncludeList()` — при null для include список масок пустой
5. `testOf_nullExcludeRaw_returnsEmptyExcludeList()` — при null для exclude список масок пустой
6. `testOf_multipleMasksSeparatedBySemicolon()` — корректный разбор нескольких масок через `;`
7. `testOf_trimsWhitespaceAroundMasks()` — пробелы вокруг масок обрезаются
8. `testOf_filtersEmptyMasks()` — пустые элементы между `;` фильтруются

### `spring.twin.scan.MaskParserTest`

**Методы тестов:**

1. `testParseMasks_nullInput_returnsEmptyList()` — null → пустой список
2. `testParseMasks_emptyString_returnsEmptyList()` — пустая строка → пустой список
3. `testParseMasks_singleMask_returnsSingletonList()` — одна маска → список из одного элемента
4. `testParseMasks_multipleMasks_returnsList()` — несколько масок через `;` → список
5. `testParseMasks_trimsWhitespace()` — пробелы обрезаются
6. `testParseMasks_filtersEmptySegments()` — `a;;b` → `[a, b]`
7. `testParseMasks_semicolonOnly_returnsEmptyList()` — строка из одних `;` → пустой список

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Создание params с валидными путями и масками | Все поля заполнены корректно |
| 2 | Пустые/null маски | Пустые списки, не null |
| 3 | Маски с пробелами и пустыми сегментами | Обрезаны и отфильтрованы |