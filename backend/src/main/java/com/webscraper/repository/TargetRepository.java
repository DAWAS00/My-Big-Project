package com.webscraper.repository;

import com.webscraper.entity.Target;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TargetRepository extends JpaRepository<Target, UUID> {
    
    Page<Target> findByUserIdAndIsActiveTrue(UUID userId, Pageable pageable);
    
    List<Target> findByUserIdAndIsActiveTrue(UUID userId);
    
    long countByUserId(UUID userId);
}
