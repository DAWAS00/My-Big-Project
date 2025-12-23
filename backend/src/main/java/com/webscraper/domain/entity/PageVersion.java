package com.webscraper.domain.entity;

import com.webscraper.domain.valueobject.ContentHash;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain entity representing a version of a scraped page.
 */
public class PageVersion {
    private final UUID id;
    private final UUID pageId;
    private UUID jobId;
    private final String rawHtml;
    private final ContentHash contentHash;
    private int httpStatus;
    private Integer responseTimeMs;
    private final Instant scrapedAt;

    public static PageVersion create(UUID pageId, UUID jobId, String rawHtml, int httpStatus, Integer responseTimeMs) {
        return new PageVersion(
            UUID.randomUUID(),
            pageId,
            jobId,
            rawHtml,
            ContentHash.of(rawHtml),
            httpStatus,
            responseTimeMs,
            Instant.now()
        );
    }

    public PageVersion(UUID id, UUID pageId, UUID jobId, String rawHtml,
                       ContentHash contentHash, int httpStatus, Integer responseTimeMs, Instant scrapedAt) {
        this.id = id;
        this.pageId = pageId;
        this.jobId = jobId;
        this.rawHtml = rawHtml;
        this.contentHash = contentHash;
        this.httpStatus = httpStatus;
        this.responseTimeMs = responseTimeMs;
        this.scrapedAt = scrapedAt;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getPageId() { return pageId; }
    public UUID getJobId() { return jobId; }
    public String getRawHtml() { return rawHtml; }
    public ContentHash getContentHash() { return contentHash; }
    public int getHttpStatus() { return httpStatus; }
    public Integer getResponseTimeMs() { return responseTimeMs; }
    public Instant getScrapedAt() { return scrapedAt; }

    // Domain behavior
    public boolean hasSameContent(ContentHash other) {
        return this.contentHash.equals(other);
    }
}
