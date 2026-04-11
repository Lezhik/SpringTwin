# 019-graph-builder-merge — API

## Статус: pending

## Описание

Добавление поддержки `mergeInnerClasses` в `DependencyGraphBuilder`. Метод `build()` получает перегрузку с параметром `boolean mergeInnerClasses`, которая после построения графа вызывает `InnerClassMerger.mergeInnerClasses()` при необходимости.

## Изменения в классах

### `spring.twin.scan.DependencyGraphBuilder` (модификация)

**Новый метод:**

- `Map<String, Set<String>> build(Path classesDir, List<String> includeMasks, List<String> excludeMasks, boolean mergeInnerClasses)` — строит граф зависимостей и, если `mergeInnerClasses == true`, вызывает `InnerClassMerger.mergeInnerClasses(graph, true)` для объединения внутренних классов. Если `mergeInnerClasses == false`, возвращает граф без изменений.

**Существующий метод:**

- `Map<String, Set<String>> build(Path classesDir, List<String> includeMasks, List<String> excludeMasks)` — сохраняется без изменений, делегирует в новый метод со значением `mergeInnerClasses = false` для обратной совместимости (поведение по умолчанию не меняется для существующих вызовов; значение по умолчанию `true` будет на уровне CLI/Service).

## Зависимости

- Фича 017: `InnerClassMerger`

## Результат

Класс `DependencyGraphBuilder` обновлён: добавлен перегруженный метод `build()` с параметром `mergeInnerClasses`, существующий метод сохранён для обратной совместимости.