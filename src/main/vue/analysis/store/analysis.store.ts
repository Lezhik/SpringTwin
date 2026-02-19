import { defineStore } from 'pinia';

export interface AnalysisState {
  isAnalyzing: boolean;
  analysisProgress: number;
}

export const useAnalysisStore = defineStore('analysis', {
  state: (): AnalysisState => ({
    isAnalyzing: false,
    analysisProgress: 0
  }),
  
  actions: {
    startAnalysis() {
      this.isAnalyzing = true;
      this.analysisProgress = 0;
    },
    
    setProgress(progress: number) {
      this.analysisProgress = progress;
    },
    
    completeAnalysis() {
      this.isAnalyzing = false;
      this.analysisProgress = 100;
    },
    
    cancelAnalysis() {
      this.isAnalyzing = false;
      this.analysisProgress = 0;
    }
  },
  
  getters: {
  }
});

export default useAnalysisStore;