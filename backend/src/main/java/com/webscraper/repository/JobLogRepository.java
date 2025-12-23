package com.webscraper.repository;

import com.webscraper.entity.JobLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JobLogRepository extends JpaRepository<JobLog, UUID> {
    
    Page<JobLog> findByJobIdOrderByCreatedAtDesc(UUID jobId, Pageable pageable);
    
    Page<JobLog> findByJobIdAndLevelOrderByCreatedAtDesc(UUID jobId, JobLog.Level level, Pageable pageable);
}
