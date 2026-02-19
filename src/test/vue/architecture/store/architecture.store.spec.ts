import { describe, it, expect, vi } from 'vitest';
import { useArchitectureStore } from '@/architecture/store/architecture.store';
import { setActivePinia, createPinia } from 'pinia';

describe('Architecture Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });

  it('should create store instance correctly', () => {
    const store = useArchitectureStore();
    expect(store).toBeDefined();
  });

  it('should initialize with correct default values', () => {
    const store = useArchitectureStore();
    expect(store.classNodes).toEqual([]);
    expect(store.methodNodes).toEqual([]);
    expect(store.endpointNodes).toEqual([]);
    expect(store.selectedNode).toBe(null);
  });

  it('should set class nodes', () => {
    const store = useArchitectureStore();
    const testClassNodes = [
      { id: '1', name: 'TestClass', fullName: 'com.example.TestClass', packageName: 'com.example', labels: ['Controller'] },
      { id: '2', name: 'TestService', fullName: 'com.example.TestService', packageName: 'com.example', labels: ['Service'] }
    ];

    store.setClassNodes(testClassNodes);
    expect(store.classNodes).toEqual(testClassNodes);
  });

  it('should set method nodes', () => {
    const store = useArchitectureStore();
    const testMethodNodes = [
      { id: '1', name: 'testMethod', signature: 'void testMethod()', returnType: 'void', modifiers: ['public'], className: 'TestClass' },
      { id: '2', name: 'getAll', signature: 'List<String> getAll()', returnType: 'List<String>', modifiers: ['public'], className: 'TestService' }
    ];

    store.setMethodNodes(testMethodNodes);
    expect(store.methodNodes).toEqual(testMethodNodes);
  });

  it('should set endpoint nodes', () => {
    const store = useArchitectureStore();
    const testEndpointNodes = [
      { id: '1', path: '/api/test', httpMethod: 'GET', produces: 'application/json', consumes: 'application/json' },
      { id: '2', path: '/api/test/{id}', httpMethod: 'POST', produces: 'application/json', consumes: 'application/json' }
    ];

    store.setEndpointNodes(testEndpointNodes);
    expect(store.endpointNodes).toEqual(testEndpointNodes);
  });

  it('should select a node', () => {
    const store = useArchitectureStore();
    const testNode = { id: '1', name: 'TestClass', fullName: 'com.example.TestClass', packageName: 'com.example', labels: ['Controller'] };

    store.selectNode(testNode);
    expect(store.selectedNode?.id).toBe(testNode.id);
  });

  it('should clear all nodes', () => {
    const store = useArchitectureStore();
    const testClassNodes = [
      { id: '1', name: 'TestClass', fullName: 'com.example.TestClass', packageName: 'com.example', labels: ['Controller'] }
    ];

    store.setClassNodes(testClassNodes);
    expect(store.classNodes.length).toBe(1);

    store.clearNodes();
    expect(store.classNodes).toEqual([]);
    expect(store.methodNodes).toEqual([]);
    expect(store.endpointNodes).toEqual([]);
    expect(store.selectedNode).toBe(null);
  });
});