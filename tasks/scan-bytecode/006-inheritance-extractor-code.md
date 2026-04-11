# 006-inheritance-extractor — Реализация

## Статус: complete

## Описание

Реализация `InheritanceExtractor` для извлечения зависимостей наследования и имплементации.

## Классы и методы

### `spring.twin.scan.InheritanceExtractor`

**Тип:** Spring bean (`@Component`)

**Зависимости (конструктор):**
- `GenericTypeExtractor` — через статические методы (утилитный класс)

**Реализация:**

- `Set<String> extract(byte[] classBytes)` — создаёт `ClassNode` через ASM `ClassReader`. Извлекает:
  1. `superName` — если не `java/lang/Object`, конвертирует через `FqcnNormalizer.fromInternalName()`
  2. `interfaces` — каждый интерфейс конвертирует через `FqcnNormalizer.fromInternalName()`
  3. `signature` — если не null, извлекает типы через `GenericTypeExtractor.extractTypes()`
  Результат — объединённое множество всех найденных FQCN, отфильтрованное от пустых Optional. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Прочитать байткод через `new ClassReader(classBytes)` → `ClassNode`
2. Извлечь суперкласс (кроме `java.lang.Object`)
3. Извлечь все имплементируемые интерфейсы
4. Если есть generic-сигнатура — извлечь типы через `GenericTypeExtractor`
5. Объединить все типы в одно множество FQCN