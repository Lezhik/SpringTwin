# 012-dependency-graph-builder — Реализация

## Статус: pending

## Описание

Реализация `DependencyGraphBuilder` для построения полного графа зависимостей.

## Классы и методы

### `spring.twin.scan.DependencyGraphBuilder`

**Тип:** Spring bean (`@Component`)

**Конструктор:**
- `DependencyGraphBuilder(ClassFileScanner classFileScanner, BytecodeClassAnalyzer bytecodeClassAnalyzer)` — внедрение зависимостей

**Реализация:**

- `Map<String, Set<String>> build(Path classesDir, List<String> includeMasks, List<String> excludeMasks)` — основной метод построения графа:
  1. Вызвать `classFileScanner.scan(classesDir)` для получения списка `.class` файлов
  2. Для каждого файла: прочитать байты через `Files.readAllBytes()`
  3. Извлечь FQCN через `bytecodeClassAnalyzer.extractClassName(bytes)`
  4. Проверить FQCN через `MaskMatcher.shouldInclude(fqcn, includeMasks, excludeMasks)` — пропустить если false
  5. Извлечь зависимости через `bytecodeClassAnalyzer.extractDependencies(bytes)`
  6. Отфильтровать зависимости через `MaskMatcher.shouldInclude()`
  7. Добавить запись в `TreeMap<String, TreeSet<String>>` для сортировки
  8. Вернуть `LinkedHashMap` с отсортированными ключами и значениями
  **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. Сканирование директории → список файлов
2. Анализ каждого файла → FQCN + зависимости
3. Фильтрация по маскам → только нужные классы и зависимости
4. Сортировка → детерминированный порядок
5. Формирование графа → Map<String, Set<String>>