package com.smartcrm.dto.dashboard;

import com.smartcrm.dto.activity.ActivityResponse;
import com.smartcrm.dto.lead.LeadResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardSummaryResponse {
    private long totalLeads;
    private long newLeads;
    private long qualifiedLeads;
    private long convertedLeads;
    private long lostLeads;
    private double conversionRate;
    private long followUpsDueToday;
    private long overdueFollowUps;
    private BigDecimal totalPipelineValue;
    private BigDecimal convertedRevenue;
    private List<LeadResponse> recentLeads;
    private List<ActivityResponse> recentActivities;
    private Map<String, Long> statusDistribution;
    private Map<String, Long> sourceDistribution;

    public DashboardSummaryResponse() {}

    public long getTotalLeads() {
        return totalLeads;
    }

    public void setTotalLeads(long totalLeads) {
        this.totalLeads = totalLeads;
    }

    public long getNewLeads() {
        return newLeads;
    }

    public void setNewLeads(long newLeads) {
        this.newLeads = newLeads;
    }

    public long getQualifiedLeads() {
        return qualifiedLeads;
    }

    public void setQualifiedLeads(long qualifiedLeads) {
        this.qualifiedLeads = qualifiedLeads;
    }

    public long getConvertedLeads() {
        return convertedLeads;
    }

    public void setConvertedLeads(long convertedLeads) {
        this.convertedLeads = convertedLeads;
    }

    public long getLostLeads() {
        return lostLeads;
    }

    public void setLostLeads(long lostLeads) {
        this.lostLeads = lostLeads;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public long getFollowUpsDueToday() {
        return followUpsDueToday;
    }

    public void setFollowUpsDueToday(long followUpsDueToday) {
        this.followUpsDueToday = followUpsDueToday;
    }

    public long getOverdueFollowUps() {
        return overdueFollowUps;
    }

    public void setOverdueFollowUps(long overdueFollowUps) {
        this.overdueFollowUps = overdueFollowUps;
    }

    public BigDecimal getTotalPipelineValue() {
        return totalPipelineValue;
    }

    public void setTotalPipelineValue(BigDecimal totalPipelineValue) {
        this.totalPipelineValue = totalPipelineValue;
    }

    public BigDecimal getConvertedRevenue() {
        return convertedRevenue;
    }

    public void setConvertedRevenue(BigDecimal convertedRevenue) {
        this.convertedRevenue = convertedRevenue;
    }

    public List<LeadResponse> getRecentLeads() {
        return recentLeads;
    }

    public void setRecentLeads(List<LeadResponse> recentLeads) {
        this.recentLeads = recentLeads;
    }

    public List<ActivityResponse> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<ActivityResponse> recentActivities) {
        this.recentActivities = recentActivities;
    }

    public Map<String, Long> getStatusDistribution() {
        return statusDistribution;
    }

    public void setStatusDistribution(Map<String, Long> statusDistribution) {
        this.statusDistribution = statusDistribution;
    }

    public Map<String, Long> getSourceDistribution() {
        return sourceDistribution;
    }

    public void setSourceDistribution(Map<String, Long> sourceDistribution) {
        this.sourceDistribution = sourceDistribution;
    }
}
