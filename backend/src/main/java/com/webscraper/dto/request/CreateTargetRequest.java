package com.webscraper.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class CreateTargetRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Base URL is required")
    private String baseUrl;
    
    private String description;
    
    private Map<String, Object> scrapeConfig;
}
