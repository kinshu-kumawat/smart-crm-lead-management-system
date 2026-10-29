package com.smartcrm.service;

import com.smartcrm.dto.activity.ActivityRequest;
import com.smartcrm.dto.activity.ActivityResponse;
import com.smartcrm.entity.Activity;
import com.smartcrm.entity.Lead;
import com.smartcrm.entity.User;
import com.smartcrm.exception.ResourceNotFoundException;
import com.smartcrm.repository.ActivityRepository;
import com.smartcrm.repository.LeadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final LeadRepository leadRepository;

    public ActivityService(ActivityRepository activityRepository, LeadRepository leadRepository) {
        this.activityRepository = activityRepository;
        this.leadRepository = leadRepository;
    }

    public List<ActivityResponse> getActivitiesByLead(Long leadId) {
        if (!leadRepository.existsById(leadId)) {
            throw new ResourceNotFoundException("Lead", "id", leadId);
        }
        return activityRepository.findByLeadIdOrderByActivityTimeDesc(leadId)
                .stream()
                .map(ActivityResponse::new)
                .collect(Collectors.toList());
    }

    public List<ActivityResponse> getRecentActivities() {
        return activityRepository.findTop10RecentActivities()
                .stream()
                .map(ActivityResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ActivityResponse createActivity(Long leadId, ActivityRequest request, User currentUser) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        Activity activity = new Activity();
        activity.setLead(lead);
        activity.setUser(currentUser);
        activity.setType(request.getType());
        activity.setTitle(request.getTitle().trim());
        activity.setDescription(request.getDescription());
        activity.setActivityTime(request.getActivityTime() != null ? request.getActivityTime() : LocalDateTime.now());

        activity = activityRepository.save(activity);
        return new ActivityResponse(activity);
    }
}
