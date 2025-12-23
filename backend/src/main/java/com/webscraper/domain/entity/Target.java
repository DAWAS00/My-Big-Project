package com.webscraper.domain.entity;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Domain entity representing a scraping target (website).
 */
public class Target {
    private final UUID id;
    private final UUID userId;
    private String name;
    private String baseUrl;
    private String description;
    private Map<String, Object> scrapeConfig;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    public static Target create(UUID userId, String name, String baseUrl, 
                                 String description, Map<String, Object> config) {
        return new Target(
            UUID.randomUUID(),
            userId,
            name,
            baseUrl,
            description,
            config != null ? config : Map.of(),
            true,
            Instant.now(),
            Instant.now()
        );
    }

    public Target(UUID id, UUID userId, String name, String baseUrl, String description,
                  Map<String, Object> scrapeConfig, boolean active, 
                  Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.baseUrl = baseUrl;
        this.description = description;
        this.scrapeConfig = scrapeConfig;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getName() { return name; }
    public String getBaseUrl() { return baseUrl; }
    public String getDescription() { return description; }
    public Map<String, Object> getScrapeConfig() { return scrapeConfig; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // Domain behavior
    public void update(String name, String baseUrl, String description, Map<String, Object> config) {
        this.name = name;
        this.baseUrl = baseUrl;
        this.description = description;
        if (config != null) {
            this.scrapeConfig = config;
        }
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public boolean isOwnedBy(UUID userId) {
        return this.userId.equals(userId);
    }
}
