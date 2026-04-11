# 022-e2e-merge-tests — API

## Статус: pending

## Описание

End-to-End тесты для опции `--merge-inner-classes`. Тесты вызывают `ScanBytecodeService.execute()` с различными значениями `mergeInnerClasses` и проверяют выходной JSON файл.

## Классы

### `spring.twin.scan.e2e.ScanBytecodeE2eMergeTest`

**Тип:** Integration тест с Spring Boot Test

**Аннотации:** `@SpringBootTest`, `@TempDir`

**Поля:**
- `@TempDir Path tempDir` — временная директория для выходных файлов
- `@Autowired ScanBytecodeService scanBytecodeService` — внедрённый сервис
- `Path classesDir` — путь к скомпилированным testee-классам

**Методы:**

- `void e2e_mergeInnerClassesTrue_innerClassesMergedIntoOuter()` — запуск с `mergeInnerClasses=true`, проверка что `Outer$Inner` отсутствует в ключах, а `Outer` содержит зависимости внутреннего класса

- `void e2e_mergeInnerClassesFalse_innerClassesSeparate()` — запуск с `mergeInnerClasses=false`, проверка что `Outer$Inner` присутствует как отдельный ключ в графе

- `void e2e_mergeInnerClassesDefault_isTrue()` — запуск с `ScanBytecodeParams` через 4-параметровый конструктор (по умолчанию `mergeInnerClasses=true`), проверка что внутренние классы объединены

- `void e2e_mergeInnerClassesTrue_noSelfReference()` — после мержа внешний класс не содержит самоссылку

- `void e2e_mergeInnerClassesTrue_dependencyOnInnerClass_remappedToOuter()` — если другой класс зависит от `Outer$Inner`, после мержа зависимость заменяется на `Outer`

- `void e2e_mergeInnerClassesTrue_sortedOutput()` — ключи и значения в выходном JSON отсортированы после мержа

## Зависимости

- Все предыдущие фичи (021 — полная интеграция)

## Результат

Создан класс `ScanBytecodeE2eMergeTest` с заглушками тестовых методов.