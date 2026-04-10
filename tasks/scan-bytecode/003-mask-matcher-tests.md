# 003-mask-matcher — Тесты

## Статус: complete

## Описание

Unit тесты для `MaskMatcher`.

## Тестируемый класс

`spring.twin.scan.MaskMatcher`

## Класс тестов

### `spring.twin.scan.MaskMatcherTest`

**Методы тестов:**

1. `testMatches_wildcardStar_matchesAny()` — маска `*` соответствует любому FQCN
2. `testMatches_wildcardStarPrefix_matches()` — `com.example.*` соответствует `com.example.OrderService`
3. `testMatches_wildcardStarSuffix_matches()` — `*Service` соответствует `com.example.OrderService`
4. `testMatches_wildcardStarMiddle_matches()` — `com.*.Service` соответствует `com.example.Service`
5. `testMatches_wildcardQuestionMark_singleChar()` — `com.example.Order?ervice` соответствует `com.example.OrderService`
6. `testMatches_wildcardQuestionMark_noMatch_extraChar()` — `com.example.Order?` не соответствует `com.example.OrderService`
7. `testMatches_plainSubstring_containsCheck()` — маска `order` соответствует `com.example.OrderService` (contains, case-sensitive)
8. `testMatches_plainSubstring_noMatch()` — маска `xyz` не соответствует `com.example.OrderService`
9. `testMatches_exactMatch_matches()` — `com.example.OrderService` точно соответствует себе
10. `testMatches_multipleWildcards_matches()` — `*.service.*` соответствует `com.example.service.OrderService`
11. `testMatchesAny_emptyMaskList_returnsTrue()` — пустой список масок → `true`
12. `testMatchesAny_matchingMask_returnsTrue()` — хотя бы одна маска совпадает → `true`
13. `testMatchesAny_noMatchingMask_returnsFalse()` — ни одна маска не совпадает → `false`
14. `testShouldInclude_emptyIncludeAndExclude_returnsTrue()` — оба списка пусты → класс включается
15. `testShouldInclude_matchingInclude_returnsTrue()` — соответствует include → включается
16. `testShouldInclude_notMatchingInclude_returnsFalse()` — не соответствует include → не включается
17. `testShouldInclude_matchingExclude_returnsFalse()` — соответствует exclude → не включается (даже если соответствует include)
18. `testShouldInclude_includeAndExclude_bothMatch_excludeWins()` — соответствует и include и exclude → exclude побеждает, не включается
19. `testShouldInclude_nonEmptyIncludeNotMatching_returnsFalse()` — непустой include, FQCN не соответствует → не включается
20. `testMatches_caseSensitive()` — `com.example.orderservice` не соответствует `com.example.OrderService` (case-sensitive)

## Сценарии

| # | Сценарий | FQCN | Маска | Результат |
|---|----------|------|-------|-----------|
| 1 | Звёздочка в начале | `com.example.Service` | `*.Service` | true |
| 2 | Звёздочка в конце | `com.example.Service` | `com.example.*` | true |
| 3 | Вопросительный знак | `com.example.Service` | `com.example.Servic?` | true |
| 4 | Подстрока | `com.example.OrderService` | `order` | false (case-sensitive) |
| 5 | Подстрока | `com.example.OrderService` | `Order` | true |
| 6 | Пустой список масок | любой | `[]` | true |
| 7 | Include + Exclude | `com.example.Service` | include=`*`, exclude=`*.Service` | false |