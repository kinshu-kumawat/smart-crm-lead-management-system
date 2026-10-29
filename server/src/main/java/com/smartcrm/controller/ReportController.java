package com.smartcrm.controller;

import com.smartcrm.dto.common.ApiResponse;
import com.smartcrm.service.LeadService;
import com.smartcrm.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "CRM reporting and CSV export")
public class ReportController {

    private final ReportService reportService;
    private final LeadService leadService;

    public ReportController(ReportService reportService, LeadService leadService) {
        this.reportService = reportService;
        this.leadService = leadService;
    }

    @GetMapping("/export")
    @Operation(summary = "Export all leads to CSV file format")
    public void exportLeadsCsv(HttpServletResponse response) throws IOException {
        reportService.exportLeadsCsv(response);
    }
}
