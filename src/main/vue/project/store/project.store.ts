import { defineStore } from 'pinia';

export interface ProjectState {
  projectName: string;
  includePackages: string[];
  excludePackages: string[];
}

export const useProjectStore = defineStore('project', {
  state: (): ProjectState => ({
    projectName: '',
    includePackages: [],
    excludePackages: []
  }),
  
  actions: {
    updateProjectName(name: string) {
      this.projectName = name;
    },
    
    updateIncludePackages(packages: string[]) {
      this.includePackages = packages;
    },
    
    updateExcludePackages(packages: string[]) {
      this.excludePackages = packages;
    }
  },
  
  getters: {
    projectName: (state) => state.projectName,
    includePackages: (state) => state.includePackages,
    excludePackages: (state) => state.excludePackages
  }
});

export default useProjectStore;