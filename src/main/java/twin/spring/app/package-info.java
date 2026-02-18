/**
 * Корневой модуль приложения.
 * Агрегирует все остальные модули, предоставляет общие компоненты UI, главную страницу и навигацию.
 */
@org.springframework.modulith.ApplicationModule(
    id = "app",
    displayName = "Application Module",
    allowedDependencies = {"project", "architecture", "analysis", "report", "migration", "mcp"}
)
package twin.spring.app;