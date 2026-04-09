# 012-dependency-graph-builder — API

## Статус: pending

## Описание

Построение полного графа зависимостей на основе сканирования `.class` файлов. Объединяет результаты анализа отдельных классов, применяет маски include/exclude и формирует структуру `Map<String, Set<String>>`.

## Классы

### `spring.twin.scan.DependencyGraphBuilder`

**Тип:** Spring bean с внедрением зависимостей через конструктор

**Конструктор:**
- `DependencyGraphBuilder(ClassFileScanner classFileScanner, BytecodeClassAnalyzer bytecodeClassAnalyzer)`

**Методы:**

- `Map<String, Set<String>> build(Path classesDir, List<String> includeMasks, List<String> excludeMasks)` — сканирует директорию на наличие `.class` файлов, для каждого файла анализирует зависимости через `BytecodeClassAnalyzer`, фильтрует классы по маскам include/exclude через `MaskMatcher`, формирует граф зависимостей. Возвращает `LinkedHashMap` для детерминированного порядка ключей (отсортированных по FQCN). **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Получить список `.class` файлов через `ClassFileScanner.scan()`
2. Для каждого файла прочитать байткод и проанализировать через `BytecodeClassAnalyzer`
3. Извлечь FQCN класса через `BytecodeClassAnalyzer.extractClassName()`
4. Проверить FQCN через `MaskMatcher.shouldInclude()` — если не проходит, пропустить
5. Извлечь зависимости через `BytecodeClassAnalyzer.extractDependencies()`
6. Отфильтровать зависимости: убрать примитивы, оставить только те, что проходят `MaskMatcher.shouldInclude()`
7. Добавить запись в граф: FQCN → Set зависимостей
8. Вернуть отсортированный граф

## Зависимости

- Фича 004: `ClassFileScanner`
- Фича 011: `BytecodeClassAnalyzer`
- Фича 003: `MaskMatcher`

## Результат

Создан класс `DependencyGraphBuilder` с заглушками методов.