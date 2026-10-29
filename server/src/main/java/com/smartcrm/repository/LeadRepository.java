package com.smartcrm.repository;

import com.smartcrm.entity.Lead;
import com.smartcrm.entity.enums.LeadSource;
import com.smartcrm.entity.enums.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long>, JpaSpecificationExecutor<Lead> {

    long countByStatus(LeadStatus status);

    long countByAssignedToId(Long userId);

    List<Lead> findByAssignedToId(Long userId);

    List<Lead> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT l.status, COUNT(l), COALESCE(SUM(l.estimatedDealValue), 0) FROM Lead l GROUP BY l.status")
    List<Object[]> countAndSumByStatus();

    @Query("SELECT l.leadSource, COUNT(l) FROM Lead l GROUP BY l.leadSource")
    List<Object[]> countByLeadSource();

    @Query("SELECT COALESCE(SUM(l.estimatedDealValue), 0) FROM Lead l")
    BigDecimal sumTotalEstimatedDealValue();

    @Query("SELECT COALESCE(SUM(l.estimatedDealValue), 0) FROM Lead l WHERE l.status = 'CONVERTED'")
    BigDecimal sumConvertedDealValue();
}
