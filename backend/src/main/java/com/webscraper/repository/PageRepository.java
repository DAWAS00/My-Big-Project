package com.webscraper.repository;

import com.webscraper.entity.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PageRepository extends JpaRepository<Page, UUID> {
    
    org.springframework.data.domain.Page<Page> findByTargetId(UUID targetId, Pageable pageable);
    
    Optional<Page> findByUrlHash(String urlHash);
    
    boolean existsByUrlHash(String urlHash);
    
    @Query("SELECT COUNT(p) FROM Page p WHERE p.target.id = :targetId")
    long countByTargetId(UUID targetId);
}
