package com.webscraper.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Page {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private Target target;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discovered_by_job_id")
    private ScrapeJob discoveredByJob;
    
    @Column(nullable = false)
    private String url;
    
    @Column(name = "url_hash", nullable = false, unique = true)
    private String urlHash;
    
    @Column(name = "last_scraped_at")
    private Instant lastScrapedAt;
    
    @Column(name = "scrape_count")
    @Builder.Default
    private Integer scrapeCount = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
