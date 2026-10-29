package com.smartcrm.service;

import com.smartcrm.dto.followup.FollowUpRequest;
import com.smartcrm.dto.followup.FollowUpResponse;
import com.smartcrm.entity.FollowUp;
import com.smartcrm.entity.Lead;
import com.smartcrm.entity.User;
import com.smartcrm.entity.enums.FollowUpStatus;
import com.smartcrm.exception.ResourceNotFoundException;
import com.smartcrm.repository.FollowUpRepository;
import com.smartcrm.repository.LeadRepository;
import com.smartcrm.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowUpService {

    private final FollowUpRepository followUpRepository;
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;

    public FollowUpService(FollowUpRepository followUpRepository, LeadRepository leadRepository, UserRepository userRepository) {
        this.followUpRepository = followUpRepository;
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
    }

    public List<FollowUpResponse> getFollowUps(User currentUser, String filter, Long leadId) {
        List<FollowUp> list;

        if (leadId != null) {
            list = followUpRepository.findByLeadIdOrderByDueDateAsc(leadId);
        } else {
            list = followUpRepository.findAllWithDetails();
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        return list.stream()
                .filter(f -> {
                    if (filter == null || filter.equalsIgnoreCase("ALL")) return true;
                    if (filter.equalsIgnoreCase("OVERDUE")) {
                        return f.getStatus() == FollowUpStatus.PENDING && f.getDueDate().isBefore(now);
                    }
                    if (filter.equalsIgnoreCase("TODAY")) {
                        return f.getStatus() == FollowUpStatus.PENDING &&
                               !f.getDueDate().isBefore(startOfDay) &&
                               !f.getDueDate().isAfter(endOfDay);
                    }
                    if (filter.equalsIgnoreCase("UPCOMING")) {
                        return f.getStatus() == FollowUpStatus.PENDING && f.getDueDate().isAfter(endOfDay);
                    }
                    if (filter.equalsIgnoreCase("COMPLETED")) {
                        return f.getStatus() == FollowUpStatus.COMPLETED;
                    }
                    return true;
                })
                .map(FollowUpResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public FollowUpResponse createFollowUp(FollowUpRequest request, User currentUser) {
        Lead lead = leadRepository.findById(request.getLeadId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", request.getLeadId()));

        FollowUp followUp = new FollowUp();
        followUp.setLead(lead);
        if (request.getAssignedToUserId() != null) {
            User assigned = userRepository.findById(request.getAssignedToUserId())
                    .orElse(currentUser);
            followUp.setAssignedTo(assigned);
        } else {
            followUp.setAssignedTo(currentUser);
        }
        followUp.setTitle(request.getTitle().trim());
        followUp.setDescription(request.getDescription());
        followUp.setDueDate(request.getDueDate());
        followUp.setStatus(request.getStatus() != null ? request.getStatus() : FollowUpStatus.PENDING);

        followUp = followUpRepository.save(followUp);
        return new FollowUpResponse(followUp);
    }

    @Transactional
    public FollowUpResponse updateFollowUp(Long id, FollowUpRequest request, User currentUser) {
        FollowUp followUp = followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FollowUp", "id", id));

        followUp.setTitle(request.getTitle().trim());
        followUp.setDescription(request.getDescription());
        followUp.setDueDate(request.getDueDate());
        followUp.setStatus(request.getStatus());

        if (request.getAssignedToUserId() != null) {
            User assigned = userRepository.findById(request.getAssignedToUserId())
                    .orElse(currentUser);
            followUp.setAssignedTo(assigned);
        }

        if (followUp.getStatus() == FollowUpStatus.COMPLETED && followUp.getCompletedAt() == null) {
            followUp.setCompletedAt(LocalDateTime.now());
        }

        followUp = followUpRepository.save(followUp);
        return new FollowUpResponse(followUp);
    }

    @Transactional
    public FollowUpResponse completeFollowUp(Long id, User currentUser) {
        FollowUp followUp = followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FollowUp", "id", id));

        followUp.setStatus(FollowUpStatus.COMPLETED);
        followUp.setCompletedAt(LocalDateTime.now());

        followUp = followUpRepository.save(followUp);
        return new FollowUpResponse(followUp);
    }
}
