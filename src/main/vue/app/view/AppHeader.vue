<template>
  <header class="app-header" name="app-header" role="banner">
    <div class="app-header__container">
      <div class="app-header__logo">
        <router-link to="/" class="app-header__logo-link" name="logo-link">
          <span class="app-header__logo-icon" name="logo-icon">🏗️</span>
          <span class="app-header__logo-text" name="logo-text">Spring Twin</span>
        </router-link>
      </div>

      <nav class="app-header__nav" role="navigation" aria-label="Primary navigation">
        <ul class="app-header__nav-list" name="nav-list">
          <li class="app-header__nav-item" name="nav-item">
            <router-link 
              to="/" 
              class="app-header__nav-link"
              :class="{ 'app-header__nav-link--active': isActive('/') }"
              name="nav-link-home"
            >
              Главная
            </router-link>
          </li>
          <li class="app-header__nav-item" name="nav-item">
            <router-link 
              to="/project" 
              class="app-header__nav-link"
              :class="{ 'app-header__nav-link--active': isActive('/project') }"
              name="nav-link-project"
            >
              Проект
            </router-link>
          </li>
          <li class="app-header__nav-item" name="nav-item">
            <router-link 
              to="/architecture" 
              class="app-header__nav-link"
              :class="{ 'app-header__nav-link--active': isActive('/architecture') }"
              name="nav-link-architecture"
            >
              Архитектура
            </router-link>
          </li>
          <li class="app-header__nav-item" name="nav-item">
            <router-link 
              to="/analysis" 
              class="app-header__nav-link"
              :class="{ 'app-header__nav-link--active': isActive('/analysis') }"
              name="nav-link-analysis"
            >
              Анализ
            </router-link>
          </li>
          <li class="app-header__nav-item" name="nav-item">
            <router-link 
              to="/report" 
              class="app-header__nav-link"
              :class="{ 'app-header__nav-link--active': isActive('/report') }"
              name="nav-link-report"
            >
              Отчеты
            </router-link>
          </li>
          <li class="app-header__nav-item" name="nav-item">
            <router-link 
              to="/mcp" 
              class="app-header__nav-link"
              :class="{ 'app-header__nav-link--active': isActive('/mcp') }"
              name="nav-link-mcp"
            >
              MCP
            </router-link>
          </li>
        </ul>
      </nav>

      <div class="app-header__actions" name="header-actions">
        <button class="app-header__action-btn" name="action-btn" @click="toggleSidebar">
          <span class="app-header__action-icon" name="action-icon">☰</span>
          <span class="app-header__action-text" name="action-text">Меню</span>
        </button>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';

const route = useRoute();

/**
 * Checks if the current route matches the given path.
 */
const isActive = (path: string) => {
  if (path === '/') {
    return route.path === '/';
  }
  return route.path.startsWith(path);
};

/**
 * Toggles sidebar visibility (for mobile).
 */
const toggleSidebar = () => {
  const sidebar = document.querySelector('.app-sidebar');
  if (sidebar) {
    sidebar.classList.toggle('app-sidebar--open');
  }
};
</script>

<style scoped>
.app-header {
  background-color: #4CAF50;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.app-header__container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1.5rem;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 60px;
}

.app-header__logo {
  flex-shrink: 0;
}

.app-header__logo-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: white;
  transition: opacity 0.2s ease;
}

.app-header__logo-link:hover {
  opacity: 0.9;
}

.app-header__logo-icon {
  font-size: 1.8rem;
  margin-right: 0.5rem;
}

.app-header__logo-text {
  font-size: 1.2rem;
  font-weight: 600;
  letter-spacing: -0.02em;
}

.app-header__nav {
  flex: 1;
  margin: 0 2rem;
}

.app-header__nav-list {
  display: flex;
  align-items: center;
  margin: 0;
  padding: 0;
  list-style: none;
  gap: 0.5rem;
}

.app-header__nav-item {
  margin: 0;
}

.app-header__nav-link {
  display: block;
  padding: 0.5rem 1rem;
  color: white;
  text-decoration: none;
  font-size: 0.95rem;
  font-weight: 500;
  border-radius: 4px;
  transition: all 0.2s ease;
  opacity: 0.9;
}

.app-header__nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
  opacity: 1;
}

.app-header__nav-link--active {
  background-color: rgba(255, 255, 255, 0.2);
  opacity: 1;
}

.app-header__actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.app-header__action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.app-header__action-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.app-header__action-icon {
  font-size: 1.1rem;
}

.app-header__action-text {
  font-weight: 500;
}

@media (max-width: 768px) {
  .app-header__container {
    padding: 0 1rem;
  }

  .app-header__nav {
    display: none;
  }

  .app-header__logo-text {
    font-size: 1rem;
  }

  .app-header__action-text {
    display: none;
  }
}
</style>