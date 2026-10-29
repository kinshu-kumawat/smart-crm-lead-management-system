package com.smartcrm.controller;

import com.smartcrm.dto.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "System health check endpoint")
public class HealthController {

    @GetMapping
    @Operation(summary = "Check application health and status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkHealth() {
        Map<String, Object> details = Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString(),
                "application", "Smart CRM & Lead Management System",
                "version", "1.0.0"
        );
        return ResponseEntity.ok(ApiResponse.ok("System healthy", details));
    }
}
