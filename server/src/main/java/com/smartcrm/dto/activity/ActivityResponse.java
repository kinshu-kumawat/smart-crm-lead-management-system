package com.smartcrm.dto.activity;

import com.smartcrm.dto.auth.UserDto;
import com.smartcrm.entity.Activity;
import com.smartcrm.entity.enums.ActivityType;
import java.time.LocalDateTime;

public class ActivityResponse {
    private Long id;
    private Long leadId;
    private String leadName;
    private UserDto user;
    private ActivityType type;
    private String title;
    private String description;
    private LocalDateTime activityTime;
    private LocalDateTime createdAt;

    public ActivityResponse() {}

    public ActivityResponse(Activity activity) {
        if (activity != null) {
            this.id = activity.getId();
            if (activity.getLead() != null) {
                this.leadId = activity.getLead().getId();
                this.leadName = activity.getLead().getFullName();
            }
            if (activity.getUser() != null) {
                this.user = new UserDto(activity.getUser());
            }
            this.type = activity.getType();
            this.title = activity.getTitle();
            this.description = activity.getDescription();
            this.activityTime = activity.getActivityTime();
            this.createdAt = activity.getCreatedAt();
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

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
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

    public LocalDateTime getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(LocalDateTime activityTime) {
        this.activityTime = activityTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
