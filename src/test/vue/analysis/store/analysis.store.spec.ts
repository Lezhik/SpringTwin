import { describe, it, expect, vi } from 'vitest';
import { useAnalysisStore } from '@/analysis/store/analysis.store';
import { setActivePinia, createPinia } from 'pinia';

describe('Analysis Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });

  it('should create store instance correctly', () => {
    const store = useAnalysisStore();
    expect(store).toBeDefined();
  });

  it('should initialize with correct default values', () => {
    const store = useAnalysisStore();
    expect(store.isAnalyzing).toBe(false);
    expect(store.analysisProgress).toBe(0);
  });

  it('should start and complete analysis', () => {
    const store = useAnalysisStore();
    store.startAnalysis();
    expect(store.isAnalyzing).toBe(true);
    expect(store.analysisProgress).toBe(0);

    store.completeAnalysis();
    expect(store.isAnalyzing).toBe(false);
    expect(store.analysisProgress).toBe(100);
  });

  it('should set analysis progress', () => {
    const store = useAnalysisStore();
    store.startAnalysis();
    store.setProgress(50);
    expect(store.analysisProgress).toBe(50);

    store.setProgress(75);
    expect(store.analysisProgress).toBe(75);
  });

  it('should cancel analysis', () => {
    const store = useAnalysisStore();
    store.startAnalysis();
    store.setProgress(50);
    
    store.cancelAnalysis();
    expect(store.isAnalyzing).toBe(false);
    expect(store.analysisProgress).toBe(0);
  });

  it('should handle complete analysis from different states', () => {
    const store = useAnalysisStore();
    store.completeAnalysis();
    expect(store.isAnalyzing).toBe(false);
    expect(store.analysisProgress).toBe(100);

    store.startAnalysis();
    store.setProgress(30);
    store.completeAnalysis();
    expect(store.isAnalyzing).toBe(false);
    expect(store.analysisProgress).toBe(100);
  });
});