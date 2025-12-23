package com.webscraper.domain.entity;

import com.webscraper.domain.valueobject.UrlHash;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain entity representing a discovered page.
 */
public class Page {
    private final UUID id;
    private final UUID targetId;
    private UUID discoveredByJobId;
    private final String url;
    private final UrlHash urlHash;
    private Instant lastScrapedAt;
    private int scrapeCount;
    private final Instant createdAt;

    public static Page create(UUID targetId, UUID jobId, String url) {
        return new Page(
            UUID.randomUUID(),
            targetId,
            jobId,
            url,
            UrlHash.of(url),
            null,
            0,
            Instant.now()
        );
    }

    public Page(UUID id, UUID targetId, UUID discoveredByJobId, String url,
                UrlHash urlHash, Instant lastScrapedAt, int scrapeCount, Instant createdAt) {
        this.id = id;
        this.targetId = targetId;
        this.discoveredByJobId = discoveredByJobId;
        this.url = url;
        this.urlHash = urlHash;
        this.lastScrapedAt = lastScrapedAt;
        this.scrapeCount = scrapeCount;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getTargetId() { return targetId; }
    public UUID getDiscoveredByJobId() { return discoveredByJobId; }
    public String getUrl() { return url; }
    public UrlHash getUrlHash() { return urlHash; }
    public Instant getLastScrapedAt() { return lastScrapedAt; }
    public int getScrapeCount() { return scrapeCount; }
    public Instant getCreatedAt() { return createdAt; }

    // Domain behavior
    public void markScraped() {
        this.lastScrapedAt = Instant.now();
        this.scrapeCount++;
    }
}
