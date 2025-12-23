package com.webscraper.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class AIQueryRequest {
    
    @NotBlank(message = "Query is required")
    private String query;
    
    private UUID targetId;  // Optional: scope to specific target
    
    private String requestType = "QUERY";
    
    private Double temperature = 0.7;
    
    private Integer maxTokens = 1000;
}
