package com.smartcrm.dto.followup;

import com.smartcrm.dto.auth.UserDto;
import com.smartcrm.entity.FollowUp;
import com.smartcrm.entity.enums.FollowUpStatus;
import java.time.LocalDateTime;

public class FollowUpResponse {
    private Long id;
    private Long leadId;
    private String leadName;
    private String leadCompany;
    private UserDto assignedTo;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private FollowUpStatus status;
    private LocalDateTime completedAt;
    private boolean overdue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FollowUpResponse() {}

    public FollowUpResponse(FollowUp followUp) {
        if (followUp != null) {
            this.id = followUp.getId();
            if (followUp.getLead() != null) {
                this.leadId = followUp.getLead().getId();
                this.leadName = followUp.getLead().getFullName();
                this.leadCompany = followUp.getLead().getCompany();
            }
            if (followUp.getAssignedTo() != null) {
                this.assignedTo = new UserDto(followUp.getAssignedTo());
            }
            this.title = followUp.getTitle();
            this.description = followUp.getDescription();
            this.dueDate = followUp.getDueDate();
            this.status = followUp.getStatus();
            this.completedAt = followUp.getCompletedAt();
            this.overdue = followUp.getStatus() == FollowUpStatus.PENDING &&
                           followUp.getDueDate() != null &&
                           followUp.getDueDate().isBefore(LocalDateTime.now());
            this.createdAt = followUp.getCreatedAt();
            this.updatedAt = followUp.getUpdatedAt();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }

    public String getLeadName() {
        return leadName;
    }

    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }

    public String getLeadCompany() {
        return leadCompany;
    }

    public void setLeadCompany(String leadCompany) {
        this.leadCompany = leadCompany;
    }

    public UserDto getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UserDto assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public FollowUpStatus getStatus() {
        return status;
    }

    public void setStatus(FollowUpStatus status) {
        this.status = status;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
