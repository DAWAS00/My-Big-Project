package com.webscraper.application.port.out;

import com.webscraper.domain.entity.ScrapeJob;
import com.webscraper.domain.valueobject.JobStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port for job persistence operations.
 */
public interface JobRepository {
    
    ScrapeJob save(ScrapeJob job);
    
    Optional<ScrapeJob> findById(UUID id);
    
    List<ScrapeJob> findByUserId(UUID userId, int page, int size);
    
    List<ScrapeJob> findByUserIdAndStatus(UUID userId, JobStatus status, int page, int size);
    
    List<ScrapeJob> findByTargetId(UUID targetId);
    
    List<ScrapeJob> findPendingByTargetId(UUID targetId);
    
    List<ScrapeJob> findPendingJobsOrderedBySchedule();
}
