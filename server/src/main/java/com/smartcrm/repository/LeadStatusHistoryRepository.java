package com.smartcrm.repository;

import com.smartcrm.entity.LeadStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadStatusHistoryRepository extends JpaRepository<LeadStatusHistory, Long> {

    @Query("SELECT h FROM LeadStatusHistory h LEFT JOIN FETCH h.changedBy WHERE h.lead.id = :leadId ORDER BY h.createdAt DESC")
    List<LeadStatusHistory> findByLeadIdOrderByCreatedAtDesc(@Param("leadId") Long leadId);
}
