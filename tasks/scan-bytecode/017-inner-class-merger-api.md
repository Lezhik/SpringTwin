# 017-inner-class-merger — API

## Статус: complete

## Описание

Утилитный класс для объединения вложенных классов с их родительскими классами. Когда опция `--merge-inner-classes` включена (по умолчанию), записи вида `com.example.Outer$Inner` должны объединяться с `com.example.Outer`: зависимости внутреннего класса добавляются к зависимостям внешнего, а сам внутренний класс удаляется из ключей графа.

## Классы

### `spring.twin.scan.InnerClassMerger`

**Тип:** утилитный класс (final, приватный конструктор)

**Методы:**

- `static boolean isInnerClass(String fqcn)` — определяет, является ли FQCN внутренним классом (содержит символ `$`). Возвращает `true`, если в FQCN есть `$`, не являющийся первым символом.

- `static String getOuterClassName(String fqcn)` — извлекает имя внешнего класса из FQCN внутреннего класса. Для `com.example.Outer$Inner` возвращает `com.example.Outer`. Для `com.example.Outer$Inner$Deep` возвращает `com.example.Outer`. Если FQCN не является внутренним классом, возвращает исходный FQCN без изменений.

- `static Map<String, Set<String>> mergeInnerClasses(Map<String, Set<String>> graph)` — выполняет объединение внутренних классов с внешними в графе зависимостей. Для каждого ключа, который является внутренним классом: удаляет его из ключей, добавляет его зависимости в множество зависимостей внешнего класса. Если внешний класс отсутствует в графе, создаёт новую запись с зависимостями внутреннего класса. Возвращает новый `LinkedHashMap` с отсортированными ключами и значениями.

- `static Map<String, Set<String>> mergeInnerClasses(Map<String, Set<String>> graph, boolean merge)` — если `merge == true`, делегирует к `mergeInnerClasses(graph)`. Если `merge == false`, возвращает исходный граф без изменений.

## Зависимости

Нет зависимостей от других фич.

## Результат

Создан класс `InnerClassMerger` с заглушками методов (методы возвращают null/пустые значения, бросают `UnsupportedOperationException`).