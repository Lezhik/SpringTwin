/**
 * Модуль генерации отчетов.
 * Отвечает за формирование explain-отчетов по endpoint, классу и методу, а также экспорт контекста для LLM.
 */
@org.springframework.modulith.ApplicationModule(
    id = "report",
    displayName = "Report Module",
    allowedDependencies = {"architecture"}
)
package twin.spring.report;