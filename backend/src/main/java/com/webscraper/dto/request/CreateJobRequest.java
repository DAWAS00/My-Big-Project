package com.webscraper.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
public class CreateJobRequest {
    
    @NotNull(message = "Target ID is required")
    private UUID targetId;
    
    private Map<String, Object> config;
    
    private Instant scheduledAt;
}
