package com.smartcrm.controller;

import com.smartcrm.dto.common.ApiResponse;
import com.smartcrm.dto.dashboard.DashboardAnalyticsResponse;
import com.smartcrm.dto.dashboard.DashboardSummaryResponse;
import com.smartcrm.entity.User;
import com.smartcrm.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Executive KPI summary and analytics charts data")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get high-level CRM KPI stats and recent activity")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary(@AuthenticationPrincipal User currentUser) {
        DashboardSummaryResponse summary = dashboardService.getSummary(currentUser);
        return ResponseEntity.ok(ApiResponse.ok(summary));
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get chart analytics data (monthly generation, pipeline stages, lead sources)")
    public ResponseEntity<ApiResponse<DashboardAnalyticsResponse>> getAnalytics(@AuthenticationPrincipal User currentUser) {
        DashboardAnalyticsResponse analytics = dashboardService.getAnalytics(currentUser);
        return ResponseEntity.ok(ApiResponse.ok(analytics));
    }
}
