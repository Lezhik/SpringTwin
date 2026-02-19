import { createApp } from 'vue';
import App from './App.vue';
import pinia from './store';

/**
 * Vue application entry point.
 * Creates and mounts the root Vue instance.
 */
const app = createApp(App);

// Use Pinia store
app.use(pinia);

// Global error handler
app.config.errorHandler = (err, instance, info) => {
  console.error('Vue Error:', err);
  console.error('Component:', instance);
  console.error('Info:', info);
};

// Mount the app
app.mount('#app');