import { defineStore } from 'pinia';

export interface McpState {
  isConnected: boolean;
  activeTools: string[];
}

export const useMcpStore = defineStore('mcp', {
  state: (): McpState => ({
    isConnected: false,
    activeTools: []
  }),
  
  actions: {
    connect() {
      this.isConnected = true;
    },
    
    disconnect() {
      this.isConnected = false;
      this.activeTools = [];
    },
    
    addTool(tool: string) {
      if (!this.activeTools.includes(tool)) {
        this.activeTools.push(tool);
      }
    },
    
    removeTool(tool: string) {
      this.activeTools = this.activeTools.filter(t => t !== tool);
    }
  },
  
  getters: {
  }
});

export default useMcpStore;