package com.smartcrm.repository;

import com.smartcrm.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("SELECT a FROM Activity a JOIN FETCH a.lead LEFT JOIN FETCH a.user WHERE a.lead.id = :leadId ORDER BY a.activityTime DESC")
    List<Activity> findByLeadIdOrderByActivityTimeDesc(Long leadId);

    @Query("SELECT a FROM Activity a JOIN FETCH a.lead LEFT JOIN FETCH a.user ORDER BY a.activityTime DESC")
    List<Activity> findTop10RecentActivities();
}
