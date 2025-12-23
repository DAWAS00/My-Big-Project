package com.webscraper.web.response;

import com.webscraper.domain.entity.Target;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record TargetResponse(
    UUID id,
    String name,
    String baseUrl,
    String description,
    Map<String, Object> scrapeConfig,
    boolean active,
    Instant createdAt
) {
    public static TargetResponse from(Target target) {
        return new TargetResponse(
            target.getId(),
            target.getName(),
            target.getBaseUrl(),
            target.getDescription(),
            target.getScrapeConfig(),
            target.isActive(),
            target.getCreatedAt()
        );
    }
}
