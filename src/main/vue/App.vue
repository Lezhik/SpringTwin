<template>
  <div id="app" name="app-root">
    <header class="app-header" name="app-header">
      <h1 name="app-title">Spring Twin</h1>
      <p name="app-subtitle">Понимай архитектуру. Управляй сложностью.</p>
    </header>
    
    <main class="app-content" name="main-content">
      <div class="loading" v-if="isLoading" name="loading-indicator">
        Загрузка...
      </div>
      <div v-else class="welcome-message" name="welcome-message">
        <h2>Добро пожаловать в Spring Twin</h2>
        <p>MCP-агент для анализа Spring Boot проектов</p>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

// Reactive state
const isLoading = ref(false);
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  background-color: #f5f5f5;
  color: #333;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background-color: #4CAF50;
  color: white;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.app-header h1 {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
}

.app-header p {
  font-size: 1.1rem;
  opacity: 0.9;
}

.app-content {
  flex: 1;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.loading {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
  color: #666;
}

.welcome-message {
  text-align: center;
  padding: 2rem;
}

.welcome-message h2 {
  color: #4CAF50;
  margin-bottom: 1rem;
}

.welcome-message p {
  color: #666;
  font-size: 1.1rem;
}
</style>