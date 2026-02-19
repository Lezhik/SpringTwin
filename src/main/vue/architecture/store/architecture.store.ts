import { defineStore } from 'pinia';

export interface ClassNode {
  id: string;
  name: string;
  fullName: string;
  packageName: string;
  labels: string[];
}

export interface MethodNode {
  id: string;
  name: string;
  signature: string;
  returnType: string;
  modifiers: string[];
  className: string;
}

export interface EndpointNode {
  id: string;
  path: string;
  httpMethod: string;
  produces: string;
  consumes: string;
}

export type Node = ClassNode | MethodNode | EndpointNode;

export interface ArchitectureState {
  classNodes: ClassNode[];
  methodNodes: MethodNode[];
  endpointNodes: EndpointNode[];
  selectedNode: Node | null;
}

export const useArchitectureStore = defineStore('architecture', {
  state: (): ArchitectureState => ({
    classNodes: [],
    methodNodes: [],
    endpointNodes: [],
    selectedNode: null
  }),
  
  actions: {
    setClassNodes(nodes: ClassNode[]) {
      this.classNodes = nodes;
    },
    
    setMethodNodes(nodes: MethodNode[]) {
      this.methodNodes = nodes;
    },
    
    setEndpointNodes(nodes: EndpointNode[]) {
      this.endpointNodes = nodes;
    },
    
    selectNode(node: Node | null) {
      this.selectedNode = node;
    },
    
    clearNodes() {
      this.classNodes = [];
      this.methodNodes = [];
      this.endpointNodes = [];
      this.selectedNode = null;
    }
  },
  
  getters: {
  }
});

export default useArchitectureStore;