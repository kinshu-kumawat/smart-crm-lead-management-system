package com.smartcrm.controller;

import com.smartcrm.dto.common.ApiResponse;
import com.smartcrm.dto.common.PagedResponse;
import com.smartcrm.dto.lead.LeadRequest;
import com.smartcrm.dto.lead.LeadResponse;
import com.smartcrm.dto.lead.LeadStatusUpdateRequest;
import com.smartcrm.entity.LeadStatusHistory;
import com.smartcrm.entity.User;
import com.smartcrm.entity.enums.LeadSource;
import com.smartcrm.entity.enums.LeadStatus;
import com.smartcrm.entity.enums.Priority;
import com.smartcrm.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
@Tag(name = "Leads", description = "CRUD and pipeline operations for Leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    @Operation(summary = "Get leads with pagination, search, and dynamic filtering")
    public ResponseEntity<ApiResponse<PagedResponse<LeadResponse>>> getLeads(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LeadStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) LeadSource source,
            @RequestParam(required = false) Long assignedToUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal User currentUser) {

        PagedResponse<LeadResponse> pagedLeads = leadService.getLeads(
                search, status, priority, source, assignedToUserId, page, size, sortBy, sortDir, currentUser
        );
        return ResponseEntity.ok(ApiResponse.ok(pagedLeads));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all leads without pagination (for Kanban pipeline board)")
    public ResponseEntity<ApiResponse<List<LeadResponse>>> getAllLeads(@AuthenticationPrincipal User currentUser) {
        List<LeadResponse> leads = leadService.getAllLeadsList(currentUser);
        return ResponseEntity.ok(ApiResponse.ok(leads));
    }

    @PostMapping
    @Operation(summary = "Create a new lead")
    public ResponseEntity<ApiResponse<LeadResponse>> createLead(
            @Valid @RequestBody LeadRequest request,
            @AuthenticationPrincipal User currentUser) {
        LeadResponse created = leadService.createLead(request, currentUser);
        return new ResponseEntity<>(ApiResponse.ok("Lead created successfully", created), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lead by ID")
    public ResponseEntity<ApiResponse<LeadResponse>> getLeadById(@PathVariable Long id) {
        LeadResponse lead = leadService.getLeadById(id);
        return ResponseEntity.ok(ApiResponse.ok(lead));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing lead")
    public ResponseEntity<ApiResponse<LeadResponse>> updateLead(
            @PathVariable Long id,
            @Valid @RequestBody LeadRequest request,
            @AuthenticationPrincipal User currentUser) {
        LeadResponse updated = leadService.updateLead(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Lead updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update lead pipeline status")
    public ResponseEntity<ApiResponse<LeadResponse>> updateLeadStatus(
            @PathVariable Long id,
            @Valid @RequestBody LeadStatusUpdateRequest request,
            @AuthenticationPrincipal User currentUser) {
        LeadResponse updated = leadService.updateLeadStatus(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Lead status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete lead by ID")
    public ResponseEntity<ApiResponse<Void>> deleteLead(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        leadService.deleteLead(id, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Lead deleted successfully", null));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get lead lifecycle status change history")
    public ResponseEntity<ApiResponse<List<LeadStatusHistory>>> getLeadStatusHistory(@PathVariable Long id) {
        List<LeadStatusHistory> history = leadService.getStatusHistory(id);
        return ResponseEntity.ok(ApiResponse.ok(history));
    }
}
