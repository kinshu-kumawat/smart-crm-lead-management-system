package com.smartcrm.dto.ai;

import jakarta.validation.constraints.NotBlank;

public class AiChatRequest {

    @NotBlank(message = "Message is required")
    private String message;

    private Long leadId; // Optional context for lead-specific queries

    public AiChatRequest() {}

    public AiChatRequest(String message, Long leadId) {
        this.message = message;
        this.leadId = leadId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }
}
