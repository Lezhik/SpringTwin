import { describe, it, expect, vi } from 'vitest';
import { useReportStore } from '@/report/store/report.store';
import { setActivePinia, createPinia } from 'pinia';

describe('Report Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });

  it('should create store instance correctly', () => {
    const store = useReportStore();
    expect(store).toBeDefined();
  });

  it('should initialize with correct default values', () => {
    const store = useReportStore();
    expect(store.selectedReportType).toBe(null);
    expect(store.reportContent).toBe(null);
  });

  it('should select report type', () => {
    const store = useReportStore();
    const testType = 'endpoint';
    store.selectReportType(testType);
    expect(store.selectedReportType).toBe(testType);
  });

  it('should set report content', () => {
    const store = useReportStore();
    const testContent = '<div>Report content</div>';
    store.setReportContent(testContent);
    expect(store.reportContent).toBe(testContent);
  });

  it('should clear report selection', () => {
    const store = useReportStore();
    const testType = 'class';
    const testContent = '<div>Class report</div>';

    store.selectReportType(testType);
    store.setReportContent(testContent);

    store.selectReportType(null);
    store.setReportContent(null);

    expect(store.selectedReportType).toBe(null);
    expect(store.reportContent).toBe(null);
  });
});