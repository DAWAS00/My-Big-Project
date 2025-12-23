package com.webscraper.web.response;

import com.webscraper.domain.entity.ScrapeJob;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record JobResponse(
    UUID id,
    UUID targetId,
    String status,
    Map<String, Object> config,
    Instant scheduledAt,
    Instant startedAt,
    Instant completedAt,
    int pagesFound,
    int pagesScraped,
    String errorMessage,
    Instant createdAt
) {
    public static JobResponse from(ScrapeJob job) {
        return new JobResponse(
            job.getId(),
            job.getTargetId(),
            job.getStatus().name(),
            job.getConfig(),
            job.getScheduledAt(),
            job.getStartedAt(),
            job.getCompletedAt(),
            job.getPagesFound(),
            job.getPagesScraped(),
            job.getErrorMessage(),
            job.getCreatedAt()
        );
    }
}
