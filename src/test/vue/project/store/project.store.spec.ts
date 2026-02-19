import { describe, it, expect, vi } from 'vitest';
import { useProjectStore } from '@/project/store/project.store';
import { setActivePinia, createPinia } from 'pinia';

describe('Project Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });

  it('should create store instance correctly', () => {
    const store = useProjectStore();
    expect(store).toBeDefined();
  });

  it('should initialize with correct default values', () => {
    const store = useProjectStore();
    expect(store.projects).toEqual([]);
    expect(store.currentProject).toBe(null);
    expect(store.includePackages).toEqual([]);
    expect(store.excludePackages).toEqual([]);
  });

  it('should add and remove projects', () => {
    const store = useProjectStore();
    const testProject = {
      id: '1',
      name: 'Test Project',
      path: '/test/project'
    };

    store.addProject(testProject);
    expect(store.projects.length).toBe(1);
    expect(store.projects[0].name).toBe(testProject.name);
    expect(store.projects[0].path).toBe(testProject.path);
    expect(store.projects[0].createdAt).toBeDefined();

    store.removeProject(testProject.id);
    expect(store.projects.length).toBe(0);
  });

  it('should set current project', () => {
    const store = useProjectStore();
    const testProject = {
      id: '1',
      name: 'Test Project',
      path: '/test/project',
      createdAt: Date.now()
    };

    store.setCurrentProject(testProject);
    expect(store.currentProject?.id).toBe(testProject.id);
    expect(store.currentProject?.name).toBe(testProject.name);
  });

  it('should update include and exclude packages', () => {
    const store = useProjectStore();
    const includePackages = ['com.example', 'org.test'];
    const excludePackages = ['com.example.test'];

    store.updateIncludePackages(includePackages);
    store.updateExcludePackages(excludePackages);

    expect(store.includePackages).toEqual(includePackages);
    expect(store.excludePackages).toEqual(excludePackages);
  });

  it('should remove current project when project is removed', () => {
    const store = useProjectStore();
    const testProject = {
      id: '1',
      name: 'Test Project',
      path: '/test/project'
    };

    store.addProject(testProject);
    store.setCurrentProject(store.projects[0]);
    expect(store.currentProject?.id).toBe('1');

    store.removeProject('1');
    expect(store.currentProject).toBe(null);
  });
});