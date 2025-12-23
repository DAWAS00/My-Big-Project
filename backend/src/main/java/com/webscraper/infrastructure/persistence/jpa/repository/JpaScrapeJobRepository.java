package com.webscraper.infrastructure.persistence.jpa.repository;

import com.webscraper.infrastructure.persistence.jpa.entity.ScrapeJobJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaScrapeJobRepository extends JpaRepository<ScrapeJobJpaEntity, UUID> {
    
    Page<ScrapeJobJpaEntity> findByUserId(UUID userId, Pageable pageable);
    
    Page<ScrapeJobJpaEntity> findByUserIdAndStatus(UUID userId, String status, Pageable pageable);
    
    List<ScrapeJobJpaEntity> findByTargetId(UUID targetId);
    
    @Query("SELECT j FROM ScrapeJobJpaEntity j WHERE j.targetId = :targetId AND j.status = 'PENDING'")
    List<ScrapeJobJpaEntity> findPendingByTargetId(UUID targetId);
    
    @Query("SELECT j FROM ScrapeJobJpaEntity j WHERE j.status = 'PENDING' ORDER BY j.scheduledAt ASC NULLS LAST")
    List<ScrapeJobJpaEntity> findPendingJobsOrderedBySchedule();
}
