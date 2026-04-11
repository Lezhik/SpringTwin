# 022-e2e-merge-tests — Реализация

## Статус: pending

## Описание

Реализация E2E тестов для опции `--merge-inner-classes`.

## Классы и методы

### `spring.twin.scan.e2e.ScanBytecodeE2eMergeTest`

**Тип:** Integration тест с Spring Boot Test

**Реализация:**

- `Path getTesteeClassesDir()` — возвращает `Path.of("build/classes/java/test/spring/twin/testee/")`. **Метод должен быть аннотирован Javadoc на английском языке.**

- `Map<String, List<String>> readOutputJson(Path outputFile)` — читает и парсит выходной JSON файл через `ObjectMapper`. **Метод должен быть аннотирован Javadoc на английском языке.**

- `void e2e_mergeInnerClassesTrue_innerClassesMergedIntoOuter()` — создаёт `ScanBytecodeParams(classesDir, outputFile, List.of(), List.of(), true)`, вызывает `scanBytecodeService.execute()`, проверяет что `spring.twin.testee.Outer$Inner` отсутствует в ключах, а `spring.twin.testee.Outer` присутствует. **Метод должен быть аннотирован Javadoc на английском языке.**

- `void e2e_mergeInnerClassesFalse_innerClassesSeparate()` — создаёт `ScanBytecodeParams(classesDir, outputFile, List.of(), List.of(), false)`, вызывает `scanBytecodeService.execute()`, проверяет что `spring.twin.testee.Outer$Inner` присутствует как отдельный ключ. **Метод должен быть аннотирован Javadoc на английском языке.**

- `void e2e_mergeInnerClassesDefault_isTrue()` — создаёт `ScanBytecodeParams` через 4-параметровый конструктор, проверяет что внутренние классы объединены. **Метод должен быть аннотирован Javadoc на английском языке.**

- `void e2e_mergeInnerClassesTrue_noSelfReference()` — проверяет что ни один класс в результате не содержит себя в списке зависимостей. **Метод должен быть аннотирован Javadoc на английском языке.**

- `void e2e_mergeInnerClassesTrue_dependencyOnInnerClass_remappedToOuter()` — проверяет что если класс зависит от `Outer$Inner`, после мержа зависимость ссылается на `Outer`. **Метод должен быть аннотирован Javadoc на английском языке.**

- `void e2e_mergeInnerClassesTrue_sortedOutput()` — проверяет что ключи и значения отсортированы. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Все тесты используют реальную директорию со скомпилированными testee-классами
2. Тесты с `mergeInnerClasses=true` проверяют объединение внутренних классов с внешними
3. Тесты с `mergeInnerClasses=false` проверяют что внутренние классы остаются отдельными записями
4. Тест на значение по умолчанию проверяет обратную совместимость
5. Тест на самоссылки гарантирует корректность мержа
6. Тест на ремаппинг зависимостей проверяет что ссылки на внутренние классы в значениях заменяются