import { describe, it, expect, vi } from 'vitest';
import { useAppStore } from '@/app/store/app.store';
import { setActivePinia, createPinia } from 'pinia';

describe('App Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });

  it('should create store instance correctly', () => {
    const store = useAppStore();
    expect(store).toBeDefined();
  });

  it('should initialize with correct default values', () => {
    const store = useAppStore();
    expect(store.isLoading).toBe(false);
    expect(store.currentRoute).toBe('');
    expect(store.notifications).toEqual([]);
    expect(store.theme).toBe('light');
    expect(store.error).toBe(null);
  });

  it('should update loading state', () => {
    const store = useAppStore();
    store.showLoading(true);
    expect(store.isLoading).toBe(true);
    store.showLoading(false);
    expect(store.isLoading).toBe(false);
  });

  it('should update error state', () => {
    const store = useAppStore();
    const testError = 'Test error message';
    store.showError(testError);
    expect(store.error).toBe(testError);
    store.showError(null);
    expect(store.error).toBe(null);
  });

  it('should update current route', () => {
    const store = useAppStore();
    const testRoute = '/test-route';
    store.setCurrentRoute(testRoute);
    expect(store.currentRoute).toBe(testRoute);
  });

  it('should add and remove notifications', () => {
    const store = useAppStore();
    const testNotification = {
      type: 'success' as const,
      message: 'Test notification'
    };

    store.addNotification(testNotification);
    expect(store.notifications.length).toBe(1);
    expect(store.notifications[0].message).toBe(testNotification.message);
    expect(store.notifications[0].type).toBe(testNotification.type);
    expect(store.notifications[0].id).toBeDefined();
    expect(store.notifications[0].timestamp).toBeDefined();

    const notificationId = store.notifications[0].id;
    store.removeNotification(notificationId);
    expect(store.notifications.length).toBe(0);
  });

  it('should toggle theme', () => {
    const store = useAppStore();
    expect(store.theme).toBe('light');
    store.toggleTheme();
    expect(store.theme).toBe('dark');
    store.toggleTheme();
    expect(store.theme).toBe('light');
  });
});