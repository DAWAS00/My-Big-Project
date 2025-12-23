package com.webscraper.web.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record CreateJobRequest(
    @NotNull(message = "Target ID is required")
    UUID targetId,
    
    Map<String, Object> config,
    
    Instant scheduledAt
) {}
