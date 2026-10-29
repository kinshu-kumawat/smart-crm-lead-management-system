package com.smartcrm.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardAnalyticsResponse {

    public static class MonthlyData {
        private String month;
        private long leads;
        private long converted;
        private BigDecimal revenue;

        public MonthlyData() {}

        public MonthlyData(String month, long leads, long converted, BigDecimal revenue) {
            this.month = month;
            this.leads = leads;
            this.converted = converted;
            this.revenue = revenue;
        }

        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }
        public long getLeads() { return leads; }
        public void setLeads(long leads) { this.leads = leads; }
        public long getConverted() { return converted; }
        public void setConverted(long converted) { this.converted = converted; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
    }

    public static class PipelineStageData {
        private String stage;
        private long count;
        private BigDecimal totalValue;

        public PipelineStageData() {}

        public PipelineStageData(String stage, long count, BigDecimal totalValue) {
            this.stage = stage;
            this.count = count;
            this.totalValue = totalValue;
        }

        public String getStage() { return stage; }
        public void setStage(String stage) { this.stage = stage; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
        public BigDecimal getTotalValue() { return totalValue; }
        public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    }

    public static class SourceData {
        private String source;
        private long count;

        public SourceData() {}

        public SourceData(String source, long count) {
            this.source = source;
            this.count = count;
        }

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }

    private List<MonthlyData> monthlyTrends;
    private List<PipelineStageData> stageFunnel;
    private List<SourceData> sourceDistribution;

    public DashboardAnalyticsResponse() {}

    public List<MonthlyData> getMonthlyTrends() {
        return monthlyTrends;
    }

    public void setMonthlyTrends(List<MonthlyData> monthlyTrends) {
        this.monthlyTrends = monthlyTrends;
    }

    public List<PipelineStageData> getStageFunnel() {
        return stageFunnel;
    }

    public void setStageFunnel(List<PipelineStageData> stageFunnel) {
        this.stageFunnel = stageFunnel;
    }

    public List<SourceData> getSourceDistribution() {
        return sourceDistribution;
    }

    public void setSourceDistribution(List<SourceData> sourceDistribution) {
        this.sourceDistribution = sourceDistribution;
    }
}
