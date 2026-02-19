import { defineStore } from 'pinia';

export interface ArchitectureState {
  selectedClass: string | null;
  selectedMethod: string | null;
}

export const useArchitectureStore = defineStore('architecture', {
  state: (): ArchitectureState => ({
    selectedClass: null,
    selectedMethod: null
  }),
  
  actions: {
    selectClass(className: string | null) {
      this.selectedClass = className;
    },
    
    selectMethod(methodName: string | null) {
      this.selectedMethod = methodName;
    }
  },
  
  getters: {
    selectedClass: (state) => state.selectedClass,
    selectedMethod: (state) => state.selectedMethod
  }
});

export default useArchitectureStore;