# 035-e2e-details-tests — Тесты

## Статус: complete

## Описание

Написание сценариев E2E тестов для детализированного пайплайна. Описывает проверяемые сценарии и ожидаемые результаты.

## Тестируемый класс

`spring.twin.scan.e2e.ScanBytecodeE2eDetailsTest`

## Тестируемые методы

- `e2e_scanBytecodeDetails_producesCorrectJson()`
- `e2e_scanBytecodeDetails_withMasks_filtersClasses()`
- `e2e_scanBytecodeDetails_withMergeInnerClasses_mergesInner()`
- `e2e_scanBytecodeDetails_withoutMergeInnerClasses_separateEntries()`
- `e2e_scanBytecodeDetails_jsonFormat_linkDetailsStructure()`
- `e2e_scanBytecodeDetails_jsonFormat_sortedKeys()`
- `e2e_scanBytecodeDetails_allLinkTypesPresent()`

## Сценарии

| # | Сценарий | Входные параметры | Ожидаемый результат JSON |
|---|----------|------------------|------------------------|
| 1 | Полный пайплайн | --classes testee, --output temp | JSON с Map<String, Map<String, Set<LinkDetails>>>, ключи — FQCN, значения — карты зависимостей |
| 2 | С масками | --include service.*, --exclude *Impl | Только классы, соответствующие маскам |
| 3 | Merge inner | --merge-inner-classes true | Вложенные классы объединены с родительскими |
| 4 | No merge inner | --merge-inner-classes false | Вложенные классы как отдельные записи |
| 5 | Структура LinkDetails | Полный пайплайн | Каждый элемент содержит поля type и details |
| 6 | Сортировка ключей | Полный пайплайн | Ключи 1-го и 2-го уровня отсортированы по алфавиту |
| 7 | Все типы связей | Полный пайплайн | Присутствуют: SUPERCLASS, INTERFACE, FIELD, METHOD, CLASS_ANNOTATION, FIELD_ANNOTATION, METHOD_ANNOTATION, METHOD_ARG_ANNOTATION, STATIC_BLOCK |

## Проверки JSON

Для каждого теста:
1. Считать выходной JSON файл
2. Парсить через Jackson в `Map<String, Map<String, List<Map<String, String>>>>`
3. Проверить структуру: внешний ключ — FQCN, внутренний ключ — FQCN зависимости, значение — массив LinkDetails
4. Проверить что каждый LinkDetails содержит поля `type` и `details`
5. Проверить что `type` — строка из допустимого множества LinkType