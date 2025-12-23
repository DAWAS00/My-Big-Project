package com.webscraper.infrastructure.persistence.mapper;

import com.webscraper.domain.entity.ScrapeJob;
import com.webscraper.domain.valueobject.JobStatus;
import com.webscraper.infrastructure.persistence.jpa.entity.ScrapeJobJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ScrapeJobMapper {
    
    public ScrapeJob toDomain(ScrapeJobJpaEntity entity) {
        return new ScrapeJob(
            entity.getId(),
            entity.getTargetId(),
            entity.getUserId(),
            JobStatus.valueOf(entity.getStatus()),
            entity.getConfig() != null ? entity.getConfig() : Map.of(),
            entity.getScheduledAt(),
            entity.getStartedAt(),
            entity.getCompletedAt(),
            entity.getPagesFound() != null ? entity.getPagesFound() : 0,
            entity.getPagesScraped() != null ? entity.getPagesScraped() : 0,
            entity.getErrorMessage(),
            entity.getCreatedAt()
        );
    }
    
    public ScrapeJobJpaEntity toJpa(ScrapeJob job) {
        ScrapeJobJpaEntity entity = new ScrapeJobJpaEntity();
        entity.setId(job.getId());
        entity.setTargetId(job.getTargetId());
        entity.setUserId(job.getUserId());
        entity.setStatus(job.getStatus().name());
        entity.setConfig(job.getConfig());
        entity.setScheduledAt(job.getScheduledAt());
        entity.setStartedAt(job.getStartedAt());
        entity.setCompletedAt(job.getCompletedAt());
        entity.setPagesFound(job.getPagesFound());
        entity.setPagesScraped(job.getPagesScraped());
        entity.setErrorMessage(job.getErrorMessage());
        entity.setCreatedAt(job.getCreatedAt());
        return entity;
    }
}
