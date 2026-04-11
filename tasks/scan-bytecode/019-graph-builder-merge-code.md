# 019-graph-builder-merge — Реализация

## Статус: complete

## Описание

Добавление поддержки `mergeInnerClasses` в `DependencyGraphBuilder`.

## Классы и методы

### `spring.twin.scan.DependencyGraphBuilder` (модификация)

**Реализация:**

- `Map<String, Set<String>> build(Path classesDir, List<String> includeMasks, List<String> excludeMasks, boolean mergeInnerClasses)` — алгоритм:
  1. Вызывает существующий метод `build(classesDir, includeMasks, excludeMasks)` для построения исходного графа
  2. Вызывает `InnerClassMerger.mergeInnerClasses(graph, mergeInnerClasses)` для условного объединения внутренних классов
  3. Возвращает результат
  **Метод должен быть аннотирован Javadoc на английском языке.**

- Существующий метод `build(Path, List, List)` — остаётся без изменений, возвращает граф без мержа внутренних классов. Это обеспечивает обратную совместимость.

## Логика работы

1. Новый метод `build()` с 4 параметрами делегирует построение графа существующему методу, затем применяет `InnerClassMerger` в зависимости от флага
2. Существующий метод `build()` с 3 параметрами не меняется — все существующие вызовы продолжают работать
3. `InnerClassMerger` — статический утилитный класс, не требует внедрения через конструктор