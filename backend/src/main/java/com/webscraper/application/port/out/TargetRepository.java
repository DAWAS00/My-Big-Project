package com.webscraper.application.port.out;

import com.webscraper.domain.entity.Target;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port for target persistence operations.
 */
public interface TargetRepository {
    
    Target save(Target target);
    
    Optional<Target> findById(UUID id);
    
    List<Target> findByUserId(UUID userId, int page, int size);
    
    List<Target> findActiveByUserId(UUID userId);
    
    long countByUserId(UUID userId);
    
    void delete(UUID id);
}
