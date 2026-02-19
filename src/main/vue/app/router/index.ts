import { createRouter, createWebHistory } from 'vue-router';

/**
 * Vue Router configuration for Spring Twin application.
 * 
 * Routes are configured with lazy loading for better performance.
 */
const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('../view/HomeView.vue'),
    meta: {
      title: 'Главная',
      description: 'Spring Twin - MCP-агент для анализа Spring Boot проектов'
    }
  },
  {
    path: '/project',
    name: 'project',
    component: () => import('../../project/view/ProjectView.vue'),
    meta: {
      title: 'Проект',
      description: 'Конфигурация проекта и управление анализами'
    }
  },
  {
    path: '/architecture',
    name: 'architecture',
    component: () => import('../../architecture/view/ArchitectureView.vue'),
    meta: {
      title: 'Архитектура',
      description: 'Архитектурный график и модели данных'
    }
  },
  {
    path: '/analysis',
    name: 'analysis',
    component: () => import('../../analysis/view/AnalysisView.vue'),
    meta: {
      title: 'Анализ',
      description: 'Процессы анализа и индексации проекта'
    }
  },
  {
    path: '/report',
    name: 'report',
    component: () => import('../../report/view/ReportView.vue'),
    meta: {
      title: 'Отчеты',
      description: 'Генерация и просмотр отчетов'
    }
  },
  {
    path: '/mcp',
    name: 'mcp',
    component: () => import('../../mcp/view/McpView.vue'),
    meta: {
      title: 'MCP',
      description: 'MCP-интеграция с внешними инструментами'
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('../view/NotFoundView.vue'),
    meta: {
      title: 'Страница не найдена',
      description: 'Запрошенная страница не существует'
    }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

/**
 * Sets document title based on route meta.
 */
router.afterEach((to) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} | Spring Twin`;
  } else {
    document.title = 'Spring Twin';
  }
});

export default router;