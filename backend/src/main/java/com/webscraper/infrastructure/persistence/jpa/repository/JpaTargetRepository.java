package com.webscraper.infrastructure.persistence.jpa.repository;

import com.webscraper.infrastructure.persistence.jpa.entity.TargetJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaTargetRepository extends JpaRepository<TargetJpaEntity, UUID> {
    
    Page<TargetJpaEntity> findByUserIdAndIsActiveTrue(UUID userId, Pageable pageable);
    
    List<TargetJpaEntity> findByUserIdAndIsActiveTrue(UUID userId);
    
    long countByUserId(UUID userId);
}
