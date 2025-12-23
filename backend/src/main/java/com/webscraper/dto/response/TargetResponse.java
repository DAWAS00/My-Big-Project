package com.webscraper.dto.response;

import com.webscraper.entity.Target;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class TargetResponse {
    private UUID id;
    private String name;
    private String baseUrl;
    private String description;
    private Map<String, Object> scrapeConfig;
    private Boolean isActive;
    private Instant createdAt;
    
    public static TargetResponse from(Target target) {
        return TargetResponse.builder()
                .id(target.getId())
                .name(target.getName())
                .baseUrl(target.getBaseUrl())
                .description(target.getDescription())
                .scrapeConfig(target.getScrapeConfig())
                .isActive(target.getIsActive())
                .createdAt(target.getCreatedAt())
                .build();
    }
}
