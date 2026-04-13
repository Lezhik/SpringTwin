# 035-e2e-details-tests — Реализация

## Статус: complete

## Описание

Реализация E2E тестов для полного пайплайна scan-bytecode с детализированным форматом вывода.

## Классы и методы

### `spring.twin.scan.e2e.ScanBytecodeE2eDetailsTest` (создание)

**Методы:**

1. `void e2e_scanBytecodeDetails_producesCorrectJson()` — **Метод должен быть аннотирован Javadoc на английском языке.**
   - Создать `ScanBytecodeService` с реальными зависимостями
   - Вызвать `executeDetails(params)` с параметрами: classesDir=testee, outputFile=tempFile
   - Считать и парсить выходной JSON
   - Проверить: JSON не пустой, содержит записи с FQCN ключами, каждая запись содержит Map<String, List<LinkDetails>>

2. `void e2e_scanBytecodeDetails_withMasks_filtersClasses()` — **Метод должен быть аннотирован Javadoc на английском языке.**
   - Параметры: include="service.*", exclude=""
   - Проверить: только классы из пакета service присутствуют в ключах

3. `void e2e_scanBytecodeDetails_withMergeInnerClasses_mergesInner()` — **Метод должен быть аннотирован Javadoc на английском языке.**
   - Параметры: mergeInnerClasses=true
   - Проверить: нет ключей с `$` в имени

4. `void e2e_scanBytecodeDetails_withoutMergeInnerClasses_separateEntries()` — **Метод должен быть аннотирован Javadoc на английском языке.**
   - Параметры: mergeInnerClasses=false
   - Проверить: ключи с `$` присутствуют

5. `void e2e_scanBytecodeDetails_jsonFormat_linkDetailsStructure()` — **Метод должен быть аннотирован Javadoc на английском языке.**
   - Парсить JSON, проверить что каждый элемент массива содержит поля `type` и `details`

6. `void e2e_scanBytecodeDetails_jsonFormat_sortedKeys()` — **Метод должен быть аннотирован Javadoc на английском языке.**
   - Проверить что ключи JSON файла отсортированы по алфавиту

7. `void e2e_scanBytecodeDetails_allLinkTypesPresent()` — **Метод должен быть аннотирован Javadoc на английском языке.**
   - Собрать все уникальные значения `type` из JSON
   - Проверить что присутствуют: SUPERCLASS, INTERFACE, FIELD, METHOD, CLASS_ANNOTATION, FIELD_ANNOTATION, METHOD_ANNOTATION, METHOD_ARG_ANNOTATION, STATIC_BLOCK

## Используемые testee-классы

- `ComplexService` — для проверки всех типов связей
- `Outer` (с вложенными классами) — для проверки merge-inner-classes
- `service.OrderService` — для проверки масок

## Логика работы

1. Создать сервис с реальными компонентами
2. Вызвать executeDetails с параметрами
3. Парсить выходной JSON через Jackson
4. Проверить структуру и содержание