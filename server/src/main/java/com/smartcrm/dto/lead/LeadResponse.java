package com.smartcrm.dto.lead;

import com.smartcrm.dto.auth.UserDto;
import com.smartcrm.entity.Lead;
import com.smartcrm.entity.enums.LeadSource;
import com.smartcrm.entity.enums.LeadStatus;
import com.smartcrm.entity.enums.Priority;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LeadResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String company;
    private String email;
    private String phone;
    private String jobTitle;
    private LeadSource leadSource;
    private String industry;
    private BigDecimal estimatedDealValue;
    private LeadStatus status;
    private Priority priority;
    private UserDto assignedTo;
    private String notes;
    private Integer aiScore;
    private String aiScoreExplanation;
    private String aiRecommendedAction;
    private LocalDateTime aiLastScoredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LeadResponse() {}

    public LeadResponse(Lead lead) {
        if (lead != null) {
            this.id = lead.getId();
            this.firstName = lead.getFirstName();
            this.lastName = lead.getLastName();
            this.fullName = lead.getFullName();
            this.company = lead.getCompany();
            this.email = lead.getEmail();
            this.phone = lead.getPhone();
            this.jobTitle = lead.getJobTitle();
            this.leadSource = lead.getLeadSource();
            this.industry = lead.getIndustry();
            this.estimatedDealValue = lead.getEstimatedDealValue();
            this.status = lead.getStatus();
            this.priority = lead.getPriority();
            if (lead.getAssignedTo() != null) {
                this.assignedTo = new UserDto(lead.getAssignedTo());
            }
            this.notes = lead.getNotes();
            this.aiScore = lead.getAiScore();
            this.aiScoreExplanation = lead.getAiScoreExplanation();
            this.aiRecommendedAction = lead.getAiRecommendedAction();
            this.aiLastScoredAt = lead.getAiLastScoredAt();
            this.createdAt = lead.getCreatedAt();
            this.updatedAt = lead.getUpdatedAt();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public LeadSource getLeadSource() {
        return leadSource;
    }

    public void setLeadSource(LeadSource leadSource) {
        this.leadSource = leadSource;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public BigDecimal getEstimatedDealValue() {
        return estimatedDealValue;
    }

    public void setEstimatedDealValue(BigDecimal estimatedDealValue) {
        this.estimatedDealValue = estimatedDealValue;
    }

    public LeadStatus getStatus() {
        return status;
    }

    public void setStatus(LeadStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public UserDto getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UserDto assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getAiScore() {
        return aiScore;
    }

    public void setAiScore(Integer aiScore) {
        this.aiScore = aiScore;
    }

    public String getAiScoreExplanation() {
        return aiScoreExplanation;
    }

    public void setAiScoreExplanation(String aiScoreExplanation) {
        this.aiScoreExplanation = aiScoreExplanation;
    }

    public String getAiRecommendedAction() {
        return aiRecommendedAction;
    }

    public void setAiRecommendedAction(String aiRecommendedAction) {
        this.aiRecommendedAction = aiRecommendedAction;
    }

    public LocalDateTime getAiLastScoredAt() {
        return aiLastScoredAt;
    }

    public void setAiLastScoredAt(LocalDateTime aiLastScoredAt) {
        this.aiLastScoredAt = aiLastScoredAt;
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
