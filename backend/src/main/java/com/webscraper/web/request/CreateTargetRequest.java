package com.webscraper.web.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record CreateTargetRequest(
    @NotBlank(message = "Name is required")
    String name,
    
    @NotBlank(message = "Base URL is required")
    String baseUrl,
    
    String description,
    
    Map<String, Object> scrapeConfig
) {}
