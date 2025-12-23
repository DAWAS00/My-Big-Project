package com.webscraper.dto.response;

import com.webscraper.entity.ScrapeJob;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class JobResponse {
    private UUID id;
    private UUID targetId;
    private String targetName;
    private String status;
    private Map<String, Object> config;
    private Instant scheduledAt;
    private Instant startedAt;
    private Instant completedAt;
    private Integer pagesFound;
    private Integer pagesScraped;
    private String errorMessage;
    private Instant createdAt;
    
    public static JobResponse from(ScrapeJob job) {
        return JobResponse.builder()
                .id(job.getId())
                .targetId(job.getTarget().getId())
                .targetName(job.getTarget().getName())
                .status(job.getStatus().name())
                .config(job.getConfig())
                .scheduledAt(job.getScheduledAt())
                .startedAt(job.getStartedAt())
                .completedAt(job.getCompletedAt())
                .pagesFound(job.getPagesFound())
                .pagesScraped(job.getPagesScraped())
                .errorMessage(job.getErrorMessage())
                .createdAt(job.getCreatedAt())
                .build();
    }
}
