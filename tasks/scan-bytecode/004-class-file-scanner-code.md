# 004-class-file-scanner — Реализация

## Статус: completed

## Описание

Реализация `ClassFileScanner` для рекурсивного поиска `.class` файлов.

## Классы и методы

### `spring.twin.scan.ClassFileScanner`

**Тип:** Spring bean (`@Component`)

**Реализация:**

- `List<Path> scan(Path classesDir)` — использует `Files.walkFileTree()` или `Files.walk()` для рекурсивного обхода директории. Фильтрует файлы по расширению `.class`. Если директория не существует — бросает `UncheckedIOException`. Возвращает отсортированный список для детерминированности. **Метод должен быть аннотирован Javadoc на английском языке.**

- `Optional<String> toClassName(Path classesDir, Path classFile)` — вычисляет относительный путь от `classesDir` к `classFile` через `classesDir.relativize(classFile)`. Убирает расширение `.class`. Заменяет `File.separatorChar` на `.`. Если файл не заканчивается на `.class` — возвращает `Optional.empty()`. **Метод должен быть аннотирован Javadoc на английском языке.**

## Логика работы

1. `scan` — обходит файловое дерево, собирает `.class` файлы, сортирует результат
2. `toClassName` — относительный путь → FQCN: удаление `.class`, замена разделителей на `.`