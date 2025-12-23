package com.webscraper.application.port.out;

import com.webscraper.domain.entity.Page;
import com.webscraper.domain.valueobject.UrlHash;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port for page persistence operations.
 */
public interface PageRepository {
    
    Page save(Page page);
    
    Optional<Page> findById(UUID id);
    
    Optional<Page> findByUrlHash(UrlHash urlHash);
    
    boolean existsByUrlHash(UrlHash urlHash);
    
    List<Page> findByTargetId(UUID targetId, int page, int size);
    
    long countByTargetId(UUID targetId);
}
