import { defineStore } from 'pinia';

export interface Project {
  id: string;
  name: string;
  path: string;
  createdAt: number;
}

export interface ProjectState {
  projects: Project[];
  currentProject: Project | null;
  includePackages: string[];
  excludePackages: string[];
}

export const useProjectStore = defineStore('project', {
  state: (): ProjectState => ({
    projects: [],
    currentProject: null,
    includePackages: [],
    excludePackages: []
  }),
  
  actions: {
    addProject(project: Omit<Project, 'createdAt'>) {
      const newProject: Project = {
        ...project,
        createdAt: Date.now()
      };
      this.projects.push(newProject);
    },
    
    removeProject(id: string) {
      this.projects = this.projects.filter(p => p.id !== id);
      if (this.currentProject?.id === id) {
        this.currentProject = null;
      }
    },
    
    setCurrentProject(project: Project | null) {
      this.currentProject = project;
    },
    
    updateIncludePackages(packages: string[]) {
      this.includePackages = packages;
    },
    
    updateExcludePackages(packages: string[]) {
      this.excludePackages = packages;
    }
  },
  
  getters: {
  }
});

export default useProjectStore;