# 031-dependency-graph-builder-details — Реализация

## Статус: complete

## Описание

Реализация методов `buildDetails()` в `DependencyGraphBuilder` для построения детализированного графа зависимостей.

## Классы и методы

### `spring.twin.scan.DependencyGraphBuilder` (модификация)

**Новые методы:**

- `Map<String, Map<String, Set<LinkDetails>>> buildDetails(Path classesDir, List<String> includeMasks, List<String> excludeMasks)` — строит детализированный граф зависимостей. **Метод должен быть аннотирован Javadoc на английском языке.**

- `Map<String, Map<String, Set<LinkDetails>>> buildDetails(Path classesDir, List<String> includeMasks, List<String> excludeMasks, boolean mergeInnerClasses)` — строит детализированный граф с опцией объединения вложенных классов. **Метод должен быть аннотирован Javadoc на английском языке.**

**Реализация buildDetails (без merge):**

1. Сканировать директорию через `classFileScanner.scan(classesDir)`
2. Для каждого .class файла:
   - Прочитать байткод через `Files.readAllBytes(classFile)`
   - Извлечь FQCN через `bytecodeClassAnalyzer.extractClassName(classBytes)`
   - Проверить FQCN через `MaskMatcher.shouldInclude(fqcn, includeMasks, excludeMasks)` — если нет, пропустить
   - Извлечь детализированные зависимости через `bytecodeClassAnalyzer.extractDependenciesDetails(classBytes)`
   - Результат: `Map<String, Map<String, Set<LinkDetails>>>` где внешний ключ — FQCN анализируемого класса
   - Отфильтровать зависимости: удалить примитивные типы и исключённые по маскам FQCN из внутренних ключей
   - Добавить в общий граф
3. Вернуть отсортированный граф (TreeMap для ключей)

**Реализация buildDetails (с merge):**

1. Вызвать `buildDetails(classesDir, includeMasks, excludeMasks)` для построения исходного графа
2. Если `mergeInnerClasses == true` — применить `InnerClassMerger.mergeInnerClassesDetails(graph, true)`
3. Вернуть результат

## Логика работы

1. Сканирование → фильтрация классов → извлечение деталей → фильтрация зависимостей → агрегация
2. Для merge: применить InnerClassMerger к детализированному графу