# 016-e2e-tests — API

## Статус: complete

## Описание

End-to-End тесты для команды `cluster`. Тесты принимают на вход параметры для метода main и проверяют выходной JSON файл.

## Классы

### 1. `spring.twin.cluster.e2e.ClusterE2eTest`

**Тип:** Тестовый класс с аннотацией `@SpringBootTest`

**Поля:**
- `tempDir: Path` — временная директория с аннотацией `@TempDir`
- `objectMapper: ObjectMapper` — для чтения JSON

**Методы:**
- `void runClusterAndVerifyOutput(String depsFile, String outputFile, String resolution, Map<String, Object> expectedStructure)` — вспомогательный метод для запуска команды и проверки выходного файла. **Метод должен быть аннотирован Javadoc на английском языке.**

## Зависимости

- Фича 015: cluster-command
- Все предыдущие фичи 001-015

## Результат

Создан класс `ClusterE2eTest` с заглушками методов.