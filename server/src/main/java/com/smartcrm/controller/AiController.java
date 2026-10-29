package com.smartcrm.controller;

import com.smartcrm.dto.ai.*;
import com.smartcrm.dto.common.ApiResponse;
import com.smartcrm.entity.User;
import com.smartcrm.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Engine", description = "AI Lead Scoring, Follow-up generator, Summaries, and Sales Chatbot")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/lead-score")
    @Operation(summary = "Calculate AI lead score, priority recommendation, and action plan")
    public ResponseEntity<ApiResponse<AiScoreResponse>> scoreLead(
            @RequestBody Map<String, Long> payload,
            @AuthenticationPrincipal User currentUser) {
        Long leadId = payload.get("leadId");
        if (leadId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("leadId is required"));
        }
        AiScoreResponse response = aiService.scoreLead(leadId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Lead scored successfully", response));
    }

    @PostMapping("/follow-up-message")
    @Operation(summary = "Generate personalized AI follow-up message draft (Email, WhatsApp, LinkedIn, Meeting)")
    public ResponseEntity<ApiResponse<AiFollowUpResponse>> generateFollowUpMessage(
            @Valid @RequestBody AiFollowUpRequest request,
            @AuthenticationPrincipal User currentUser) {
        AiFollowUpResponse response = aiService.generateFollowUpMessage(request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Follow-up draft generated", response));
    }

    @PostMapping("/lead-summary")
    @Operation(summary = "Generate concise AI executive briefing and next steps for a lead")
    public ResponseEntity<ApiResponse<AiSummaryResponse>> generateLeadSummary(
            @RequestBody Map<String, Long> payload,
            @AuthenticationPrincipal User currentUser) {
        Long leadId = payload.get("leadId");
        if (leadId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("leadId is required"));
        }
        AiSummaryResponse response = aiService.generateLeadSummary(leadId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Lead summarized successfully", response));
    }

    @PostMapping("/chat")
    @Operation(summary = "Chat with AI Sales Assistant about leads, pipeline metrics, and sales strategies")
    public ResponseEntity<ApiResponse<AiChatResponse>> chat(
            @Valid @RequestBody AiChatRequest request,
            @AuthenticationPrincipal User currentUser) {
        AiChatResponse response = aiService.handleChatQuery(request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/status")
    @Operation(summary = "Check AI service configuration and health status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatus() {
        boolean configured = aiService.isGeminiConfigured();
        Map<String, Object> status = Map.of(
                "geminiConfigured", configured,
                "provider", configured ? "Google Gemini" : "Built-in Smart CRM AI Engine",
                "message", configured ? "Gemini AI is active and responding."
                        : "Gemini API key is not configured. Built-in intelligent heuristic engine is active."
        );
        return ResponseEntity.ok(ApiResponse.ok(status));
    }
}
