package com.smartcrm.controller;

import com.smartcrm.dto.common.ApiResponse;
import com.smartcrm.dto.followup.FollowUpRequest;
import com.smartcrm.dto.followup.FollowUpResponse;
import com.smartcrm.entity.User;
import com.smartcrm.service.FollowUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow-ups")
@Tag(name = "Follow-ups", description = "Follow-up schedule and task manager")
public class FollowUpController {

    private final FollowUpService followUpService;

    public FollowUpController(FollowUpService followUpService) {
        this.followUpService = followUpService;
    }

    @GetMapping
    @Operation(summary = "Get follow-ups with filter (ALL, OVERDUE, TODAY, UPCOMING, COMPLETED)")
    public ResponseEntity<ApiResponse<List<FollowUpResponse>>> getFollowUps(
            @RequestParam(required = false, defaultValue = "ALL") String filter,
            @RequestParam(required = false) Long leadId,
            @AuthenticationPrincipal User currentUser) {
        List<FollowUpResponse> followUps = followUpService.getFollowUps(currentUser, filter, leadId);
        return ResponseEntity.ok(ApiResponse.ok(followUps));
    }

    @PostMapping
    @Operation(summary = "Schedule a new follow-up")
    public ResponseEntity<ApiResponse<FollowUpResponse>> createFollowUp(
            @Valid @RequestBody FollowUpRequest request,
            @AuthenticationPrincipal User currentUser) {
        FollowUpResponse created = followUpService.createFollowUp(request, currentUser);
        return new ResponseEntity<>(ApiResponse.ok("Follow-up scheduled successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing follow-up")
    public ResponseEntity<ApiResponse<FollowUpResponse>> updateFollowUp(
            @PathVariable Long id,
            @Valid @RequestBody FollowUpRequest request,
            @AuthenticationPrincipal User currentUser) {
        FollowUpResponse updated = followUpService.updateFollowUp(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Follow-up updated successfully", updated));
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark a follow-up as completed")
    public ResponseEntity<ApiResponse<FollowUpResponse>> completeFollowUp(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        FollowUpResponse completed = followUpService.completeFollowUp(id, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Follow-up completed successfully", completed));
    }
}
