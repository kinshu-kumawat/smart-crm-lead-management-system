package com.smartcrm.entity;

import com.smartcrm.entity.enums.LeadSource;
import com.smartcrm.entity.enums.LeadStatus;
import com.smartcrm.entity.enums.Priority;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 100)
    private String company;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "lead_source", nullable = false, length = 40)
    private LeadSource leadSource = LeadSource.WEBSITE;

    @Column(length = 80)
    private String industry;

    @Column(name = "estimated_deal_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal estimatedDealValue = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LeadStatus status = LeadStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority = Priority.MEDIUM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user_id")
    private User assignedTo;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "ai_score")
    private Integer aiScore;

    @Column(name = "ai_score_explanation", columnDefinition = "TEXT")
    private String aiScoreExplanation;

    @Column(name = "ai_recommended_action", columnDefinition = "TEXT")
    private String aiRecommendedAction;

    @Column(name = "ai_last_scored_at")
    private LocalDateTime aiLastScoredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Lead() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.fullName == null || this.fullName.isBlank()) {
            this.fullName = ((this.firstName != null ? this.firstName : "") + " " + (this.lastName != null ? this.lastName : "")).trim();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.firstName != null || this.lastName != null) {
            this.fullName = ((this.firstName != null ? this.firstName : "") + " " + (this.lastName != null ? this.lastName : "")).trim();
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
        this.estimatedDealValue = estimatedDealValue != null ? estimatedDealValue : BigDecimal.ZERO;
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

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
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
