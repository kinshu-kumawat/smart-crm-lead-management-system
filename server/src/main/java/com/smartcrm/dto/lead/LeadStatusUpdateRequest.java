package com.smartcrm.dto.lead;

import com.smartcrm.entity.enums.LeadStatus;
import jakarta.validation.constraints.NotNull;

public class LeadStatusUpdateRequest {

    @NotNull(message = "New status is required")
    private LeadStatus status;

    private String notes;

    public LeadStatusUpdateRequest() {}

    public LeadStatusUpdateRequest(LeadStatus status, String notes) {
        this.status = status;
        this.notes = notes;
    }

    public LeadStatus getStatus() {
        return status;
    }

    public void setStatus(LeadStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
