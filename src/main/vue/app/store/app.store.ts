import { defineStore } from 'pinia';

export interface Notification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  message: string;
  timestamp: number;
}

export interface AppState {
  isLoading: boolean;
  currentRoute: string;
  notifications: Notification[];
  theme: 'light' | 'dark';
  error: string | null;
}

export const useAppStore = defineStore('app', {
  state: (): AppState => ({
    isLoading: false,
    currentRoute: '',
    notifications: [],
    theme: 'light',
    error: null
  }),
  
  actions: {
    showLoading(isLoading: boolean) {
      this.isLoading = isLoading;
    },
    
    showError(error: string | null) {
      this.error = error;
    },
    
    setCurrentRoute(route: string) {
      this.currentRoute = route;
    },
    
    addNotification(notification: Omit<Notification, 'id' | 'timestamp'>) {
      const newNotification: Notification = {
        ...notification,
        id: Date.now().toString(),
        timestamp: Date.now()
      };
      this.notifications.push(newNotification);
    },
    
    removeNotification(id: string) {
      this.notifications = this.notifications.filter(n => n.id !== id);
    },
    
    toggleTheme() {
      this.theme = this.theme === 'light' ? 'dark' : 'light';
    }
  },
  
  getters: {
  }
});

export default useAppStore;