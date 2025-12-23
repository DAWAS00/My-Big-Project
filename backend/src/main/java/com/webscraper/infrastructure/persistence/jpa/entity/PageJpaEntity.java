package com.webscraper.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pages")
public class PageJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "target_id", nullable = false)
    private UUID targetId;
    
    @Column(name = "discovered_by_job_id")
    private UUID discoveredByJobId;
    
    @Column(nullable = false)
    private String url;
    
    @Column(name = "url_hash", nullable = false, unique = true)
    private String urlHash;
    
    @Column(name = "last_scraped_at")
    private Instant lastScrapedAt;
    
    @Column(name = "scrape_count")
    private Integer scrapeCount = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }
    
    public UUID getDiscoveredByJobId() { return discoveredByJobId; }
    public void setDiscoveredByJobId(UUID discoveredByJobId) { this.discoveredByJobId = discoveredByJobId; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getUrlHash() { return urlHash; }
    public void setUrlHash(String urlHash) { this.urlHash = urlHash; }
    
    public Instant getLastScrapedAt() { return lastScrapedAt; }
    public void setLastScrapedAt(Instant lastScrapedAt) { this.lastScrapedAt = lastScrapedAt; }
    
    public Integer getScrapeCount() { return scrapeCount; }
    public void setScrapeCount(Integer scrapeCount) { this.scrapeCount = scrapeCount; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
