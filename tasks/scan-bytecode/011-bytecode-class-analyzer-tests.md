# 011-bytecode-class-analyzer — Тесты

## Статус: complete

## Описание

Unit тесты для `BytecodeClassAnalyzer`. Тесты используют скомпилированные классы-образцы из `src/test/java/spring/twin/testee/`.

## Тестируемый класс

`spring.twin.scan.BytecodeClassAnalyzer`

## Классы-образцы (testee)

Используются ранее созданные классы из `src/test/java/spring/twin/testee/`, а также:

- `ComplexService.java` — класс, объединяющий несколько видов зависимостей:
  - extends другой класс
  - implements интерфейс
  - имеет поля с generic-типами
  - имеет методы с параметрами и возвращаемыми типами
  - имеет аннотации
  - использует типы в теле методов

## Класс тестов

### `spring.twin.scan.BytecodeClassAnalyzerTest`

**Методы тестов:**

1. `testExtractClassName_returnsFqcn()` — корректное извлечение имени класса из байткода
2. `testExtractDependencies_combinesAllExtractors()` — результаты всех экстракторов объединены
3. `testExtractDependencies_includesInheritance()` — содержит зависимости наследования
4. `testExtractDependencies_includesFieldTypes()` — содержит типы полей
5. `testExtractDependencies_includesMethodTypes()` — содержит типы параметров и возвратов методов
6. `testExtractDependencies_includesAnnotations()` — содержит типы аннотаций
7. `testExtractDependencies_includesCodeUsage()` — содержит типы из использования в коде
8. `testExtractDependencies_noDuplicates()` — множество не содержит дубликатов
9. `testExtractClassName_nullBytes_throwsException()` — null → бросает исключение

## Сценарии

| # | Сценарий | Ожидаемый результат |
|---|----------|-------------------|
| 1 | Извлечение имени класса | FQCN класса |
| 2 | Комплексный класс | Все виды зависимостей объединены |
| 3 | Нет дубликатов | Set содержит уникальные FQCN |