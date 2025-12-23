package com.webscraper.repository;

import com.webscraper.entity.PageVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PageVersionRepository extends JpaRepository<PageVersion, UUID> {
    
    Page<PageVersion> findByPageIdOrderByScrapedAtDesc(UUID pageId, Pageable pageable);
    
    @Query("SELECT pv FROM PageVersion pv WHERE pv.page.id = :pageId ORDER BY pv.scrapedAt DESC LIMIT 1")
    Optional<PageVersion> findLatestByPageId(UUID pageId);
    
    @Query("SELECT pv.contentHash FROM PageVersion pv WHERE pv.page.id = :pageId ORDER BY pv.scrapedAt DESC LIMIT 1")
    Optional<String> findLatestContentHashByPageId(UUID pageId);
}
