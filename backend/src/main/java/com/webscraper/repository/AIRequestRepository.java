package com.webscraper.repository;

import com.webscraper.entity.AIRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AIRequestRepository extends JpaRepository<AIRequest, UUID> {
    
    Page<AIRequest> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    Page<AIRequest> findByUserIdAndTargetIdOrderByCreatedAtDesc(UUID userId, UUID targetId, Pageable pageable);
}
