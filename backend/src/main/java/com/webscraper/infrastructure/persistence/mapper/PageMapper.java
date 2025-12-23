package com.webscraper.infrastructure.persistence.mapper;

import com.webscraper.domain.entity.Page;
import com.webscraper.domain.valueobject.UrlHash;
import com.webscraper.infrastructure.persistence.jpa.entity.PageJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PageMapper {
    
    public Page toDomain(PageJpaEntity entity) {
        return new Page(
            entity.getId(),
            entity.getTargetId(),
            entity.getDiscoveredByJobId(),
            entity.getUrl(),
            new UrlHash(entity.getUrlHash()),
            entity.getLastScrapedAt(),
            entity.getScrapeCount() != null ? entity.getScrapeCount() : 0,
            entity.getCreatedAt()
        );
    }
    
    public PageJpaEntity toJpa(Page page) {
        PageJpaEntity entity = new PageJpaEntity();
        entity.setId(page.getId());
        entity.setTargetId(page.getTargetId());
        entity.setDiscoveredByJobId(page.getDiscoveredByJobId());
        entity.setUrl(page.getUrl());
        entity.setUrlHash(page.getUrlHash().value());
        entity.setLastScrapedAt(page.getLastScrapedAt());
        entity.setScrapeCount(page.getScrapeCount());
        entity.setCreatedAt(page.getCreatedAt());
        return entity;
    }
}
