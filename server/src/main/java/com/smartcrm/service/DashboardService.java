package com.smartcrm.service;

import com.smartcrm.dto.activity.ActivityResponse;
import com.smartcrm.dto.dashboard.DashboardAnalyticsResponse;
import com.smartcrm.dto.dashboard.DashboardSummaryResponse;
import com.smartcrm.dto.lead.LeadResponse;
import com.smartcrm.entity.Lead;
import com.smartcrm.entity.User;
import com.smartcrm.entity.enums.FollowUpStatus;
import com.smartcrm.entity.enums.LeadStatus;
import com.smartcrm.repository.ActivityRepository;
import com.smartcrm.repository.FollowUpRepository;
import com.smartcrm.repository.LeadRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final LeadRepository leadRepository;
    private final ActivityRepository activityRepository;
    private final FollowUpRepository followUpRepository;

    public DashboardService(LeadRepository leadRepository, ActivityRepository activityRepository, FollowUpRepository followUpRepository) {
        this.leadRepository = leadRepository;
        this.activityRepository = activityRepository;
        this.followUpRepository = followUpRepository;
    }

    public DashboardSummaryResponse getSummary(User currentUser) {
        DashboardSummaryResponse summary = new DashboardSummaryResponse();

        long total = leadRepository.count();
        long newCount = leadRepository.countByStatus(LeadStatus.NEW);
        long qualifiedCount = leadRepository.countByStatus(LeadStatus.QUALIFIED);
        long convertedCount = leadRepository.countByStatus(LeadStatus.CONVERTED);
        long lostCount = leadRepository.countByStatus(LeadStatus.LOST);

        summary.setTotalLeads(total);
        summary.setNewLeads(newCount);
        summary.setQualifiedLeads(qualifiedCount);
        summary.setConvertedLeads(convertedCount);
        summary.setLostLeads(lostCount);

        double convRate = total > 0 ? (double) convertedCount / total * 100.0 : 0.0;
        summary.setConversionRate(Math.round(convRate * 10.0) / 10.0);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        summary.setOverdueFollowUps(followUpRepository.countOverdue(FollowUpStatus.PENDING, now));
        summary.setFollowUpsDueToday(followUpRepository.countDueToday(FollowUpStatus.PENDING, startOfDay, endOfDay));

        BigDecimal pipelineTotal = leadRepository.sumTotalEstimatedDealValue();
        BigDecimal convertedTotal = leadRepository.sumConvertedDealValue();
        summary.setTotalPipelineValue(pipelineTotal != null ? pipelineTotal : BigDecimal.ZERO);
        summary.setConvertedRevenue(convertedTotal != null ? convertedTotal : BigDecimal.ZERO);

        // Recent leads
        List<LeadResponse> recentLeads = leadRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(LeadResponse::new)
                .collect(Collectors.toList());
        summary.setRecentLeads(recentLeads);

        // Recent activities
        List<ActivityResponse> recentActivities = activityRepository.findTop10RecentActivities()
                .stream()
                .map(ActivityResponse::new)
                .collect(Collectors.toList());
        summary.setRecentActivities(recentActivities);

        // Status distribution map
        Map<String, Long> statusDist = new LinkedHashMap<>();
        for (LeadStatus s : LeadStatus.values()) {
            statusDist.put(s.name(), leadRepository.countByStatus(s));
        }
        summary.setStatusDistribution(statusDist);

        // Source distribution map
        Map<String, Long> sourceDist = new LinkedHashMap<>();
        List<Object[]> sourceResults = leadRepository.countByLeadSource();
        for (Object[] row : sourceResults) {
            sourceDist.put(row[0].toString(), (Long) row[1]);
        }
        summary.setSourceDistribution(sourceDist);

        return summary;
    }

    public DashboardAnalyticsResponse getAnalytics(User currentUser) {
        DashboardAnalyticsResponse analytics = new DashboardAnalyticsResponse();

        // 1. Pipeline stage funnel
        List<DashboardAnalyticsResponse.PipelineStageData> stages = new ArrayList<>();
        List<Object[]> statusAgg = leadRepository.countAndSumByStatus();
        Map<String, Object[]> statusMap = statusAgg.stream()
                .collect(Collectors.toMap(r -> r[0].toString(), r -> r));

        for (LeadStatus ls : LeadStatus.values()) {
            Object[] row = statusMap.get(ls.name());
            long count = row != null ? ((Number) row[1]).longValue() : 0L;
            BigDecimal val = row != null ? (BigDecimal) row[2] : BigDecimal.ZERO;
            stages.add(new DashboardAnalyticsResponse.PipelineStageData(ls.name(), count, val));
        }
        analytics.setStageFunnel(stages);

        // 2. Source performance
        List<DashboardAnalyticsResponse.SourceData> sources = new ArrayList<>();
        List<Object[]> sourceResults = leadRepository.countByLeadSource();
        for (Object[] row : sourceResults) {
            sources.add(new DashboardAnalyticsResponse.SourceData(row[0].toString(), ((Number) row[1]).longValue()));
        }
        analytics.setSourceDistribution(sources);

        // 3. Monthly trends (computed over all leads)
        List<Lead> allLeads = leadRepository.findAll();
        Map<String, List<Lead>> groupedByMonth = allLeads.stream()
                .collect(Collectors.groupingBy(l -> l.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM yyyy"))));

        List<DashboardAnalyticsResponse.MonthlyData> monthlyList = new ArrayList<>();
        // Current and past 5 months
        for (int i = 5; i >= 0; i--) {
            LocalDate m = LocalDate.now().minusMonths(i);
            String monthKey = m.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            List<Lead> leadsInMonth = groupedByMonth.getOrDefault(monthKey, Collections.emptyList());

            long totalInMonth = leadsInMonth.size();
            long convertedInMonth = leadsInMonth.stream().filter(l -> l.getStatus() == LeadStatus.CONVERTED).count();
            BigDecimal revInMonth = leadsInMonth.stream()
                    .filter(l -> l.getStatus() == LeadStatus.CONVERTED)
                    .map(Lead::getEstimatedDealValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            monthlyList.add(new DashboardAnalyticsResponse.MonthlyData(monthKey, totalInMonth, convertedInMonth, revInMonth));
        }
        analytics.setMonthlyTrends(monthlyList);

        return analytics;
    }
}
