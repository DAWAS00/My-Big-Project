package com.webscraper.dto.response;

import com.webscraper.entity.Page;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PageResponse {
    private UUID id;
    private String url;
    private Instant lastScrapedAt;
    private Integer scrapeCount;
    private Integer versionCount;
    private Instant createdAt;
    
    public static PageResponse from(Page page, int versionCount) {
        return PageResponse.builder()
                .id(page.getId())
                .url(page.getUrl())
                .lastScrapedAt(page.getLastScrapedAt())
                .scrapeCount(page.getScrapeCount())
                .versionCount(versionCount)
                .createdAt(page.getCreatedAt())
                .build();
    }
}
