package com.smartcrm.dto.ai;

import jakarta.validation.constraints.NotNull;

public class AiFollowUpRequest {

    @NotNull(message = "Lead ID is required")
    private Long leadId;

    private String channel = "EMAIL"; // EMAIL, WHATSAPP, LINKEDIN, MEETING
    private String tone = "Professional"; // Professional, Friendly, Urgent, Consultative
    private String objective; // e.g. "Schedule discovery call", "Check in on proposal"
    private String customNotes;

    public AiFollowUpRequest() {}

    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel != null ? channel : "EMAIL";
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone != null ? tone : "Professional";
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getCustomNotes() {
        return customNotes;
    }

    public void setCustomNotes(String customNotes) {
        this.customNotes = customNotes;
    }
}
