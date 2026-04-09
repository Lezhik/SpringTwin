# 002-fqcn-normalizer — Реализация

## Статус: pending

## Описание

Реализация `FqcnNormalizer` для нормализации имён классов из JVM дескрипторов.

## Классы и методы

### `spring.twin.scan.FqcnNormalizer`

**Тип:** final утилитный класс

**Реализация:**

- Приватный конструктор — предотвращает инстанцирование

- `static Optional<String> fromInternalName(String internalName)` — если `internalName` == null, возвращает `Optional.empty()`. Проверяет имя на соответствие примитивным типам (`int`, `long`, `boolean`, `byte`, `short`, `char`, `float`, `double`, `void`) — если совпадает, возвращает `Optional.empty()`. Иначе вызывает `internalToFqcn()` и оборачивает в `Optional.of()`. **Метод должен быть аннотирован Javadoc на английском языке.**

- `static Optional<String> fromDescriptor(String descriptor)` — если `descriptor` == null или пустой, возвращает `Optional.empty()`. Удаляет ведущие символы `[` (массивы). Если следующий символ `L`, извлекает содержимое между `L` и `;`, затем нормализует через `internalToFqcn()`. Если после удаления `[` остался символ примитива (`I`, `J`, `B`, `S`, `C`, `F`, `D`, `Z`, `V`), возвращает `Optional.empty()`. **Метод должен быть аннотирован Javadoc на английском языке.**

- `static String internalToFqcn(String internalName)` — заменяет все `/` на `.` в строке. Не фильтрует примитивы. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `fromInternalName` — фильтрует примитивы, конвертирует `/` → `.`
2. `fromDescriptor` — снимает массивные скобки `[`, извлекает объектный тип из `L...;`, фильтрует примитивы
3. `internalToFqcn` — простая замена символов, используется как базовая операция другими методами