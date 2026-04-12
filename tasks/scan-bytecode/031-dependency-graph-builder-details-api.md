# 031-dependency-graph-builder-details — API

## Статус: pending

## Описание

Добавление детализированного метода построения графа в `DependencyGraphBuilder`. Новый метод возвращает `Map<String, Map<String, Set<LinkDetails>>>` вместо `Map<String, Set<String>>`, соответствуя новому формату SPEC.

## Классы

### `spring.twin.scan.DependencyGraphBuilder` (модификация)

**Новые методы:**

- `Map<String, Map<String, Set<LinkDetails>>> buildDetails(Path classesDir, List<String> includeMasks, List<String> excludeMasks)` — строит детализированный граф зависимостей. Возвращает двухуровневую карту: внешний ключ — FQCN класса, внутренний ключ — FQCN зависимости, значение — множество LinkDetails. **Метод должен быть аннотирован Javadoc на английском языке.**

- `Map<String, Map<String, Set<LinkDetails>>> buildDetails(Path classesDir, List<String> includeMasks, List<String> excludeMasks, boolean mergeInnerClasses)` — строит детализированный граф с опцией объединения вложенных классов. **Метод должен быть аннотирован Javadoc на английском языке.**

**Правила фильтрации:**

- Примитивные типы исключаются из результата
- Зависимости фильтруются по include/exclude маскам через `MaskMatcher.shouldInclude()`
- Ключи (FQCN анализируемых классов) также фильтруются по маскам

**Существующие методы:**

- `Map<String, Set<String>> build(...)` — остаётся без изменений для обратной совместимости

## Зависимости

- Фича 030: `BytecodeClassAnalyzer.extractDependenciesDetails()`
- Фича 004: `ClassFileScanner`
- Фича 003: `MaskMatcher`

## Результат

Добавлены методы `buildDetails()` в класс `DependencyGraphBuilder` с заглушками реализации.