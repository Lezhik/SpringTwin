/**
 * Модуль процессов анализа.
 * Отвечает за AST индексацию Java исходников, анализ байткода и обнаружение Spring аннотаций.
 */
@org.springframework.modulith.ApplicationModule(
    id = "analysis",
    displayName = "Analysis Module",
    allowedDependencies = {"project", "architecture"}
)
package twin.spring.analysis;