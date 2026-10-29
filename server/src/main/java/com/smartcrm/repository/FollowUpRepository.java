package com.smartcrm.repository;

import com.smartcrm.entity.FollowUp;
import com.smartcrm.entity.enums.FollowUpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp, Long> {

    @Query("SELECT f FROM FollowUp f JOIN FETCH f.lead LEFT JOIN FETCH f.assignedTo WHERE f.lead.id = :leadId ORDER BY f.dueDate ASC")
    List<FollowUp> findByLeadIdOrderByDueDateAsc(@Param("leadId") Long leadId);

    @Query("SELECT f FROM FollowUp f JOIN FETCH f.lead LEFT JOIN FETCH f.assignedTo ORDER BY f.dueDate ASC")
    List<FollowUp> findAllWithDetails();

    @Query("SELECT f FROM FollowUp f JOIN FETCH f.lead LEFT JOIN FETCH f.assignedTo WHERE f.assignedTo.id = :userId ORDER BY f.dueDate ASC")
    List<FollowUp> findByAssignedToIdWithDetails(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM FollowUp f WHERE f.status = :status AND f.dueDate < :now")
    long countOverdue(@Param("status") FollowUpStatus status, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(f) FROM FollowUp f WHERE f.status = :status AND f.dueDate >= :startOfDay AND f.dueDate <= :endOfDay")
    long countDueToday(@Param("status") FollowUpStatus status, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
