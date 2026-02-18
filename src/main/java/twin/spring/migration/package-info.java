/**
 * Модуль миграций базы данных.
 * Отвечает за миграции Neo4j: создание индексов, ограничений и начальных данных.
 */
@org.springframework.modulith.ApplicationModule(
    id = "migration",
    displayName = "Migration Module",
    allowedDependencies = {"architecture"}
)
package twin.spring.migration;