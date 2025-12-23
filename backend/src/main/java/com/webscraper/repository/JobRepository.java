package com.webscraper.repository;

import com.webscraper.entity.ScrapeJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<ScrapeJob, UUID> {
    
    Page<ScrapeJob> findByTargetId(UUID targetId, Pageable pageable);
    
    Page<ScrapeJob> findByUserId(UUID userId, Pageable pageable);
    
    Page<ScrapeJob> findByUserIdAndStatus(UUID userId, ScrapeJob.Status status, Pageable pageable);
    
    List<ScrapeJob> findByStatusOrderByScheduledAtAsc(ScrapeJob.Status status);
    
    @Query("SELECT j FROM ScrapeJob j WHERE j.target.id = :targetId AND j.status = 'PENDING'")
    List<ScrapeJob> findPendingJobsByTarget(UUID targetId);
    
    @Query("""
        SELECT j.status AS status, COUNT(j) AS count 
        FROM ScrapeJob j 
        WHERE j.user.id = :userId 
        GROUP BY j.status
        """)
    List<Object[]> getJobStatsByUser(UUID userId);
}
