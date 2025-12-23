package com.webscraper.application.port.out;

import java.time.Duration;
import java.util.Optional;

/**
 * Port for caching operations.
 * Implemented by Redis, in-memory, or other cache providers.
 */
public interface CacheService {
    
    /**
     * Store a value in cache with TTL.
     */
    void put(String key, Object value, Duration ttl);
    
    /**
     * Store a value with default TTL (1 hour).
     */
    default void put(String key, Object value) {
        put(key, value, Duration.ofHours(1));
    }
    
    /**
     * Get a value from cache.
     */
    <T> Optional<T> get(String key, Class<T> type);
    
    /**
     * Remove a value from cache.
     */
    void evict(String key);
    
    /**
     * Remove all values matching a pattern.
     */
    void evictByPattern(String pattern);
    
    /**
     * Check if key exists.
     */
    boolean exists(String key);
}
