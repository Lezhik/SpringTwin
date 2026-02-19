import { defineStore } from 'pinia';

export interface AppState {
  isLoading: boolean;
  error: string | null;
}

export const useAppStore = defineStore('app', {
  state: (): AppState => ({
    isLoading: false,
    error: null
  }),
  
  actions: {
    showLoading(isLoading: boolean) {
      this.isLoading = isLoading;
    },
    
    showError(error: string | null) {
      this.error = error;
    }
  },
  
  getters: {
    isLoading: (state) => state.isLoading,
    error: (state) => state.error
  }
});

export default useAppStore;