# 035-e2e-details-tests — API

## Статус: pending

## Описание

End-to-end тесты для полного пайплайна scan-bytecode с новым форматом вывода. Тесты принимают на вход параметры для метода main и проверяют выходной JSON файл.

## Классы

### `spring.twin.scan.e2e.ScanBytecodeE2eDetailsTest`

**Тип:** тестовый класс (не Spring bean)

**Методы:**

- `void e2e_scanBytecodeDetails_producesCorrectJson()` — полный пайплайн: передаёт параметры в `ScanBytecodeService.executeDetails()`, проверяет структуру и содержание выходного JSON файла
- `void e2e_scanBytecodeDetails_withMasks_filtersClasses()` — пайплайн с include/exclude масками, проверяет фильтрацию в выходном JSON
- `void e2e_scanBytecodeDetails_withMergeInnerClasses_mergesInner()` — пайплайн с merge-inner-classes=true, проверяет объединение вложенных классов
- `void e2e_scanBytecodeDetails_withoutMergeInnerClasses_separateEntries()` — пайплайн с merge-inner-classes=false, проверяет раздельные записи для вложенных классов
- `void e2e_scanBytecodeDetails_jsonFormat_linkDetailsStructure()` — проверяет что каждый LinkDetails в JSON имеет поля `type` и `details`
- `void e2e_scanBytecodeDetails_jsonFormat_sortedKeys()` — проверяет что ключи первого и второго уровня отсортированы по алфавиту
- `void e2e_scanBytecodeDetails_allLinkTypesPresent()` — проверяет что в выходном JSON присутствуют все типы связей: SUPERCLASS, INTERFACE, FIELD, METHOD, CLASS_ANNOTATION, FIELD_ANNOTATION, METHOD_ANNOTATION, METHOD_ARG_ANNOTATION, STATIC_BLOCK

## Параметры для тестов

Тесты используют скомпилированные классы из `src/test/java/spring/twin/testee/` и временный файл для вывода.

## Зависимости

- Фича 034: `ScanBytecodeService.executeDetails()`
- Все предыдущие фичи 023-034

## Результат

Создан тестовый класс `ScanBytecodeE2eDetailsTest` с заглушками тестовых методов.