package com.webscraper.domain.entity;

import com.webscraper.domain.valueobject.JobStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Domain entity representing a scraping job.
 */
public class ScrapeJob {
    private final UUID id;
    private final UUID targetId;
    private final UUID userId;
    private JobStatus status;
    private Map<String, Object> config;
    private Instant scheduledAt;
    private Instant startedAt;
    private Instant completedAt;
    private int pagesFound;
    private int pagesScraped;
    private String errorMessage;
    private final Instant createdAt;

    public static ScrapeJob create(UUID targetId, UUID userId, Map<String, Object> config, Instant scheduledAt) {
        return new ScrapeJob(
            UUID.randomUUID(),
            targetId,
            userId,
            JobStatus.PENDING,
            config != null ? config : Map.of(),
            scheduledAt,
            null, null,
            0, 0, null,
            Instant.now()
        );
    }

    public ScrapeJob(UUID id, UUID targetId, UUID userId, JobStatus status,
                     Map<String, Object> config, Instant scheduledAt,
                     Instant startedAt, Instant completedAt,
                     int pagesFound, int pagesScraped, String errorMessage,
                     Instant createdAt) {
        this.id = id;
        this.targetId = targetId;
        this.userId = userId;
        this.status = status;
        this.config = config;
        this.scheduledAt = scheduledAt;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.pagesFound = pagesFound;
        this.pagesScraped = pagesScraped;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getTargetId() { return targetId; }
    public UUID getUserId() { return userId; }
    public JobStatus getStatus() { return status; }
    public Map<String, Object> getConfig() { return config; }
    public Instant getScheduledAt() { return scheduledAt; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public int getPagesFound() { return pagesFound; }
    public int getPagesScraped() { return pagesScraped; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getCreatedAt() { return createdAt; }

    // Domain behavior
    public void start() {
        if (status != JobStatus.PENDING) {
            throw new IllegalStateException("Can only start PENDING jobs");
        }
        this.status = JobStatus.RUNNING;
        this.startedAt = Instant.now();
    }

    public void complete(int pagesFound, int pagesScraped) {
        this.status = JobStatus.COMPLETED;
        this.pagesFound = pagesFound;
        this.pagesScraped = pagesScraped;
        this.completedAt = Instant.now();
    }

    public void fail(String errorMessage) {
        this.status = JobStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = Instant.now();
    }

    public void cancel() {
        if (status != JobStatus.PENDING && status != JobStatus.RUNNING) {
            throw new IllegalStateException("Can only cancel PENDING or RUNNING jobs");
        }
        this.status = JobStatus.CANCELLED;
        this.completedAt = Instant.now();
    }

    public boolean isCancellable() {
        return status == JobStatus.PENDING || status == JobStatus.RUNNING;
    }

    public boolean isOwnedBy(UUID userId) {
        return this.userId.equals(userId);
    }
}
