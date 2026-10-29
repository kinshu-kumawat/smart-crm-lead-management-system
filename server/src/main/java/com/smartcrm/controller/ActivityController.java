package com.smartcrm.controller;

import com.smartcrm.dto.activity.ActivityRequest;
import com.smartcrm.dto.activity.ActivityResponse;
import com.smartcrm.dto.common.ApiResponse;
import com.smartcrm.entity.User;
import com.smartcrm.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Activities", description = "Calls, emails, meetings, and notes tracking")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/leads/{id}/activities")
    @Operation(summary = "Get all activities for a lead")
    public ResponseEntity<ApiResponse<List<ActivityResponse>>> getActivitiesByLead(@PathVariable Long id) {
        List<ActivityResponse> activities = activityService.getActivitiesByLead(id);
        return ResponseEntity.ok(ApiResponse.ok(activities));
    }

    @PostMapping("/leads/{id}/activities")
    @Operation(summary = "Log an activity for a lead")
    public ResponseEntity<ApiResponse<ActivityResponse>> createActivity(
            @PathVariable Long id,
            @Valid @RequestBody ActivityRequest request,
            @AuthenticationPrincipal User currentUser) {
        ActivityResponse created = activityService.createActivity(id, request, currentUser);
        return new ResponseEntity<>(ApiResponse.ok("Activity logged successfully", created), HttpStatus.CREATED);
    }

    @GetMapping("/activities/recent")
    @Operation(summary = "Get recent CRM activities")
    public ResponseEntity<ApiResponse<List<ActivityResponse>>> getRecentActivities() {
        List<ActivityResponse> activities = activityService.getRecentActivities();
        return ResponseEntity.ok(ApiResponse.ok(activities));
    }
}
