<template>
  <div class="not-found-view" name="not-found-view">
    <div class="not-found-content" name="not-found-content">
      <div class="not-found-icon" name="not-found-icon">404</div>
      <h1 class="not-found-title" name="not-found-title">Страница не найдена</h1>
      <p class="not-found-description" name="not-found-description">
        К сожалению, запрошенная страница не существует или была перемещена.
      </p>
      <div class="not-found-actions" name="not-found-actions">
        <router-link 
          to="/" 
          class="not-found-action-btn"
          name="home-btn"
        >
          На главную
        </router-link>
        <button 
          class="not-found-action-btn not-found-action-btn--secondary"
          name="back-btn"
          @click="goBack"
        >
          Назад
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();

const goBack = () => {
  if (window.history.length > 1) {
    router.back();
  } else {
    router.push('/');
  }
};
</script>

<style scoped>
.not-found-view {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  background-color: #f5f5f5;
}

.not-found-content {
  text-align: center;
  max-width: 500px;
  padding: 2rem;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.not-found-icon {
  font-size: 4rem;
  font-weight: 700;
  color: #4CAF50;
  margin-bottom: 1rem;
}

.not-found-title {
  font-size: 2rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 1rem;
}

.not-found-description {
  font-size: 1rem;
  line-height: 1.6;
  color: #666;
  margin-bottom: 2rem;
}

.not-found-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.not-found-action-btn {
  padding: 0.75rem 1.5rem;
  background-color: #4CAF50;
  color: white;
  text-decoration: none;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.not-found-action-btn:hover {
  background-color: #45a049;
  transform: translateY(-1px);
}

.not-found-action-btn--secondary {
  background-color: #6c757d;
}

.not-found-action-btn--secondary:hover {
  background-color: #5a6268;
}

@media (max-width: 768px) {
  .not-found-content {
    margin: 1rem;
    padding: 1.5rem;
  }

  .not-found-icon {
    font-size: 3rem;
  }

  .not-found-title {
    font-size: 1.5rem;
  }

  .not-found-actions {
    flex-direction: column;
  }

  .not-found-action-btn {
    width: 100%;
  }
}
</style>