# 003-mask-matcher — Реализация

## Статус: complete

## Описание

Реализация `MaskMatcher` для сопоставления FQCN с масками include/exclude.

## Классы и методы

### `spring.twin.scan.MaskMatcher`

**Тип:** final утилитный класс

**Реализация:**

- Приватный конструктор — предотвращает инстанцирование

- `static boolean matches(String fqcn, String mask)` — если маска содержит `*` или `?`, преобразует маску в regex: `*` → `.*`, `?` → `.`, экранирует остальные regex-метасимволы. Если маска не содержит спецсимволов — проверка `fqcn.contains(mask)`. Проверка case-sensitive. **Метод должен быть аннотирован Javadoc на английском языке.**

- `static boolean matchesAny(String fqcn, List<String> masks)` — если `masks` == null или пустой, возвращает `true`. Иначе — любой элемент из masks, для которого `matches(fqcn, mask)` возвращает true. **Метод должен быть аннотирован Javadoc на английском языке.**

- `static boolean shouldInclude(String fqcn, List<String> includeMasks, List<String> excludeMasks)` — возвращает `matchesAny(fqcn, includeMasks) && !matchesAny(fqcn, excludeMasks)`. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `matches` — основная логика сопоставления. Маски с `*`/`?` конвертируются в regex (с экранированием остальных символов). Простые маски проверяются через `contains`.
2. `matchesAny` — проверка по списку масок, пустой список = любое совпадение.
3. `shouldInclude` — комбинирует include и exclude: включается только если проходит include и не проходит exclude.