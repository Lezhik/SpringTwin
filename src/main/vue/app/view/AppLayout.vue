<template>
  <div class="app-layout" name="app-layout">
    <AppHeader />
    
    <div class="app-layout__body">
      <AppSidebar />
      
      <main class="app-layout__content" name="main-content" role="main">
        <div class="app-layout__content-container">
          <router-view />
        </div>
      </main>
    </div>
    
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import AppHeader from './AppHeader.vue';
import AppSidebar from './AppSidebar.vue';
import AppFooter from './AppFooter.vue';
</script>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.app-layout__body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.app-layout__content {
  flex: 1;
  overflow-y: auto;
  background-color: #f5f5f5;
}

.app-layout__content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1.5rem;
  width: 100%;
}

/* Responsive design */
@media (max-width: 1023px) {
  .app-layout__content-container {
    padding: 1.5rem 1rem;
  }
}

@media (max-width: 767px) {
  .app-layout__content-container {
    padding: 1rem;
  }
}

/* Custom scrollbar for content area */
.app-layout__content::-webkit-scrollbar {
  width: 8px;
}

.app-layout__content::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.app-layout__content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.app-layout__content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>