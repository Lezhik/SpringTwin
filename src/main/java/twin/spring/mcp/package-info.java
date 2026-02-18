/**
 * Модуль MCP-интеграции.
 * Отвечает за MCP tools для интеграции с Cursor AI и Kilo Code.
 */
@org.springframework.modulith.ApplicationModule(
    id = "mcp",
    displayName = "MCP Module",
    allowedDependencies = {"report", "architecture"}
)
package twin.spring.mcp;