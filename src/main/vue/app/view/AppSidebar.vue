<template>
  <aside class="app-sidebar" name="app-sidebar" role="complementary" aria-label="Sidebar navigation">
    <div class="app-sidebar__content">
      <nav class="app-sidebar__nav" role="navigation" aria-label="Module navigation">
        <ul class="app-sidebar__nav-list" name="sidebar-nav-list">
          <NavItem 
            v-for="item in navigationItems" 
            :key="item.id" 
            :item="item"
          />
        </ul>
      </nav>

      <div class="app-sidebar__info" name="sidebar-info">
        <div class="app-sidebar__info-section">
          <h3 class="app-sidebar__info-title" name="info-title">О приложении</h3>
          <p class="app-sidebar__info-text" name="info-text">
            Spring Twin — MCP-агент для анализа Spring Boot проектов.
          </p>
        </div>
        <div class="app-sidebar__info-section">
          <h3 class="app-sidebar__info-title" name="info-title">Версия</h3>
          <p class="app-sidebar__info-text" name="info-text">1.0.0</p>
        </div>
      </div>
    </div>

    <button 
      class="app-sidebar__close" 
      name="sidebar-close"
      @click="closeSidebar"
      aria-label="Закрыть меню"
    >
      <span class="app-sidebar__close-icon" name="close-icon">×</span>
    </button>
  </aside>
</template>

<script setup lang="ts">
import NavItem from './NavItem.vue';
import { defaultNavigationItems } from '../api/navigation';

/**
 * Default navigation items for the sidebar.
 */
const navigationItems = defaultNavigationItems;

/**
 * Closes the sidebar (for mobile).
 */
const closeSidebar = () => {
  const sidebar = document.querySelector('.app-sidebar');
  if (sidebar) {
    sidebar.classList.remove('app-sidebar--open');
  }
};
</script>

<style scoped>
.app-sidebar {
  width: 260px;
  background-color: #f8f9fa;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  height: 100%;
  position: fixed;
  left: 0;
  top: 60px;
  bottom: 0;
  z-index: 999;
  transform: translateX(-100%);
  transition: transform 0.3s ease;
}

.app-sidebar--open {
  transform: translateX(0);
}

.app-sidebar__content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.app-sidebar__nav {
  flex: 1;
  overflow-y: auto;
}

.app-sidebar__nav-list {
  margin: 0;
  padding: 0.5rem 0;
  list-style: none;
}

.app-sidebar__info {
  padding: 1.5rem;
  background-color: #e9ecef;
  border-top: 1px solid #dee2e6;
}

.app-sidebar__info-section {
  margin-bottom: 1rem;
}

.app-sidebar__info-section:last-child {
  margin-bottom: 0;
}

.app-sidebar__info-title {
  margin: 0 0 0.5rem 0;
  font-size: 0.85rem;
  font-weight: 600;
  color: #495057;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.app-sidebar__info-text {
  margin: 0;
  font-size: 0.85rem;
  color: #6c757d;
  line-height: 1.4;
}

.app-sidebar__close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.1);
  border: none;
  color: #6c757d;
  font-size: 1.2rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  display: none;
}

.app-sidebar__close:hover {
  background-color: rgba(0, 0, 0, 0.2);
  color: #333;
}

/* Desktop */
@media (min-width: 1024px) {
  .app-sidebar {
    position: static;
    transform: translateX(0);
    border-right: 1px solid #e0e0e0;
  }

  .app-sidebar__close {
    display: none;
  }
}

/* Tablet */
@media (min-width: 768px) and (max-width: 1023px) {
  .app-sidebar {
    width: 240px;
  }
}

/* Mobile */
@media (max-width: 767px) {
  .app-sidebar {
    width: 280px;
    box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  }

  .app-sidebar__close {
    display: flex;
  }
}

/* Custom scrollbar for sidebar */
.app-sidebar__nav::-webkit-scrollbar {
  width: 6px;
}

.app-sidebar__nav::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.app-sidebar__nav::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.app-sidebar__nav::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>