package com.webscraper.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "page_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageVersion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private ScrapeJob job;
    
    @Column(name = "raw_html", nullable = false, columnDefinition = "TEXT")
    private String rawHtml;
    
    @Column(name = "content_hash", nullable = false)
    private String contentHash;
    
    @Column(name = "http_status")
    @Builder.Default
    private Integer httpStatus = 200;
    
    @Column(name = "response_time_ms")
    private Integer responseTimeMs;
    
    @CreationTimestamp
    @Column(name = "scraped_at", nullable = false, updatable = false)
    private Instant scrapedAt;
}
