<template>
  <div class="home-view" name="home-view">
    <div class="home-view__hero">
      <h1 class="home-view__title" name="home-title">
        Понимай архитектуру. Управляй сложностью.
      </h1>
      <p class="home-view__subtitle" name="home-subtitle">
        Spring Twin — MCP-агент для анализа Spring Boot проектов с построением архитектурного графа и формированием контекстных отчетов для совместно с Cloud LLM.
      </p>
    </div>

    <div class="home-view__features">
      <div class="feature-card" name="feature-card" v-for="feature in features" :key="feature.id">
        <div class="feature-card__icon" name="feature-icon">{{ feature.icon }}</div>
        <h3 class="feature-card__title" name="feature-title">{{ feature.title }}</h3>
        <p class="feature-card__description" name="feature-description">
          {{ feature.description }}
        </p>
      </div>
    </div>

    <div class="home-view__getting-started">
      <h2 class="home-view__section-title" name="section-title">Как начать</h2>
      <div class="step-list" name="step-list">
        <div class="step-item" name="step-item" v-for="step in steps" :key="step.id">
          <div class="step-item__number" name="step-number">{{ step.number }}</div>
          <div class="step-item__content">
            <h4 class="step-item__title" name="step-title">{{ step.title }}</h4>
            <p class="step-item__description" name="step-description">{{ step.description }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const features = [
  {
    id: 1,
    icon: '🏗️',
    title: 'Архитектурный график',
    description: 'Построение детального графа зависимостей и связей между компонентами Spring Boot приложения.'
  },
  {
    id: 2,
    icon: '🔍',
    title: 'Анализ кода',
    description: 'Интеллектуальный анализ исходного кода и байткода для выявления архитектурных паттернов.'
  },
  {
    id: 3,
    icon: '📊',
    title: 'Генерация отчетов',
    description: 'Создание контекстных explain-отчетов по endpoint, классу или методу для Cloud LLM.'
  },
  {
    id: 4,
    icon: '🤖',
    title: 'MCP интеграция',
    description: 'Удобная интеграция с Cursor AI и Kilo Code для эффективной разработки.'
  }
];

const steps = [
  {
    id: 1,
    number: 1,
    title: 'Настройка проекта',
    description: 'Укажите путь к вашему Spring Boot проекту и конфигурацию анализа.'
  },
  {
    id: 2,
    number: 2,
    title: 'Запуск индексации',
    description: 'Запустите процесс индексации для построения архитектурного графа.'
  },
  {
    id: 3,
    number: 3,
    title: 'Анализ результатов',
    description: 'Исследуйте граф зависимостей и получайте детализированные отчеты.'
  },
  {
    id: 4,
    number: 4,
    title: 'Экспорт контекста',
    description: 'Экспортируйте структурированный контекст для анализа с помощью Cloud LLM.'
  }
];
</script>

<style scoped>
.home-view {
  max-width: 1200px;
  margin: 0 auto;
}

.home-view__hero {
  text-align: center;
  padding: 4rem 2rem;
  background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 3rem;
}

.home-view__title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  line-height: 1.2;
}

.home-view__subtitle {
  font-size: 1.2rem;
  line-height: 1.6;
  opacity: 0.95;
  max-width: 800px;
  margin: 0 auto;
}

.home-view__features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.feature-card {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
}

.feature-card__icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-card__title {
  font-size: 1.3rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: #333;
}

.feature-card__description {
  font-size: 0.95rem;
  line-height: 1.6;
  color: #666;
}

.home-view__getting-started {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.home-view__section-title {
  font-size: 1.8rem;
  font-weight: 600;
  margin-bottom: 2rem;
  color: #333;
  text-align: center;
}

.step-list {
  max-width: 800px;
  margin: 0 auto;
}

.step-item {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid #e0e0e0;
}

.step-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.step-item__number {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  background-color: #4CAF50;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 1.1rem;
}

.step-item__title {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #333;
}

.step-item__description {
  font-size: 0.95rem;
  line-height: 1.6;
  color: #666;
}

@media (max-width: 768px) {
  .home-view__hero {
    padding: 2rem 1.5rem;
  }

  .home-view__title {
    font-size: 2rem;
  }

  .home-view__subtitle {
    font-size: 1rem;
  }

  .home-view__features {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .feature-card {
    padding: 1.5rem;
  }

  .step-item {
    flex-direction: column;
    gap: 1rem;
  }
}
</style>