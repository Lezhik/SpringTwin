import { describe, it, expect, vi } from 'vitest';
import { useMcpStore } from '@/mcp/store/mcp.store';
import { setActivePinia, createPinia } from 'pinia';

describe('MCP Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });

  it('should create store instance correctly', () => {
    const store = useMcpStore();
    expect(store).toBeDefined();
  });

  it('should initialize with correct default values', () => {
    const store = useMcpStore();
    expect(store.isConnected).toBe(false);
    expect(store.activeTools).toEqual([]);
  });

  it('should connect and disconnect', () => {
    const store = useMcpStore();
    store.connect();
    expect(store.isConnected).toBe(true);

    store.disconnect();
    expect(store.isConnected).toBe(false);
    expect(store.activeTools).toEqual([]);
  });

  it('should add and remove tools', () => {
    const store = useMcpStore();
    const testTool = 'test-tool';

    store.addTool(testTool);
    expect(store.activeTools).toEqual([testTool]);

    store.removeTool(testTool);
    expect(store.activeTools).toEqual([]);
  });

  it('should not add duplicate tools', () => {
    const store = useMcpStore();
    const testTool = 'test-tool';

    store.addTool(testTool);
    store.addTool(testTool);

    expect(store.activeTools).toEqual([testTool]);
  });

  it('should clear active tools on disconnect', () => {
    const store = useMcpStore();
    const testTool = 'test-tool';

    store.connect();
    store.addTool(testTool);
    expect(store.activeTools).toEqual([testTool]);

    store.disconnect();
    expect(store.activeTools).toEqual([]);
  });

  it('should handle removing non-existent tools', () => {
    const store = useMcpStore();
    const testTool = 'test-tool';

    expect(store.activeTools).toEqual([]);
    store.removeTool(testTool);
    expect(store.activeTools).toEqual([]);
  });
});