package com.smartcrm.dto.lead;

import com.smartcrm.entity.enums.LeadSource;
import com.smartcrm.entity.enums.LeadStatus;
import com.smartcrm.entity.enums.Priority;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class LeadRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Company is required")
    @Size(max = 100, message = "Company cannot exceed 100 characters")
    private String company;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @Size(max = 30, message = "Phone cannot exceed 30 characters")
    private String phone;

    @Size(max = 100, message = "Job title cannot exceed 100 characters")
    private String jobTitle;

    @NotNull(message = "Lead source is required")
    private LeadSource leadSource = LeadSource.WEBSITE;

    @Size(max = 80, message = "Industry cannot exceed 80 characters")
    private String industry;

    @DecimalMin(value = "0.0", inclusive = true, message = "Estimated deal value must be 0 or greater")
    private BigDecimal estimatedDealValue = BigDecimal.ZERO;

    private LeadStatus status = LeadStatus.NEW;

    private Priority priority = Priority.MEDIUM;

    private Long assignedToUserId;

    private String notes;

    public LeadRequest() {}

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
        this.leadSource = leadSource != null ? leadSource : LeadSource.WEBSITE;
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
        this.estimatedDealValue = estimatedDealValue != null ? estimatedDealValue : BigDecimal.ZERO;
    }

    public LeadStatus getStatus() {
        return status;
    }

    public void setStatus(LeadStatus status) {
        this.status = status != null ? status : LeadStatus.NEW;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority != null ? priority : Priority.MEDIUM;
    }

    public Long getAssignedToUserId() {
        return assignedToUserId;
    }

    public void setAssignedToUserId(Long assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
