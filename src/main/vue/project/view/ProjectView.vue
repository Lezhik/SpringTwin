<template>
  <div class="project-view" name="project-view">
    <h1 class="view-title" name="view-title">Проект</h1>
    <p class="view-description" name="view-description">
      Конфигурация проекта и управление анализами
    </p>
    
    <div class="view-content" name="view-content">
      <div class="placeholder-content" name="placeholder-content">
        <div class="placeholder-icon" name="placeholder-icon">📁</div>
        <h2 class="placeholder-title" name="placeholder-title">Проект модуль</h2>
        <p class="placeholder-text" name="placeholder-text">
          Здесь будет функционал для настройки и управления проектом
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Project module view logic
</script>

<style scoped>
.project-view {
  width: 100%;
}

.view-title {
  font-size: 2rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
}

.view-description {
  font-size: 1rem;
  color: #666;
  margin-bottom: 2rem;
  line-height: 1.6;
}

.view-content {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-content {
  text-align: center;
  padding: 2rem;
  max-width: 400px;
}

.placeholder-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.3;
}

.placeholder-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 1rem;
}

.placeholder-text {
  font-size: 1rem;
  color: #666;
  line-height: 1.6;
}
</style>