// Navigation DTO types for Spring Twin application

/**
 * Represents a single navigation item with optional children.
 */
export interface NavigationItem {
  id: string;
  name: string;
  path: string;
  icon: string;
  children?: NavigationItem[];
}

/**
 * Request interface for navigation operations.
 */
export interface NavigationRequest {
  path: string;
  params?: Record<string, string>;
  query?: Record<string, string>;
}

/**
 * Response interface for navigation operations.
 */
export interface NavigationResponse {
  currentPath: string;
  previousPath: string | null;
  moduleName: string;
}

/**
 * Default navigation items configuration for the application.
 */
export const defaultNavigationItems: NavigationItem[] = [
  {
    id: 'home',
    name: 'Главная',
    path: '/',
    icon: '🏠'
  },
  {
    id: 'project',
    name: 'Проект',
    path: '/project',
    icon: '📁'
  },
  {
    id: 'architecture',
    name: 'Архитектура',
    path: '/architecture',
    icon: '🏗️'
  },
  {
    id: 'analysis',
    name: 'Анализ',
    path: '/analysis',
    icon: '🔍'
  },
  {
    id: 'report',
    name: 'Отчеты',
    path: '/report',
    icon: '📊'
  },
  {
    id: 'mcp',
    name: 'MCP',
    path: '/mcp',
    icon: '🤖'
  }
];