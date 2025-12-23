package com.webscraper.infrastructure.persistence.jpa.repository;

import com.webscraper.infrastructure.persistence.jpa.entity.PageJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPageRepository extends JpaRepository<PageJpaEntity, UUID> {
    
    org.springframework.data.domain.Page<PageJpaEntity> findByTargetId(UUID targetId, Pageable pageable);
    
    Optional<PageJpaEntity> findByUrlHash(String urlHash);
    
    boolean existsByUrlHash(String urlHash);
    
    long countByTargetId(UUID targetId);
}
