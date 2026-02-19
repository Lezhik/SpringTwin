import { defineStore } from 'pinia';

export interface ReportState {
  selectedReportType: string | null;
  reportContent: string | null;
}

export const useReportStore = defineStore('report', {
  state: (): ReportState => ({
    selectedReportType: null,
    reportContent: null
  }),
  
  actions: {
    selectReportType(type: string | null) {
      this.selectedReportType = type;
    },
    
    setReportContent(content: string | null) {
      this.reportContent = content;
    }
  },
  
  getters: {
  }
});

export default useReportStore;