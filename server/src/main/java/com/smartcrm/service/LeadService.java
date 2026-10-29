package com.smartcrm.service;

import com.smartcrm.dto.common.PagedResponse;
import com.smartcrm.dto.lead.LeadRequest;
import com.smartcrm.dto.lead.LeadResponse;
import com.smartcrm.dto.lead.LeadStatusUpdateRequest;
import com.smartcrm.entity.Lead;
import com.smartcrm.entity.LeadStatusHistory;
import com.smartcrm.entity.User;
import com.smartcrm.entity.enums.LeadSource;
import com.smartcrm.entity.enums.LeadStatus;
import com.smartcrm.entity.enums.Priority;
import com.smartcrm.entity.enums.Role;
import com.smartcrm.exception.ResourceNotFoundException;
import com.smartcrm.repository.LeadRepository;
import com.smartcrm.repository.LeadStatusHistoryRepository;
import com.smartcrm.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final LeadStatusHistoryRepository statusHistoryRepository;

    public LeadService(LeadRepository leadRepository, UserRepository userRepository, LeadStatusHistoryRepository statusHistoryRepository) {
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    public PagedResponse<LeadResponse> getLeads(String search, LeadStatus status, Priority priority,
                                                LeadSource source, Long assignedToId,
                                                int page, int size, String sortBy, String sortDir,
                                                User currentUser) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Lead> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search filter
            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern),
                        cb.like(cb.lower(root.get("fullName")), pattern),
                        cb.like(cb.lower(root.get("company")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(root.get("jobTitle")), pattern)
                );
                predicates.add(searchPredicate);
            }

            // Enum filters
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }
            if (source != null) {
                predicates.add(cb.equal(root.get("leadSource"), source));
            }

            // Assigned user filter
            if (assignedToId != null) {
                predicates.add(cb.equal(root.get("assignedTo").get("id"), assignedToId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Lead> leadsPage = leadRepository.findAll(spec, pageable);
        List<LeadResponse> content = leadsPage.getContent().stream()
                .map(LeadResponse::new)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                leadsPage.getNumber(),
                leadsPage.getSize(),
                leadsPage.getTotalElements(),
                leadsPage.getTotalPages(),
                leadsPage.isLast()
        );
    }

    public List<LeadResponse> getAllLeadsList(User currentUser) {
        return leadRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(LeadResponse::new)
                .collect(Collectors.toList());
    }

    public LeadResponse getLeadById(Long id) {
        Lead lead = findLeadEntity(id);
        return new LeadResponse(lead);
    }

    public Lead findLeadEntity(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));
    }

    @Transactional
    public LeadResponse createLead(LeadRequest request, User currentUser) {
        Lead lead = new Lead();
        mapRequestToLead(request, lead);

        // Assign user
        if (request.getAssignedToUserId() != null) {
            User assigned = userRepository.findById(request.getAssignedToUserId())
                    .orElse(currentUser);
            lead.setAssignedTo(assigned);
        } else {
            lead.setAssignedTo(currentUser);
        }

        lead = leadRepository.save(lead);

        // Log initial status
        LeadStatusHistory history = new LeadStatusHistory(
                lead, currentUser, null, lead.getStatus().name(), "Lead created"
        );
        statusHistoryRepository.save(history);

        return new LeadResponse(lead);
    }

    @Transactional
    public LeadResponse updateLead(Long id, LeadRequest request, User currentUser) {
        Lead lead = findLeadEntity(id);
        LeadStatus oldStatus = lead.getStatus();

        mapRequestToLead(request, lead);

        if (request.getAssignedToUserId() != null) {
            User assigned = userRepository.findById(request.getAssignedToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToUserId()));
            lead.setAssignedTo(assigned);
        }

        lead = leadRepository.save(lead);

        // Track status change if status changed in the update
        if (oldStatus != lead.getStatus()) {
            LeadStatusHistory history = new LeadStatusHistory(
                    lead, currentUser, oldStatus.name(), lead.getStatus().name(), "Updated via lead edit"
            );
            statusHistoryRepository.save(history);
        }

        return new LeadResponse(lead);
    }

    @Transactional
    public LeadResponse updateLeadStatus(Long id, LeadStatusUpdateRequest request, User currentUser) {
        Lead lead = findLeadEntity(id);
        LeadStatus oldStatus = lead.getStatus();

        if (oldStatus != request.getStatus()) {
            lead.setStatus(request.getStatus());
            lead = leadRepository.save(lead);

            LeadStatusHistory history = new LeadStatusHistory(
                    lead, currentUser, oldStatus.name(), request.getStatus().name(),
                    request.getNotes() != null ? request.getNotes() : "Status updated"
            );
            statusHistoryRepository.save(history);
        }

        return new LeadResponse(lead);
    }

    @Transactional
    public void deleteLead(Long id, User currentUser) {
        Lead lead = findLeadEntity(id);
        leadRepository.delete(lead);
    }

    public List<LeadStatusHistory> getStatusHistory(Long leadId) {
        findLeadEntity(leadId); // ensure lead exists
        return statusHistoryRepository.findByLeadIdOrderByCreatedAtDesc(leadId);
    }

    @Transactional
    public Lead saveLead(Lead lead) {
        return leadRepository.save(lead);
    }

    private void mapRequestToLead(LeadRequest request, Lead lead) {
        lead.setFirstName(request.getFirstName().trim());
        lead.setLastName(request.getLastName().trim());
        lead.setFullName((request.getFirstName().trim() + " " + request.getLastName().trim()).trim());
        lead.setCompany(request.getCompany().trim());
        lead.setEmail(request.getEmail().toLowerCase().trim());
        lead.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        lead.setJobTitle(request.getJobTitle() != null ? request.getJobTitle().trim() : null);
        lead.setLeadSource(request.getLeadSource());
        lead.setIndustry(request.getIndustry() != null ? request.getIndustry().trim() : null);
        lead.setEstimatedDealValue(request.getEstimatedDealValue());
        lead.setStatus(request.getStatus());
        lead.setPriority(request.getPriority());
        lead.setNotes(request.getNotes());
    }
}
